package net.badlion.mpg.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.KitLoadEvent;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MPGKitManager implements Listener {

    private static final int KIT_PREVIEW_LIMIT = 1000;

    private static Map<UUID, Long> lastCustomPreviewTime = new HashMap<>();

    private static Map<UUID, Map<String, MPGKit>> kits = new ConcurrentHashMap<>();

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
	    MPGKitManager.kits.put(event.getUniqueId(), MPGKitManager.getAllKitContents(event.getUniqueId(), MPG.MPG_GAME_NAME));

	    BukkitUtil.runTask(new Runnable() {
		    @Override
		    public void run() {
			    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getUniqueId());
			    if (mpgPlayer != null) {
				    mpgPlayer.setKitsLoaded(true);
			    }
		    }
	    });
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        MPGKitManager.kits.remove(event.getPlayer().getUniqueId());
    }

    private static boolean limitPreviews(Player player) {
        Long ts = MPGKitManager.lastCustomPreviewTime.get(player.getUniqueId());
        if (ts != null) {
            if (ts + MPGKitManager.KIT_PREVIEW_LIMIT > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Do not spam kit preview, wait 1 second in between previews");
                return false;
            }
        }

        MPGKitManager.lastCustomPreviewTime.put(player.getUniqueId(), System.currentTimeMillis());

        return true;
    }

    // TODO: This method
    public static void openKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, final Player player) {
        // Prevent spam
        /*if (!MPGKitManager.limitPreviews(player)) {
            return;
        }

        SmellyInventory.FakeHolder fakeHolder;
        final Inventory inventory;
        if (smellyInventory != null && currentInventory != null) {
            fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
            fakeHolder.setParentInventory(currentInventory);

            inventory = PotPvP.getInstance().getServer().createInventory(fakeHolder, 54,
                                                                                ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " MPGKit Preview");

            inventory.setItem(53, SmellyInventory.getBackInventoryItem());
        } else {
            SmellyInventory newSmellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
                                                                            ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " MPGKit Preview");
            inventory = newSmellyInventory.getMainInventory();
        }


        final Map<MPGKit, List<ItemStack[]>> inventories = MPGKitManager.inventories.get(player.getUniqueId());
        MPGKit kit = new MPGKit(player.getUniqueId().toString(), kitRuleSet.getName());
        List<ItemStack []> items = inventories.get(kit);
        if (items != null) {
            MPGKitManager.fillInventoryWithContents(inventory, items.get(0), items.get(1));
            Gberry.log("KIT", "Retrieving kit " + kit.toString() + " in preview cache for " + player.getUniqueId().toString());
        } else {
            MPGKitManager.fillInventoryWithContents(inventory, kitRuleSet.getDefaultArmorKit(), kitRuleSet.getDefaultInventoryKit());
            Gberry.log("KIT", "Retrieving kit " + kit.toString() + " default for " + player.getUniqueId().toString());
        }

        BukkitUtil.openInventory(player, inventory);*/
    }

    public static void fillInventoryWithContents(Inventory inventory, ItemStack[] armorContents, ItemStack[] inventoryContents) {
        // Fill in armor contents
        for (int i = 0; i < armorContents.length; i++) {
            inventory.setItem(i, armorContents[3-i]);
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

    public static void saveKit(final Player player, final int kitNumber) {
        MPGKitManager.saveKit(player, kitNumber, new ItemStack(Material.WOOD_SWORD), "", "");
    }

    public static void saveKit(final Player player, final int kitNumber, final ItemStack previewItem, final String type, final String name) {
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
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            ObjectOutputStream oos2 = new ObjectOutputStream(bos2);

            List<ConfigurationSerializable> itemsList2 = new ArrayList<>();
            Collections.addAll(itemsList2, player.getInventory().getArmorContents());

            List<Map<String, Object>> items2 = CompressionUtil.serializeItemList(itemsList2);
            oos2.writeObject(items2);
            oos2.flush();
            oos2.close();
            bos2.close();
            final byte[] armorData = bos2.toByteArray();

            final Map<String, MPGKit> kits = MPGKitManager.kits.get(player.getUniqueId());
            MPGKit kit = new MPGKit(player.getUniqueId(), name, kitNumber, type, MPG.MPG_GAME_NAME, previewItem, player.getInventory().getContents(), player.getInventory().getArmorContents());
            kits.put(kit.toString(), kit);

            Gberry.log("KIT", "Storing kit " + kit.toString() + " in cache for " + player.getUniqueId());

            BukkitUtil.runTaskAsync(new Runnable() {
                public void run() {
                    Connection con = null;
                    PreparedStatement ps = null;

                    try {
                        con = Gberry.getConnection();
                        String query = "UPDATE mpg_kits SET name = ?, preview_item = ?, items = ?, armor = ? WHERE uuid = ? AND gamemode = ? AND type = ? AND kit_number = ?;\n";
                        query += "INSERT INTO mpg_kits (uuid, gamemode, type, preview_item, items, armor, kit_number, name) SELECT ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                                         "(SELECT 1 FROM mpg_kits WHERE uuid = ? AND gamemode = ? AND type = ? AND kit_number = ?);";

                        ps = con.prepareStatement(query);
                        ps.setString(1, name);
                        ps.setInt(2, previewItem.getType().ordinal());
                        ps.setObject(3, inventoryData);
                        ps.setObject(4, armorData);
                        ps.setString(5, player.getUniqueId().toString());
                        ps.setString(6, MPG.MPG_GAME_NAME);
                        ps.setString(7, type);
                        ps.setInt(8, kitNumber);
                        ps.setString(9, player.getUniqueId().toString());
                        ps.setString(10, MPG.MPG_GAME_NAME);
                        ps.setString(11, type);
                        ps.setInt(12, previewItem.getType().ordinal());
                        ps.setObject(13, inventoryData);
                        ps.setObject(14, armorData);
                        ps.setInt(15, kitNumber);
                        ps.setString(16, name);
                        ps.setString(17, player.getUniqueId().toString());
                        ps.setString(18, MPG.MPG_GAME_NAME);
                        ps.setString(19, type);
                        ps.setInt(20, kitNumber);

                        Gberry.executeUpdate(con, ps);

                        player.sendMessage(ChatColor.GREEN + "Successfully saved kit");
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

    public static MPGKit getKit(Player player, Integer kitNumber) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

        Map<String, MPGKit> kits = MPGKitManager.kits.get(player.getUniqueId());
        return kits.get(MPGKit.getKey(player.getUniqueId(), MPG.MPG_GAME_NAME, kitNumber));
    }

    public static Map<String, MPGKit> getAllKits(Player player) {
        return MPGKitManager.kits.get(player.getUniqueId());
    }

	public static void loadKit(Player player, MPGKit kit, boolean verbose) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

		// Get this kit from player's kits
		MPGKit playerKit = MPGKitManager.kits.get(player.getUniqueId()).get(kit.getName());

		// Use default kit if player doesn't have their own kit saved for this kit
		if (playerKit == null) playerKit = kit;

		player.getInventory().setArmorContents(kit.getArmorContents());
		player.getInventory().setContents(kit.getInventoryContents());

		for (PotionEffect potionEffect : kit.getPotionEffects()) {
			player.addPotionEffect(potionEffect);
		}

		player.updateInventory();
		Gberry.log("KIT", "Loaded kit " + kit.toString() + " for " + player.getUniqueId().toString());

		if (verbose) {
			if (playerKit != kit) {
				player.sendMessage(ChatColor.GREEN + "Loaded " + kit.getName() + " kit");
			} else {
				player.sendMessage(ChatColor.GREEN + "No kit found, loading default " + kit.getName() + " kit");
			}
		}
        Bukkit.getServer().getPluginManager().callEvent(new KitLoadEvent(player, kit));
    }

    /**
     * Needs to be called ASYNC
     */
    public static Map<String, MPGKit> getAllKitContents(UUID uuid, String gameMode) {
        Map<String, MPGKit> kits = new LinkedHashMap<>();

	    Connection connection = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

        try {
            String query = "SELECT * FROM mpg_kits WHERE uuid = ? AND gamemode = ? ORDER BY kit_number ASC;";

	        connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setString(2, gameMode);
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                // Items
                final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));
                final ByteArrayInputStream baisArmor = new ByteArrayInputStream(rs.getBytes("armor"));

                ItemStack[] items = null;
                ItemStack[] armor = null;

                // More efficient to handle this ASYNC
                try {
                    ObjectInputStream ins = new ObjectInputStream(baisItems);
                    List<ConfigurationSerializable> list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    items = new ItemStack[list.size()];
                    for (int i = 0; i < items.length; ++i) {
                        if (list.get(i) == null) {
                            items[i] = null;
                        } else {
                            items[i] = (ItemStack) list.get(i);
                        }
                    }

                    ins = new ObjectInputStream(baisArmor);
                    list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
                    armor = new ItemStack[list.size()];
                    for (int i = 0; i < armor.length; ++i) {
                        if (list.get(i) == null) {
                            armor[i] = null;
                        } else {
                            armor[i] = (ItemStack) list.get(i);
                        }
                    }

                    MPGKit kit = new MPGKit(uuid, rs.getString("name"), rs.getInt("kit_number"), rs.getString("type"), rs.getString("gamemode"), new ItemStack(Material.values()[rs.getInt("preview_item")]), items, armor);
                    kits.put(kit.toString(), kit);
                } catch (StreamCorruptedException e) {
                    // Corrupt kit
                    query = "DELETE FROM mpg_kits WHERE kitname = ? AND gamemode = ?;";
                    ps.close();

                    ps = connection.prepareStatement(query);
                    ps.setString(1, rs.getString("kitname"));
                    ps.setString(2, rs.getString("gamemode"));

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

    public static List<Integer> getSavedCustomKitNumbers(Player player, String tag) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            con = Gberry.getConnection();
            String query = "SELECT kitname FROM mpg_kits WHERE uuid = ? AND tag = ?;";
            ps = con.prepareStatement(query);
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, tag);
            rs = Gberry.executeQuery(con, ps);

            List<Integer> customKitNumbers = new ArrayList<>();

            while (rs.next()) {
                try {
                    // Substring to get rid of UUID
                    customKitNumbers.add(Integer.valueOf(rs.getString("kitname").substring(37)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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

}