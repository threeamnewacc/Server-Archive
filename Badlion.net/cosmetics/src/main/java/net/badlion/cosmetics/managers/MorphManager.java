package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.morphs.*;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class MorphManager implements Listener {

    private static Map<String, Morph> morphs = new LinkedHashMap<>();

    static {
        MorphManager.morphs.put("creeper_morph", new CreeperMorph());
        MorphManager.morphs.put("blaze_morph", new BlazeMorph());
        MorphManager.morphs.put("pig_morph", new PigMorph());
        MorphManager.morphs.put("skeleton_morph", new SkeletonMorph());
        MorphManager.morphs.put("wither_skeleton_morph", new WitherSkeletonMorph());
        MorphManager.morphs.put("zombie_morph", new ZombieMorph());
        MorphManager.morphs.put("sheep_morph", new SheepMorph());
        MorphManager.morphs.put("zombie_pig_morph", new ZombiePigMorph());
        MorphManager.morphs.put("snowman_morph", new SnowmanMorph());
        MorphManager.morphs.put("mooshroom_morph", new MooshroomMorph());
        MorphManager.morphs.put("cow_morph", new CowMorph());
        MorphManager.morphs.put("spider_morph", new SpiderMorph());
        MorphManager.morphs.put("bat_morph", new BatMorph());
        MorphManager.morphs.put("squid_morph", new SquidMorph());
        MorphManager.morphs.put("iron_golem_morph", new IronGolemMorph());
        MorphManager.morphs.put("dog_morph", new WolfMorph());
        MorphManager.morphs.put("cat_morph", new CatMorph());
        MorphManager.morphs.put("villager_morph", new VillagerMorph());
        MorphManager.morphs.put("slime_morph", new SlimeMorph());
        MorphManager.morphs.put("enderman_morph", new EndermanMorph());
        MorphManager.morphs.put("witch_morph", new WitchMorph());
        MorphManager.morphs.put("magma_cube_morph", new MagmaCubeMorph());
    }

    public static Morph getMorph(String morphName) {
        return MorphManager.morphs.get(morphName.toLowerCase());
    }

    public static void addMorph(final CommandSender sender, final Player player, final String morphName) {
        MorphManager.addMorph(sender, player, morphName, true);
    }

    public static void addMorph(final CommandSender sender, final Player player, final String morphName, final boolean verbose) {
        final Morph morph = MorphManager.getMorph(morphName);

        // Is morph valid?
        if (morph == null) {
            sender.sendMessage(ChatColor.RED + "Morph " + morphName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have the morph?
        if (cosmeticsSettings.hasMorph(morph)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the morph " + morph.getName());
            return;
        }

        cosmeticsSettings.addMorph(morph);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave morph " + morph.getName() + " to " + player.getName());
        }
    }

    public static void equipMorph(Player player, String morphName) {
        Morph morph = MorphManager.getMorph(morphName);
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Is morph valid?
        if (morph == null) {
            player.sendMessage(ChatColor.RED + "Morph " + morphName + " not found.");
            return;
        }

        // Do they already have the morph equipped?
        if (cosmeticsSettings.getActiveMorph() == morph) {
            player.sendMessage(ChatColor.RED + "You already have + " + morph.getName() + " equipped!");
            return;
        }

        cosmeticsSettings.setActiveMorph(morph, true);
        morph.setMorph(player);
    }

    public static Map<String, Morph> getMorphs() {
        return morphs;
    }

    // Morph sneak abilities
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }
        final Player player = event.getPlayer();
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) return;
        Morph activeMorph = cosmeticsSettings.getActiveMorph();
        // Do they have an active morph?
        if (activeMorph == null) return;

        activeMorph.handleSneak(event);
    }

    // Remove falling blocks when they land
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            if (!Cosmetics.getInstance().isMorphsEnabled()) {
                return;
            }
            if (event.getEntity().hasMetadata("fallfix")) {
                event.setCancelled(true);
            } else if (event.getEntity().hasMetadata("fallfix2")) {
                //event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.DIG_SNOW, 1.0f, 1.0f);
                event.setCancelled(true);
            }
        }
    }

    // Left/right click morph abilities
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) return;

        Morph activeMorph = cosmeticsSettings.getActiveMorph();

        // Do they have an active morph?
        if (activeMorph == null) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.MONSTER_EGG) {
            return;
        }

        activeMorph.handlePlayerInteractEvent(event);
    }

    // Stop morph-related explosions
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }
        if (event.getEntity().hasMetadata("morphexplosion")) {
            event.setCancelled(true);
        }
    }

    // Disable item pickups for certain items
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }
        if (event.getItem().hasMetadata("takeable")) {
            event.setCancelled(true);
        }
    }

    public void activateListeners() {
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(ZombieMorph.instance, Cosmetics.getInstance());
    }

    public void deactivateListeners() {
        BukkitUtil.unregisterListener(ZombieMorph.instance);
    }

}
