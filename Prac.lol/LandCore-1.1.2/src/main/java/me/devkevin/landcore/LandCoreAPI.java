package me.devkevin.landcore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.PlayerVersion;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.message.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import us.myles.ViaVersion.api.Via;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 13:49
 * CorePluginAPI / land.pvp.core / LandCore
 */
@UtilityClass
public class LandCoreAPI {
    public final static ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .durability(7)
            .name("&a")
            .lore(" ")
            .hideFlags()
            .build();

    public static String getColorPing(int ping) {
        if (ping <= 40) return CC.translate("&a" + ping);
        if (ping <= 70) return CC.translate("&e" + ping);
        if (ping <= 100) return CC.translate("&6" + ping);
        else return CC.translate("&c" + ping);
    }

    public static boolean isInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void sendToServer(Player player, String server) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendMessage(CC.translate("&aSending to " + server + "..."));
            player.sendPluginMessage(LandCore.getInstance(), "BungeeCord", out.toByteArray());
        } catch (Exception e) {
            player.sendMessage(CC.translate("&cAn Error occurred while sending to the server."));
        }
    }

    public static int getMaxEnchantLevel(Player player) {
        CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (profile.hasRank(Rank.DEVELOPER)) return 9999;
        if (profile.hasRank(Rank.DEVELOPER)) return 21;
        if (profile.hasRank(Rank.DEVELOPER)) return 14;
        if (profile.hasRank(Rank.DEVELOPER)) return 7;

        return 5;
    }

    public static Enchantment getEnchantmentByName(Object object) {
        String value = object.toString().replace("_", "").trim();

        switch (value.toUpperCase()) {
            case "PROT":
            case "PROTECTION":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "UNB":
            case "UNBREAKING":
                return Enchantment.DURABILITY;
            case "FIREP":
            case "FP":
            case "FIREPROTECTION":
                return Enchantment.PROTECTION_FIRE;
            case "FEATHERF":
            case "FL":
            case "FEATHERFALLING":
                return Enchantment.PROTECTION_FALL;
            case "BLASTP":
            case "BP":
            case "BLASTPROTECTION":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "SHARP":
            case "SHARPNESS":
                return Enchantment.DAMAGE_ALL;
            case "KNOCK":
            case "KNOCKBACK":
                return Enchantment.KNOCKBACK;
            case "FIREA":
            case "FA":
            case "FIRE":
            case "FIREASPECT":
                return Enchantment.FIRE_ASPECT;
            case "L":
            case "LOOT":
            case "LOOTING":
                return Enchantment.LOOT_BONUS_MOBS;
            case "F":
            case "FORT":
            case "FORTUNE":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "ST":
            case "SILK":
            case "SILKTOUCH":
                return Enchantment.SILK_TOUCH;
            case "EFF":
            case "EFFICIENCY":
                return Enchantment.DIG_SPEED;
            case "SM":
            case "SMITE":
                return Enchantment.DAMAGE_UNDEAD;
            case "INF":
            case "INFINITY":
                return Enchantment.ARROW_INFINITE;
            case "FLA":
            case "FLAME":
                return Enchantment.ARROW_FIRE;
            case "PUNCH":
                return Enchantment.ARROW_KNOCKBACK;
            case "POWER":
                return Enchantment.ARROW_DAMAGE;
            default:
                return null;
        }
    }

    public enum Language {
        USE_NUMBERS("USE_NUMBERS", "{prefix} &cPlease use numbers."),
        ENCHANT_MUST_HOLD_ITEM("ENCHANT.MUST-HOLD-ITEM", "{prefix} &eYou must hold item in order to enchant."),
        ENCHANT_ENCHANT_MUST_BE_POSITIVE("ENCHANT.ENCHANT-MUST-BE-POSITIVE", "{prefix} &eEnchant level must be positive."),
        ENCHANT_MAXIMUM_LEVEL_EXCEEDED("ENCHANT.MAXIMUM-LEVEL-EXCEEDED", "{prefix} &eYour maximum level for enchant is &6<level>&e."),
        ENCHANT_WRONG_ENCHANTMENT("ENCHANT.WRONG-ENCHANTMENT", "{prefix} &eYou've entered wrong enchantment."),
        ENCHANT_ITEM_DOESNT_HAVE_ENCHANT("ENCHANT.ITEM-IS-NOT-ENCHANTED", "{prefix} &eYour item does not have that enchantment."),
        ENCHANT_REMOVED("ENCHANT.REMOVED", "{prefix} &eYou've removed &6<enchant> &eenchantment from your item."),
        ENCHANT_ADDED("ENCHANT.ADDED", "{prefix} &eYou've added &6<enchant> &eenchantment to your item.");

        @Getter
        private String path;
        @Getter
        private String value;
        @Getter
        private List<String> listValue;

        Language(String path, String value) {
            this.path = path;
            this.value = value;
            this.listValue = new ArrayList<>(Collections.singletonList(value));
        }
    }

    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(Via.getAPI().getPlayerVersion(player.getUniqueId()));
    }

    public abstract class LandCoreListener implements Listener {
        public LandCoreListener(JavaPlugin javaPlugin) {
            Bukkit.getServer().getPluginManager().registerEvents(this, javaPlugin);
        }
    }

    public static String getTodayDate() {
        Date todayDate = new Date();
        DateFormat todayDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        todayDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return todayDateFormat.format(todayDate);
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        dateFormat.setTimeZone(cal.getTimeZone());
        return dateFormat.format(cal.getTime());
    }
}
