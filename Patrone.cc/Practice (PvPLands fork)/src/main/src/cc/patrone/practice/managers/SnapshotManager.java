package cc.patrone.practice.managers;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.InventorySnapshot;
import cc.patrone.practice.kit.Kit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SnapshotManager {
	private final PracticePlugin plugin;
	private final Cache<String, InventorySnapshot> snapshots = CacheBuilder
			.newBuilder()
			.expireAfterWrite(1L, TimeUnit.MINUTES)
			.build();

	public void cacheSnapshot(Player player, boolean dead, Kit kit) {
		snapshots.put(player.getName(), new InventorySnapshot(player, plugin, dead, kit));
	}

	public InventorySnapshot getSnapshot(String name) {
		return snapshots.getIfPresent(name);
	}
}
