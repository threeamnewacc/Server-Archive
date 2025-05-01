package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.arrowtrails.*;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ArrowTrailManager implements Listener {

    public static Map<String, ArrowTrail> arrowtrails = new LinkedHashMap<>();

    static {
        ArrowTrailManager.arrowtrails.put("heart_arrow_trail", new HeartArrow());
        ArrowTrailManager.arrowtrails.put("flame_arrow_trail", new FlameArrow());
        ArrowTrailManager.arrowtrails.put("green_arrow_trail", new GreenArrow());
        ArrowTrailManager.arrowtrails.put("smoke_arrow_trail", new SmokeArrow());
        ArrowTrailManager.arrowtrails.put("cloud_arrow_trail", new CloudArrow());
        ArrowTrailManager.arrowtrails.put("angry_villager_arrow_trail", new AngryVillagerArrow());
        ArrowTrailManager.arrowtrails.put("crit_arrow_trail", new CritArrow());
        ArrowTrailManager.arrowtrails.put("lava_drip_arrow_trail", new LavaDripArrow());
        ArrowTrailManager.arrowtrails.put("enchanted_arrow_trail", new EnchantArrow());
        ArrowTrailManager.arrowtrails.put("explosion_arrow_trail", new ExplosionArrow());
        //ArrowTrailManager.arrowtrails.put("footstep_arrow_trail", new FootstepArrow()); Shit trail
        ArrowTrailManager.arrowtrails.put("happy_villager_arrow_trail", new HappyVillagerArrow());
        ArrowTrailManager.arrowtrails.put("lava_arrow_trail", new LavaArrow());
        ArrowTrailManager.arrowtrails.put("music_arrow_trail", new MusicArrow());
        ArrowTrailManager.arrowtrails.put("portal_arrow_trail", new PortalArrow());
        ArrowTrailManager.arrowtrails.put("disco_arrow_trail", new DiscoArrow());
        ArrowTrailManager.arrowtrails.put("slime_arrow_trail", new SlimeArrow());
        ArrowTrailManager.arrowtrails.put("snow_arrow_trail", new SnowArrow());
        ArrowTrailManager.arrowtrails.put("firework_arrow_trail", new SparkArrow());
        ArrowTrailManager.arrowtrails.put("spell_arrow_trail", new SpellArrow());
        ArrowTrailManager.arrowtrails.put("aura_arrow_trail", new TownAuraArrow());
        ArrowTrailManager.arrowtrails.put("water_bubble_arrow_trail", new WaterBubbleArrow());
        ArrowTrailManager.arrowtrails.put("redstone_arrow_trail", new RedstoneArrow());
    }

    public static ArrowTrail getArrowTrail(String arrowtrailName) {
        return ArrowTrailManager.arrowtrails.get(arrowtrailName.toLowerCase());
    }

    public static void addArrowTrail(final CommandSender sender, final Player player, final String arrowtrailName) {
        ArrowTrailManager.addArrowTrail(sender, player, arrowtrailName, true);
    }

    public static void addArrowTrail(final CommandSender sender, final Player player, final String arrowtrailName, final boolean verbose) {
        final ArrowTrail arrowtrail = ArrowTrailManager.getArrowTrail(arrowtrailName);

        // Is arrowtrail valid?
        if (arrowtrail == null) {
            sender.sendMessage(ChatColor.RED + "ArrowTrail " + arrowtrailName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have the arrowtrail?
        if (cosmeticsSettings.hasArrowTrail(arrowtrail)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the arrow trail " + arrowtrail.getName());
            return;
        }

        cosmeticsSettings.addArrowTrail(arrowtrail);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave arrow trail " + arrowtrail.getName() + " to " + player.getName());
        }
    }

    public static void equipArrowTrail(Player player, String arrowtrailName) {
        ArrowTrail arrowtrail = ArrowTrailManager.getArrowTrail(arrowtrailName);
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Is arrowtrail valid?
        if (arrowtrail == null) {
            player.sendMessage(ChatColor.RED + "Arrow Trail " + arrowtrailName + " not found.");
            return;
        }

        // Do they already have the arrowtrail equipped?
        if (cosmeticsSettings.getActiveArrowTrail() == arrowtrail) {
            player.sendMessage(ChatColor.RED + "You already have + " + arrowtrail.getName() + " equipped!");
            return;
        }

        cosmeticsSettings.setActiveArrowTrail(arrowtrail, true);
    }

    public static Map<String, ArrowTrail> getArrowTrails() {
        return arrowtrails;
    }

    @EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
    public void onShootArrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isArrowTrailsEnabled()) {
                return;
            }
            Player player = (Player) event.getEntity().getShooter();
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
            ArrowTrail activeArrowTrail = cosmeticsSettings.getActiveArrowTrail();
            if (activeArrowTrail == null) return;
            activeArrowTrail.addTrail(player, event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.FIRST)
    public void onArrowLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isArrowTrailsEnabled()) {
                return;
            }
            Player player = (Player) event.getEntity().getShooter();
            // Make sure the player is online
            if (Gberry.isPlayerOnline(player)) {
                CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
                ArrowTrail activeArrowTrail = cosmeticsSettings.getActiveArrowTrail();
                if (activeArrowTrail == null) return;
                activeArrowTrail.removeEntity(player, event.getEntity());
            }
            event.getEntity().remove();
        }
    }

}
