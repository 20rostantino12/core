/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.mbui.reification;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.widgets.pages.Pages;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.useware.kernel.gui.behaviour.NavigationEvent;
import org.useware.kernel.gui.behaviour.SystemEvent;
import org.useware.kernel.gui.behaviour.common.CommonQNames;
import org.useware.kernel.gui.reification.Context;
import org.useware.kernel.gui.reification.ContextKey;
import org.useware.kernel.gui.reification.strategy.ReificationStrategy;
import org.useware.kernel.gui.reification.strategy.ReificationWidget;
import org.useware.kernel.model.behaviour.Resource;
import org.useware.kernel.model.behaviour.ResourceType;
import org.useware.kernel.model.structure.Container;
import org.useware.kernel.model.structure.InteractionUnit;
import org.useware.kernel.model.structure.QName;
import org.jboss.as.console.mbui.model.StereoTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.useware.kernel.model.structure.TemporalOperator.Choice;

/**
 * Strategy for a container with temporal operator == Choice.
 *
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class ChoiceStrategy implements ReificationStrategy<ReificationWidget, StereoTypes>
{
    private EventBus eventBus;
    private static final Resource<ResourceType> NAVIGATION = new Resource<ResourceType>(CommonQNames.NAVIGATION_ID, ResourceType.Navigation);
    private static final Resource<ResourceType> ACTIVATION = new Resource<ResourceType>(CommonQNames.ACTIVATION_ID, ResourceType.System);

    @Override
    public boolean prepare(InteractionUnit<StereoTypes> interactionUnit, Context context) {
        eventBus = context.get(ContextKey.EVENTBUS);
        return eventBus !=null;
    }

    @Override
    public ReificationWidget reify(final InteractionUnit<StereoTypes> interactionUnit, final Context context)
    {
        return new TabPanelAdapter(interactionUnit);
    }

    @Override
    public boolean appliesTo(final InteractionUnit<StereoTypes> interactionUnit)
    {
        return (interactionUnit instanceof Container) && (((Container) interactionUnit)
                .getTemporalOperator() == Choice);
    }


    class TabPanelAdapter  implements ReificationWidget
    {
        private TabPanelContract delegate;
        private InteractionUnit interactionUnit;
        private Map<Integer, QName> childUnits = new HashMap<Integer, QName>();

        TabPanelAdapter(final InteractionUnit<StereoTypes> interactionUnit)
        {
            this.interactionUnit = interactionUnit;

            if(interactionUnit.getStereotype()!=null)
            {
                switch (interactionUnit.getStereotype())
                {
                    case EditorPanel:
                        this.delegate = createEditorPanel(interactionUnit, eventBus);
                        break;
                    case Pages:
                        this.delegate = createPages(interactionUnit, eventBus);
                        break;
                    default:
                        throw new RuntimeException("Unsupported stereotype "+interactionUnit.getStereotype());
                }
            }
            else
            {
                this.delegate = createDefaultTabPanel(interactionUnit, eventBus);
            }
        }

        private TabPanelContract createEditorPanel(final InteractionUnit interactionUnit, final EventBus eventBus) {
            final DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
            tabLayoutpanel.addStyleName("default-tabpanel");

            tabLayoutpanel.addBeforeSelectionHandler(new NavigationHandler(interactionUnit, childUnits, tabLayoutpanel));

           /* tabLayoutpanel.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent attachEvent) {

                    if(attachEvent.isAttached() && tabLayoutpanel.getWidgetCount()>0)
                        tabLayoutpanel.selectTab(0);
                }
            });*/

            TabPanelContract tabPanelContract = new TabPanelContract() {
                @Override
                public void add(InteractionUnit unit, Widget widget) {
                    final VerticalPanel vpanel = new VerticalPanel();
                    vpanel.setStyleName("rhs-content-panel");
                    vpanel.add(widget);

                    ScrollPanel scroll = new ScrollPanel(vpanel);
                    tabLayoutpanel.add(scroll, unit.getLabel());

                    // register tab2index mapping
                    childUnits.put(tabLayoutpanel.getWidgetCount() - 1, unit.getId());
                }

                @Override
                public Widget as() {
                    return tabLayoutpanel;
                }
            };


            // activation listener
            eventBus.addHandler(SystemEvent.TYPE,
                    new ChildActivationHandler<DefaultTabLayoutPanel>(tabLayoutpanel, childUnits, new ChildUnitCommand<DefaultTabLayoutPanel>() {
                        @Override
                        public void execute(DefaultTabLayoutPanel parent, int key) {
                            tabLayoutpanel.selectTab(key, false);
                        }
                    })
            );

            eventBus.addHandler(SystemEvent.TYPE,
                    new SystemEvent.Handler() {
                        @Override
                        public boolean accepts(SystemEvent event) {

                            return event.getId().equals(CommonQNames.ACTIVATION_ID)
                                    && childUnits.containsValue(event.getPayload()
                            );
                        }

                        @Override
                        public void onSystemEvent(SystemEvent event) {
                            QName id = (QName) event.getPayload();

                            Set<Integer> keys = childUnits.keySet();
                            for (Integer key : keys) {
                                if (childUnits.get(key).equals(id)) {
                                    tabLayoutpanel.selectTab(key, false);
                                    break;
                                }
                            }
                        }
                    }
            );


            // complement model

            getInteractionUnit().setOutputs(NAVIGATION);
            getInteractionUnit().setInputs(ACTIVATION);

            return tabPanelContract;
        }

        private TabPanelContract createPages(InteractionUnit<StereoTypes> interactionUnit, EventBus eventBus) {
            final Pages pagedView = new Pages();

            pagedView.addBeforeSelectionHandler(new NavigationHandler(interactionUnit, childUnits, pagedView));

            // activation listener

            eventBus.addHandler(SystemEvent.TYPE,
                    new ChildActivationHandler<Pages>(pagedView, childUnits, new ChildUnitCommand<Pages>() {
                        @Override
                        public void execute(Pages parent, int key) {
                            // TODO: this doesn't really work (rendering bugs) ...
                            parent.selectTab(key, false);
                            parent.getDeckPanel().showWidget(key); // workaround
                        }
                    })
            );

            TabPanelContract tabPanelContract = new TabPanelContract() {

                @Override
                public void add(InteractionUnit unit, Widget widget) {

                    widget.addStyleName("rhs-content-panel");

                    pagedView.add(widget, unit.getLabel());

                    // register tab2index mapping
                    childUnits.put(pagedView.getWidgetCount() - 1, unit.getId());
                }

                @Override
                public Widget as() {
                    Widget widget = pagedView.asWidget();
                    widget.addStyleName("fill-layout");

                    widget.addAttachHandler(new AttachEvent.Handler() {
                        @Override
                        public void onAttachOrDetach(AttachEvent attachEvent) {
                            if (pagedView.getWidgetCount() > 0)
                                pagedView.selectTab(0, true);
                        }
                    });

                    //pagedView.showPage(0);
                    return pagedView;
                }
            };


            // complement model

            getInteractionUnit().setOutputs(NAVIGATION);
            getInteractionUnit().setInputs(ACTIVATION);

            return tabPanelContract;
        }

        private TabPanelContract createDefaultTabPanel(InteractionUnit interactionUnit, EventBus eventBus) {
            final TabPanel tabPanel = new TabPanel();
            tabPanel.setStyleName("default-tabpanel");

            tabPanel.addBeforeSelectionHandler(new NavigationHandler(interactionUnit, childUnits, tabPanel));

            /*tabPanel.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent attachEvent) {

                    if(attachEvent.isAttached() && tabPanel.getWidgetCount()>0)
                        tabPanel.selectTab(0);
                }
            });*/


            // activation listener
            eventBus.addHandler(SystemEvent.TYPE,
                    new ChildActivationHandler<TabPanel>(tabPanel, childUnits, new ChildUnitCommand<TabPanel>() {
                        @Override
                        public void execute(TabPanel parent, int key) {
                            // TODO: this doesn't really work (rendering bugs) ...
                            parent.selectTab(key, false);
                            parent.getDeckPanel().showWidget(key); // workaround
                        }
                    })
            );

            TabPanelContract tabPanelContract = new TabPanelContract() {
                @Override
                public void add(InteractionUnit unit, Widget widget) {
                    tabPanel.add(widget, unit.getLabel());

                    // register tab2index mapping
                    childUnits.put(tabPanel.getWidgetCount() - 1, unit.getId());
                }

                @Override
                public Widget as() {
                    return tabPanel;
                }
            };

            // complement model

            getInteractionUnit().setOutputs(NAVIGATION);
            getInteractionUnit().setInputs(ACTIVATION);


            return tabPanelContract;
        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
        {
            delegate.add(widget.getInteractionUnit(), widget.asWidget());
        }

        @Override
        public Widget asWidget()
        {
            return delegate.as();
        }
    }

    class NavigationHandler implements BeforeSelectionHandler<Integer>
    {
        private final Composite widget;
        private InteractionUnit interactionUnit;
        private Map<Integer, QName> childUnits = new HashMap<Integer, QName>();

        NavigationHandler(InteractionUnit interactionUnit, Map<Integer, QName> childUnits, Composite widget) {
            this.interactionUnit = interactionUnit;
            this.childUnits = childUnits;
            this.widget = widget;
        }

        @Override
        public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

            Object source = event.getSource();

            QName targetTab = childUnits.get(event.getItem());

            if (targetTab != null) {
                eventBus.fireEventFromSource(
                        new NavigationEvent(
                                CommonQNames.NAVIGATION_ID,
                                targetTab
                        ), interactionUnit.getId() //source
                );

            }
            event.cancel();
        }
    };

    class ChildActivationHandler<T> implements SystemEvent.Handler {

        private final T parent;
        private Map<Integer, QName> childUnits = new HashMap<Integer, QName>();
        private ChildUnitCommand<T> cmd;

        ChildActivationHandler(T parent, Map<Integer, QName> childUnits, ChildUnitCommand<T> cmd) {
            this.parent = parent;
            this.childUnits = childUnits;
            this.cmd = cmd;
        }

        @Override
        public boolean accepts(SystemEvent event) {

            return event.getId().equals(CommonQNames.ACTIVATION_ID)
                    && childUnits.containsValue(event.getPayload()
            );
        }

        @Override
        public void onSystemEvent(SystemEvent event) {
            QName id = (QName) event.getPayload();

            Set<Integer> keys = childUnits.keySet();
            for (final Integer key : keys) {
                if (childUnits.get(key).equals(id)) {

                    Scheduler.get().scheduleDeferred(
                            new Scheduler.ScheduledCommand() {
                                @Override
                                public void execute() {
                                    cmd.execute(parent, key);
                                }
                            }
                    );

                    break;
                }
            }
        }


    };

    interface ChildUnitCommand<T> {
        void execute(T parent, int key);
    }
}
