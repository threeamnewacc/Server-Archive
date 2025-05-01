package net.badlion.smellyshops;

import net.badlion.archmoney.ArchMoney;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.smellyshops.commands.*;
import net.badlion.smellyshops.listeners.PlayerListener;
import net.badlion.smellyshops.listeners.SellAllInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmellyShops extends JavaPlugin {

	private static final double PRICE_MULTIPLIER = 1.00;

    private static SmellyShops plugin;

    private ArchMoney archMoney;

	private HashMap<Location, Shop> shops = new HashMap<>();
	private HashMap<Location, ItemShop> itemShops = new HashMap<>();
	private HashMap<Location, RepairShop> repairShops = new HashMap<>();

	private HashMap<String, ShopInfo> createShopAuthorization = new HashMap<>();
	private HashMap<String, ItemShopInfo> createItemShopAuthorization = new HashMap<>();
	private HashMap<String, RepairShopInfo> createRepairShopAuthorization = new HashMap<>();
	private ArrayList<String> removeShopAuthorization = new ArrayList<>();
	private ArrayList<String> removeItemShopAuthorization = new ArrayList<>();
	private ArrayList<String> removeRepairShopAuthorization = new ArrayList<>();

    @Override
    public void onEnable() {
	    SmellyShops.plugin = this;

	    this.archMoney = (ArchMoney) this.getServer().getPluginManager().getPlugin("ArchMoney");

        this.loadShops();
        this.loadItemShops();
	    this.loadRepairShops();

	    //this.getServer().getPluginCommand("sellall").setExecutor(new SellAllCommand(this));
        this.getServer().getPluginCommand("createshop").setExecutor(new CreateShopCommand());
        this.getServer().getPluginCommand("createitemshop").setExecutor(new CreateItemShopCommand());
	    this.getServer().getPluginCommand("createrepairshop").setExecutor(new CreateRepairShopCommand());
	    this.getServer().getPluginCommand("removeshop").setExecutor(new RemoveShopCommand());
	    this.getServer().getPluginCommand("removeitemshop").setExecutor(new RemoveItemShopCommand());
	    this.getServer().getPluginCommand("removerepairshop").setExecutor(new RemoveRepairShopCommand());

	    this.getServer().getPluginManager().registerEvents(new SellAllInventoryListener(), this);
	    this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {

    }

	public double getDiscount(Player player) {
		/*if (player.hasPermission("GFactions.emperor")) {
			return 0.95D;
		}*/

		return 1D;
	}

	public void createShopSign(Sign sign, final ItemStack item, final String itemDescription, final int amount, final int price, final String buy) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Setup sign
		String color = itemDescription.substring(0, 2).replace("&", "§");

		String line1 = "";
		String line2 = "";

		// @ parsing
		if (itemDescription.contains("@")) {
			String[] lines = itemDescription.split("@");
			line1 = lines[0].substring(2, lines[0].length());
			line2 = lines[1];
		} else {
			if (itemDescription.length() > 15) {
				line1 = itemDescription.substring(2, 15);
				line2 = itemDescription.substring(15);
			} else {
				line1 = itemDescription.substring(2);
			}
		}

		sign.setLine(0, color + line1);
		sign.setLine(1, color + line2);
		sign.setLine(2, ChatColor.DARK_BLUE + "Quantity " + ChatColor.WHITE + amount);

		if (buy.equals("buy")) {
			sign.setLine(3, ChatColor.DARK_BLUE + "Buy " + ChatColor.WHITE + (int) (price * SmellyShops.PRICE_MULTIPLIER));
		} else {
			sign.setLine(3, ChatColor.DARK_BLUE + "Sell " + ChatColor.WHITE +  price);
		}

		sign.update();

		// Cache shop
		this.shops.put(sign.getLocation(),
				new Shop(sign.getLocation(), item, amount, (int) (price * SmellyShops.PRICE_MULTIPLIER), buy));

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "INSERT INTO " + GFactions.PREFIX + "_shops (x, y, z, item, item_description, amount, price, buy, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setInt(1, x);
					ps.setInt(2, y);
					ps.setInt(3, z);

					// Item
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);

					// Re-organize the data
					Map<String, Object> data = SmellyShops.this.serialize(item);
					oos.writeObject(data);

					// Close
					oos.flush();
					oos.close();
					bos.close();

					ps.setObject(4, bos.toByteArray());

					ps.setString(5, itemDescription);
					ps.setInt(6, amount);
					ps.setInt(7, price);
					ps.setString(8, buy);
					ps.setString(9, world);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void removeShopSign(Sign sign) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Remove shop from cache
		this.shops.remove(sign.getLocation());

		// Make sign blank
		sign.setLine(0, null);
		sign.setLine(1, null);
		sign.setLine(2, null);
		sign.setLine(3, null);
		sign.update();

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "DELETE FROM " + GFactions.PREFIX + "_shops WHERE world = ? AND x = ? AND y = ? AND z = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, world);
					ps.setInt(2, x);
					ps.setInt(3, y);
					ps.setInt(4, z);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void loadShops() {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM " + GFactions.PREFIX + "_shops;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						try {
							// Attempt to deserialize the information that we stored in the database
							ByteArrayInputStream bais = new ByteArrayInputStream(rs.getBytes("item"));
							ObjectInputStream ins = new ObjectInputStream(bais);
							ConfigurationSerializable cs = SmellyShops.this.deserialize((Map<String, Object>) ins.readObject());

							final String world = rs.getString("world");
							final int x = rs.getInt("x");
							final int y = rs.getInt("y");
							final int z = rs.getInt("z");

							final ItemStack item = (ItemStack) cs;
							final String itemDescription = rs.getString("item_description");
							final int amount = rs.getInt("amount");
							final int price = rs.getInt("price");
							final String buy = rs.getString("buy");

							SmellyShops.this.getServer().getScheduler().runTask(SmellyShops.this, new Runnable() {
								@Override
								public void run() {
									Location loc = new Location(SmellyShops.this.getServer().getWorld(world), x, y, z);

									// This sign isn't here anymore
									if (!(loc.getBlock().getState() instanceof Sign)) {
										Bukkit.getLogger().info("Invalid sign " + loc.toString());
										return;
									}

									SmellyShops.this.shops.put(loc,
											new Shop(loc, item, amount, (int) (price * SmellyShops.PRICE_MULTIPLIER), buy));

									// Create the sign
									Sign sign = (Sign) loc.getBlock().getState();

									String color = itemDescription.substring(0, 2).replace("&", "§");

									String line1 = "";
									String line2 = "";

									// @ parsing
									if (itemDescription.contains("@")) {
										String[] lines = itemDescription.split("@");
										line1 = lines[0].substring(2, lines[0].length());
										line2 = lines[1];
									} else {
										if (itemDescription.length() > 15) {
											line1 = itemDescription.substring(2, 15);
											line2 = itemDescription.substring(15);
										} else {
											line1 = itemDescription.substring(2);
										}
									}

									sign.setLine(0, color + line1);
									sign.setLine(1, color + line2);
									sign.setLine(2, ChatColor.DARK_BLUE + "Quantity " + ChatColor.WHITE + amount);

									if (buy.equals("buy")) {
										sign.setLine(3, ChatColor.DARK_BLUE + "Buy " + ChatColor.WHITE + (int) (price * SmellyShops.PRICE_MULTIPLIER));
									} else {
										sign.setLine(3, ChatColor.DARK_BLUE + "Sell " + ChatColor.WHITE + price);
									}

									sign.update();
								}
							});
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void createItemShopSign(Sign sign, final ItemStack item, final String itemDescription, final int amount, final int price,
	                               final Map<ItemStack, Integer> itemPrice) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Setup sign
		String color = itemDescription.substring(0, 2).replace("&", "§");

		String line1 = "";
		String line2 = "";

		// @ parsing
		if (itemDescription.contains("@")) {
			String[] lines = itemDescription.split("@");
			line1 = lines[0].substring(2, lines[0].length());
			line2 = lines[1];
		} else {
			if (itemDescription.length() > 15) {
				line1 = itemDescription.substring(2, 15);
				line2 = itemDescription.substring(15);
			} else {
				line1 = itemDescription.substring(2);
			}
		}

		sign.setLine(0, color + line1);
		sign.setLine(1, color + line2);
		sign.setLine(2, ChatColor.DARK_BLUE + "Quantity " + ChatColor.WHITE + amount);
		sign.setLine(3, ChatColor.DARK_BLUE + "Buy " + ChatColor.WHITE + (int) (price * SmellyShops.PRICE_MULTIPLIER) + ChatColor.DARK_BLUE + " + items");
		sign.update();

		// Cache item shop
		this.itemShops.put(sign.getLocation(),
				new ItemShop(sign.getLocation(), item, amount, (int) (price * SmellyShops.PRICE_MULTIPLIER), itemPrice));

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "INSERT INTO " + GFactions.PREFIX + "_item_shops (x, y, z, item, item_description, amount, price, item_price, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setInt(1, x);
					ps.setInt(2, y);
					ps.setInt(3, z);

					// Item
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);

					// Re-organize the data
					Map<String, Object> data = SmellyShops.this.serialize(item);
					oos.writeObject(data);

					// Close
					oos.flush();
					oos.close();
					bos.close();

					ps.setObject(4, bos.toByteArray());

					ps.setString(5, itemDescription);
					ps.setInt(6, amount);
					ps.setInt(7, price);
					ps.setString(8, SmellyShops.this.serializeItemPriceMap(itemPrice));
					ps.setString(9, world);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void removeItemShopSign(Sign sign) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Remove shop from cache
		this.itemShops.remove(sign.getLocation());

		// Make sign blank
		sign.setLine(0, null);
		sign.setLine(1, null);
		sign.setLine(2, null);
		sign.setLine(3, null);
		sign.update();

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "DELETE FROM " + GFactions.PREFIX + "_item_shops WHERE world = ? AND x = ? AND y = ? AND z = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, world);
					ps.setInt(2, x);
					ps.setInt(3, y);
					ps.setInt(4, z);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void loadItemShops() {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM " + GFactions.PREFIX + "_item_shops;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						try {
							// Attempt to deserialize the information that we stored in the database
							ByteArrayInputStream bais = new ByteArrayInputStream(rs.getBytes("item"));
							ObjectInputStream ins = new ObjectInputStream(bais);
							ConfigurationSerializable cs = SmellyShops.this.deserialize((Map<String, Object>) ins.readObject());

							final String world = rs.getString("world");
							final int x = rs.getInt("x");
							final int y = rs.getInt("y");
							final int z = rs.getInt("z");

							final ItemStack item = (ItemStack) cs;
							final String itemDescription = rs.getString("item_description");
							final int amount = rs.getInt("amount");
							final int price = rs.getInt("price");
							final String itemPriceSerialized = rs.getString("item_price");

							// Items
							final Map<ItemStack, Integer> itemPrice = new HashMap<>();
							if (itemPriceSerialized.length() > 0) {
								String[] itemsOwed = itemPriceSerialized.split(",");
								for (String serializedItem : itemsOwed) {
									String[] itemComponents = serializedItem.split(":");
									ItemStack item2 = new ItemStack(Material.valueOf(itemComponents[0]), 1, Short.valueOf(itemComponents[1]));
									itemPrice.put(item2, Integer.valueOf(itemComponents[2]));
								}
							}

							SmellyShops.this.getServer().getScheduler().runTask(SmellyShops.this, new Runnable() {
								@Override
								public void run() {
									Location loc = new Location(SmellyShops.this.getServer().getWorld(world), x, y, z);

									// This sign isn't here anymore
									if (!(loc.getBlock().getState() instanceof Sign)) {
										Bukkit.getLogger().info("Invalid sign " + loc.toString());
										return;
									}

									SmellyShops.this.itemShops.put(loc,
											new ItemShop(loc, item, amount, (int) (price * SmellyShops.PRICE_MULTIPLIER), itemPrice));

									// Create the sign
									Sign sign = (Sign) loc.getBlock().getState();

									String color = itemDescription.substring(0, 2).replace("&", "§");

									String line1 = "";
									String line2 = "";

									// @ parsing
									if (itemDescription.contains("@")) {
										String[] lines = itemDescription.split("@");
										line1 = lines[0].substring(2, lines[0].length());
										line2 = lines[1];
									} else {
										if (itemDescription.length() > 15) {
											line1 = itemDescription.substring(2, 15);
											line2 = itemDescription.substring(15);
										} else {
											line1 = itemDescription.substring(2);
										}
									}

									sign.setLine(0, color + line1);
									sign.setLine(1, color + line2);
									sign.setLine(2, ChatColor.DARK_BLUE + "Quantity " + ChatColor.WHITE + amount);
									sign.setLine(3, ChatColor.DARK_BLUE + "Buy " + ChatColor.WHITE + (int) (price * SmellyShops.PRICE_MULTIPLIER) + ChatColor.DARK_BLUE + " + items");
									sign.update();
								}
							});
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void createRepairShopSign(Sign sign, final int durability, final int price) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Setup sign
		sign.setLine(0, "[" + ChatColor.AQUA + "Repair" + ChatColor.RESET + "]");
		sign.setLine(1, ChatColor.DARK_BLUE.toString() + durability + " Durability");
		sign.setLine(2, ChatColor.DARK_BLUE + "Buy " + (int) (price * SmellyShops.PRICE_MULTIPLIER));
		sign.update();

		// Cache shop
		this.repairShops.put(sign.getLocation(),
				new RepairShop(sign.getLocation(), durability, (int) (price * SmellyShops.PRICE_MULTIPLIER)));

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "INSERT INTO " + GFactions.PREFIX + "_repair_shops (x, y, z, durability, price, world) VALUES (?, ?, ?, ?, ?, ?);";
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setInt(1, x);
					ps.setInt(2, y);
					ps.setInt(3, z);

					ps.setInt(4, durability);
					ps.setInt(5, price);
					ps.setString(6, world);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void removeRepairShopSign(Sign sign) {
		final String world = sign.getWorld().getName();
		final int x = sign.getX();
		final int y = sign.getY();
		final int z = sign.getZ();

		// Remove repair shop from cache
		this.repairShops.remove(sign.getLocation());

		// Make sign blank
		sign.setLine(0, null);
		sign.setLine(1, null);
		sign.setLine(2, null);
		sign.setLine(3, null);
		sign.update();

		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "DELETE FROM " + GFactions.PREFIX + "_repair_shops WHERE world = ? AND x = ? AND y = ? AND z = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, world);
					ps.setInt(2, x);
					ps.setInt(3, y);
					ps.setInt(4, z);

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	public void loadRepairShops() {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM " + GFactions.PREFIX + "_repair_shops;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						final String world = rs.getString("world");
						final int x = rs.getInt("x");
						final int y = rs.getInt("y");
						final int z = rs.getInt("z");

						final int durability = rs.getInt("durability");
						final int price = rs.getInt("price");

						SmellyShops.this.getServer().getScheduler().runTask(SmellyShops.this, new Runnable() {
							@Override
							public void run() {
								Location loc = new Location(SmellyShops.this.getServer().getWorld(world), x, y, z);

								// This sign isn't here anymore
								if (!(loc.getBlock().getState() instanceof Sign)) {
									return;
								}

								SmellyShops.this.repairShops.put(loc,
										new RepairShop(loc, durability, (int) (price * SmellyShops.PRICE_MULTIPLIER)));

								// Create the sign
								Sign sign = (Sign) loc.getBlock().getState();

								// Setup sign
								sign.setLine(0, "[" + ChatColor.AQUA + "Repair" + ChatColor.RESET + "]");
								sign.setLine(1, ChatColor.DARK_BLUE.toString() + durability + " Durability");
								sign.setLine(2, ChatColor.DARK_BLUE + "Buy " + (int) (price * SmellyShops.PRICE_MULTIPLIER));
								sign.update();
							}
						});
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
		});
	}

	private String serializeItemPriceMap(Map<ItemStack, Integer> itemPrice) {
		StringBuilder sb = new StringBuilder();
		for (ItemStack item : itemPrice.keySet()) {
			sb.append(item.getType());
			sb.append(":");
			sb.append(item.getDurability());
			sb.append(":");
			sb.append(itemPrice.get(item));
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}

		return "";
	}

    private Map<String, Object> serialize(ConfigurationSerializable cs) {
        Map<String, Object> serialized = this.recreateMap(cs.serialize());
        for (Map.Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                entry.setValue(this.serialize((ConfigurationSerializable) entry.getValue()));
            }
        }
        serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(cs.getClass()));
        return serialized;
    }

	private ConfigurationSerializable deserialize(Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// Check if any of its sub-maps are ConfigurationSerializable. They need to be done first.
			if (entry.getValue() instanceof Map && ((Map) entry.getValue()).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
				entry.setValue(this.deserialize((Map) entry.getValue()));
			}
		}
		return ConfigurationSerialization.deserializeObject(map);
	}

    private Map<String, Object> recreateMap(Map<String, Object> original) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

	public static SmellyShops getInstance() {
		return SmellyShops.plugin;
	}

    public ArchMoney getArchMoney() {
        return archMoney;
    }

    public HashMap<Location, Shop> getShops() {
        return shops;
    }

	public HashMap<Location, ItemShop> getItemShops() {
		return itemShops;
	}

	public HashMap<Location, RepairShop> getRepairShops() {
		return repairShops;
	}

	public HashMap<String, ShopInfo> getCreateShopAuthorization() {
		return createShopAuthorization;
	}

	public HashMap<String, ItemShopInfo> getCreateItemShopAuthorization() {
		return createItemShopAuthorization;
	}

	public HashMap<String, RepairShopInfo> getCreateRepairShopAuthorization() {
		return createRepairShopAuthorization;
	}

	public ArrayList<String> getRemoveShopAuthorization() {
		return removeShopAuthorization;
	}

	public ArrayList<String> getRemoveItemShopAuthorization() {
		return removeItemShopAuthorization;
	}

	public ArrayList<String> getRemoveRepairShopAuthorization() {
		return removeRepairShopAuthorization;
	}

}
