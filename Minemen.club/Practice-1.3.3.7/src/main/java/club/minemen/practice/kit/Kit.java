package club.minemen.practice.kit;

import club.minemen.core.util.finalutil.CC;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Kit {

	private final String name;

	private ItemStack[] contents = new ItemStack[36];
	private ItemStack[] armor = new ItemStack[4];
	private ItemStack[] kitEditContents = new ItemStack[36];
	private ItemStack icon;

	private List<String> excludedArenas = new ArrayList<>();
	private List<String> arenaWhiteList = new ArrayList<>();

	private boolean enabled;
	private boolean ranked;
	private boolean combo;
	private boolean sumo;
	private boolean build;
	private boolean spleef;

	public void applyToPlayer(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armor);
		player.updateInventory();
		player.sendMessage(CC.PRIMARY + "Giving you the default kit.");
	}

	public void whitelistArena(String arena) {
		if (!this.arenaWhiteList.remove(arena)) {
			this.arenaWhiteList.add(arena);
		}
	}

	public void excludeArena(String arena) {
		if (!this.excludedArenas.remove(arena)) {
			this.excludedArenas.add(arena);
		}
	}

}
