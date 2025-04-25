package cc.patrone.practice.inventory.menu.impl;

import zone.potion.CorePlugin;
import zone.potion.inventory.menu.impl.PerPlayerMenu;
import zone.potion.player.CoreProfile;
import zone.potion.utils.item.ItemBuilder;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerTimeType;
import cc.patrone.practice.player.PracticeProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class SettingsMenu extends PerPlayerMenu {
	private final PracticePlugin plugin;

	public SettingsMenu(PracticePlugin plugin) {
		super(1, "Settings");
		this.plugin = plugin;
	}

	@Override
	public void updatePlayer(Player clicker) {
		clear();

		PracticeProfile profile = plugin.getPlayerManager().getProfile(clicker.getUniqueId());

		setActionableItem(0, new ItemBuilder(Material.DIAMOND_SWORD).flags(ItemFlag.HIDE_ATTRIBUTES).name(CC.PRIMARY + "Duel Requests")
				.lore(profile.isAcceptingDuels() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			player.performCommand("toggleduelrequests");
			update();
		});

		setActionableItem(1, new ItemBuilder(Material.IRON_SWORD).flags(ItemFlag.HIDE_ATTRIBUTES).name(CC.PRIMARY + "Queue Settings")
				.lore(CC.SECONDARY + "Choose to only queue against a ",
						CC.SECONDARY + "certain ping/ELO range or client.")
				.build(), player -> {
			player.sendMessage(CC.YELLOW + "This feature is still being developed!");
			player.closeInventory();
			update();
		});

		setActionableItem(2, new ItemBuilder(Material.ITEM_FRAME).name(CC.PRIMARY + "Scoreboard")
				.lore(profile.isScoreboardEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			player.performCommand("togglescoreboard");
			update();
		});

		setActionableItem(3, new ItemBuilder(Material.NETHER_STAR).name(CC.PRIMARY + "Party Invites")
				.lore(profile.isAcceptingPartyInvites() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			profile.setAcceptingPartyInvites(!profile.isAcceptingPartyInvites());
			player.sendMessage(profile.isAcceptingPartyInvites() ? CC.RED + "Party invites disabled." : CC.GREEN + "Party invites enabled.");
			update();
		});

		setActionableItem(4, new ItemBuilder(Material.DIAMOND_HELMET).name(CC.PRIMARY + "Player Visibility")
				.lore(!profile.isHidingPlayers() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			player.performCommand("toggleplayervisibility");
			update();
		});

		setActionableItem(5, new ItemBuilder(Material.SKULL_ITEM).name(CC.PRIMARY + "Allow Spectators")
				.lore(profile.isAllowingSpectators() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			profile.setAllowingSpectators(!profile.isAllowingSpectators());
			player.sendMessage(profile.isAllowingSpectators() ? CC.RED + "Spectators disabled." : CC.GREEN + "Spectators enabled.");
			update();
		});

		PlayerTimeType timeType = profile.getCurrentTimeType();

		setActionableItem(6, new ItemBuilder(Material.WATCH).name(CC.PRIMARY + "Set Your Time")
				.lore(
						(timeType == PlayerTimeType.DAY ? CC.GREEN : CC.GRAY) + "Day",
						(timeType == PlayerTimeType.SUNSET ? CC.GREEN : CC.GRAY) + "Sunset",
						(timeType == PlayerTimeType.NIGHT ? CC.GREEN : CC.GRAY) + "Night")
				.build(), player -> {
			PlayerTimeType nextTimeType = timeType.nextTimeType();
			profile.setCurrentTimeType(nextTimeType);
			nextTimeType.apply(player);
			update();
		});

		CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(clicker.getUniqueId());

		setActionableItem(7, new ItemBuilder(Material.PAPER).name(CC.PRIMARY + "Private Messages")
				.lore(coreProfile.isMessaging() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			player.performCommand("tpm");
			update();
		});

		setActionableItem(8, new ItemBuilder(Material.EMPTY_MAP).name(CC.PRIMARY + "Global Chat")
				.lore(coreProfile.isGlobalChatEnabled() ? CC.GREEN + "Enabled" : CC.RED + "Disabled")
				.build(), player -> {
			player.performCommand("tgc");
			update();
		});
	}

	@Override
	public void setup() {
	}
}
