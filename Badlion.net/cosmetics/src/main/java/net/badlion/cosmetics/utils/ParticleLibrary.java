/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Maxim Roncace
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.badlion.cosmetics.utils;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.managers.UserDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

// TODO: Be-unlazy and make chunk methods to get players like i do in NMS
public class ParticleLibrary {

    private static Class<?> packetClass = null;
    private static Constructor<?> packetConstructor = null;
    private static Field[] fields = null;
    private static boolean netty = true;
    private static Field player_connection = null;
    private static Method player_sendPacket = null;
    private static HashMap<Class<? extends Entity>, Method> handles = new HashMap();
    private static boolean newParticlePacketConstructor = false;
    private static Class<Enum> enumParticle = null;
    private static boolean compatible = true;

    static {
        String vString = getVersion().replace("v", "");
        double v = 0.0D;
        if (!vString.isEmpty()) {
            String[] array = vString.split("_");
            v = Double.parseDouble(array[0] + "." + array[1]);
        }
        try {
            Bukkit.getLogger().info("[ParticleLib] Server major/minor version: " + v);
            if (v < 1.7D) {
                Bukkit.getLogger().info("[ParticleLib] Hooking into pre-Netty NMS classes");
                netty = false;
                packetClass = getNmsClass("Packet63WorldParticles");
                packetConstructor = packetClass.getConstructor();
                fields = packetClass.getDeclaredFields();
            } else {
                Bukkit.getLogger().info("[ParticleLib] Hooking into Netty NMS classes");
                packetClass = getNmsClass("PacketPlayOutWorldParticles");
                if (v < 1.8D) {
                    Bukkit.getLogger().info("[ParticleLib] Version is < 1.8 - using old packet constructor");
                    packetConstructor = packetClass.getConstructor(String.class, Float.TYPE, Float.TYPE, Float.TYPE,
                            Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleLib] Failed to initialize NMS components!");
            compatible = false;
        }
    }

    private ParticleType type;
    private double speed;
    private int count;
    private double radius;

    private double xd = Double.MAX_VALUE;
    private double yd = Double.MAX_VALUE;
    private double zd = Double.MAX_VALUE;
    private Map<UUID, Set<Object>> packets = new HashMap<>();
    private Map<UUID, Set<Location>> particleLocations = new HashMap<>();

    public ParticleLibrary(ParticleType type, double speed, int count, double radius) {
        this.type = type;
        this.speed = speed;
        this.count = count;
        this.radius = radius;
    }

    public ParticleLibrary(ParticleType type, double speed, int count, double xd, double yd, double zd) {
        this.type = type;
        this.speed = speed;
        this.count = count;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    private static void sendPacket(Player p, Object packet)
            throws IllegalArgumentException {
        try {
            if (player_connection == null) {
                //noinspection ConstantConditions
                player_connection = getHandle(p).getClass().getField("playerConnection");
                for (Method m : player_connection.get(getHandle(p)).getClass().getMethods()) {
                    if (m.getName().equalsIgnoreCase("sendPacket")) {
                        player_sendPacket = m;
                    }
                }
            }
            player_sendPacket.invoke(player_connection.get(getHandle(p)), packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleLib] Failed to send packet!");
        }
    }

    private static Object getHandle(Entity entity) {
        try {
            if (handles.get(entity.getClass()) != null) {
                return handles.get(entity.getClass()).invoke(entity);
            }
            Method entity_getHandle = entity.getClass().getMethod("getHandle");
            handles.put(entity.getClass(), entity_getHandle);
            return entity_getHandle.invoke(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Class<?> getNmsClass(String name) {
        String version = getVersion();
        String className = "net.minecraft.server." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleLib] Failed to load NMS class " + name + "!");
        }
        return clazz;
    }

    private static String getVersion() {
        String[] array = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
        if (array.length == 4) {
            return array[3] + ".";
        }
        return "";
    }

    public static boolean isCompatible() {
        return compatible;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getCount() {
        return this.count;
    }

    public double getRadius() {
        return this.radius;
    }

    public ParticleType getType() {
        return type;
    }

    public void queueParticles(Player owner, List<Location> locations) {
        Set<Object> packets = new HashSet<>();

        for (Location location : locations) {
            packets.add(createPacket(location));
        }

        this.packets.put(owner.getUniqueId(), packets);
    }

    public void notifyClients(Player owner) {
        // If they are in an invisible region, don't show their shit to anyone else
        if (owner.isInInvisibleRegion()) {
            return;
        }

        for (Player player : owner.getWorld().getPlayers()) {
            // Check if they can see them, helps for UHC and other game modes
            if (owner == player || (player.canSee(owner) && !player.isInInvisibleRegion())) {
                // Send packet if this is the same player or if players are visible
                if (player.getLocation().distance(owner.getLocation()) <= 16) {
                    if (owner == player || (UserDataManager.getUserData(player.getUniqueId()) != null && UserDataManager.getUserData(player.getUniqueId()).arePlayersVisible())) {
                        // Locations and distance checks
                        for (Object packet : this.packets.get(owner.getUniqueId())) {
                            sendPacket(player, packet);
                        }
                    }
                }
            }
        }

        // Clean memory
        this.packets.remove(owner.getUniqueId());
        this.particleLocations.remove(owner.getUniqueId());
    }

    // Method for sending directly to a location, bypassing the owner shit
    public void sendToLocation(List<Location> locations) {
        if (locations.isEmpty()) {
            return;
        }

        World world = Cosmetics.getInstance().getServer().getWorld("world");
        Set<Object> packets = new HashSet<>();
        for (Location location : locations) {
            packets.add(createPacket(location));
        }

        for (Player player : world.getPlayers()) {
            if (!player.isInInvisibleRegion()) {
                if (player.getLocation().distance(locations.get(0)) <= 16) {
                    for (Object packet : packets) {
                        sendPacket(player, packet);
                    }
                }
            }
        }

    }

    public void sendToLocation(Player owner, List<Location> locations) {
        if (locations.isEmpty()) {
            return;
        }

        queueParticles(owner, locations); // Queue em
        notifyClients(owner); // Check em & send em
    }

    public void sendToLocation(Player owner, Location location, boolean randomized, double yOffset, int repeat) {
        if (!randomized) {
            List<Location> locations = new ArrayList<>();
            for (int i = 0; i < repeat; i++) {
                locations.add(location);
            }
            sendToLocation(owner, locations);
            return;
        }

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            locations.add(net.badlion.cosmetics.particles.Particle.getParticleLocation(location, yOffset));
        }
        sendToLocation(owner, locations);
    }

    public void sendToLocation(Player owner, Location location, int repeat) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            locations.add(location);
        }
        sendToLocation(owner, locations);
    }

    public void sendToLocation(Player owner, List<Location> locations, int repeat) {
        List<Location> locationsCopy = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            locationsCopy.addAll(locations);
        }
        sendToLocation(owner, locationsCopy);
    }

    public void sendToLocation(Player owner, Location location) {
        sendToLocation(owner, Collections.singletonList(location));
    }

    private Object createPacket(Location location) {
        try {
            if (this.count <= 0) {
                this.count = 1;
            }

            Object packet;
            if (netty) {
                if (newParticlePacketConstructor) {
                    Object particleType = enumParticle.getEnumConstants()[this.type.getId()];
                    if (this.xd != Double.MAX_VALUE) {
                        packet = packetConstructor.newInstance(particleType,
                                true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                                (float) this.xd, (float) this.yd, (float) this.zd,
                                (float) this.speed, this.count, new int[0]);
                    } else {
                        packet = packetConstructor.newInstance(particleType,
                                true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                                (float) this.radius, (float) this.radius, (float) this.radius,
                                (float) this.speed, this.count, new int[0]);
                    }
                } else {
                    if (this.xd != Double.MAX_VALUE) {
                        packet = packetConstructor.newInstance(this.type.getName(),
                                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                                (float) this.xd, (float) this.yd, (float) this.zd,
                                (float) this.speed, this.count);
                    } else {
                        packet = packetConstructor.newInstance(this.type.getName(),
                                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                                (float) this.radius, (float) this.radius, (float) this.radius,
                                (float) this.speed, this.count);
                    }
                }
            } else {
                packet = packetConstructor.newInstance();
                for (Field f : fields) {
                    f.setAccessible(true);
                    switch (f.getName()) {
                        case "a":
                            f.set(packet, this.type.getName());
                            break;
                        case "b":
                            f.set(packet, (float) location.getX());
                            break;
                        case "c":
                            f.set(packet, (float) location.getY());
                            break;
                        case "d":
                            f.set(packet, (float) location.getZ());
                            break;
                        case "g":
                            f.set(packet, this.radius);
                            break;
                        case "h":
                            f.set(packet, this.speed);
                            break;
                        case "i":
                            f.set(packet, this.count);
                            break;
                    }
                }
            }
            return packet;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleLib] Failed to construct particle effect packet!");
        }
        return null;
    }

