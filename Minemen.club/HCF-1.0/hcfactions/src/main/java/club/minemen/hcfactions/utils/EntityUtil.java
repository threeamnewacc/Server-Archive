package club.minemen.hcfactions.utils;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;

import java.lang.reflect.Field;
import java.util.List;

public class EntityUtil {

	private static Field entityCount, pathfinderGoalSelectorTaskEntries, pathfinderGoalSelectorExecutingTaskEntries;

	static {
		try {
			entityCount = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("entityCount");
			entityCount.setAccessible(true);

			pathfinderGoalSelectorTaskEntries = PathfinderGoalSelector.class.getDeclaredField("b");
			pathfinderGoalSelectorTaskEntries.setAccessible(true);

			pathfinderGoalSelectorExecutingTaskEntries = PathfinderGoalSelector.class.getDeclaredField("c");
			pathfinderGoalSelectorExecutingTaskEntries.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Effectively disable AI for a creature
	 *
	 * @param entity Entity to clear AI tasks from
	 */
	public static void clearAI(Creature entity) {
		EntityCreature entityCreature = ((CraftCreature) entity).getHandle();

		try {
			((List) pathfinderGoalSelectorTaskEntries.get(entityCreature.goalSelector)).clear();
			((List) pathfinderGoalSelectorExecutingTaskEntries.get(entityCreature.goalSelector)).clear();

			((List) pathfinderGoalSelectorTaskEntries.get(entityCreature.targetSelector)).clear();
			((List) pathfinderGoalSelectorExecutingTaskEntries.get(entityCreature.targetSelector)).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear an entities AI except for the float task (swim upwards in water)
	 *
	 * @param entity Entity to clear AI tasks from
	 */
	public static void clearAIExceptFloat(Creature entity) {
		EntityCreature entityCreature = ((CraftCreature) entity).getHandle();

		try {
			List taskEntries = (List) pathfinderGoalSelectorTaskEntries.get(entityCreature.goalSelector);

			for (Object object : taskEntries) {
				if (!(object instanceof PathfinderGoalFloat)) {
					taskEntries.remove(object);
				}
			}

			((List) pathfinderGoalSelectorExecutingTaskEntries.get(entityCreature.goalSelector)).clear();

			((List) pathfinderGoalSelectorTaskEntries.get(entityCreature.targetSelector)).clear();
			((List) pathfinderGoalSelectorExecutingTaskEntries.get(entityCreature.targetSelector)).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a unused entity ID, suitable for spawning fake entities with packets
	 *
	 * @return Entity ID
	 */
	public static int newEntityID() {
		if (entityCount == null) {
			try {
				entityCount = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("entityCount");
				entityCount.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			int entityId = entityCount.getInt(null);
			entityId++;
			return entityId;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}
}
