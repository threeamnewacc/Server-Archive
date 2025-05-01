package net.badlion.arenasetup;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenasetup.manager.ArenaManager;
import net.badlion.gedit.sessions.Session;
import net.badlion.gedit.sessions.SessionManager;
import net.badlion.gedit.util.SchematicUtil;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetupSession {

	private String arenaName;

	private List<KitRuleSet> types;

	private WandSelection selection;

	private Location warp1;

	private Location warp2;

	public SetupSession(String arenaName, WandSelection wandSelection) {
		this.arenaName = arenaName;
		this.selection = wandSelection;
		this.types = new ArrayList<>();
	}

	public List<KitRuleSet> getTypes() {
		return types;
	}

	public Location getWarp1() {
		return warp1;
	}

	public Location getWarp2() {
		return warp2;
	}

	public String getArenaName() {
		return arenaName;
	}

	public WandSelection getSelection() {
		return selection;
	}

	public void setWarp1(Location warp1) {
		this.warp1 = warp1;
	}

	public void setWarp2(Location warp2) {
		this.warp2 = warp2;
	}

	public boolean isValid() {
		if (arenaName == null) {
			return false;
		}
		if (warp1 == null || warp2 == null) {
			return false;
		}
		if (warp2.getWorld() != warp1.getWorld()) {
			return false;
		}
		if (selection == null) {
			return false;
		}
		if (types.isEmpty()) {
			return false;
		}
		return true;
	}


	public void generateArenaData(Player player) {
		// The origin is just the minimum point in our selection, the warps will be relative to 0, 0, 0 schematic wise, and the minimum point is 0,0,0 to the schematic
		Location origin = selection.getMinPoint();


		Location warp1Origin = new Location(warp1.getWorld(), warp1.getX() - origin.getX(), warp1.getY() - origin.getY(), warp1.getZ() - origin.getZ(), warp1.getYaw(), warp1.getPitch());
		Location warp2Origin = new Location(warp2.getWorld(), warp2.getX() - origin.getX(), warp2.getY() - origin.getY(), warp2.getZ() - origin.getZ(), warp2.getYaw(), warp2.getPitch());

		ArenaManager.addWarp(arenaName + "-1", player, warp1Origin);
		ArenaManager.addWarp(arenaName + "-2", player, warp2Origin);
		StringBuilder stringBuilder = new StringBuilder();
		List<Integer> arenaTypes = new ArrayList<>();
		for (KitRuleSet kitRuleSet : getTypes()) {
			if (arenaTypes.contains(kitRuleSet.getArenaType().ordinal())) {
				continue;
			}
			arenaTypes.add(kitRuleSet.getId());
			stringBuilder.append(kitRuleSet.getId() + ",");
		}
		String types = stringBuilder.substring(0, stringBuilder.length() - 1);

		ArenaManager.addArena(player, arenaName, types, arenaName + "-1", arenaName + "-2");

		Session session = SessionManager.getSession(player);
		session.setWandSelection(selection);

		try {
			SchematicUtil.saveSession(selection, new Location(origin.getWorld(), 0, 0, 0), arenaName);
			player.sendMessage(ChatColor.GREEN + "The arena schematic has been saved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArenaSetup.getInstance().getSetupSessionMap().remove(player.getUniqueId());
		player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Finished Setup! Good work.");
	}
}
