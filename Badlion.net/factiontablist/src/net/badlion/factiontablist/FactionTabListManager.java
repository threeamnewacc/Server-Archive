package net.badlion.factiontablist;

import net.badlion.tablist.GameProfileHook;
import net.badlion.tablist.GameProfileScheduler;
import net.badlion.tablist.TabListManager;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.Ladder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FactionTabListManager extends TabListManager {

	private IChatBaseComponent header;
	private IChatBaseComponent footer;

	private List<String> staff = new LinkedList<>();
	private Map<String, Boolean> staffNames = new ConcurrentHashMap<>();
	private Map<String, Property> textureProperties = new ConcurrentHashMap<>();

	private List<Player> adminsOnline = new LinkedList<>();
	private List<Player> seniorModsOnline = new LinkedList<>();
	private List<Player> modsOnline = new LinkedList<>();
	private List<Player> trialsOnline = new LinkedList<>();
	private List<Player> famousOnline = new LinkedList<>();
	private List<Player> donatorPlusOnline = new LinkedList<>();
	private List<Player> donatorsOnline = new LinkedList<>();

	public PotionTabListManager() {
		super(PotionTabList.getInstance());

		this.header = ChatSerializer.a("{'color': 'gold', 'text': 'Welcome to the Badlion Network!'}");
		this.footer = ChatSerializer.a("{'text': '" +
				"§6NA Proxy: §ana.badlion.net                 §6Website: §awww.badlion.net\n" +
				"§6EU Proxy: §aeu.badlion.net         §6Twtr: §atwitter.com/BadlionNetwork\n" +
				"§6Teamspeak: §ats.badlion.net               §6Plug.DJ: §aplug.dj/badlion'}");

		// Handle staff textures
		GameProfileScheduler.addHook(new GameProfileHook() {
			@Override
			public GameProfile handleGameProfile(GameProfile gameProfile) {
				if (PotionTabListManager.this.staffNames.containsKey(gameProfile.getName())) {
					if (PotionTabListManager.this.textureProperties.containsKey(gameProfile.getName())) {
						gameProfile.getProperties().removeAll("textures");
						gameProfile.getProperties().put("textures", PotionTabListManager.this.textureProperties.get(gameProfile.getName()));
					}
				}

				return gameProfile;
			}
		});
	}

	public void createTabList(Player player, Map<Ladder, Integer> ratings) {
		net.badlion.tablist.TabList tabList = new net.badlion.tablist.TabList(player, 1, 60);

		// Set up strings
		Integer rating, rating2;
		tabList.setPosition(1, "§cRatings:", false);
		tabList.setPosition(2, "§9Vanilla", false);
		rating = ratings.get(Ladder.getLadder("Vanilla", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(3, "§d " + rating, false);

		tabList.setPosition(4, "§9MCSG", false);
		rating = ratings.get(Ladder.getLadder("MCSG", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(5, "§d " + rating + " ", false);

		tabList.setPosition(6, "§9NoDebuff", false);
		rating = ratings.get(Ladder.getLadder("NoDebuff", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(7, "§d " + rating + "  ", false);

		tabList.setPosition(8, "§9Archer", false);
		rating = ratings.get(Ladder.getLadder("Archer", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(9, "§d " + rating + "   ", false);

		tabList.setPosition(10, "§9UHC", false);
		rating = ratings.get(Ladder.getLadder("UHC", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(11, "§d " + rating + "    ", false);

		tabList.setPosition(12, "§9AdvancedUHC", false);
		rating = ratings.get(Ladder.getLadder("AdvancedUHC", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(13, "§d " + rating + (char) 0x26f8, false);

		tabList.setPosition(14, "§9Gold - GApple", false);
		rating = ratings.get(Ladder.getLadder("Gold", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		rating2 = ratings.get(Ladder.getLadder("GApple", Ladder.LadderType.OneVsOneRanked));
		if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
		tabList.setPosition(15, "§d " + rating + " - " + rating2, false);

		tabList.setPosition(16, "§9Iron - Diamond", false);
		rating = ratings.get(Ladder.getLadder("Iron", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		rating2 = ratings.get(Ladder.getLadder("Diamond", Ladder.LadderType.OneVsOneRanked));
		if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
		tabList.setPosition(17, "§d " + rating + " - " + rating2 + " ", false);

		tabList.setPosition(18, "§9Kohi", false);
		rating = ratings.get(Ladder.getLadder("Kohi", Ladder.LadderType.OneVsOneRanked));
		if (rating == null) rating = RatingManager.DEFAULT_RATING;
		tabList.setPosition(19, "§d " + rating + (char) 0x26c7, false);

		tabList.setPosition(21, "§cServer Info:", false);
		tabList.setPosition(22, "§9Players - " + PotionTabList.getInstance().getServer().getOnlinePlayers().size(), false);
		tabList.setPosition(23, "§9na.badlion.net", false);
		tabList.setPosition(24, "§9eu.badlion.net", false);

		tabList.setPosition(30, "§6§lBadlion PVP", false);
		tabList.setPosition(31, "§6§lBadlion PVP ", false);

		tabList.setPosition(39, "§cDonation Info:", false);
		if (player.hasPermission("badlion.staff")) {
			tabList.setPosition(40, "§9 Staff Member", false);
		} else if (player.hasPermission("PVPServer.famous")) {
			tabList.setPosition(40, "§9 Famous", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(40, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(40, "§9 Donator", false);
		} else {
			tabList.setPosition(40, "§9 N/A", false);
		}

		tabList.setPosition(41, "§cStaff Online:", false);
		for (int x = 0; x < 17; x++) {
			String name = "";

			if (x < this.staff.size()) {
				name = this.staff.get(x);
			}

			tabList.setPosition(42 + x, name, false);
		}

		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);

		tabList.setPosition(59, "§cRanked Left:", false);
		if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(60, "§9 Unlimited", false);
		} else if (rankedLeft > 0) {
			tabList.setPosition(60, "§9 " + rankedLeft, false);
		} else {
			tabList.setPosition(60, "§9 None", false);
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

	public void updateStaffList() {
		Map<Integer, String> tabChanges = new HashMap<>();

		// Update player count too to fix some random race condition
		tabChanges.put(22, "§9Players - " + Bukkit.getOnlinePlayers().size());

		for (int x = 0; x < 16; x++) {
			String name = "";

			if (x < PotionTabListManager.this.staff.size()) {
				name = PotionTabListManager.this.staff.get(x);

				//System.out.println(staff.size() + x);
			}

			tabChanges.put(42 + x, name);
		}

		PotionTabListManager.this.setAllTabListPositions(tabChanges, true);
	}

	public boolean checkIfStaffJoined(Player player) {
		boolean update = false;
		if (player.hasPermission("PVPServer.admin")) {
			this.adminsOnline.add(player);
			update = true;
		} else if (player.hasPermission("PVPServer.seniormod")) {
			this.seniorModsOnline.add(player);
			update = true;
		} else if (player.hasPermission("PVPServer.mod") && !player.hasPermission("PVPServer.trialmod")) {
			this.modsOnline.add(player);
			update = true;
		} else if (player.hasPermission("PVPServer.trialmod")) {
			this.trialsOnline.add(player);
			update = true;
		} else if (player.hasPermission("PVPServer.famous")) {
			this.famousOnline.add(player);
		} else if (player.hasPermission("badlion.donatorplus")) {
			this.donatorPlusOnline.add(player);
		} else if (player.hasPermission("badlion.donator")) {
			this.donatorsOnline.add(player);
		}

		if (update) {
			this.staff.clear(); // reset
			for (Player pl : this.adminsOnline) {
				String name = pl.getName();
				name = ChatColor.stripColor(name);
				this.staff.add("§4" + (name.length() > 14 ? name.substring(0, 14) : name));
				this.staffNames.put("§4" + (name.length() > 14 ? name.substring(0, 14) : name), true);
				for (Map.Entry<String, Property> entry : ((CraftPlayer) pl).getProfile().getProperties().entries()) {
					if (entry.getKey().equals("textures")) {
						this.textureProperties.put("§4" + (name.length() > 14 ? name.substring(0, 14) : name), entry.getValue());
					}
				}
			}

			for (Player pl : this.seniorModsOnline) {
				String name = pl.getName();
				name = ChatColor.stripColor(name);
				this.staff.add("§5" + (name.length() > 14 ? name.substring(0, 14) : name));
				this.staffNames.put("§5" + (name.length() > 14 ? name.substring(0, 14) : name), true);
				for (Map.Entry<String, Property> entry : ((CraftPlayer) pl).getProfile().getProperties().entries()) {
					if (entry.getKey().equals("textures")) {
						this.textureProperties.put("§5" + (name.length() > 14 ? name.substring(0, 14) : name), entry.getValue());
					}
				}
			}

			for (Player pl : this.modsOnline) {
				String name = pl.getName();
				name = ChatColor.stripColor(name);
				this.staff.add("§3" + (name.length() > 14 ? name.substring(0, 14) : name));
				this.staffNames.put("§3" + (name.length() > 14 ? name.substring(0, 14) : name), true);
				for (Map.Entry<String, Property> entry : ((CraftPlayer) pl).getProfile().getProperties().entries()) {
					if (entry.getKey().equals("textures")) {
						this.textureProperties.put("§3" + (name.length() > 14 ? name.substring(0, 14) : name), entry.getValue());
					}
				}
			}

			for (Player pl : this.trialsOnline) {
				String name = pl.getName();
				name = ChatColor.stripColor(name);
				this.staff.add("§2" + (name.length() > 14 ? name.substring(0, 14) : name));
				this.staffNames.put("§2" + (name.length() > 14 ? name.substring(0, 14) : name), true);
				for (Map.Entry<String, Property> entry : ((CraftPlayer) pl).getProfile().getProperties().entries()) {
					if (entry.getKey().equals("textures")) {
						this.textureProperties.put("§2" + (name.length() > 14 ? name.substring(0, 14) : name), entry.getValue());
					}
				}
			}
		}

		return update;
	}

	public boolean checkIfStaffLeft(Player player) {
		return this.checkIfStaffLeft(player, false);
	}

	public boolean checkIfStaffLeft(Player player, boolean override) {
		String name = player.getName();
		boolean update = false;

		if (this.adminsOnline.remove(player)) {
			this.staff.remove("§4" + (name.length() > 14 ? name.substring(0, 14) : name));
			this.staffNames.remove("§4" + (name.length() > 14 ? name.substring(0, 14) : name));
			update = true;
		} else if (this.seniorModsOnline.remove(player)) {
			this.staff.remove("§5" + (name.length() > 14 ? name.substring(0, 14) : name));
			this.staffNames.remove("§5" + (name.length() > 14 ? name.substring(0, 14) : name));
			update = true;
		} else if (this.modsOnline.remove(player)) {
			this.staff.remove("§3" + (name.length() > 14 ? name.substring(0, 14) : name));
			this.staffNames.remove("§3" + (name.length() > 14 ? name.substring(0, 14) : name));
			update = true;
		} else if (this.trialsOnline.remove(player)) {
			this.staff.remove("§2" + (name.length() > 14 ? name.substring(0, 14) : name));
			this.staffNames.remove("§2" + (name.length() > 14 ? name.substring(0, 14) : name));
			update = true;
		} else if (this.famousOnline.remove(player)) {
			update = false;
		} else if (this.donatorPlusOnline.remove(player)) {
			update = false;
		} else if (this.donatorsOnline.remove(player)) {
			update = false;
		}

		if(override) {
			return false;
		}

		return update;
	}

	public IChatBaseComponent getHeader() {
		return header;
	}

	public IChatBaseComponent getFooter() {
		return footer;
	}

	public List<Player> getAdminsOnline() {
		return adminsOnline;
	}

	public List<Player> getSeniorModsOnline() {
		return seniorModsOnline;
	}

	public List<Player> getModsOnline() {
		return modsOnline;
	}

	public List<Player> getTrialsOnline() {
		return trialsOnline;
	}

	public List<Player> getFamousOnline() {
		return famousOnline;
	}

	public List<Player> getDonatorPlusOnline() {
		return donatorPlusOnline;
	}

	public List<Player> getDonatorsOnline() {
		return donatorsOnline;
	}

}
