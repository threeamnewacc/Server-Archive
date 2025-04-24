package me.devkevin.practice.hcf.classes;

import club.inverted.chatcolor.CC;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.devkevin.practice.Practice;
import me.devkevin.practice.hcf.HCFClass;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:32
 * Archer / me.devkevin.practice.hcf.classes / Practice
 */
public class Archer extends HCFClass {

    private final Practice plugin = Practice.getInstance();

    public static PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
    public static PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 7);
    public static PotionEffect ARCHER_RESISTANCE_EFFECT = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 2);

    public static TObjectLongMap<UUID> archerSpeedCooldowns = new TObjectLongHashMap<>();
    public static TObjectLongMap<UUID> archerJumpCooldowns = new TObjectLongHashMap<>();
    public static TObjectLongMap<UUID> archerResistanceCooldowns = new TObjectLongHashMap<>();

    public static ArrayList<UUID> tag = new ArrayList<>();
    public static HashMap<UUID, UUID> TAGGED = new HashMap<>();
    public static Random random = new Random();

    public static long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30);
    public static long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30);
    public static long ARCHER_RESISTANCE_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30);

    public Archer() {
        super("Archer");
        getEffects().add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        getEffects().add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    @Override
    public boolean canApply(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack helmet = playerInventory.getHelmet();
        if ((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)) {
            return false;
        }

        ItemStack chestplate = playerInventory.getChestplate();
        if ((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)) {
            return false;
        }

        ItemStack leggings = playerInventory.getLeggings();
        if ((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)) {
            return false;
        }

        ItemStack boots = playerInventory.getBoots();
        return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (((entity instanceof Player)) && ((damager instanceof Arrow))) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource source = arrow.getShooter();
            if ((source instanceof Player)) {
                Player damaged = (Player) event.getEntity();
                Player shooter = (Player) source;
                HCFClass equipped = this.plugin.getHcfManager().getHCFClass(shooter);
                if ((equipped == null) || (!equipped.equals(this))) {
                    return;
                }
                if ((this.plugin.getHcfManager().getHCFClass(damaged) != null) && (this.plugin.getHcfManager().getHCFClass(damaged).equals(this))) {
                    return;
                }

                new TaggedTask(damaged);

                int heartdamage = 1;
                if (TAGGED.containsKey(damaged.getUniqueId())) {
                    heartdamage = 2;
                }

                event.setDamage(0);

                damaged.setHealth(Math.max(0, damaged.getHealth() - (heartdamage * 2)));

                TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
                double distance = shooter.getLocation().distance(damaged.getLocation());

                shooter.sendMessage(CC.translate("&bRange: " + String.format("%.1f", distance)));
                shooter.sendMessage(CC.translate("&7Marked &b" + damaged.getName() + " &7for &b10 seconds &b" + heartdamage + "(&4heart)"));

                damaged.sendMessage(CC.translate("&bMarked! &7" + shooter.getName() + " has &bshot &7you and &bmarked &7you (+25% damage) for &b10 seconds&7. &b(" + String.format("%.1f", distance) + " blocks away)"));

                LeatherArmorMeta helmMeta = (LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta();
                LeatherArmorMeta chestMeta = (LeatherArmorMeta) shooter.getInventory().getChestplate().getItemMeta();
                LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) shooter.getInventory().getLeggings().getItemMeta();
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta();

                Color green = Color.fromRGB(6717235);

                double r = random.nextDouble();

                if ((r <= 0.5D) && (helmMeta.getColor().equals(green)) && (chestMeta.getColor().equals(green)) && (leggingsMeta.getColor().equals(green)) && (bootsMeta.getColor().equals(green))) {
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
                    shooter.sendMessage(ChatColor.GRAY + "Since your armor is green, you gave " + damaged.getName() + " the poison effect for 6 seconds...");
                    damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName() + "'s armor is green, you were given the poison effect for 6 seconds...");
                }
                Color blue = Color.fromRGB(3361970);
                if ((r <= 0.5D) && (helmMeta.getColor().equals(blue)) && (chestMeta.getColor().equals(blue)) && (leggingsMeta.getColor().equals(blue)) && (bootsMeta.getColor().equals(blue))) {
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
                    shooter.sendMessage(ChatColor.GRAY + "Since your armor is blue, you gave " + damaged.getName() + " the slowness effect for 6 seconds...");
                    damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName() + "'s armor is blue, you were given the slowness effect for 6 seconds...");
                }
                Color gray = Color.fromRGB(5000268);
                if ((r <= 0.5D) && (helmMeta.getColor().equals(gray)) && (chestMeta.getColor().equals(gray)) && (leggingsMeta.getColor().equals(gray)) && (bootsMeta.getColor().equals(gray))) {
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                    shooter.sendMessage(ChatColor.GRAY + "Since your armor is gray, you gave " + damaged.getName() + " the blindness effect for 6 seconds...");
                    damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName() + "'s armor is gray, you were given the blindness effect for 6 seconds...");
                }
                Color black = Color.fromRGB(1644825);
                if ((r <= 0.2D) && (helmMeta.getColor().equals(black)) && (chestMeta.getColor().equals(black)) && (leggingsMeta.getColor().equals(black)) && (bootsMeta.getColor().equals(black))) {
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
                    shooter.sendMessage(ChatColor.GRAY + "Since your armor is black, you gave " + damaged.getName() + " the wither effect for 6 seconds...");
                    damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName() + "'s armor is black, you were given the wither effect for 6 seconds...");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Action action = event.getAction();
        if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.SUGAR)) {
            if (this.plugin.getHcfManager().getHCFClass(event.getPlayer()) != this) {
                return;
            }

            long timestamp = archerSpeedCooldowns.get(uuid);
            long millis = System.currentTimeMillis();
            long remaining = timestamp == archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
            if (remaining > 0L) {
                player.sendMessage(CC.RED + "You cannot use this for another " + CC.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
                archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);

                Profile practicePlayerData = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    if (practicePlayerData.getState() == ProfileState.FIGHTING) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    }
                }, 161L);
            }
        } else if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.FEATHER)) {
            if (this.plugin.getHcfManager().getHCFClass(event.getPlayer()) != this) {
                return;
            }

            long timestamp1 = archerJumpCooldowns.get(uuid);
            long millis1 = System.currentTimeMillis();
            long remaining1 = timestamp1 == archerJumpCooldowns.getNoEntryValue() ? -1L : timestamp1 - millis1;
            if (remaining1 > 0L) {
                player.sendMessage(CC.RED + "You cannot use this for another " + CC.BOLD + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_JUMP_EFFECT);
                archerJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_JUMP_COOLDOWN_DELAY);
            }
        } else if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.IRON_INGOT)) {
            if (this.plugin.getHcfManager().getHCFClass(event.getPlayer()) != this) {
                return;
            }

            long timestamp = archerResistanceCooldowns.get(uuid);
            long millis = System.currentTimeMillis();
            long remaining = timestamp == archerResistanceCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
            if (remaining > 0L) {
                player.sendMessage(CC.RED + "You cannot use this for another " + CC.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_RESISTANCE_EFFECT);
                archerResistanceCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_RESISTANCE_COOLDOWN_DELAY);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!tag.contains(player.getUniqueId())) {
            return;
        }

        double damage = event.getDamage() * 0.25;
        event.setDamage(event.getDamage() + damage);
    }

    @Override
    public boolean onEquip(Player player) {
        for (PotionEffect effect : getEffects()) {
            player.addPotionEffect(effect, true);
        }

        return true;
    }

    @Override
    public void onUnEquip(Player player) {
        for (PotionEffect effect : this.getEffects()) {
            for (PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > HCFClass.DEFAULT_MAX_DURATION && active.getType().equals(effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
    }

    @Override
    public void onQuit(Player player) {
        TAGGED.remove(player.getUniqueId());
    }

    public static class TaggedTask extends BukkitRunnable {

        private final Player victim;

        public TaggedTask(Player victim) {
            this.victim = victim;
            tag.add(victim.getUniqueId());
            this.runTaskLater(Practice.getInstance(), 200L);
        }

        public void run() {
            tag.remove(this.victim.getUniqueId());
        }
    }
}
