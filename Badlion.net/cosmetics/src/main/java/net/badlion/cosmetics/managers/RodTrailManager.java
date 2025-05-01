package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.rodtrails.*;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class RodTrailManager implements Listener {

    public static Map<String, RodTrail> rodtrails = new LinkedHashMap<>();

    static {
        RodTrailManager.rodtrails.put("heart_rod_trail", new HeartRod());
        RodTrailManager.rodtrails.put("flame_rod_trail", new FlameRod());
        RodTrailManager.rodtrails.put("green_rod_trail", new GreenRod());
        RodTrailManager.rodtrails.put("smoke_rod_trail", new SmokeRod());
        RodTrailManager.rodtrails.put("cloud_rod_trail", new CloudRod());
        RodTrailManager.rodtrails.put("angry_villager_rod_trail", new AngryVillagerRod());
        RodTrailManager.rodtrails.put("crit_rod_trail", new CritRod());
        RodTrailManager.rodtrails.put("lava_drip_rod_trail", new LavaDripRod());
        RodTrailManager.rodtrails.put("enchanted_rod_trail", new EnchantRod());
        RodTrailManager.rodtrails.put("explosion_rod_trail", new ExplosionRod());
        RodTrailManager.rodtrails.put("happy_villager_rod_trail", new HappyVillagerRod());
        RodTrailManager.rodtrails.put("lava_rod_trail", new LavaRod());
        RodTrailManager.rodtrails.put("music_rod_trail", new MusicRod());
        RodTrailManager.rodtrails.put("portal_rod_trail", new PortalRod());
        RodTrailManager.rodtrails.put("disco_rod_trail", new DiscoRod());
        RodTrailManager.rodtrails.put("slime_rod_trail", new SlimeRod());
        RodTrailManager.rodtrails.put("snow_rod_trail", new SnowRod());
        RodTrailManager.rodtrails.put("firework_rod_trail", new SparkRod());
        RodTrailManager.rodtrails.put("spell_rod_trail", new SpellRod());
        RodTrailManager.rodtrails.put("aura_rod_trail", new TownAuraRod());
        RodTrailManager.rodtrails.put("water_bubble_rod_trail", new WaterBubbleRod());
        RodTrailManager.rodtrails.put("redstone_rod_trail", new RedstoneRod());
    }

    public static RodTrail getRodTrail(String rodtrailName) {
        return RodTrailManager.rodtrails.get(rodtrailName.toLowerCase());
    }

    public static void addRodTrail(final CommandSender sender, final Player player, final String rodtrailName) {
        RodTrailManager.addRodTrail(sender, player, rodtrailName, true);
    }

    public static void addRodTrail(final CommandSender sender, final Player player, final String rodtrailName, final boolean verbose) {
        final RodTrail rodtrail = RodTrailManager.getRodTrail(rodtrailName);

        // Is rodtrail valid?
        if (rodtrail == null) {
            sender.sendMessage(ChatColor.RED + "Rod Trail " + rodtrailName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have the rodtrail?
        if (cosmeticsSettings.hasRodTrail(rodtrail)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the rod trail " + rodtrail.getName());
            return;
        }

        cosmeticsSettings.addRodTrail(rodtrail);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave rod trail " + rodtrail.getName() + " to " + player.getName());
        }
    }

    public static void equipRodTrail(Player player, String rodtrailName) {
        RodTrail rodtrail = RodTrailManager.getRodTrail(rodtrailName);
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Is rodtrail valid?
        if (rodtrail == null) {
            player.sendMessage(ChatColor.RED + "Rod Trail " + rodtrailName + " not found.");
            return;
        }

        // Do they already have the rodtrail equipped?
        if (cosmeticsSettings.getActiveRodTrail() == rodtrail) {
            player.sendMessage(ChatColor.RED + "You already have + " + rodtrail.getName() + " equipped!");
            return;
        }

        cosmeticsSettings.setActiveRodTrail(rodtrail, true);
    }

    public static Map<String, RodTrail> getRodTrails() {
        return rodtrails;
    }

    @EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
    public void onShootRod(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof FishHook && event.getEntity().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isRodTrailsEnabled()) {
                return;
            }
            Player player = (Player) event.getEntity().getShooter();
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
            RodTrail activeRodTrail = cosmeticsSettings.getActiveRodTrail();
            if (activeRodTrail == null) return;
            activeRodTrail.addTrail(player, event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.FIRST)
    public void onRodLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof FishHook && event.getEntity().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isRodTrailsEnabled()) {
                return;
            }
            Player player = (Player) event.getEntity().getShooter();
            // Make sure the player is online
            if (Gberry.isPlayerOnline(player)) {
                CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
                RodTrail activeRodTrail = cosmeticsSettings.getActiveRodTrail();
                if (activeRodTrail == null) return;
                activeRodTrail.removeEntity(player, event.getEntity());
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getHook().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isRodTrailsEnabled()) {
                return;
            }
            Player player = (Player) event.getHook().getShooter();
            // Make sure the player is online
            if (Gberry.isPlayerOnline(player)) {
                CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
                RodTrail activeRodTrail = cosmeticsSettings.getActiveRodTrail();
                if (activeRodTrail == null) return;
                activeRodTrail.removeEntity(player, event.getHook());
            }
        }
    }

}
