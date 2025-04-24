package club.minemen.hcfactions.listener;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Role;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

/**
 * @since 12/3/2017
 */
@RequiredArgsConstructor
public class ChatListener implements Listener {

	private final HCFactions plugin;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		final FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		final Faction faction = factionPlayer.getFaction();

		if (factionPlayer.getChatMode() != ChatMode.PUBLIC) {
			if (faction == null) {
				factionPlayer.setChatMode(ChatMode.PUBLIC);
				player.sendFormattedMessage("{0}You are not in a faction!", ChatColor.RED);
				return;
			}

			event.setCancelled(true);

			if (factionPlayer.getChatMode() == ChatMode.FACTION) {
				String fullFactionMessage = String.format("%s(%sFC%s) %s: %s", ChatColor.YELLOW, ChatColor.DARK_GREEN,
						ChatColor.YELLOW, ChatColor.DARK_GREEN + Role.factionChatRank(player) + player.getName(), ChatColor.GREEN + event.getMessage());

				faction.getOnlinePlayers().forEach(selfFactionPlayer -> selfFactionPlayer.sendMessage(fullFactionMessage));

				Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("[F-Spy] [" + faction.getTag() + "] " + player.getName() + ": " + event.getMessage()));

				// Send to any players who are spying chat
				for (FactionPlayer fplayer : FPlayers.getInstance().getOnline()) {
					if (fplayer.isSpyingChat()) {
						fplayer.sendMessage("[F-Spy] [" + faction.getTag() + "] " + player.getName() + ": " + event.getMessage());
					}
				}
			} else if (factionPlayer.getChatMode() == ChatMode.ALLIANCE) {
				String fullAllyMessage = String.format("%s(%sAC%s) %s%s:%s %s", ChatColor.BLUE, ChatColor.DARK_GREEN,
						ChatColor.BLUE, ChatColor.DARK_GREEN, player.getName(), ChatColor.GREEN, event.getMessage());

				faction.getAlliedFactions().forEach(alliedPlayer -> alliedPlayer.getOnlinePlayers()
						.forEach(allyPlayer -> allyPlayer.sendMessage(fullAllyMessage)));
				faction.getOnlinePlayers().forEach(selfFactionPlayer -> selfFactionPlayer.sendMessage(fullAllyMessage));

				for (FactionPlayer fplayer : FPlayers.getInstance().getOnline()) {
					if (fplayer.isSpyingChat()) {
						fplayer.sendMessage("[A-Spy] [" + faction.getTag() + "] " + player.getName() + ": " + event.getMessage());
					}
				}

				Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("[A-Spy] [" + faction.getTag() + "] " + player.getName() + ": " + event.getMessage()));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPublicChat(AsyncPlayerChatEvent event) {
		final FactionPlayer factionPlayer = FPlayers.getInstance().get(event.getPlayer());
		final Faction faction = factionPlayer.getFaction();

		if (Conf.chatTagRelationColored) {
			event.setCancelled(true);

			String message = String.format(CC.GOLD + "[" + CC.RESET + faction.getTag() + CC.GOLD + "] " + CC.RESET + event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			this.plugin.getServer().getConsoleSender().sendMessage(message);
			this.plugin.getLogger().info("Chat: " + ChatColor.stripColor(message));

			for (Player listeningPlayer : event.getRecipients()) {
				FactionPlayer otherPlayer = FPlayers.getInstance().get(listeningPlayer);
				String factionTag = CC.GOLD + "[" + CC.RESET + factionPlayer.getChatTag(otherPlayer) + CC.GOLD + "] " + CC.RESET;
				listeningPlayer.sendMessage(String.format(factionTag + event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
			}
		} else {
			event.setFormat(CC.GOLD + "[" + CC.YELLOW + factionPlayer.getChatTag() + CC.GOLD + "] " + CC.RESET + event.getFormat());
		}


	}
}
