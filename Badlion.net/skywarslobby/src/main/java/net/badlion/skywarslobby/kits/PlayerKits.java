package net.badlion.skywarslobby.kits;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.skywarslobby.inventories.KitSelectionInventory;
import net.badlion.skywarslobby.managers.KitCreationManager;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerKits {

	private UUID uuid;

	private boolean loaded = false;

	private SkyWarsKit kitEditing;

	private Set<Integer> unlockedItems = new HashSet<>();

	private Map<KitType, Map<Integer, SkyWarsKit>> kits = new HashMap<>();

	public PlayerKits(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * This is called ASYNC
	 */
	public void fetchKits(Connection connection, KitType kitType) {
		Map<Integer, SkyWarsKit> map = new HashMap<>();
		this.kits.put(kitType, map);

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			String query = "SELECT * FROM mpg_kits WHERE uuid = ? AND gamemode = ? AND type = ? ORDER BY kit_number ASC;";
			ps = connection.prepareStatement(query);

			ps.setString(1, this.uuid.toString());
			ps.setString(2, "SKYWARS");
			ps.setString(3, kitType.toString());

			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				// Items
				final ByteArrayInputStream baisArmor = new ByteArrayInputStream(rs.getBytes("armor"));
				final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));

				ItemStack[] armorContents;
				ItemStack[] inventoryContents;

				// More efficient to handle this ASYNC
				try {
					ObjectInputStream ins = new ObjectInputStream(baisArmor);
					List<ConfigurationSerializable>list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());

					armorContents = new ItemStack[list.size()];
					for (int i = 0; i < armorContents.length; ++i) {
						if (list.get(i) == null) {
							armorContents[i] = null;
						} else {
							armorContents[i] = (ItemStack) list.get(i);
						}
					}

					ins = new ObjectInputStream(baisItems);
					list = CompressionUtil.deserializeItemList((List<Map<String, Object>>) ins.readObject());
					inventoryContents = new ItemStack[list.size()];
					for (int i = 0; i < inventoryContents.length; ++i) {
						if (list.get(i) == null) {
							inventoryContents[i] = null;
						} else {
							inventoryContents[i] = (ItemStack) list.get(i);
						}
					}

					SkyWarsKit kit = new SkyWarsKit(this.uuid, KitType.valueOf(rs.getString("type").toUpperCase()), rs.getInt("kit_number"),
							new ItemStack(Material.values()[rs.getInt("preview_item")]), armorContents, inventoryContents);
					map.put(kit.getKitNumber(), kit);
				} catch (StreamCorruptedException e) {
					// Corrupt kit
					query = "DELETE FROM mpg_kits WHERE kitname = ? AND gamemode = ? AND type = ?;";
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
			Gberry.closeComponents(rs, ps);
		}

		// Make sure they have all the kits (even if empty)
		for (int i = 1; i <= KitSelectionInventory.TOTAL_KITS; i++) {
			if (map.get(i) == null) {
				map.put(i, new SkyWarsKit(this.uuid, kitType, i, new ItemStack(Material.STAINED_GLASS_PANE), null, null));
			}
		}
	}

	/**
	 * This is called ASYNC
	 */
	public void fetchUnlockedKitItems(Connection connection) {
		String query = "SELECT * FROM skywars_unlocked_kit_items WHERE uuid = ?;";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement(query);

			ps.setString(1, PlayerKits.this.uuid.toString());

			rs = Gberry.executeQuery(connection, ps);

			// Load all their particles
			while (rs.next()) {
				String serializedItemIDs = rs.getString(0);

				for (String itemID : serializedItemIDs.split(",")) {
					this.unlockedItems.add(Integer.valueOf(itemID));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps);
		}
	}

	public UUID getUuid() {
		return uuid;
	}


	public SkyWarsKit getKitEditing() {
		return kitEditing;
	}

	public void setKitEditing(SkyWarsKit kitEditing) {
		if (this.kitEditing != null) {
			// Reset old kit's total creating weight
			this.kitEditing.resetTotalCreatingWeight();
		}

		this.kitEditing = kitEditing;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded() {
		this.loaded = true;
	}

	public boolean hasUnlockedItem(ItemStack item) {
		if (true) return true;
		return this.unlockedItems.contains(KitCreationManager.getItemID(item));
	}

	public SkyWarsKit getKit(KitType kitType, int kitNumber) {
		return this.kits.get(kitType).get(kitNumber);
	}

	public Map<Integer, SkyWarsKit> getAllKits(KitType kitType) {
		return this.kits.get(kitType);
	}

}
