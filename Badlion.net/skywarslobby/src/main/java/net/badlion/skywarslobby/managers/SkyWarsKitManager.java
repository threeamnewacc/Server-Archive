package net.badlion.skywarslobby.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.skywarslobby.kits.KitType;
import net.badlion.skywarslobby.kits.PlayerKits;
import net.badlion.skywarslobby.kits.SkyWarsKit;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SkyWarsKitManager extends BukkitUtil.Listener {

    public static int KIT_PREVIEW_LIMIT = 1000;

    private static Map<UUID, Long> lastCustomPreviewTime = new HashMap<>();

	private static Map<UUID, PlayerKits> playerKits = new HashMap<>();

    public static void initialize() {
        new SkyWarsKitManager();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
	    // Create a PlayerKits object for the player
	    SkyWarsKitManager.createPlayerKits(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAsyncPlayerJoinEvent(final AsyncPlayerJoinEvent event) {
	    final PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(event.getUuid());
	    if (playerKits != null) {
		    playerKits.fetchKits(event.getConnection(), KitType.NORMAL);
		    playerKits.fetchKits(event.getConnection(), KitType.OP);

		    playerKits.fetchUnlockedKitItems(event.getConnection());

		    event.getRunnables().add(new Runnable() {
			    public void run() {
				    playerKits.setLoaded();
			    }
		    });
	    }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        SkyWarsKitManager.removePlayerKits(event.getPlayer().getUniqueId());
    }

	private static boolean limitPreviews(Player player) {
        Long ts = SkyWarsKitManager.lastCustomPreviewTime.get(player.getUniqueId());
        if (ts != null) {
            if (ts + SkyWarsKitManager.KIT_PREVIEW_LIMIT > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Do not spam kit preview, wait 1 second in between previews");
                return false;
            }
        }

        SkyWarsKitManager.lastCustomPreviewTime.put(player.getUniqueId(), System.currentTimeMillis());

        return true;
    }

	public static PlayerKits createPlayerKits(UUID uuid) {
		return SkyWarsKitManager.playerKits.put(uuid, new PlayerKits(uuid));
	}

	public static PlayerKits getPlayerKits(Player player) {
		return SkyWarsKitManager.getPlayerKits(player.getUniqueId());
	}

	public static PlayerKits getPlayerKits(UUID uuid) {
		return SkyWarsKitManager.playerKits.get(uuid);
	}

	public static PlayerKits removePlayerKits(UUID uuid) {
		return SkyWarsKitManager.playerKits.remove(uuid);
	}

    // TODO: This method
    public static void openKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, final Player player) {
        // Prevent spam
        /*if (!SkyWarsKitManager.limitPreviews(player)) {
            return;
        }

        SmellyInventory.FakeHolder fakeHolder;
        final Inventory inventory;
        if (smellyInventory != null && currentInventory != null) {
            fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
            fakeHolder.setParentInventory(currentInventory);

            inventory = PotPvP.getInstance().getServer().createInventory(fakeHolder, 54,
                                                                                ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " SkyWarsKit Preview");

            inventory.setItem(53, SmellyInventory.getBackInventoryItem());
        } else {
            SmellyInventory newSmellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
                                                                            ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " SkyWarsKit Preview");
            inventory = newSmellyInventory.getMainInventory();
        }


        final Map<SkyWarsKit, List<ItemStack[]>> inventories = SkyWarsKitManager.inventories.get(player.getUniqueId());
        SkyWarsKit kit = new SkyWarsKit(player.getUniqueId().toString(), kitRuleSet.getName());
        List<ItemStack []> items = inventories.get(kit);
        if (items != null) {
            SkyWarsKitManager.fillInventoryWithContents(inventory, items.get(0), items.get(1));
            Gberry.log("KIT", "Retrieving kit " + kit.toString() + " in preview cache for " + player.getUniqueId().toString());
        } else {
            SkyWarsKitManager.fillInventoryWithContents(inventory, kitRuleSet.getDefaultArmorKit(), kitRuleSet.getDefaultInventoryKit());
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

    public static void saveKit(final Player player, final SkyWarsKit kit) {
	    // Save armor and inventory items in kit object
	    kit.setArmorContents(player.getInventory().getArmorContents());
	    kit.setInventoryContents(player.getInventory().getContents());

        try {
            // Serialize it all
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            // Compress inventory data
            List<ConfigurationSerializable> itemsList = new ArrayList<>();
            Collections.addAll(itemsList, kit.getInventoryContents());

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
            Collections.addAll(itemsList2, kit.getArmorContents());

	        List<Map<String, Object>> items2 = CompressionUtil.serializeItemList(itemsList2);

	        oos2.writeObject(items2);
            oos2.flush();
            oos2.close();
            bos2.close();
            final byte[] armorData = bos2.toByteArray();

            Gberry.log("KIT", "Storing " + kit.getKitType() + " kit #" + kit.getKitNumber() + " in cache for " + player.getUniqueId());

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
                        ps.setString(1, "");
                        ps.setInt(2, kit.getPreviewItem().getType().ordinal());
                        ps.setObject(3, inventoryData);
                        ps.setObject(4, armorData);
                        ps.setString(5, player.getUniqueId().toString());
                        ps.setString(6, "SKYWARS");
                        ps.setString(7, kit.getKitType().toString());
                        ps.setInt(8, kit.getKitNumber());
                        ps.setString(9, player.getUniqueId().toString());
                        ps.setString(10, "SKYWARS");
                        ps.setString(11, kit.getKitType().toString());
                        ps.setInt(12, kit.getPreviewItem().getType().ordinal());
                        ps.setObject(13, inventoryData);
                        ps.setObject(14, armorData);
                        ps.setInt(15, kit.getKitNumber());
                        ps.setString(16, ""); // TODO: NAME ALWAYS BLANK?
                        ps.setString(17, player.getUniqueId().toString());
                        ps.setString(18, "SKYWARS");
                        ps.setString(19, kit.getKitType().toString());
                        ps.setInt(20, kit.getKitNumber());

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

    public static SkyWarsKit getKit(Player player, KitType kitType, Integer kitNumber) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

	    PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(player);
	    return playerKits.getKit(kitType, kitNumber);
    }

    public static Map<Integer, SkyWarsKit> getAllKits(Player player, KitType kitType) {
	    return SkyWarsKitManager.getPlayerKits(player).getAllKits(kitType);
    }

    public static void loadKit(Player player, SkyWarsKit kit) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

	    if (kit.getArmorContents() != null) System.out.println(kit.getArmorContents().length);

	    player.getInventory().setArmorContents(kit.getArmorContents());

	    if (kit.getInventoryContents() != null) {
		    player.getInventory().setContents(kit.getInventoryContents());
	    } else {
		    player.getInventory().clear();
	    }

	    player.updateInventory();

	    player.getInventory().setHeldItemSlot(0);

	    Gberry.log("KIT", "Loaded kit  " + kit.getKitType() +" kit #" + kit.getKitNumber() + " for " + player.getUniqueId().toString());
	    player.sendMessage(ChatColor.GREEN + "Successfully loaded " + kit.getKitType().toString() + " kit #" + kit.getKitNumber());
    }

}