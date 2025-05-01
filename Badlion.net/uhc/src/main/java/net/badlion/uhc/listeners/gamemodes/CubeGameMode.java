package net.badlion.uhc.listeners.gamemodes;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.BorderShrinkEvent;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.GameTimeElapsedEvent;
import net.badlion.uhc.events.PermaDayEvent;
import net.badlion.uhc.listeners.WorldGenerationListener;
import net.badlion.uhc.tasks.BorderShrinkTask;
import net.badlion.uhc.tasks.GameTimeTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CubeGameMode implements GameMode {

    public CubeGameMode() {
        BorderShrinkTask.useBedRockBorder = false;
    }

    public ItemStack getExplanationItem() {
        return new ItemStack(Material.WOOL);
    }

    public String getAuthor() {
        return "Cube";
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
                    player.setFoodLevel(20);
                    player.setSaturation(20);
                    player.setExhaustion(0);
                }
            }

        }.runTaskLater(BadlionUHC.getInstance(), 60 * 20 * 10);
    }

    @EventHandler(priority= EventPriority.FIRST)
    public void onBorderShrink(BorderShrinkEvent event) {
        event.setOverride(true);

        WorldGenerationListener.addRedGlassBorder(event.getNewRadius());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	    if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

        World world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
        world.setTime(world.getTime() + 12000 % 24000);
    }

    @EventHandler
    public void onObsidianPlaced(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp() && event.getBlock().getType() == Material.OBSIDIAN) {
            if (event.getPlayer().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Cannot place obsidian in the nether.");
            }
        }
    }

    @EventHandler
    public void onRedGlassBroken(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() && event.getBlock().getType() == Material.STAINED_GLASS && event.getBlock().getData() == (byte) 14) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSaturationLoss(FoodLevelChangeEvent event) {
        if (GameTimeTask.getNumOfSeconds() < 600) {
            event.setCancelled(true);

            if (event.getEntity() instanceof Player) {
                ((Player) event.getEntity()).setFoodLevel(20);
                ((Player) event.getEntity()).setSaturation(20);
                ((Player) event.getEntity()).setExhaustion(0);
            }
        }
    }

    @EventHandler
    public void onPermaDayEnabled(PermaDayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameTimeElapsed(GameTimeElapsedEvent event) {
        if (event.getMinutes() % 20 == 0) {
            for (Player player : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
            }
        }
    }

    @Override
    public void unregister() {
        GameStartEvent.getHandlerList().unregister(this);
        BorderShrinkEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        FoodLevelChangeEvent.getHandlerList().unregister(this);
        PermaDayEvent.getHandlerList().unregister(this);
        GameTimeElapsedEvent.getHandlerList().unregister(this);

        BorderShrinkTask.useBedRockBorder = true;
    }

}
