package net.badlion.gberry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractListCommandHandler implements Listener {

	protected String admins, managers, seniorMods, mods, otherStaff, trials, famousPlus, famous, lions, donatorPlus, donators;

	private List<Player> hiddenStaffMembers = new ArrayList<>();

	private List<Player> adminsOnline = new LinkedList<>();
	private List<Player> managersOnline = new LinkedList<>();
	private List<Player> seniorModsOnline = new LinkedList<>();
	private List<Player> modsOnline = new LinkedList<>();
	private List<Player> otherStaffOnline = new LinkedList<>();
	private List<Player> trialsOnline = new LinkedList<>();
	private List<Player> famousPlusOnline = new LinkedList<>();
	private List<Player> famousOnline = new LinkedList<>();
	private List<Player> lionsOnline = new LinkedList<>();
	private List<Player> donatorPlusOnline = new LinkedList<>();
	private List<Player> donatorsOnline = new LinkedList<>();

	public AbstractListCommandHandler() {
		Gberry.plugin.getServer().getPluginManager().registerEvents(this, Gberry.plugin);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		// Update /list
		Gberry.plugin.getListCommandHandler().addPlayerToList(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		// Update /list
		Gberry.plugin.getListCommandHandler().removePlayerFromList(event.getPlayer());

		// Remove from hidden staff list
		this.hiddenStaffMembers.remove(event.getPlayer());
	}

	public abstract void handleListCommand(Player player);

	public void hideStaffFromList(Player player) {
		this.hiddenStaffMembers.add(player);
	}

	public void addPlayerToList(Player player) {
		// Don't add disguised players to /list
		if (player.isDisguised()) {
			return;
		}

		if (player.hasPermission("badlion.admin")) {
			this.adminsOnline.add(player);
		} else if (player.hasPermission("badlion.manager")) {
			this.managersOnline.add(player);
		} else if (player.hasPermission("badlion.senior")) {
			this.seniorModsOnline.add(player);
		} else if (player.hasPermission("badlion.mod")) {
			this.modsOnline.add(player);
		} else if (player.hasPermission("badlion.trial")) {
			this.trialsOnline.add(player);
		} else if (player.hasPermission("badlion.staff")) {
			this.otherStaffOnline.add(player);
		} else if (player.hasPermission("badlion.famousplus")) {
			this.famousPlusOnline.add(player);
		} else if (player.hasPermission("badlion.famous")) {
			this.famousOnline.add(player);
		} else if (player.hasPermission("badlion.lion")) {
			this.lionsOnline.add(player);
		} else if (player.hasPermission("badlion.donatorplus")) {
			this.donatorPlusOnline.add(player);
		} else if (player.hasPermission("badlion.donator")) {
			this.donatorsOnline.add(player);
		}
	}

	public void removePlayerFromList(Player player) {
		if (this.adminsOnline.remove(player)) {
		} else if (this.managersOnline.remove(player)) {
		} else if (this.seniorModsOnline.remove(player)) {
		} else if (this.modsOnline.remove(player)) {
		} else if (this.otherStaffOnline.remove(player)) {
		} else if (this.trialsOnline.remove(player)) {
		} else if (this.famousPlusOnline.remove(player)) {
		} else if (this.famousOnline.remove(player)) {
		} else if (this.lionsOnline.remove(player)) {
		} else if (this.donatorPlusOnline.remove(player)) {
		} else if (this.donatorsOnline.remove(player)) {
		}
	}

	public void updateListCommandStrings() {
		this.admins = this.serializePlayerNames(this.adminsOnline);
		this.managers = this.serializePlayerNames(this.managersOnline);
		this.seniorMods = this.serializePlayerNames(this.seniorModsOnline);
		this.mods = this.serializePlayerNames(this.modsOnline);
		this.otherStaff = this.serializePlayerNames(this.otherStaffOnline);
		this.trials = this.serializePlayerNames(this.trialsOnline);
		this.famousPlus = this.serializePlayerNames(this.famousPlusOnline);
		this.famous = this.serializePlayerNames(this.famousOnline);
		this.lions = this.serializePlayerNames(this.lionsOnline);
		this.donatorPlus = this.serializePlayerNames(this.donatorPlusOnline);
		this.donators = this.serializePlayerNames(this.donatorsOnline);
	}

	private String serializePlayerNames(List<Player> list) {
		StringBuilder sb = new StringBuilder();

		if (list.size() > 0) {
			for (Player player : list) {
				sb.append(", ");
				sb.append(player.getDisplayName());
				sb.append(ChatColor.RESET);
			}

			return sb.substring(2);
		} else {
			return "None";
		}
	}

	public List<Player> getHiddenStaffMembers() {
		return this.hiddenStaffMembers;
	}

	public List<Player> getAdminsOnline() {
		return this.adminsOnline;
	}

	public List<Player> getManagersOnline() {
		return this.managersOnline;
	}

	public List<Player> getSeniorModsOnline() {
		return this.seniorModsOnline;
	}

	public List<Player> getModsOnline() {
		return this.modsOnline;
	}

	public List<Player> getOtherStaffOnline() {
		return this.otherStaffOnline;
	}

	public List<Player> getTrialsOnline() {
		return this.trialsOnline;
	}

	public List<Player> getFamousPlusOnline() {
		return this.famousPlusOnline;
	}

	public List<Player> getFamousOnline() {
		return this.famousOnline;
	}

	public List<Player> getLionsOnline() {
		return this.lionsOnline;
	}

	public List<Player> getDonatorPlusOnline() {
		return this.donatorPlusOnline;
	}

	public List<Player> getDonatorsOnline() {
		return this.donatorsOnline;
	}

}
