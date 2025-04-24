package club.minemen.practice.ffa;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.ffa.killstreak.KillStreak;
import club.minemen.practice.ffa.killstreak.impl.DebuffKillStreak;
import club.minemen.practice.ffa.killstreak.impl.GappleKillStreak;
import club.minemen.practice.ffa.killstreak.impl.GodAppleKillStreak;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class FFAManager {

	@Getter
	private final Map<Item, Long> itemTracker = new HashMap<>();
	@Getter
	private final Map<UUID, Integer> killStreakTracker = new HashMap<>();

	@Getter
	private final Set<KillStreak> killStreaks = new HashSet<>();

	private final Practice plugin = Practice.getInstance();

	private final CustomLocation spawnPoint;
	private final Kit kit;

	public void addPlayer(Player player) {
		if (this.killStreaks.isEmpty()) {
			this.killStreaks.add(new GappleKillStreak());
			this.killStreaks.add(new DebuffKillStreak());
			this.killStreaks.add(new GodAppleKillStreak());
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		playerData.setPlayerState(PlayerState.FFA);

		player.teleport(this.spawnPoint.toBukkitLocation());

		player.sendMessage(CC.SECONDARY + "Welcome to the FFA arena!");

		this.kit.applyToPlayer(player);

		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack = player.getInventory().getContents()[i];
			if (itemStack != null && itemStack.getType() == Material.POTION) {
				player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
			}
		}

		player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

		for (PlayerData data : this.plugin.getPlayerManager().getAllData()) {
			Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
			if (data.getPlayerState() == PlayerState.FFA) {
				player.showPlayer(player1);
				player1.showPlayer(player);
			} else {
				player.hidePlayer(player1);
				player1.hidePlayer(player);
			}
		}
	}

	public void removePlayer(Player player) {
		for (PlayerData data : this.plugin.getPlayerManager().getAllData()) {
			Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
			if (data.getPlayerState() == PlayerState.FFA) {
				player.hidePlayer(player1);
				player1.hidePlayer(player);
			}
		}

		this.plugin.getPlayerManager().sendToSpawnAndReset(player);
	}

}
