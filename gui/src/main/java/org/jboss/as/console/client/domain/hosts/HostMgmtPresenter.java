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

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.rbac.HostManagementGatekeeper;
import org.jboss.as.console.client.rbac.UnauthorisedPresenter;
import org.jboss.as.console.client.rbac.UnauthorizedEvent;
import org.jboss.as.console.client.shared.state.DomainEntityManager;
import org.jboss.as.console.client.shared.state.HostList;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class HostMgmtPresenter
        extends Presenter<HostMgmtPresenter.MyView, HostMgmtPresenter.MyProxy> implements
        UnauthorizedEvent.UnauthorizedHandler {

    private final PlaceManager placeManager;

    private boolean hasBeenRevealed;

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();
    private BootstrapContext bootstrap;
    private String lastSubPlace;
    private Header header;
    private final DomainEntityManager domainManager;
    private final UnauthorisedPresenter unauthorisedPresenter;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostMgmtPresenter)
    @UseGatekeeper(HostManagementGatekeeper.class)
    public interface MyProxy extends Proxy<HostMgmtPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HostMgmtPresenter presenter);
        void updateHosts(HostList hosts);
    }

    @Inject
    public HostMgmtPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            BootstrapContext bootstrap, Header header, DomainEntityManager domainManager,
            UnauthorisedPresenter unauthorisedPresenter) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.bootstrap = bootstrap;
        this.header = header;
        this.domainManager = domainManager;
        this.unauthorisedPresenter = unauthorisedPresenter;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(UnauthorizedEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // first thing: update host data
        domainManager.getHosts(new SimpleCallback<HostList>() {
            @Override
            public void onSuccess(HostList hostList) {
                getView().updateHosts(hostList);
                loadViews();
            }
        });

    }

    private void loadViews() {

        if(bootstrap.getInitialPlace()!=null)
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    Console.getEventBus().fireEvent(
                            new LHSHighlightEvent(bootstrap.getInitialPlace())
                    );

                    bootstrap.setInitialPlace(null);
                }
            });
        }

        header.highlight(NameTokens.HostMgmtPresenter);

        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();

        if(!currentToken.equals(getProxy().getNameToken()))
        {
            lastSubPlace = currentToken;
        }
        else if(lastSubPlace!=null)
        {
            placeManager.revealPlace(new PlaceRequest(lastSubPlace));
        }

        // first request, select default contents
        if(!hasBeenRevealed)
        {

            placeManager.revealPlace( new PlaceRequest(NameTokens.ServerPresenter));
            hasBeenRevealed = true;

        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onUnauthorized(final UnauthorizedEvent event) {
        setInSlot(TYPE_MainContent, unauthorisedPresenter);
    }
}
