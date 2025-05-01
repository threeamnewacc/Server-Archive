package net.badlion.skywars.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.skywars.SkyWars;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        SkyWars.getInstance().getCurrentGame().getWorld().addToDestroy(event.getBlock());
    }

    @EventHandler
    public void onBlockPlace(PlayerBucketEmptyEvent event) {
        SkyWars.getInstance().getCurrentGame().getWorld().addToDestroy(event.getBlockClicked().getRelative(event.getBlockFace()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "                                      Skywars");
        player.sendMessage("");
        player.sendMessage("       Collect resources from nearby chests and the islands");
        player.sendMessage("            in order to gear up and slay the other players");
        player.sendMessage("  The middle has stronger items and anvils/enchanting tables");
        player.sendMessage("                            Last player or team alive wins!");

        if (SkyWars.getInstance().isFFA()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "                 Teaming is not allowed in FFA Mode!");
        }

        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
    }

}
