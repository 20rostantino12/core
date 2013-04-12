package org.jboss.as.console.mbui.behaviour;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.useware.kernel.gui.behaviour.ModelDrivenCommand;
import org.useware.kernel.gui.behaviour.Procedure;
import org.useware.kernel.gui.behaviour.StatementContext;
import org.useware.kernel.model.behaviour.Resource;
import org.useware.kernel.model.behaviour.ResourceType;
import org.useware.kernel.model.mapping.MappingType;
import org.jboss.as.console.mbui.model.mapping.AddressMapping;
import org.jboss.as.console.mbui.model.mapping.DMRMapping;
import org.useware.kernel.model.Dialog;
import org.useware.kernel.model.structure.InteractionUnit;
import org.useware.kernel.model.structure.QName;

import java.util.HashMap;

/**
 * A default procedure that writes attributes change sets to resource.
 * It creates a composite operation with write-attribute operations for each changed attribute of a resource.
 * <p/>
 * The {@link org.useware.kernel.gui.behaviour.StatementContext} is used to resolve the resource parent context (i.e. profile, server, host).
 * <p/>
 * The actual address is resolved from the {@link org.jboss.as.console.mbui.model.mapping.DMRMapping} attached to the {@link InteractionUnit} that triggered this procedure (justification).
 *
 * @see org.useware.kernel.gui.behaviour.InteractionEvent#getSource()
 *
 * @author Heiko Braun
 * @date 1/21/13
 */
public class SaveChangesetProcedure extends Procedure {

    public final static QName ID = new QName("org.jboss.as", "save");
    private DispatchAsync dispatcher;
    private Dialog dialog;

    private InteractionUnit unit;
    private AddressMapping address;

    public SaveChangesetProcedure(
            Dialog dialog,
            final QName justification,
            DispatchAsync dispatcher) {

        super(ID, justification);
        this.dialog = dialog;
        this.dispatcher = dispatcher;

        init();

        setCommand(new ModelDrivenCommand<HashMap>() {
            @Override
            public void execute(Dialog dialog, HashMap data) {

                saveResource(unit.getLabel(), address, data);
            }
        });

        // behaviour model meta data
        setInputs(new Resource<ResourceType>(ID, ResourceType.Interaction));

        // TODO: Strictly speaking this should emit system events instead of calling the coordinator API directly
    }

    private void init() {
        unit = dialog.findUnit(getJustification());

        DMRMapping DMRMapping = (DMRMapping) unit.findMapping(MappingType.DMR);
        address = AddressMapping.fromString(DMRMapping.getAddress());

    }

    private void saveResource(final String name, AddressMapping address, HashMap<String, Object> changeset) {

        StatementContext statementContext = statementScope.getContext(unit.getId());

        ModelNodeAdapter adapter = new ModelNodeAdapter();

        ModelNode operation = adapter.fromChangeset(
                changeset,
                address.asResource(statementContext));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                if (response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed(name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified(name));

                // arguable: does each save lead to a reset?
                // arguable: calling reset directly opposed to invoking a procedure...
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        coordinator.reset();
                    }
                });

            }
        });

    }
}
