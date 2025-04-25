package us.zonix.practice.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;

public final class PlayerUtil {
    private PlayerUtil() {
    }

    public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack1 = player.getInventory().getContents()[i];
            if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }

    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

    public static void clearPlayer(final Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        (new BukkitRunnable() {
            public void run() {
                player.setFireTicks(0);
            }
        }).runTaskLater(Practice.getInstance(), 1L);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, (byte)0);
        ((CraftPlayer)player).getHandle().setFakingDeath(false);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.updateInventory();
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
        Firework f = (Firework)location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);

        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    public static void respawnPlayer(final PlayerDeathEvent event) {
        (new BukkitRunnable() {
            public void run() {
                try {
                    Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle").invoke(event.getEntity());
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);
                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
            }
        }).runTaskLater(Practice.getInstance(), 2L);
    }

    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }
}
