package club.minemen.classes.manager;

import club.minemen.classes.playerclass.IPlayerClass;
import club.minemen.classes.playerclass.classes.MinerClass;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @since 12/31/2017
 */
@Getter
public final class ClassManager {

	private final Set<IPlayerClass> classes = Sets.newHashSet(new MinerClass());
	private final Map<UUID, IPlayerClass> playerClassMap = new HashMap<>();

	public void setPlayersClass(Player player, IPlayerClass playerClass) {
		if (playerClass == null) {
			this.playerClassMap.remove(player.getUniqueId());
		} else {
			this.playerClassMap.put(player.getUniqueId(), playerClass);
		}
	}

	public IPlayerClass getPlayersClass(Player player) {
		return this.playerClassMap.get(player.getUniqueId());
	}

}
