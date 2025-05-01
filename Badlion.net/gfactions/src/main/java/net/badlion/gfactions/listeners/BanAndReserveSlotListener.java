package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.commands.SlotCommand;
import net.badlion.gfactions.managers.DeathBanManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.List;

public class BanAndReserveSlotListener implements Listener {

    private GFactions plugin;

    public BanAndReserveSlotListener(GFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinAsync(final AsyncPlayerPreLoginEvent event) {
	    // Are they deathbanned?
	    if (DeathBanManager.isDeathBanned(event.getUniqueId())) {
		    // EOTW
		    if (true) {
			    event.setKickMessage("You are currently death banned. You cannot rejoin during the EOTW!");
			    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
		        return;
		    }

		    // Do they have lives available?
		    if (DeathBanManager.getNumOfLives(event.getUniqueId()) > 0) {
			    Long lastLoginTime = DeathBanManager.getLastJoinTime(event.getUniqueId());

			    // If they are joining and have not joined in the past 5 minutes
			    if (lastLoginTime == null || lastLoginTime + 5 * 60 * 1000 < System.currentTimeMillis()) {
				    Timestamp timestamp = DeathBanManager.getDeathBanTime(event.getUniqueId());
				    double remaining = timestamp.getTime() - System.currentTimeMillis();
				    String rem = DeathBanManager.calculateTimeRemaining(remaining);

				    event.setKickMessage("You are currently death banned. Use a life by rejoining within the next 5 minutes or wait " + rem);
				    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
				    DeathBanManager.putLastJoinTime(event.getUniqueId());
			    } else {
				    // Let them through
				    DeathBanManager.unDeathBanPlayer(event.getUniqueId());

				    DeathBanManager.removeLastJoinTime(event.getUniqueId());
			    }
		    } else {
			    Timestamp timestamp = DeathBanManager.getDeathBanTime(event.getUniqueId());
			    double remaining = timestamp.getTime() - System.currentTimeMillis();
			    String rem = DeathBanManager.calculateTimeRemaining(remaining);

			    event.setKickMessage("You are currently death banned. Buy a life at http://store.badlion.net/ or wait " + rem);
			    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
		    }
	    }
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(2);
		if (item != null && item.getType() == Material.STRING && item.containsEnchantment(Enchantment.DURABILITY)) {
			player.setHealth(20D);
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.setExp(0);
			player.setLevel(0);

			player.getInventory().setItem(2, null);

			player.teleport(GFactions.plugin.getSpawnLocation());
		}
	}

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (DeathBanManager.isDeathBanned(event.getPlayer().getUniqueId())) {
            Timestamp timestamp = DeathBanManager.getDeathBanTime(event.getPlayer().getUniqueId());
            double remaining = timestamp.getTime() - System.currentTimeMillis();
            String rem = DeathBanManager.calculateTimeRemaining(remaining);

            event.getPlayer().kickPlayer("You have been death banned for " + rem + ". You can buy a life at http://store.badlion.net");
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {
        if (event.getEntity().hasPermission("GFactions.admin")) {
            return;
        }

        DeathBanManager.deathbanPlayer(event.getEntity());

        BukkitUtil.runTaskLater(new Runnable() {
            @Override
            public void run() {
                Timestamp timestamp = DeathBanManager.getDeathBanTime(event.getEntity().getUniqueId());
                if (timestamp != null && event.getEntity().isOnline()) {
                    double remaining = timestamp.getTime() - System.currentTimeMillis();
                    String rem = DeathBanManager.calculateTimeRemaining(remaining);

                    event.getEntity().kickPlayer("You have been death banned for " + rem + ". You can buy a life at http://store.badlion.net");
                } else {
                    event.getEntity().kickPlayer("You have been death banned. You can buy a life at http://store.badlion.net");
                }
            }
        }, 5 * 20);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerLoginEvent event) {
		// Only allow them in if they are being kicked because server is full
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && (event.getPlayer().hasPermission("badlion.donator") || event.getPlayer().hasPermission("GFactions.stone"))) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
            return;
        }

        // Ugly...might need to clean up if we get a lot of people
        for (List<String> list : SlotCommand.slotMap.values()) {
            for (String name : list) {
                if (event.getPlayer().getName().toLowerCase().equals(name)) {
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                    return;
                }
            }
        }
    }
}
