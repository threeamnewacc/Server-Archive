package net.badlion.arenacommon.kits;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KitCommon {

    private static Map<UUID, Long> lastCustomPreviewTime = new HashMap<>();

    // List<ItemStack[]> is Armor, Inventory
    public static Map<UUID, Map<KitType, List<Kit>>> inventories = new ConcurrentHashMap<>();

    /*
        Kit Saving
    */

    public static void saveKit(Player player, KitRuleSet kitRuleSet, int kitId) {
        KitCommon.saveKit(player, kitRuleSet, player.getUniqueId().toString(), kitRuleSet.getName(), kitId);
    }

    private static void saveKit(final Player player, KitRuleSet kitRuleSet, final String kitname, final String tag, final int kitId) {
        if (!kitRuleSet.allowsExtraArmorSets() || !kitRuleSet.allowsExtraShields()) {
            // Check for extra armor sets and remove them
            if (kitRuleSet.checkForExtraShieldsOrArmorSets(player)) {
                player.sendMessage(ChatColor.RED + "Extra armor sets are not allowed for this rule set. They have been removed.");
            }
        }

        try {
            // Serialize it all
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            // Compress inventory data
            List<ConfigurationSerializable> itemsList = new ArrayList<>();
            Collections.addAll(itemsList, player.getInventory().getContents());

            List<Map<String, Object>> items = CompressionUtil.serializeItemList(itemsList);
            oos.writeObject(items);
            oos.flush();
            oos.close();
            bos.close();
            final byte[] inventoryData = bos.toByteArray();

            // Compress armor data
            itemsList.clear();
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);

            Collections.addAll(itemsList, player.getInventory().getArmorContents());

            List<Map<String, Object>> items2 = CompressionUtil.serializeItemList(itemsList);
            oos.writeObject(items2);
            oos.flush();
            oos.close();
            bos.close();
            final byte[] armorData = bos.toByteArray();

            // Update the cache
            final Map<KitType, List<Kit>> inventories = KitCommon.inventories.get(player.getUniqueId());
            KitType kitType = new KitType(kitname, tag);
            final Kit kit = new Kit(kitname);
            kit.setId(kitId);
            kit.inventoryItems = player.getInventory().getContents();
            kit.armorItems = player.getInventory().getArmorContents();

            List<ItemStack[]> itemStacks = new ArrayList<>();
            itemStacks.add(player.getInventory().getArmorContents());
            itemStacks.add(player.getInventory().getContents());

	        // This was used for 1.9, it is no longer used
            ItemStack[] extraItemsArray = new ItemStack[1];

            itemStacks.add(extraItemsArray);
            if (inventories.get(kitType) == null) {
                inventories.put(kitType, new ArrayList<Kit>());
            }
            inventories.get(kitType).add(kit);

            Gberry.log("KIT", "Storing kit " + kit.toString() + " in cache for " + player.getUniqueId());

            BukkitUtil.runTaskAsync(new Runnable() {
                public void run() {
                    Connection con = null;
                    PreparedStatement ps = null;

                    try {
                        con = Gberry.getConnection();
                        String query;

	                    query = "UPDATE kits_s13 SET owner = ?, kitname = ?, kitid = ?, items = ?, armor = ?, tag = ? WHERE tag = ? AND kitname = ? AND kitid = ?;\n";
	                    query += "INSERT INTO kits_s13 (owner, kitname, kitId, items, armor, tag) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
			                    "(SELECT 1 FROM kits_s13 WHERE tag = ? AND kitname = ? AND kitid = ?);";

                        int i = 1;
                        ps = con.prepareStatement(query);
                        ps.setString(i++, player.getUniqueId().toString());
                        ps.setString(i++, kitname);
                        ps.setInt(i++, kitId);
                        ps.setObject(i++, inventoryData);

                        ps.setObject(i++, armorData);
                        ps.setString(i++, tag);
                        ps.setString(i++, tag);
                        ps.setString(i++, kitname);
                        ps.setInt(i++, kitId);
                        ps.setString(i++, player.getUniqueId().toString());
                        ps.setString(i++, kitname);
                        ps.setInt(i++, kitId);
                        ps.setObject(i++, inventoryData);

                        ps.setObject(i++, armorData);
                        ps.setString(i++, tag);
                        ps.setString(i++, tag);
                        ps.setString(i++, kitname);
                        ps.setInt(i, kitId);

                        Gberry.executeUpdate(con, ps);

                        player.sendMessage(ChatColor.GREEN + "Saved " + tag + " kit: " + ChatColor.GOLD + (kitId + 1));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        Bukkit.getLogger().severe(ex.getMessage());
                        player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if this error persists.");
                    } finally {
                        Gberry.closeComponents(ps, con);
                    }
                }

            });
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe(ex.getMessage());
            player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if this error persists.");
        }
    }

    /*
        Kit Loading
     */

    public static void loadKit(Player player, KitRuleSet kitRuleSet, int kitId) {
        KitCommon.loadKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), kitId);
        kitRuleSet.applyKnockbackToPlayer(player); // Apply KB each time a kit is loaded
    }

    private static void loadKit(final Player player, final String kitName, final String kitRulesetName, final int kitId) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

        KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(kitRulesetName);
        Set<PotionEffect> potionEffects = kitRuleSet.getPotionEffects();
        Map<KitType, List<Kit>> inventories = KitCommon.inventories.get(player.getUniqueId());
        KitType kitType = new KitType(kitName, kitRulesetName);
        List<Kit> kits = inventories.get(kitType);

        Kit kit = null;
        if (kits != null) {
            for (Kit k : kits) {
                if (k.getId() == kitId) {
                    kit = k;
                }
            }
        }
        if (kit != null) {
            kit.giveToPlayer(player);

            for (PotionEffect potionEffect : potionEffects) {
                player.addPotionEffect(potionEffect);
            }

            player.updateInventory();
            Gberry.log("KIT", "Loaded kit " + kitType.toString() + " for " + player.getUniqueId().toString());
            player.sendMessage(ChatColor.GREEN + "Loaded " + kitRuleSet.getName() + " kit: " + ChatColor.GOLD + (kitId + 1));
        } else {
            if (kitName.equals(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.GREEN + "No kit found, loading default " + kitRulesetName + " kit");
                KitCommon.loadDefaultKit(player, kitRuleSet, false);
            } else {
                // No custom kit found
                player.sendMessage(ChatColor.RED + "No kit found to load.");

                // Clear their inventory because they might have lobby items
                player.getInventory().clear();
                player.getInventory().setArmorContents(new ItemStack[4]);
            }
        }

        Bukkit.getServer().getPluginManager().callEvent(new KitLoadEvent(player, kitRuleSet));
    }

    public static Kit getKit(final Player player, final String kitName, final String tag, int kitId) {
        Map<KitType, List<Kit>> inventories = KitCommon.inventories.get(player.getUniqueId());
        KitType kitType = new KitType(kitName, tag);

        List<Kit> kits = inventories.get(kitType);
        Kit kit = null;
        if (kits != null) {
            for (Kit k : kits) {
                if (k.getId() == kitId) {
                    kit = k;
                }
            }
        }
        return kit;
    }


    public static void deleteKit(final Player player, final String kitName, final String tag, final int kitId) {
        KitType kitType = new KitType(kitName, tag);
        List<Kit> kits = inventories.get(player.getUniqueId()).get(kitType);
        Iterator<Kit> kitIterator = kits.iterator();
        while (kitIterator.hasNext()) {
            if (kitIterator.next().getId() == kitId) {
                kitIterator.remove();
                break;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection con = null;
                PreparedStatement ps = null;
                String query;
                try {
                    con = Gberry.getConnection();
                    query = "DELETE FROM kits_s13 WHERE owner = ? AND kitname = ? AND tag = ? AND kitid = ?;";

                    ps = con.prepareStatement(query);
                    ps.setString(1, player.getUniqueId().toString());
                    ps.setString(2, kitName);
                    ps.setString(3, tag);
                    ps.setInt(4, kitId);

                    Gberry.executeUpdate(con, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    Gberry.closeComponents(ps, con);
                }
            }
        }.runTaskAsynchronously(Gberry.plugin);
    }


    /**
     * Needs to be called ASYNC
     */
    public static Map<KitType, List<Kit>> getAllKitContents(Connection connection, UUID uuid) {
        Map<KitType, List<Kit>> kits = new HashMap<>();

        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String query = "SELECT * FROM kits_s13 WHERE owner = ?;";
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                // Items
                final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));

	            // This was used for 1.9, it is no longer used
                final ByteArrayInputStream baisItemsExtra = null;

                final ByteArrayInputStream baisArmor = new ByteArrayInputStream(rs.getBytes("armor"));

                ItemStack[] items = null;
                ItemStack[] extraItems = null;
                ItemStack[] armor = null;

                // More efficient to handle this ASYNC
                try {
                    ObjectInputStream ins = new ObjectInputStream(baisItems);
                    List<ConfigurationSerializable> list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    items = new ItemStack[list.size()];
                    for (int i = 0; i < items.length; i++) {
                        if (list.get(i) == null) {
                            items[i] = null;
                        } else {
                            items[i] = (ItemStack) list.get(i);
                        }
                    }

                    ins = new ObjectInputStream(baisArmor);
                    list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    armor = new ItemStack[list.size()];
                    for (int i = 0; i < armor.length; i++) {
                        if (list.get(i) == null) {
                            armor[i] = null;
                        } else {
                            armor[i] = (ItemStack) list.get(i);
                        }
                    }

                    Kit kit = new Kit(rs.getString("kitname"));
                    kit.setId(rs.getInt("kitid"));
                    kit.inventoryItems = items;
                    kit.armorItems = armor;

                    KitType kitType = new KitType(rs.getString("kitname"), rs.getString("tag"));
                    if (kits.get(kitType) == null) {
                        kits.put(kitType, new ArrayList<Kit>());
                    }
                    kits.get(kitType).add(kit);
                } catch (StreamCorruptedException e) {
                    // Corrupt kit
                    query = "DELETE FROM kits_s13 WHERE kitname = ? AND tag = ? AND kitid = ?;";
                    ps.close();

                    ps = connection.prepareStatement(query);
                    ps.setString(1, rs.getString("kitname"));
                    ps.setString(2, rs.getString("tag"));
                    ps.setInt(3, rs.getInt("kitid"));

                    Gberry.executeUpdate(connection, ps);
                } catch (IOException | ClassNotFoundException e) {
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return kits;
    }

    public static Map<UUID, List<Kit>> getAllKitContentsForPlayersAndRuleset(Connection connection, List<String> uuids, KitRuleSet kitRuleSet) {
        Map<UUID, List<Kit>> kits = new HashMap<>();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < uuids.size(); i++) {
            builder.append("?,");
        }

        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String query = "SELECT * FROM kits_s13 WHERE owner IN (" + builder.substring(0, builder.length() - 1) + ") AND tag = ?;";
            ps = connection.prepareStatement(query);
            int pIndex = 1;
            for (int u = 0; u < uuids.size(); u++) {
                ps.setString(pIndex++, uuids.get(u));
            }
            ps.setString(pIndex++, kitRuleSet.getName());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                // Items
                final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));

	            // This was used for 1.9, it is no longer used
                final ByteArrayInputStream baisItemsExtra = null;

                final ByteArrayInputStream baisArmor = new ByteArrayInputStream(rs.getBytes("armor"));

                UUID ownerId = UUID.fromString(rs.getString("owner"));
                ItemStack[] items = null;
                ItemStack[] extraItems = null;
                ItemStack[] armor = null;

                // More efficient to handle this ASYNC
                try {
                    ObjectInputStream ins = new ObjectInputStream(baisItems);
                    List<ConfigurationSerializable> list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    items = new ItemStack[list.size()];
                    for (int i = 0; i < items.length; i++) {
                        if (list.get(i) == null) {
                            items[i] = null;
                        } else {
                            items[i] = (ItemStack) list.get(i);
                        }
                    }

                    ins = new ObjectInputStream(baisArmor);
                    list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    armor = new ItemStack[list.size()];
                    for (int i = 0; i < armor.length; i++) {
                        if (list.get(i) == null) {
                            armor[i] = null;
                        } else {
                            armor[i] = (ItemStack) list.get(i);
                        }
                    }

                    Kit kit = new Kit(rs.getString("kitname"));
                    kit.setId(rs.getInt("kitid"));
                    kit.inventoryItems = items;
                    kit.armorItems = armor;

                    if (kits.get(ownerId) == null) {
                        kits.put(ownerId, new ArrayList<Kit>());
                    }
                    kits.get(ownerId).add(kit);
                } catch (StreamCorruptedException e) {
                    // Corrupt kit
                    query = "DELETE FROM kits_s13 WHERE kitname = ? AND tag = ? AND kitid = ?;";
                    ps.close();

                    ps = connection.prepareStatement(query);
                    ps.setString(1, rs.getString("kitname"));
                    ps.setString(2, rs.getString("tag"));
                    ps.setInt(3, rs.getInt("kitid"));

                    Gberry.executeUpdate(connection, ps);
                } catch (IOException | ClassNotFoundException e) {
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return kits;
    }

    public static List<Integer> getSavedCustomKitNumbers(Player player, KitRuleSet kitRuleSet) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            con = Gberry.getConnection();
            String query = "SELECT kitname FROM kits_s13 WHERE owner = ? AND tag = ?;";
            ps = con.prepareStatement(query);
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, kitRuleSet.getName());
            rs = Gberry.executeQuery(con, ps);

            List<Integer> customKitNumbers = new ArrayList<>();

            while (rs.next()) {
                int kitId = rs.getInt("kitid");
                customKitNumbers.add(kitId);
            }

            // Sort our list ascending (hopefully)
            Collections.sort(customKitNumbers);

            return customKitNumbers;

        } catch (SQLException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe(ex.getMessage());
            player.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if this error persists.");
        } finally {
            Gberry.closeComponents(rs, ps, con);
        }
        return null;
    }

    public static void loadDefaultKit(Player player, KitRuleSet kitRuleSet, boolean message) {
        player.getInventory().setContents(kitRuleSet.getDefaultInventoryKit());
        player.getInventory().setArmorContents(kitRuleSet.getDefaultArmorKit());
        kitRuleSet.applyKnockbackToPlayer(player); // Apply KB each time a kit is loaded

        for (PotionEffect potionEffect : kitRuleSet.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }

        player.updateInventory();

        if (message) {
            player.sendMessage(ChatColor.GREEN + "Loaded default " + kitRuleSet.getName() + " kit.");
        }

	    Bukkit.getServer().getPluginManager().callEvent(new KitLoadEvent(player, kitRuleSet));
    }

    public static void fillInventoryWithContents(Inventory inventory, ItemStack[] armorContents, ItemStack[] inventoryContents) {
        // Fill in armor contents
        for (int i = 0; i < armorContents.length; i++) {
            inventory.setItem(i, armorContents[3 - i]);
        }

        // Fill in main inventory contents
        for (int i = 9; i < inventoryContents.length; i++) {
            inventory.setItem(i, inventoryContents[i]);
        }

        // Fill in hotbar contents
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i + 36, inventoryContents[i]);
        }
    }

}
