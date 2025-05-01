package net.badlion.gberry.utils;

import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.entity.Creature;

import java.util.Iterator;
import java.util.List;

public class EntityUtil {

    /**
     * Effectively disable AI for a creature
     *
     * @param entity Entity to clear AI tasks from
     */
    public static void clearAI(Creature entity) {
        Object handle = TinyProtocolReferences.getEntityHandle.invoke(entity);
        Object goalSelector = TinyProtocolReferences.entityGoalSelector.get(handle);
        TinyProtocolReferences.pathfinderGoalSelectorTaskEntries.get(goalSelector).clear();
        TinyProtocolReferences.pathfinderGoalSelectorExecutingTaskEntries.get(goalSelector).clear();
        Object targetSelector = TinyProtocolReferences.entityTargetSelector.get(handle);
        TinyProtocolReferences.pathfinderGoalSelectorTaskEntries.get(targetSelector).clear();
        TinyProtocolReferences.pathfinderGoalSelectorExecutingTaskEntries.get(targetSelector).clear();
    }

    /**
     * Clear an entities AI except for the float task (swim upwards in water)
     *
     * @param entity Entity to clear AI tasks from
     */
    public static void clearAIExceptFloat(Creature entity) {
        Object handle = TinyProtocolReferences.getEntityHandle.invoke(entity);
        Object goalSelector = TinyProtocolReferences.entityGoalSelector.get(handle);
        List taskEntries = TinyProtocolReferences.pathfinderGoalSelectorTaskEntries.get(goalSelector);
        Iterator iter = taskEntries.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            Object action = TinyProtocolReferences.pathfinderGoalSelectorItemAction.get(item);
            if (!TinyProtocolReferences.pathfinderGoalFloat.isInstance(action)) {
                iter.remove();
            }
        }
        TinyProtocolReferences.pathfinderGoalSelectorExecutingTaskEntries.get(goalSelector).clear();
        Object targetSelector = TinyProtocolReferences.entityTargetSelector.get(handle);
        TinyProtocolReferences.pathfinderGoalSelectorTaskEntries.get(targetSelector).clear();
        TinyProtocolReferences.pathfinderGoalSelectorExecutingTaskEntries.get(targetSelector).clear();
    }

    /**
     * Get a unused entity ID, suitable for spawning fake entities with packets
     * @return Entity ID
     */
    public static int newEntityID() {
        int id = TinyProtocolReferences.entityCount.get(null);
        TinyProtocolReferences.entityCount.set(null, ++id);
        return id;
    }
}
