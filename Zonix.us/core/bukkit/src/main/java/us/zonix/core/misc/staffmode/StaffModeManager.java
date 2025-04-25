package us.zonix.core.misc.staffmode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.core.util.ItemUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class StaffModeManager  {

    private HashSet<UUID> staffToggled, vanishedPlayers;
    private HashMap<UUID, ItemStack[]> inventories, armors;

    public StaffModeManager() {
        this.vanishedPlayers = new HashSet<>();
        this.staffToggled = new HashSet<>();
        this.inventories = new HashMap<>();
        this.armors = new HashMap<>();

        this.unvanishPlayers();
    }

    public void toggleStaffMode(Player player) {

        if(!this.staffToggled.contains(player.getUniqueId())) {

            player.getInventory().setHeldItemSlot(4);
            vanishPlayer(player);

            saveArmor(player);
            saveInventory(player);

            player.getInventory().setItem(0, ItemUtil.createItem(Material.STICK, "&c&lRandom TP"));
            player.getInventory().setItem(1, ItemUtil.createItem(Material.ICE, "&c&lFreeze/Unfreeze"));
            player.getInventory().setItem(2, ItemUtil.createItem(Material.BLAZE_ROD, "&c&lInspect Inventory"));
            player.getInventory().setItem(6, ItemUtil.createItem(Material.CARPET, "&c&lCarpet"));
            player.getInventory().setItem(7, ItemUtil.createItem(Material.GLASS, "&c&lUnvanish"));
            player.getInventory().setItem(8, ItemUtil.createItem(Material.COMPASS, "&c&lJump-To"));

            this.staffToggled.add(player.getUniqueId());

            player.setAllowFlight(true);
            player.setFlying(true);

            player.setGameMode(GameMode.CREATIVE);

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

        } else {

            unvanishPlayer(player);

            returnArmor(player);
            returnInventory(player);
            this.staffToggled.remove(player.getUniqueId());

            player.setAllowFlight(false);
            player.setFlying(false);

            player.setGameMode(GameMode.SURVIVAL);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        player.sendMessage(ChatColor.GREEN + "You have toggled Staff Mode.");

    }

    public boolean hasStaffToggled(Player player) {
        return this.staffToggled.contains(player.getUniqueId());
    }

    private void saveInventory(Player player) {

        if(!this.inventories.containsKey(player.getUniqueId())) {
            this.inventories.put(player.getUniqueId(), player.getInventory().getContents());
        }

        player.getInventory().clear();

    }

    private void returnInventory(Player player) {

        if(this.inventories.containsKey(player.getUniqueId())) {

            ItemStack[] contents = this.inventories.get(player.getUniqueId());

            if(contents.length > 0) {
                player.getInventory().setContents(contents);
            }

            this.inventories.remove(player.getUniqueId());

        }

    }

    private void saveArmor(Player player) {

        if(!this.armors.containsKey(player.getUniqueId())) {
            this.armors.put(player.getUniqueId(), player.getInventory().getArmorContents());
        }

        player.getInventory().setArmorContents(null);

    }

    private void returnArmor(Player player) {

        if(this.armors.containsKey(player.getUniqueId())) {

            ItemStack[] armor = this.armors.get(player.getUniqueId());

            if(armor.length > 0) {
                player.getInventory().setArmorContents(armor);
            }

            this.armors.remove(player.getUniqueId());

        }

    }

    public HashSet<UUID> getVanishedPlayers() {
        return this.vanishedPlayers;
    }

    public void vanishPlayer(Player player) {
        for(Player online : Bukkit.getServer().getOnlinePlayers()) {

            if(!online.hasPermission("permissions.staff")) {
                online.hidePlayer(player);
            }
        }

        this.vanishedPlayers.add(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "You have vanished.");
    }

    public void unvanishPlayer(Player player) {
        for(Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.showPlayer(player);
        }

        this.vanishedPlayers.remove(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "You have unvanished.");
    }


    private void unvanishPlayers() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            for(Player online : Bukkit.getServer().getOnlinePlayers()) {
                online.showPlayer(player);
            }
        }
    }
}
