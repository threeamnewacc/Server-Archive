package us.zonix.hcfactions.deathsign;

import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DeathSignListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Skull) {
                Skull skull = (Skull) block.getState();
                if (skull.getOwner() != null) {
                    if (!skull.getOwner().startsWith("MHF_")) {
                        event.getPlayer().sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("DEATH_SIGN.SKULL_CLICK").replace("%PLAYER%", skull.getOwner() + (skull.getOwner().endsWith("s") ? "'" : "'s")));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Player killer = player.getKiller();
        if (killer != null) {
            player.getWorld().dropItemNaturally(player.getLocation(), new DeathSign(killer.getName(), player.getName()).toItemStack());
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(player.getName()).build());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (state instanceof Sign) {
            DeathSign deathSign = DeathSign.getByItemStack(player.getItemInHand());
            if (deathSign != null) {
                player.closeInventory();
                Material material = block.getType();
                block.setType(Material.AIR);

                Block newBlock = block.getWorld().getBlockAt(block.getLocation());
                newBlock.setType(material);

                deathSign.toSign(newBlock);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (state instanceof Sign && player.getGameMode() == GameMode.SURVIVAL) {
            DeathSign deathSign = DeathSign.getBySign((Sign) state);
            if (deathSign != null) {
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(block.getLocation(), deathSign.toItemStack());
                event.setCancelled(true);
            }
        }
    }

}
