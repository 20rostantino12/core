package org.useware.kernel.model.structure;

import org.useware.kernel.model.behaviour.Resource;
import org.useware.kernel.model.behaviour.ResourceType;

/**
 * The trigger element is used to specify a command from the user perspective.
 * This might be a function call or a navigation trigger.
 * <p/>
 * It consists of an interaction unit id, but also requires a specific trigger type declaration
 * that specifies the output of this unit.
 *
 * @author Heiko Braun
 * @date 1/16/13
 */
public class Trigger<S extends Enum<S>> extends InteractionUnit<S> {

    public Trigger(QName unitId, QName triggerType, String label) {
        super(unitId, label);

        // the suffix determines the operation name
        assert triggerType.getSuffix()!=null : "Invalid trigger type declaration: "+triggerType;

        // explicit output
        setOutputs(new Resource<ResourceType>(triggerType, ResourceType.Interaction));
    }

    @Override
    public String toString()
    {
        return "Trigger {" + getId() + ", label="+ getLabel()+"}";
    }
}