    public enum ParticleType {
        EXPLOSION_NORMAL("explode", 0, 17),
        EXPLOSION_LARGE("largeexplode", 1, 1),
        EXPLOSION_HUGE("hugeexplosion", 2, 0),
        FIREWORKS_SPARK("fireworksSpark", 3, 2),
        WATER_BUBBLE("bubble", 4, 3),
        WATER_SPLASH("splash", 5, 21),
        WATER_WAKE("wake", 6, -1),
        SUSPENDED("suspended", 7, 4),
        SUSPENDED_DEPTH("depthsuspend", 8, 5),
        CRIT("crit", 9, 7),
        CRIT_MAGIC("magicCrit", 10, 8),
        SMOKE_NORMAL("smoke", 11, -1),
        SMOKE_LARGE("largesmoke", 12, 22),
        SPELL("spell", 13, 11),
        SPELL_INSTANT("instantSpell", 14, 12),
        SPELL_MOB("mobSpell", 15, 9),
        SPELL_MOB_AMBIENT("mobSpellAmbient", 16, 10),
        SPELL_WITCH("witchMagic", 17, 13),
        DRIP_WATER("dripWater", 18, 27),
        DRIP_LAVA("dripLava", 19, 28),
        VILLAGER_ANGRY("angryVillager", 20, 31),
        VILLAGER_HAPPY("happyVillager", 21, 32),
        TOWN_AURA("townaura", 22, 6),
        NOTE("note", 23, 24),
        PORTAL("portal", 24, 15),
        ENCHANTMENT_TABLE("enchantmenttable", 25, 16),
        FLAME("flame", 26, 18),
        LAVA("lava", 27, 19),
        FOOTSTEP("footstep", 28, 20),
        CLOUD("cloud", 29, 23),
        REDSTONE("reddust", 30, 24),
        SNOWBALL("snowballpoof", 31, 25),
        SNOW_SHOVEL("snowshovel", 32, 28),
        SLIME("slime", 33, 29),
        HEART("heart", 34, 30),
        BARRIER("barrier", 35, -1),
        ITEM_CRACK("iconcrack_", 36, 33),
        BLOCK_CRACK("tilecrack_", 37, 34),
        BLOCK_DUST("blockdust_", 38, -1),
        WATER_DROP("droplet", 39, -1),
        ITEM_TAKE("take", 40, -1),
        MOB_APPEARANCE("mobappearance", 41, -1),
        DRAGON_BREATH("dragonrbreath", 42, -1),
        END_ROD("endrod", 43, -1),
        DAMAGE_INDICATOR("damageindicator", 44, -1),
        SWEEP_ATTACK("sweepattack", 45, -1);

        private String name;
        private int id;
        private int legacyId;

        ParticleType(String name, int id, int legacyId) {
            this.name = name;
            this.id = id;
            this.legacyId = legacyId;
        }

        String getName() {
            return this.name;
        }

        int getId() {
            return this.id;
        }

        int getLegacyId() {
            return this.legacyId;
        }

    }

}
