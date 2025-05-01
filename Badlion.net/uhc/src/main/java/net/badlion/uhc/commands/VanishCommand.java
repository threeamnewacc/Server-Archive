package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;

public class VanishCommand implements CommandExecutor {

	public static HashSet<Player> players = new HashSet<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

            // Don't let regular users use this cmd
            if (!player.isOp()) {
                return true;
            }

            if (args.length > 0) {
                Player p = BadlionUHC.getInstance().getServer().getPlayer(args[0]);
                if (p == null) {
                    player.sendMessage(ChatColor.RED + "Player is not online or doesn't exist.");
                } else {
                    player = p;
                }
            }

            // Vanish whoever now
			if (VanishCommand.players.contains(player)) {
                VanishCommand.unvanishPlayer(player);
			} else {
                VanishCommand.vanishPlayer(player);
			}
		}

		return true;
	}

    public static void vanishPlayer(final Player player) {
        VanishCommand.players.add(player);
        player.spigot().setCollidesWithEntities(false);
        player.sendMessage(ChatColor.GREEN + "You have vanished!");

        // Hide players
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);

            // Hide mods/spectators/host from the player that just logged in
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(p.getUniqueId());
            if (uhcPlayer.getState().ordinal() >= UHCPlayer.State.SPEC.ordinal()) {
                player.hidePlayer(p);
            }
        }

        // Give them a compass yo, and a watch yo
        BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!player.getInventory().contains(Material.COMPASS)) {
                    player.getInventory().addItem(new ItemStack(Material.COMPASS), BadlionUHC.getInstance().getSpectatorItem());
                    player.updateInventory();
                }
            }
        }, 5);

        player.setGameMode(GameMode.CREATIVE);

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
            scoreboard.getObjective(DisplaySlot.SIDEBAR).unregister();
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }

    public static void unvanishPlayer(Player player) {
        VanishCommand.players.remove(player);
        player.spigot().setCollidesWithEntities(true);
        player.sendMessage(ChatColor.GREEN + "You are now visible!");

        // Show players
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }

}
