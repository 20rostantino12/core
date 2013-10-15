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

package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/6/11
 */
public class XADatasourceStep2 {

    private NewXADatasourceWizard wizard;
    private SingleSelectionModel<JDBCDriver> selectionModel;
    private XADataSource editedEntity;
    private CellTable<JDBCDriver> table;
    private ComboBox groupSelection;
    private boolean isStandalone;
    private HTML errorMessages;
    private Integer selectedTab;


    public XADatasourceStep2(NewXADatasourceWizard wizard) {
        this.wizard = wizard;
        this.isStandalone = wizard.getBootstrap().isStandalone();
    }

    void edit(XADataSource dataSource) {
        this.editedEntity = dataSource;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h3>"+ Console.CONSTANTS.subsys_jca_xadataSource_step2()+"</h3>"));


        // ---


        table = new DefaultCellTable<JDBCDriver>(5);

        TextColumn<JDBCDriver> nameColumn = new TextColumn<JDBCDriver>() {
            @Override
            public String getValue(JDBCDriver record) {
                return record.getName();
            }
        };

        /*TextColumn<JDBCDriver> xaClassColumn = new TextColumn<JDBCDriver>() {
            @Override
            public String getValue(JDBCDriver record) {
                return record.getXaDataSourceClass();
            }
        };*/

        table.addColumn(nameColumn, "Name");
        //table.addColumn(xaClassColumn, "Datasource Class");

        selectionModel = new SingleSelectionModel<JDBCDriver>();
        table.setSelectionModel(selectionModel);

        // filter and select first record
        provisionTable(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);

        errorMessages = new HTML("");
        errorMessages.setStyleName("error-panel");
        errorMessages.setVisible(false);

        layout.add(errorMessages);
        final TextAreaItem dsClass = new TextAreaItem("dataSourceClass", "XA Data Source Class");

        VerticalPanel driverPanel = new VerticalPanel();
        driverPanel.add(table);
        driverPanel.add(pager);
        driverPanel.add(new ContentGroupLabel("XA Data Source Class"));
        driverPanel.add(dsClass.asWidget());

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                String xaDataSourceClass = selectionModel.getSelectedObject().getXaDataSourceClass();
                dsClass.setValue(xaDataSourceClass);
            }
        });



        final Form<JDBCDriver> form = new Form<JDBCDriver>(JDBCDriver.class);
        TextBoxItem name = new TextBoxItem("name", "Name", true);
        TextBoxItem driverClass = new TextBoxItem("driverClass", "Driver Class", false);
        TextBoxItem xaClass = new TextBoxItem("xaDataSourceClass", "XA DataSource Class", true);
        NumberBoxItem major = new NumberBoxItem("majorVersion", "Major Version")
        {
            {
                setRequired(false);
            }
        };
        NumberBoxItem minor = new NumberBoxItem("minorVersion", "Minor Version")
        {
            {
                setRequired(false);
            }
        };

        form.setFields(name, driverClass, xaClass, major, minor);

        // --



        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                selectedTab = event.getSelectedItem();
            }
        });

        tabs.add(driverPanel, "Detected Driver");
        tabs.add(form.asWidget(), "Specify Driver");

        layout.add(tabs);
        tabs.selectTab(0);

        // --

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                errorMessages.setVisible(false);
                JDBCDriver driver = null;

                if(selectedTab==0)
                {
                    form.clearValues();

                    SingleSelectionModel<JDBCDriver> selection =
                            (SingleSelectionModel<JDBCDriver>) table.getSelectionModel();
                    driver = selection.getSelectedObject();

                    if(dsClass.getValue()==null
                            || dsClass.getValue().equals(""))
                    {
                        errorMessages.setText("XA Datasource Class is required!");
                        errorMessages.setVisible(true);
                        return;
                    }
                    driver.setXaDataSourceClass(dsClass.getValue());
                }
                else
                {
                    FormValidation formValidation = form.validate();
                    if(!formValidation.hasErrors())
                    {
                        driver = form.getUpdatedEntity();
                    }
                }

                if(driver!=null) { // force selected driver

                    editedEntity.setDriverName(driver.getName());
                    editedEntity.setDriverClass(driver.getDriverClass());
                    editedEntity.setDataSourceClass(driver.getXaDataSourceClass());
                    editedEntity.setMajorVersion(driver.getMajorVersion());
                    editedEntity.setMinorVersion(driver.getMinorVersion());

                    wizard.onConfigureDriver(editedEntity);
                }
                else {
                    errorMessages.setText("A driver needs to be specified or chosen!");
                    errorMessages.setVisible(true);
                }

            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                wizard.getPresenter().closeDialogue();
            }
        };

        DialogueOptions options = new DialogueOptions(
                Console.CONSTANTS.common_label_next(),submitHandler,
                Console.CONSTANTS.common_label_cancel(),cancelHandler
        );

        return new WindowContentBuilder(layout,options).build();
    }

    private void provisionTable(final CellTable<JDBCDriver> table) {

        /*wizard.getPresenter().loadDriver(new SimpleCallback<List<JDBCDriver>>() {
   @Override
   public void onSuccess(List<JDBCDriver> drivers) {
         //
   }
});         */

        List<JDBCDriver> drivers = wizard.getDrivers();
        table.setRowCount(drivers.size(), true);
        table.setRowData(drivers);

        // clear selection
        JDBCDriver selectedDriver = selectionModel.getSelectedObject();
        if (selectedDriver != null)
            selectionModel.setSelected(selectedDriver, false);

        // new default selection
        if (drivers.size() > 0) {
            selectionModel.setSelected(drivers.get(0), true);
        }
    }

}
