package me.devkevin.practice.util;

import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PlayerUtil {

    private static Field SPAWN_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;

    public static void playDeathAnimation(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            Set<Player> sentTo = new HashSet<>();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player) {
                    Player watcher = (Player) entity;
                    if (!watcher.getUniqueId().equals(player.getUniqueId())) {
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
                        sentTo.add(watcher);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }
            }, 40L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideOrShowPlayer(Player player, Player target, boolean hide) {
        CraftPlayer craftPlayer = (CraftPlayer) target.getPlayer();

        if (hide) {
            player.hidePlayer(target);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
        } else {
            player.showPlayer(target);
        }
    }

    public final static ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .durability(7)
            .name("&a")
            .lore(" ")
            .hideFlags()
            .build();

    public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getContents()[i];
            if (item == null || item.getType() == type || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }

    public static boolean isInventoryEmpty(Inventory inv) {
        ItemStack[] contents;
        for (int length = (contents = inv.getContents()).length, i = 0; i < length; ++i) {
            final ItemStack item = contents[i];
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public static void lockPos(Player player, int seconds) {
        player.setFlying(false);
        player.setSprinting(false);
        player.setWalkSpeed(0.0F);
        player.setFoodLevel(0);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * seconds, 250));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * seconds, 250));

        Bukkit.getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> {
            player.setFlying(false);
            player.setSprinting(true);
            player.setWalkSpeed(0.2F);
            player.setFoodLevel(20);
        }, seconds * 20L);
    }

    public static void reset(Player player) {
        reset(player, true);
    }

    public static void reset(Player player, boolean resetHeldSlot) {
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

        switch (profile.getState()) {
            case SPAWN:
                InternalNametag.reloadPlayer(player);
                InternalNametag.reloadOthersFor(player);
                break;
            case EDITING:
                Bukkit.getOnlinePlayers().forEach(p -> {
                    player.hidePlayer(p);
                    p.hidePlayer(player);
                });
                break;
        }


        if (!player.hasMetadata("frozen")) {
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
        }

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.spigot().setCollidesWithEntities(true);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0); // removes players arrows on body

        if (resetHeldSlot) {
            player.getInventory().setHeldItemSlot(0);
        }

        player.updateInventory();
    }

    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

    public static String toNiceString(String string) {
        string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.toCharArray().length; i++) {
            char c = string.toCharArray()[i];
            if (i > 0) {
                char prev = string.toCharArray()[i - 1];
                if (prev == ' ' || prev == '[' || prev == '(') {
                    if (i == string.toCharArray().length - 1 || c != 'x' ||
                            !Character.isDigit(string.toCharArray()[i + 1])) {
                        c = Character.toUpperCase(c);
                    }
                }
            } else {
                if (c != 'x' ||  !Character.isDigit(string.toCharArray()[i + 1])) {
                    c = Character.toUpperCase(c);
                }
            }
            sb.append(c);
        }

        return sb.toString();
    }

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendMessage(String message, Set<Player> players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendFirework(FireworkEffect effect, Location location) {
        Firework f = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);

        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.v1_8_R3.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }

    public static String getBridgesScore(int point, boolean friend) {
        String icon = "⬤";
        String color = friend ? "&9" : "&c";
        String defaultString = "&7";

        String text = icon + icon + icon + icon + icon;
        switch (point) {
            case 1:
                text = color + icon + defaultString + icon + icon + icon + icon;
                break;
            case 2:
                text = color + icon + icon + defaultString + icon + icon + icon;
                break;
            case 3:
                text = color + icon + icon + icon + defaultString + icon + icon;
                break;
            case 4:
                text = color + icon + icon + icon + icon + defaultString + icon;
                break;
            case 5:
                text = color + text;
                break;
        }

        return defaultString + text;
    }

    public static String getBedWarsScore(boolean bed, boolean friend) {
        String alive = "&a&l✓";
        String dead = "&c&l✗";

        String text = alive;
        if (!bed) {
            text = "&c" + dead;
        }

        return "&a" + text;
    }

    public static String getBattleRushScore(int point, boolean friend) {
        String icon = "⬤";
        String color = friend ? "&9" : "&c";
        String defaultString = "&7";

        String text = icon + icon + icon;
        switch (point) {
            case 1:
                text = color + icon + defaultString + icon + icon;
                break;
            case 2:
                text = color + icon + icon + defaultString + icon;
                break;
            case 3:
                text = color + text;
                break;
        }

        return defaultString + text;
    }


    static {
        try {
            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);

            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);

            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static UUID getUUIDByName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }
}
