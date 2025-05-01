package net.badlion.potpvp.helpers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.PotPvPPlayer;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.inventories.duel.DuelChooseKitInventory;
import net.badlion.potpvp.inventories.lobby.EventsInventory;
import net.badlion.potpvp.inventories.lobby.KitCreationKitSelectionInventory;
import net.badlion.potpvp.inventories.lobby.Unranked1v1Inventory;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KitHelper extends BukkitUtil.Listener {

	public static int KIT_PREVIEW_LIMIT = 1000;

	private static Map<UUID, Long> lastCustomPreviewTime = new HashMap<>();

	// List<ItemStack[]> is Armor, Inventory
    private static Map<UUID, Map<Kit, List<ItemStack[]>>> inventories = new ConcurrentHashMap<>();

	public KitHelper() {
		// Hack for duel ladder type
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			kitRuleSet.getLadderPopulations().put(Ladder.LadderType.Duel, 0);
		}

		// Get unranked ladders from database
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM potion_unranked_kits" + PotPvP.getInstance().getDBExtra() + ";";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();

					ps = connection.prepareStatement(query);

					rs = Gberry.executeQuery(connection, ps);

					final List<KitRuleSet> unrankedKits = new ArrayList<>();
					while (rs.next()) {
						KitRuleSet kit = KitRuleSet.getKitRuleSet(rs.getString("kit_name"));
						if (kit != null) {
							unrankedKits.add(kit);
						} else {
							Bukkit.getLogger().severe("KIT " + rs.getString("kit_name") + " FOR UNRANKED LADDER NOT FOUND");
						}
					}

					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							// Cache unranked kits in kitruleset
							KitRuleSet.setUnrankedKits(unrankedKits);

							// Enable in duels if this is an unranked ladder
							for (KitRuleSet unrankedKit : unrankedKits) {
								unrankedKit.setEnabledInDuels();
							}

							// Fill inventories that need to load after we grab unranked kits
							DuelChooseKitInventory.fillDuelChooseKitInventories();
							EventsInventory.fillSelectKitInventory();
							KitCreationKitSelectionInventory.fillKitCreationSelectionInventory();
							Unranked1v1Inventory.fillUnranked1v1Inventory();
						}
					});
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		});
	}

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        KitHelper.inventories.put(event.getPlayer().getUniqueId(), new HashMap<Kit, List<ItemStack[]>>());
	}

	@EventHandler
	public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
		KitHelper.inventories.put(event.getUuid(), KitHelper.getAllKitContents(event.getConnection(), event.getUuid()));

		event.getRunnables().add(new Runnable() {
			public void run() {
				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
				if (potPvPPlayer != null) {
					potPvPPlayer.setKitsLoaded(true);
				}
			}
		});
	}

    @EventHandler
    public void onPlayerQuits(PlayerQuitEvent event) {
        KitHelper.inventories.remove(event.getPlayer().getUniqueId());
    }

	public static ItemStack createCustomKitInventoryItem(int customKitNumber) {
		return ItemStackUtil.createItem(
				Material.WRITTEN_BOOK, ChatColor.GREEN + "Custom Kit - " + customKitNumber, ChatColor.YELLOW + "Middle click to preview kit");
	}

	public static ItemStack createEventKitInventoryItem(int customKitNumber) {
		return ItemStackUtil.createItem(
				Material.WRITTEN_BOOK, ChatColor.GREEN + "Event Kit - " + customKitNumber, ChatColor.YELLOW + "Middle click to preview kit");
	}

	public static int getCustomKitNumberFromItem(ItemStack item) {
		// Get custom kit number
		int customKitNumber = -1;
		try {
			Gberry.log("KIT", "Splitting " + item.toString());
			String[] strings = item.getItemMeta().getDisplayName().split("- ");
			customKitNumber = Integer.valueOf(strings[1]);
		} catch (NumberFormatException e) {
			Bukkit.getLogger().severe("Custom kit number not found");
		}

		return customKitNumber;
	}

	private static boolean limitPreviews(Player player) {
		Long ts = KitHelper.lastCustomPreviewTime.get(player.getUniqueId());
		if (ts != null) {
			if (ts + KitHelper.KIT_PREVIEW_LIMIT > System.currentTimeMillis()) {
				player.sendMessage(ChatColor.RED + "Do not spam kit preview, wait 1 second in between previews");
				return false;
			}
		}

		KitHelper.lastCustomPreviewTime.put(player.getUniqueId(), System.currentTimeMillis());

		return true;
	}

	public static void openKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, Player player, ItemStack item) {
		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);
		if (kitRuleSet instanceof CustomRuleSet) {
			KitHelper.openCustomKitPreviewInventory(smellyInventory, currentInventory, player, kitRuleSet, KitHelper.getCustomKitNumberFromItem(item));
		} else {
			KitHelper.openKitPreviewInventory(smellyInventory, currentInventory, player, kitRuleSet);
		}
	}

	public static void openKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, final Player player, final KitRuleSet kitRuleSet) {
		// Prevent spam
		if (!KitHelper.limitPreviews(player)) {
			return;
		}

		SmellyInventory.FakeHolder fakeHolder;
		final Inventory inventory;
		if (smellyInventory != null && currentInventory != null) {
			fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
			fakeHolder.setParentInventory(currentInventory);

			inventory = PotPvP.getInstance().getServer().createInventory(fakeHolder, 54,
					ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " Kit Preview");

			inventory.setItem(53, SmellyInventory.getBackInventoryItem());
		} else {
			SmellyInventory newSmellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
					ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " Kit Preview");
			inventory = newSmellyInventory.getMainInventory();
		}


		final Map<Kit, List<ItemStack[]>> inventories = KitHelper.inventories.get(player.getUniqueId());
		Kit kit = new Kit(player.getUniqueId().toString(), kitRuleSet.getName());
		List<ItemStack []> items = inventories.get(kit);
        if (items != null) {
            KitHelper.fillInventoryWithContents(inventory, items.get(0), items.get(1), items.get(2));
            Gberry.log("KIT", "Retrieving kit " + kit.toString() + " in preview cache for " + player.getUniqueId().toString());
        } else {
			KitHelper.fillInventoryWithContents(inventory, kitRuleSet.getDefaultArmorKit(), kitRuleSet.getDefaultInventoryKit(), kitRuleSet.getDefaultExtraItem());
			Gberry.log("KIT", "Retrieving kit " + kit.toString() + " default for " + player.getUniqueId().toString());
		}

		BukkitUtil.openInventory(player, inventory);
	}

	private static void openCustomKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, final Player player, final KitRuleSet kitRuleSet, final int customKitNumber) {
		// Prevent spam
		if (!KitHelper.limitPreviews(player)) {
			return;
		}

		SmellyInventory.FakeHolder fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
		fakeHolder.setParentInventory(currentInventory);

		// Create inventory
		final Inventory inventory = PotPvP.getInstance().getServer().createInventory(fakeHolder,
				54, ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " Kit " + customKitNumber + " Preview");

		// Get kit and load one if we have it
		final Map<Kit, List<ItemStack[]>> inventories = KitHelper.inventories.get(player.getUniqueId());
		Kit kit = new Kit(player.getUniqueId().toString() + "-" + customKitNumber, kitRuleSet.getName());
		List<ItemStack []> items = inventories.get(kit);
		if (items != null) {
			KitHelper.fillInventoryWithContents(inventory, items.get(0), items.get(1), items.get(2));
		} else {
			player.sendMessage(ChatColor.RED + "No kit found to load.");
			return;
		}

		inventory.setItem(53, SmellyInventory.getBackInventoryItem());
		BukkitUtil.openInventory(player, inventory);
	}

	public static void openEventKitPreviewInventoryForEvent(SmellyInventory smellyInventory, Inventory currentInventory, final Player player, final Event event) {
		// Prevent spam
		if (!KitHelper.limitPreviews(player)) {
			return;
		}

		SmellyInventory.FakeHolder fakeHolder;
		final Inventory inventory;
		if (smellyInventory != null && currentInventory != null) {
			fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
			fakeHolder.setParentInventory(currentInventory);

			inventory = PotPvP.getInstance().getServer().createInventory(fakeHolder, 54,
					ChatColor.AQUA + ChatColor.BOLD.toString() + event.getKitRuleSet().getName() + " Kit Preview");

			inventory.setItem(53, SmellyInventory.getBackInventoryItem());
		} else {
			SmellyInventory newSmellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
					ChatColor.AQUA + ChatColor.BOLD.toString() + event.getKitRuleSet().getName() + " Kit Preview");
			inventory = newSmellyInventory.getMainInventory();
		}

		ItemStack[] armorContents = event.getArmorContents();
		ItemStack[] inventoryContents = event.getInventoryContents();
		ItemStack[] extraItemContents = event.getExtraItemContents();

		KitHelper.fillInventoryWithContents(inventory, armorContents, inventoryContents, extraItemContents);

		BukkitUtil.openInventory(player, inventory);
	}

	public static void fillInventoryWithContents(Inventory inventory, ItemStack[] armorContents, ItemStack[] inventoryContents, ItemStack[] extraItemsContents) {
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

		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			inventory.setItem(8, extraItemsContents[0]);
		}
	}

	public static void saveKit(Player player, KitRuleSet kitRuleSet) {
		KitHelper.saveKit(player, kitRuleSet, player.getUniqueId().toString(), kitRuleSet.getName());
	}

	public static void saveKit(Player player, KitRuleSet kitRuleSet, int customKitNumber) {
		KitHelper.saveKit(player, kitRuleSet, player.getUniqueId().toString() + "-" + customKitNumber, kitRuleSet.getName());
	}

	private static void saveKit(final Player player, KitRuleSet kitRuleSet, final String kitname, final String tag) {
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

			byte[] temp = null;

			// Compress extra item data
			if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
				itemsList.clear();
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(bos);

				itemsList.add(player.getInventory().getItemInOffHand());

				List<Map<String, Object>> extraItems = CompressionUtil.serializeItemList(itemsList);
				oos.writeObject(extraItems);
				oos.flush();
				oos.close();
				bos.close();
				temp = bos.toByteArray();
			}

			// Cuz final
			final byte[] extraItemData = temp;

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
            final Map<Kit, List<ItemStack[]>> inventories = KitHelper.inventories.get(player.getUniqueId());
			Kit kit = new Kit(kitname, tag);
			List<ItemStack[]> itemStacks = new ArrayList<>();
			itemStacks.add(player.getInventory().getArmorContents());
			itemStacks.add(player.getInventory().getContents());

			ItemStack[] extraItemsArray = new ItemStack[1];
			if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
				extraItemsArray[0] = player.getInventory().getItemInOffHand();
			}

			itemStacks.add(extraItemsArray);
            inventories.put(kit, itemStacks);

            Gberry.log("KIT", "Storing kit " + kit.toString() + " in cache for " + player.getUniqueId());

			BukkitUtil.runTaskAsync(new Runnable() {
                public void run() {
                    Connection con = null;
                    PreparedStatement ps = null;

                    try {
                        con = Gberry.getConnection();
						String query;

						if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
							query = "UPDATE kits" + PotPvP.getInstance().getDBExtra() + " SET owner = ?, kitname = ?, items = ?, extra_items = ?, armor = ?, tag = ? WHERE tag = ? AND kitname = ?;\n";
							query += "INSERT INTO kits" + PotPvP.getInstance().getDBExtra() + " (owner, kitname, items, extra_items, armor, tag) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
											 "(SELECT 1 FROM kits" + PotPvP.getInstance().getDBExtra() + " WHERE tag = ? AND kitname = ?);";
						} else {
							query = "UPDATE kits" + PotPvP.getInstance().getDBExtra() + " SET owner = ?, kitname = ?, items = ?, armor = ?, tag = ? WHERE tag = ? AND kitname = ?;\n";
							query += "INSERT INTO kits" + PotPvP.getInstance().getDBExtra() + " (owner, kitname, items, armor, tag) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
											 "(SELECT 1 FROM kits" + PotPvP.getInstance().getDBExtra() + " WHERE tag = ? AND kitname = ?);";
						}

						int i = 1;
                        ps = con.prepareStatement(query);
                        ps.setString(i++, player.getUniqueId().toString());
                        ps.setString(i++, kitname);
                        ps.setObject(i++, inventoryData);

	                    if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
							ps.setObject(i++, extraItemData);
						}

                        ps.setObject(i++, armorData);
                        ps.setString(i++, tag);
                        ps.setString(i++, tag);
                        ps.setString(i++, kitname);
                        ps.setString(i++, player.getUniqueId().toString());
                        ps.setString(i++, kitname);
                        ps.setObject(i++, inventoryData);

	                    if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
							ps.setObject(i++, extraItemData);
						}

                        ps.setObject(i++, armorData);
                        ps.setString(i++, tag);
                        ps.setString(i++, tag);
                        ps.setString(i, kitname);

                        Gberry.executeUpdate(con, ps);

                        player.sendMessage(ChatColor.GREEN + "Successfully saved " + tag + " kit");
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

	public static boolean loadKits(Group group, KitRuleSet kitRuleSet) { // TODO: THIS WOULDN'T WORK FOR CUSTOM KITS, SO WTF DO WE USE FOR CUSTOM KITS?
		for (Player pl : group.players()) {                               // TODO: WE DUPLICATE THIS LIKE THE LOADKIT() SHIT WITH NEW KITNUMBER PARAM
			KitHelper.loadKit(pl, kitRuleSet);
		}

		return true;
	}

	public static void loadKit(Player player, KitRuleSet kitRuleSet) {
		KitHelper.loadKit(player, player.getUniqueId().toString(), kitRuleSet.getName());
		kitRuleSet.applyKnockbackToPlayer(player); // Apply KB each time a kit is loaded
	}

	public static void loadKit(Player player, KitRuleSet kitRuleSet, int customKitNumber) {
		KitHelper.loadKit(player, player.getUniqueId() + "-" + customKitNumber, kitRuleSet.getName());
		kitRuleSet.applyKnockbackToPlayer(player); // Apply KB each time a kit is loaded
	}

	private static void loadKit(final Player player, final String kitName, final String tag) {
        Gberry.log("KIT2", "Loading kit for " + player.getName());

		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(tag);
		Set<PotionEffect> potionEffects = kitRuleSet.getPotionEffects();
        Map<Kit, List<ItemStack[]>> inventories = KitHelper.inventories.get(player.getUniqueId());
		Kit kit = new Kit(kitName, tag);
		List<ItemStack[]> itemStacks = inventories.get(kit);

        if (itemStacks != null) {
	        player.getInventory().setContents(itemStacks.get(1));
	        player.getInventory().setArmorContents(itemStacks.get(0));

			if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
				player.getInventory().setItemInOffHand(itemStacks.get(2)[0]);
			}

			for (PotionEffect potionEffect : potionEffects) {
				player.addPotionEffect(potionEffect);
			}

			player.updateInventory();
			Gberry.log("KIT", "Loaded kit " + kit.toString() + " for " + player.getUniqueId().toString());
			player.sendMessage(ChatColor.GREEN + "Successfully loaded " + tag + " kit");
		} else {
	        if (kitName.equals(player.getUniqueId().toString())) {
		        player.sendMessage(ChatColor.RED + "No kit found, loading default " + tag + " kit");
		        KitHelper.loadDefaultKit(player, kitRuleSet, false);
	        } else {
		        // No custom kit found
		        player.sendMessage(ChatColor.RED + "No kit found to load.");

		        // Clear their inventory because they might have lobby items
		        player.getInventory().clear();
		        player.getInventory().setArmorContents(new ItemStack[4]);
	        }
        }

		// Call KitLoadEvent
		PotPvP.getInstance().getServer().getPluginManager().callEvent(new KitLoadEvent(player, kitRuleSet));
	}

	public static List<ItemStack[]> getKit(Player player, KitRuleSet kitRuleSet, int customKitNumber) {
		return KitHelper.getKit(player, player.getUniqueId() + "-" + customKitNumber, kitRuleSet.getName());
	}

	public static List<ItemStack[]> getKit(final Player player, final String kitName, final String tag) {
		Map<Kit, List<ItemStack[]>> inventories = KitHelper.inventories.get(player.getUniqueId());
		Kit kit = new Kit(kitName, tag);

		return inventories.get(kit);
	}

	/**
	 * Needs to be called ASYNC
	 */
	public static Map<Kit, List<ItemStack[]>> getAllKitContents(Connection connection, UUID uuid) {
		Map<Kit, List<ItemStack[]>> kits = new HashMap<>();

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			String query = "SELECT * FROM kits" + PotPvP.getInstance().getDBExtra() + " WHERE owner = ?;";
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				// Items
				final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));

				final ByteArrayInputStream baisItemsExtra;
				if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
					baisItemsExtra = new ByteArrayInputStream(rs.getBytes("extra_items"));
				} else {
					baisItemsExtra = null;
				}

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

					// Extra Items
					if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
						ins = new ObjectInputStream(baisItemsExtra);
						list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
						extraItems = new ItemStack[list.size()];
						for (int i = 0; i < extraItems.length; i++) {
							if (list.get(i) == null) {
								extraItems[i] = null;
							} else {
								extraItems[i] = (ItemStack) list.get(i);
							}
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

					List<ItemStack[]> kitContents = new LinkedList<>();
					kitContents.add(armor);
					kitContents.add(items);

					// Do this for 1.7 too because if it's empty it makes no difference,
					// avoids having 1.9 checks in a billion places for kits stuff
					kitContents.add(extraItems);

					Kit kit = new Kit(rs.getString("kitname"), rs.getString("tag"));
					kits.put(kit, kitContents);
				} catch (StreamCorruptedException e) {
					// Corrupt kit
					query = "DELETE FROM kits" + PotPvP.getInstance().getDBExtra() + " WHERE kitname = ? AND tag = ?;";
					ps.close();

					ps = connection.prepareStatement(query);
					ps.setString(1, rs.getString("kitname"));
					ps.setString(2, rs.getString("tag"));

					Gberry.executeUpdate(connection, ps);
				} catch (IOException | ClassNotFoundException e) {
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps);
		}

		return kits;
	}

	public static List<Integer> getSavedCustomKitNumbers(Player player, String tag) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = Gberry.getConnection();
			String query = "SELECT kitname FROM kits" + PotPvP.getInstance().getDBExtra() + " WHERE owner = ? AND tag = ?;";
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

	public static void loadDefaultKit(Player player, KitRuleSet kitRuleSet, boolean message) {
		player.getInventory().setContents(kitRuleSet.getDefaultInventoryKit());
		player.getInventory().setArmorContents(kitRuleSet.getDefaultArmorKit());

		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			player.getInventory().setItemInOffHand(kitRuleSet.getDefaultExtraItem()[0]);
		}

		for (PotionEffect potionEffect : kitRuleSet.getPotionEffects()) {
			player.addPotionEffect(potionEffect);
		}

		player.updateInventory();

		if (message) {
			player.sendMessage(ChatColor.GREEN + "Loaded default " + kitRuleSet.getName() + " kit.");
		}
	}

	public static void fillMatchMakingQueueInventory(Inventory inventory, Ladder.LadderType ladderType) {
		for (Ladder ladder : Ladder.getLadderMap(ladderType).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: 0");
			itemLore.add(ChatColor.BLUE + "In queue: 0");
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			inventory.addItem(item);
		}
	}

	public static class Kit {

		private String kitName;
		private String tag;

		public Kit(String kitName, String tag) {
			this.kitName = kitName;
			this.tag = tag;
		}

		public String getKitName() {
			return kitName;
		}

		public String getTag() {
			return tag;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof Kit) {
				return ((Kit) obj).getKitName().equals(this.kitName) && ((Kit) obj).getTag().equals(this.tag);
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash * 31 + this.kitName.hashCode();
			hash = hash * 31 + this.tag.hashCode();
			return hash;
		}

		@Override
		public String toString() {
			return this.kitName + " " + this.tag;
		}
	}

}