package net.badlion.auction.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.andrepl.chatlib.Text;
import net.badlion.auction.Auction;
import net.badlion.auction.ItemForSale;
import net.badlion.auction.tasks.EndAuctionTask;
import net.badlion.auction.tasks.InsertItemsIntoInventoryTask;
import net.badlion.auction.tasks.WarningTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

public class AuctionCommand implements CommandExecutor {
	
	private Auction plugin;
	private ArrayList<Player> playersWhoWantAuctionMessages;
	private String prefix;
	private String bidIncreaseMessage;
	private String itemEnchantmentMessage;
	private String startBidPriceMessage1;
	private String startBidPriceMessage2;
	private String startAuctionMessage;
	private String potionMessage;
	private String monsterEggMessage;
	private String mobSpawner;
	private String anvilMessage;
	private String durabilityMessage;
	
	private String helpMessage1;
	private String helpMessage2;
	private String helpMessage3;
	private String helpMessage4;
	private String helpMessage5;
	private String helpMessage6;
	private String helpMessage7;
	
	public AuctionCommand(Auction plugin) {
		this.plugin = plugin;
		this.playersWhoWantAuctionMessages = new ArrayList<Player>();
		this.prefix = ChatColor.YELLOW + "[Auction] " + ChatColor.BLUE;
		this.bidIncreaseMessage = this.prefix + "Bid has been raised to " + ChatColor.YELLOW + "$";
		this.itemEnchantmentMessage = this.prefix + "Item has enchantment: " + ChatColor.GREEN;
		this.startBidPriceMessage1 = this.prefix + "Bidding starts at " + ChatColor.YELLOW;
		this.startBidPriceMessage2 = ChatColor.BLUE + " and minimum bid increment is " + ChatColor.YELLOW;
		this.startAuctionMessage = this.prefix + ChatColor.LIGHT_PURPLE + "Auction begining! 1 minute remaining! Hide auction messages with /auction off";
		this.potionMessage = this.prefix + "Item has the following effect: " + ChatColor.GREEN;
		this.monsterEggMessage = this.prefix + "Monster Egg is of the following type: " + ChatColor.GREEN;
		this.mobSpawner = this.prefix + "Mob Spawner is of the following type: " + ChatColor.GREEN;
		this.anvilMessage = this.prefix + "Anvil's condition is currently: " + ChatColor.GREEN;
		this.durabilityMessage = this.prefix + "Item has the following durability: " + ChatColor.GREEN;
		
		this.helpMessage1 = this.prefix + "Help instructions for using the Auction House.";
		this.helpMessage2 = this.prefix + "/auction start <amount> <price> <increment> - Start an auction with <amount> of item in hand";
		this.helpMessage3 = this.prefix + "with starting <price> and required minimum bid of <increment>";
		this.helpMessage4 = this.prefix + "**Increment must be greater than or equal to the starting price.";
		this.helpMessage5 = this.prefix + "/auction bid <amount> OR /bid <amount> - Bid on the current auction.";
		this.helpMessage6 = this.prefix + "/auction info - Get info on the current auction.";
		this.helpMessage7 = this.prefix + "/auction on|off - Turn on/off the auction messages. Default is off.";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] tmpArgs) {
		final String [] args;
		if (label.equals("bid")) {
			args = new String[tmpArgs.length + 1];
			args[0] = "bid";
			int i = 1;
			for (String s : tmpArgs) {
				args[i++] = s;
			}
		} else {
			args = tmpArgs;
		}

		if (sender instanceof Player) {
			final Player player = (Player) sender;
			
			if (args.length < 1) {
				this.auctionHelp(player);
				return true;
			}
			
			// DEBUG
			if (player.isOp() && args[0].equals("store")) {
				final ItemStack item = player.getItemInHand();
				player.setItemInHand(null);
				
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					
					@Override
					public void run() {
						plugin.insertHeldAuctionItem(player.getUniqueId().toString(), item);
					}
					
				});
				return true;
			} else if (player.isOp() && args[0].equals("withdraw")) {
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					
					@Override
					public void run() {
						Map<String, Object> data = plugin.getHeldAuctionItems(player.getUniqueId().toString());
						@SuppressWarnings("unchecked")
						ArrayList<ItemStack> items = (ArrayList<ItemStack>) data.get("items");
						for (ItemStack item : items) {
							player.getInventory().addItem(item);
						}
					}
					
				});
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
				this.auctionHelp(player);
			} else if (args[0].equals("start") || args[0].equals("sell")) {
				if (args.length < 4) {
					this.auctionHelp(player);
				} else {
					if (this.plugin.isAllowAuctions()) {
						this.handleStartAuction(args, player);
					} else {
						player.sendMessage(ChatColor.RED + "Cannot put items up for auction at this time.");
					}
				}
			} else if (args[0].equals("bid")) {
				if (args.length < 2) {
					this.auctionHelp(player);
				} else {
					this.handleBid(args, player);
				}
			} else if (args[0].equals("info")) {
				ItemForSale item = this.plugin.getItemUpForSale();
				if (item != null) {
					this.sendInfoMessage(item, player);
				}
			} /*else if (args[0].equals("end")) {
				
			}*/ else if (args[0].equals("cancel")) {
				this.cancelAuction(player);
			} else if (args[0].equals("on")) {
				if (this.playersWhoWantAuctionMessages.contains(player)) {
					player.sendMessage(ChatColor.RED + "You already have auction messages enabled.");
				} else {
					this.playersWhoWantAuctionMessages.add(player);
					player.sendMessage(ChatColor.GREEN + "Auction messages enabled.");
					
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
						
						@Override
						public void run() {
							plugin.deletePlayerAlerts(player.getUniqueId().toString());
						}
						
					});
				}
			} else if (args[0].equals("off")) {
				this.playersWhoWantAuctionMessages.remove(player);
				player.sendMessage(ChatColor.GREEN + "Auction messages disabled.");
				
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					
					@Override
					public void run() {
						plugin.turnOnPlayerAlerts(player.getUniqueId().toString());
					}
				});
			} else if (args[0].equals("claim")) {
				this.claimItems(player);
			} else {
				this.auctionHelp(player);
			}
		}

		return true;
	}
	
	private void auctionHelp(Player player) {
		player.sendMessage(this.helpMessage1);
		player.sendMessage(this.helpMessage2);
		player.sendMessage(this.helpMessage3);
		player.sendMessage(this.helpMessage4);
		player.sendMessage(this.helpMessage5);
		player.sendMessage(this.helpMessage6);
		player.sendMessage(this.helpMessage7);
	}
	
	private boolean handleStartAuction(String [] args, Player player) {
		// Permission check
		if (!player.hasPermission("GFactions.kit.member")) {
			player.sendMessage(ChatColor.RED + "You must be at least a villager to start an auction. Use /register [email] and verify your email to become a villager.");
		}

		// Ok we have the right parameters, get them
		int amount = 0, price = 0, inc = 0;
		try {
			amount = Integer.parseInt(args[1]);
			price = Integer.parseInt(args[2]);
			inc = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Invalid parameters given to /auction start. This command only takes numbers.");
			return false;
		}

		if (price > 10000000) {
			player.sendMessage(ChatColor.RED + "Price is too high");
			return false;
		}

		if (amount <= 0 || price <= 0 || inc <= 0) {
			player.sendMessage(ChatColor.RED + "Cannot give values of zero or less");
			return false;
		}
		
		if (inc < (double) price * 0.05) { // 5%
			player.sendMessage(ChatColor.RED + "Cannot give such a small increment value.  Minimum of $" + (double) price * 0.05);
			return false;
		} else if (inc > price) {
			player.sendMessage(ChatColor.RED + "Cannot give an increment value larger than $" + price);
			return false;
		}
		
		ItemStack item = player.getItemInHand();

		if (item == null) {
			player.sendMessage(ChatColor.RED + "You do not have an item in your hand.");
			return false;
		}

		if (item.getAmount() < amount) {
			player.sendMessage(ChatColor.RED + "Cannot auction off this amount. You do not have enough.");
			return false;
		}
		
		if (this.plugin.getItemsUpForAuction().size() >= 15) {
			player.sendMessage(ChatColor.RED + "There are already 15 items up for auction. Try again later.");
			return false;
		}
		
		if (this.plugin.getCombatTagApi().isInCombat(player)) {
			player.sendMessage(ChatColor.RED + "Cannot put an item up for auction when you are in combat.");
			return false;
		}

		// Stop the scamming
		ItemMeta meta = item.getItemMeta();
		if (meta != null && meta.hasDisplayName()) {
			if (!meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "God") && item.getType() != Material.MOB_SPAWNER) {
				player.sendMessage(ChatColor.RED + "Cannot sell items that have been renamed.");
				return false;
			}
		}

        // Don't let players sell written books as a way to scam people
        if (item.getType().equals(Material.WRITTEN_BOOK)) {
            player.sendMessage(ChatColor.RED + "Cannot sell written books.");
            return false;
        }
		
		// Get this player's faction
		FPlayer fplayer = FPlayers.i.get(player);
		Faction faction = fplayer.getFaction();
		int numOfItemsUpForSaleByFaction = 0;
		for (ItemForSale ifs : this.plugin.getItemsUpForAuction()) {
			FPlayer fp = FPlayers.i.get(ifs.getPlayer());
			Faction f = fp.getFaction();
			if (faction.getId().equals(f.getId())) {
				numOfItemsUpForSaleByFaction++;
			}
		}
		
		// Limit to 3 items per faction in the AH
		if (numOfItemsUpForSaleByFaction >= 3) {
			player.sendMessage(ChatColor.RED + "Your faction can only have 3 items up for auction at once.");
			return false;
		}
		
		// Take away this number of items from the player
		ItemStack itemCopy = item.clone();
		itemCopy.setAmount(amount);
		item.setAmount(item.getAmount() - amount);
		player.setItemInHand(item);
		
		// Generate the actual Item and put it up for sale
		ItemForSale itemForSale = new ItemForSale(itemCopy, player, price, inc);
		if (this.plugin.getItemUpForSale() == null) {
			this.plugin.setItemUpForSale(itemForSale);
			this.plugin.getPlayersWhoHaveItemsUpForSale().add(player);
		} else {
			this.plugin.getItemsUpForAuction().add(itemForSale);
			this.plugin.getPlayersWhoHaveItemsUpForSale().add(player);
			player.sendMessage(ChatColor.GREEN + "Your item has been put in the queue for selling. Use /auction cancel to pull it out of the queue.");
			return true;
		}
		
		player.sendMessage(ChatColor.GREEN + "Your item has been put up for auction.");
		this.sendInfoMessage(itemForSale);
		new WarningTask(this.plugin, 30).runTaskLater(this.plugin, 30 * 20); // 30 sec later
		new WarningTask(this.plugin, 10).runTaskLater(this.plugin, 50 * 20); // 50 sec later
		new EndAuctionTask(this.plugin).runTaskLater(this.plugin, 60 * 20); // 1 min
		
		return true;
	}
	
	public boolean handleBid(String [] args, Player player) {
		int bid = 0;
		
		try {
			bid = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Please enter a valid bid (number).");
			return false;
		}
		
		ItemForSale item = this.plugin.getItemUpForSale();
		if (item == null) {
			player.sendMessage(ChatColor.RED + "No item currently up for auction.");
			return false;
		}
		
		// You cannot bid on your own item
		if (item.getPlayer().getUniqueId().equals(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "Cannot bid on your own item.");
			return false;
		}
			
		int currentBid = item.getCurrentBid();
		if (currentBid + item.getIncrement() > bid) {
			player.sendMessage(ChatColor.RED + "Did not bid enough. Must bid at least " + (currentBid + item.getIncrement()));
			return false;
		}
		
		// Do they have enough?
		if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) < bid) {
			player.sendMessage(ChatColor.RED + "You do not have enough money to place this bid.");
			return false;
		}
		
		// Are they in the same faction as the seller?
		FPlayer fplayer = FPlayers.i.get(player);
		FPlayer fplayer2 = FPlayers.i.get(item.getPlayer());
		Faction faction = fplayer.getFaction();
		Faction faction2 = fplayer2.getFaction();
		if (!faction.getId().equals("0") && faction.getId().equals(faction2.getId())) {
			player.sendMessage(ChatColor.RED + "You cannot bid on an item sold by a member of your faction.");
			return false;
		}
		
		// Update the highest bid
		item.setCurrentBid(bid);
		item.getBids().add(bid);
		item.getPlayers().add(player);
		item.getTimestamps().add(new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
		
		// Spam those who want to be spammed
		this.sendBidMessage(item);

		player.sendMessage(ChatColor.GREEN + "Your bid has been placed.");
		
		return true;
	}
	
	private void sendBidMessage(ItemForSale item) {
		// Shoot message off to only those who are interested
		String newBidString = this.bidIncreaseMessage + item.getCurrentBid();
		for (Player p : this.playersWhoWantAuctionMessages) {
			p.sendMessage(newBidString);
		}
	}
	
	public String prettyMaterialName(ItemStack item) {
		Material material = item.getType();
		if (material == Material.DIAMOND_BARDING) {
			return "DIAMOND HORSE ARMOR";
		} else if (material == Material.GOLD_BARDING) {
			return "GOLD HORSE ARMOR";
		} else if (material == Material.IRON_BARDING) {
			return "IRON HORSE ARMOR";
		} else if (material == Material.MONSTER_EGGS) {
			return "STONE MONSTER EGG";
		} else if (material == Material.GOLDEN_APPLE && item.getDurability() == 1) {
			return "OPPLE"; 
		} else {
			return material.name().replace('_', ' ');
		}
	}
	
	public void sendInfoMessage(ItemForSale item, Player player) {
		// Build all them strings
		String itemString = this.prefix + item.getPlayer().getName() + ChatColor.BLUE + " is auctioning " + ChatColor.GOLD + item.getItem().getAmount() + ChatColor.AQUA + " [";
		String totalPriceString = this.startBidPriceMessage1 + item.getPrice() + this.startBidPriceMessage2 + item.getIncrement();

		// New way, use ChatLib library and fukkit im done and going home
		if (player == null) {
			// Send to everyone
			Text text = new Text(itemString);
			text.appendItem(item.getItem());
            text.append("§b]"); // Hardcoded ChatColor.AQUA to work
            for (Player p : this.playersWhoWantAuctionMessages) {
				p.sendMessage(this.startAuctionMessage);
                text.send(p);
				p.sendMessage(totalPriceString);
			}
		} else {
			// Send to the person who requested it
			Text text = new Text(itemString);
			text.appendItem(item.getItem());
            text.append("§b]"); // Hardcoded ChatColor.AQUA to work
			player.sendMessage(this.startAuctionMessage);
			text.send(player);
			player.sendMessage(totalPriceString);
		}



		/*String totalPriceString = this.startBidPriceMessage1 + item.getPrice() + this.startBidPriceMessage2 + item.getIncrement();
		ItemStack is = item.getItem();
		
		ArrayList<String> enchantmentStrings = new ArrayList<String>();
		// What if it is a potion?
		if (is.getType() == Material.POTION) {
			Potion potion = Potion.fromItemStack(is);
			enchantmentStrings.add(this.potionMessage + (potion.isSplash() ? "SPLASH " : "") + potion.getType().name().replace('_', ' ') + " LVL " + potion.getLevel());
			item.getEnchantments().put((potion.isSplash() ? "SPLASH " : "") + potion.getType().name().replace('_', ' '), potion.getLevel());
		} else if (is.getType() == Material.MONSTER_EGG) {
			SpawnEgg egg = (SpawnEgg) is.getData();
			enchantmentStrings.add(this.monsterEggMessage + egg.getSpawnedType().name().replace('_', ' '));
			item.getEnchantments().put(egg.getSpawnedType().name().replace('_', ' '), 0);
		} else if (is.getType() == Material.ANVIL) {
			String anvilDmg = "";
			if (is.getData().getData() == 1) {
				anvilDmg = "SLIGHTLY DAMAGED";
			} else if (is.getData().getData() == 2) {
				anvilDmg = "VERY DAMAGED";
			} else {
				anvilDmg = "NOT DAMAGED";
			}
			enchantmentStrings.add(this.anvilMessage + anvilDmg);
			item.getEnchantments().put(anvilDmg, 0);
		} else if (is.getType() == Material.MOB_SPAWNER) {
			if (is.getItemMeta() != null) {
				// Two types of mob spawners to handle
				if (is.getItemMeta().getDisplayName() != null) {
					enchantmentStrings.add(this.mobSpawner + is.getItemMeta().getDisplayName());
				} else if (is.getItemMeta().getLore().size() > 0) {
					enchantmentStrings.add(this.mobSpawner + is.getItemMeta().getLore().get(0));
				} else {
					enchantmentStrings.add(this.mobSpawner + "Pig Spawner");
				}
			} else {
				enchantmentStrings.add(this.mobSpawner + "Pig Spawner");
			}
		} else {
			// If enchanted book we need the "stored" enchantments, not the regular enchantments
			Map<Enchantment, Integer> enchantments = is.getType() == Material.ENCHANTED_BOOK ? 
					((EnchantmentStorageMeta) is.getItemMeta()).getStoredEnchants() : is.getEnchantments();
			Iterator<Entry<Enchantment, Integer>> it = enchantments.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Enchantment, Integer> pair = (Map.Entry<Enchantment, Integer>) it.next();
				enchantmentStrings.add(this.itemEnchantmentMessage + pair.getKey().getName().replace('_', ' ') + " LVL " + Gberry.toRomanNumeral(pair.getValue()));
				item.getEnchantments().put(pair.getKey().getName().replace('_', ' '), pair.getValue());
			}
			
			enchantmentStrings.add(this.durabilityMessage + (is.getType().getMaxDurability() - is.getDurability()) + "/" + is.getType().getMaxDurability());
		}
		
		if (player == null) {
			// Send to everyone
			for (Player p : this.playersWhoWantAuctionMessages) {
				p.sendMessage(this.startAuctionMessage);
				p.sendMessage(itemString);
				for (String s : enchantmentStrings) {
					p.sendMessage(s);
				}
				p.sendMessage(totalPriceString);
			}
		} else {
			// Send to the person who requested it
			player.sendMessage(this.startAuctionMessage);
			player.sendMessage(itemString);
			for (String s : enchantmentStrings) {
				player.sendMessage(s);
			}
			player.sendMessage(totalPriceString);
		}

		// Hax yo
//		IChatBaseComponent[] components = CraftChatMessage.fromString(this.prefix + "The following item is up for auction");
//		IChatBaseComponent[] finalComponents = new IChatBaseComponent[components.length + 1];
//		for (int i = 0; i < components.length; ++i) {
//			finalComponents[i] = components[i];
//		}
//		net.minecraft.server.v1_7_R4.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(is);
//		finalComponents[finalComponents.length - 1 ] = nmsItemStack.E();
//		Player p = Bukkit.getServer().getPlayerExact("MasterGberry");
//		if (((CraftPlayer)p).getHandle().playerConnection == null) return;
//
//		for (IChatBaseComponent component : components) {
//			((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(component));
//		}
		Text text = new Text(this.prefix + "The following item is up for auction ");
		text.appendItem(is);
		Player p = Bukkit.getServer().getPlayerExact("MasterGberry");
		text.send(p); */
	}
	
	public void sendInfoMessage(ItemForSale item) {
		this.sendInfoMessage(item, null);
	}
	
	public void incrementNextItem() {
		// Any items in queue? Next one
		if (this.plugin.getItemsUpForAuction().size() >= 1) {
			ItemForSale nextItem = this.plugin.getItemsUpForAuction().remove();
			nextItem.getPlayer().sendMessage(ChatColor.GREEN + "Your item has been put up for auction.");
			this.plugin.setItemUpForSale(nextItem);
			this.plugin.getAuctionCommand().sendInfoMessage(nextItem);
			new WarningTask(this.plugin, 30).runTaskLater(this.plugin, 30 * 20); // 30 sec later
			new WarningTask(this.plugin, 10).runTaskLater(this.plugin, 50 * 20); // 50 sec later
			new EndAuctionTask(this.plugin).runTaskLater(this.plugin, 60 * 20); // 1 min
		}
	}
	
	public void claimItems(final Player player) {
		// Does this player have room in their inventory?
		int isEmpty = player.getInventory().firstEmpty();
		if (isEmpty == -1) {
			player.sendMessage(ChatColor.RED + "Your inventory is full right now. Claim items when you have room to put them in your inventory.");
			return;
		}
		
		// This is done all async
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				// Get the items from the DB, and then throw them into a task to be handled 1 tick later synchronously.
				Map<String, Object> results = plugin.getHeldAuctionItems(player.getUniqueId().toString());
				
				if (results == null) {
					player.sendMessage(ChatColor.RED + "You have no items to claim in the Auction House.");
					return;
				}
				
				ArrayList<ItemStack> items = (ArrayList<ItemStack>) results.get("items");
				ArrayList<Long> dates = (ArrayList<Long>) results.get("dates");
				plugin.getServer().getScheduler().runTaskLater(plugin, new InsertItemsIntoInventoryTask(plugin, player.getUniqueId(), items, dates), 1);
			}
			
		});
	}
	
	private void cancelAuction(Player player) {
		// Ok lets try to cancel the first item for this player we find in the queue
		Queue<ItemForSale> items = this.plugin.getItemsUpForAuction();
		
		ItemForSale toRemove = null;
		for (ItemForSale item : items) {
			// Found an item
			if (item.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
				toRemove = item;
				break;
			}
		}
		
		// If we found something
		if (toRemove != null) {
			// Clean memory
			items.remove(toRemove);
			
			// Now give them back their item
			int firstEmpty = player.getInventory().firstEmpty();
			if (firstEmpty == -1) {
				// Full inventory, to the database we go
				this.plugin.storeItemInDatabase(player, toRemove.getItem());
				player.sendMessage(ChatColor.GREEN + "You do not have space right now for this item. Use \"/auction claim\" to retrieve your item later.");
			} else {
				player.getInventory().addItem(toRemove.getItem());
				player.sendMessage(ChatColor.GREEN + "You have cancelled the auction for one of your items (whichever was put up for sale first).");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You do not have any items in the auction house to cancel. You cannot cancel an item once it has gone up for auction.");
		}
	}

	public ArrayList<Player> getPlayersWhoWantAuctionMessages() {
		return playersWhoWantAuctionMessages;
	}

	public void setPlayersWhoWantAuctionMessages(
			ArrayList<Player> playersWhoWantAuctionMessages) {
		this.playersWhoWantAuctionMessages = playersWhoWantAuctionMessages;
	}

	public Auction getPlugin() {
		return plugin;
	}

	public void setPlugin(Auction plugin) {
		this.plugin = plugin;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getBidIncreaseMessage() {
		return bidIncreaseMessage;
	}

	public void setBidIncreaseMessage(String bidIncreaseMessage) {
		this.bidIncreaseMessage = bidIncreaseMessage;
	}

	public String getItemEnchantmentMessage() {
		return itemEnchantmentMessage;
	}

	public void setItemEnchantmentMessage(String itemEnchantmentMessage) {
		this.itemEnchantmentMessage = itemEnchantmentMessage;
	}

	public String getStartBidPriceMessage1() {
		return startBidPriceMessage1;
	}

	public void setStartBidPriceMessage1(String startBidPriceMessage1) {
		this.startBidPriceMessage1 = startBidPriceMessage1;
	}

	public String getStartBidPriceMessage2() {
		return startBidPriceMessage2;
	}

	public void setStartBidPriceMessage2(String startBidPriceMessage2) {
		this.startBidPriceMessage2 = startBidPriceMessage2;
	}

	public String getStartAuctionMessage() {
		return startAuctionMessage;
	}

	public void setStartAuctionMessage(String startAuctionMessage) {
		this.startAuctionMessage = startAuctionMessage;
	}

	public String getHelpMessage1() {
		return helpMessage1;
	}

	public void setHelpMessage1(String helpMessage1) {
		this.helpMessage1 = helpMessage1;
	}

	public String getHelpMessage2() {
		return helpMessage2;
	}

	public void setHelpMessage2(String helpMessage2) {
		this.helpMessage2 = helpMessage2;
	}

	public String getHelpMessage3() {
		return helpMessage3;
	}

	public void setHelpMessage3(String helpMessage3) {
		this.helpMessage3 = helpMessage3;
	}

	public String getHelpMessage4() {
		return helpMessage4;
	}

	public void setHelpMessage4(String helpMessage4) {
		this.helpMessage4 = helpMessage4;
	}

	public String getHelpMessage5() {
		return helpMessage5;
	}

	public void setHelpMessage5(String helpMessage5) {
		this.helpMessage5 = helpMessage5;
	}

	public String getHelpMessage6() {
		return helpMessage6;
	}

	public void setHelpMessage6(String helpMessage6) {
		this.helpMessage6 = helpMessage6;
	}

	public String getHelpMessage7() {
		return helpMessage7;
	}

	public void setHelpMessage7(String helpMessage7) {
		this.helpMessage7 = helpMessage7;
	}

}
