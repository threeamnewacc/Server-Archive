package net.badlion.potpvp.ffaworlds;

import net.badlion.gguard.ProtectedRegion;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.FFAState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SGFFAWorld extends FFAWorld implements Listener {

    private List<Block> chestsTier1 = new ArrayList<>();
    private List<Block> chestsTier2 = new ArrayList<>();

    private int chestTier1Count = 0;
    private int chestTier2Count = 0;

    private Map<Block, Inventory> inventoriesTier1 = new HashMap<>();
    private Map<Block, Inventory> inventoriesTier2 = new HashMap<>();

    private Map<UUID, Boolean> hasTakenFallDamage = new HashMap<>();

    private static Random random = new Random();

    private ItemStack[] inventory = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];

    public SGFFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
        super(ffaItem, kitRuleSet);

        // Add Chests
	    World world = PotPvP.getInstance().getServer().getWorld("world");
	    this.chestsTier1.add(world.getBlockAt(103,31,-5248));
	    this.chestsTier1.add(world.getBlockAt(104,29,-5254));
	    this.chestsTier1.add(world.getBlockAt(106,29,-5141));
	    this.chestsTier1.add(world.getBlockAt(107,31,-5234));
	    this.chestsTier1.add(world.getBlockAt(110,29,-5202));
	    this.chestsTier1.add(world.getBlockAt(110,38,-5183));
	    this.chestsTier1.add(world.getBlockAt(110,39,-5198));
	    this.chestsTier1.add(world.getBlockAt(113,30,-5284));
	    this.chestsTier1.add(world.getBlockAt(117,29,-5129));
	    this.chestsTier1.add(world.getBlockAt(119,38,-5205));
	    this.chestsTier1.add(world.getBlockAt(119,39,-5180));
	    this.chestsTier1.add(world.getBlockAt(120,29,-5284));
	    this.chestsTier1.add(world.getBlockAt(122,29,-5154));
	    this.chestsTier1.add(world.getBlockAt(122,43,-5194));
	    this.chestsTier1.add(world.getBlockAt(129,29,-5154));
	    this.chestsTier1.add(world.getBlockAt(133,29,-5268));
	    this.chestsTier1.add(world.getBlockAt(134,30,-5143));
	    this.chestsTier1.add(world.getBlockAt(135,31,-5284));
	    this.chestsTier1.add(world.getBlockAt(136,28,-5125));
	    this.chestsTier1.add(world.getBlockAt(136,30,-5190));
	    this.chestsTier1.add(world.getBlockAt(138,29,-5211));
	    this.chestsTier1.add(world.getBlockAt(138,38,-5142));
	    this.chestsTier1.add(world.getBlockAt(144,28,-5158));
	    this.chestsTier1.add(world.getBlockAt(144,40,-5159));
	    this.chestsTier1.add(world.getBlockAt(145,29,-5204));
	    this.chestsTier1.add(world.getBlockAt(147,29,-5180));
	    this.chestsTier1.add(world.getBlockAt(150,31,-5101));
	    this.chestsTier1.add(world.getBlockAt(152,31,-5227));
	    this.chestsTier1.add(world.getBlockAt(154,29,-5128));
	    this.chestsTier1.add(world.getBlockAt(154,49,-5128));
	    this.chestsTier1.add(world.getBlockAt(162,29,-5247));
	    this.chestsTier1.add(world.getBlockAt(164,29,-5153));
	    this.chestsTier1.add(world.getBlockAt(166,73,-5177));
	    this.chestsTier1.add(world.getBlockAt(169,31,-5104));
	    this.chestsTier1.add(world.getBlockAt(170,27,-5211));
	    this.chestsTier1.add(world.getBlockAt(170,29,-5177));
	    this.chestsTier1.add(world.getBlockAt(170,29,-5135));
	    this.chestsTier1.add(world.getBlockAt(172,44,-5134));
	    this.chestsTier1.add(world.getBlockAt(179,29,-5166));
	    this.chestsTier1.add(world.getBlockAt(180,29,-5149));
	    this.chestsTier1.add(world.getBlockAt(185,29,-5203));
	    this.chestsTier1.add(world.getBlockAt(186,30,-5248));
	    this.chestsTier1.add(world.getBlockAt(191,29,-5119));
	    this.chestsTier1.add(world.getBlockAt(194,29,-5150));
	    this.chestsTier1.add(world.getBlockAt(194,31,-5238));
	    this.chestsTier1.add(world.getBlockAt(195,29,-5265));
	    this.chestsTier1.add(world.getBlockAt(195,37,-5122));
	    this.chestsTier1.add(world.getBlockAt(196,30,-5282));
	    this.chestsTier1.add(world.getBlockAt(197,29,-5178));
	    this.chestsTier1.add(world.getBlockAt(204,29,-5132));
	    this.chestsTier1.add(world.getBlockAt(205,37,-5123));
	    this.chestsTier1.add(world.getBlockAt(206,51,-5131));
	    this.chestsTier1.add(world.getBlockAt(208,51,-5111));
	    this.chestsTier1.add(world.getBlockAt(210,29,-5116));
	    this.chestsTier1.add(world.getBlockAt(213,29,-5114));
	    this.chestsTier1.add(world.getBlockAt(216,29,-5128));
	    this.chestsTier1.add(world.getBlockAt(216,32,-5186));
	    this.chestsTier1.add(world.getBlockAt(217,29,-5274));
	    this.chestsTier1.add(world.getBlockAt(220,30,-5203));
	    this.chestsTier1.add(world.getBlockAt(221,29,-5122));
	    this.chestsTier1.add(world.getBlockAt(222,29,-5270));
	    this.chestsTier1.add(world.getBlockAt(222,30,-5204));
	    this.chestsTier1.add(world.getBlockAt(224,30,-5129));
	    this.chestsTier1.add(world.getBlockAt(228,33,-5167));
	    this.chestsTier1.add(world.getBlockAt(234,29,-5256));
	    this.chestsTier1.add(world.getBlockAt(234,29,-5205));
	    this.chestsTier1.add(world.getBlockAt(234,29,-5119));
	    this.chestsTier1.add(world.getBlockAt(234,31,-5157));
	    this.chestsTier1.add(world.getBlockAt(235,70,-5167));
	    this.chestsTier1.add(world.getBlockAt(236,30,-5259));
	    this.chestsTier1.add(world.getBlockAt(241,36,-5161));
	    this.chestsTier1.add(world.getBlockAt(241,36,-5160));
	    this.chestsTier1.add(world.getBlockAt(242,30,-5259));
	    this.chestsTier1.add(world.getBlockAt(242,34,-5131));
	    this.chestsTier1.add(world.getBlockAt(242,35,-5172));
	    this.chestsTier1.add(world.getBlockAt(242,50,-5154));
	    this.chestsTier1.add(world.getBlockAt(243,31,-5129));
	    this.chestsTier1.add(world.getBlockAt(243,37,-5125));
	    this.chestsTier1.add(world.getBlockAt(252,29,-5234));
	    this.chestsTier1.add(world.getBlockAt(254,29,-5125));
	    this.chestsTier1.add(world.getBlockAt(257,29,-5133));
	    this.chestsTier1.add(world.getBlockAt(258,31,-5283));
	    this.chestsTier1.add(world.getBlockAt(265,29,-5120));
	    this.chestsTier1.add(world.getBlockAt(268,29,-5168));
	    this.chestsTier1.add(world.getBlockAt(269,30,-5255));
	    this.chestsTier1.add(world.getBlockAt(271,29,-5220));
	    this.chestsTier1.add(world.getBlockAt(274,29,-5240));
	    this.chestsTier1.add(world.getBlockAt(275,29,-5145));
	    this.chestsTier1.add(world.getBlockAt(277,29,-5164));
	    this.chestsTier1.add(world.getBlockAt(279,29,-5167));
	    this.chestsTier1.add(world.getBlockAt(281,30,-5193));
	    this.chestsTier1.add(world.getBlockAt(282,29,-5203));
	    this.chestsTier1.add(world.getBlockAt(282,45,-5196));
	    this.chestsTier1.add(world.getBlockAt(283,29,-5266));
	    this.chestsTier1.add(world.getBlockAt(285,29,-5212));
	    this.chestsTier1.add(world.getBlockAt(285,29,-5168));
	    this.chestsTier1.add(world.getBlockAt(287,29,-5240));
	    this.chestsTier1.add(world.getBlockAt(293,29,-5204));
	    this.chestsTier1.add(world.getBlockAt(295,31,-5133));
	    this.chestsTier1.add(world.getBlockAt(299,29,-5251));
	    this.chestsTier1.add(world.getBlockAt(303,35,-5282));

	    this.chestsTier2.add(world.getBlockAt(103,33,-5102));
	    this.chestsTier2.add(world.getBlockAt(104,38,-5255));
	    this.chestsTier2.add(world.getBlockAt(105,28,-5217));
	    this.chestsTier2.add(world.getBlockAt(105,29,-5257));
	    this.chestsTier2.add(world.getBlockAt(110,29,-5181));
	    this.chestsTier2.add(world.getBlockAt(116,29,-5193));
	    this.chestsTier2.add(world.getBlockAt(117,30,-5267));
	    this.chestsTier2.add(world.getBlockAt(120,38,-5188));
	    this.chestsTier2.add(world.getBlockAt(123,30,-5102));
	    this.chestsTier2.add(world.getBlockAt(125,40,-5207));
	    this.chestsTier2.add(world.getBlockAt(126,28,-5131));
	    this.chestsTier2.add(world.getBlockAt(141,35,-5261));
	    this.chestsTier2.add(world.getBlockAt(149,39,-5154));
	    this.chestsTier2.add(world.getBlockAt(154,35,-5107));
	    this.chestsTier2.add(world.getBlockAt(157,46,-5207));
	    this.chestsTier2.add(world.getBlockAt(165,73,-5166));
	    this.chestsTier2.add(world.getBlockAt(169,33,-5131));
	    this.chestsTier2.add(world.getBlockAt(177,73,-5171));
	    this.chestsTier2.add(world.getBlockAt(182,27,-5262));
	    this.chestsTier2.add(world.getBlockAt(212,62,-5172));
	    this.chestsTier2.add(world.getBlockAt(214,34,-5137));
	    this.chestsTier2.add(world.getBlockAt(220,29,-5134));
	    this.chestsTier2.add(world.getBlockAt(221,35,-5230));
	    this.chestsTier2.add(world.getBlockAt(233,33,-5139));
	    this.chestsTier2.add(world.getBlockAt(234,41,-5257));
	    this.chestsTier2.add(world.getBlockAt(237,30,-5217));
	    this.chestsTier2.add(world.getBlockAt(239,36,-5162));
	    this.chestsTier2.add(world.getBlockAt(266,30,-5102));
	    this.chestsTier2.add(world.getBlockAt(291,29,-5203));

        for (Block block : this.chestsTier1) {
            // Sanity checks
            if (block.getType() != Material.CHEST) {
                throw new RuntimeException(block.toString());
            }

            this.inventoriesTier1.put(block, Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.AQUA + "Tier 1 Chest"));
        }

        for (Block block : this.chestsTier2) {
            // Sanity checks
            if (block.getType() != Material.ENDER_CHEST) {
                throw new RuntimeException(block.toString());
            }

            this.inventoriesTier2.put(block, Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.DARK_GREEN + "Tier 2 Chest"));
        }

        this.armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);

        this.inventory[0] = new ItemStack(Material.WOOD_SWORD);
        this.inventory[1] = new ItemStack(Material.ENDER_PEARL);

        // Fill once at start
        for (Block block : SGFFAWorld.this.chestsTier1) {
            Inventory inventory = SGFFAWorld.this.inventoriesTier1.get(block);
            if (SGFFAWorld.isChestEmpty(inventory)) {
                SGFFAWorld.addItemsToChest(inventory, 1);
            }
        }

        for (Block block : SGFFAWorld.this.chestsTier2) {
            Inventory inventory = SGFFAWorld.this.inventoriesTier2.get(block);
            if (SGFFAWorld.isChestEmpty(inventory)) {
                SGFFAWorld.addItemsToChest(inventory, 2);
            }
        }

        new FillChestTask().runTaskTimer(PotPvP.getInstance(), 10, 10);
    }

    /**
     * Add player
     */
    public boolean addPlayer(Player player) {
        player.teleport(this.spawn);
        PlayerHelper.healAndPrepPlayerForBattle(player);

	    player.getInventory().setContents(this.inventory);
	    player.getInventory().setArmorContents(this.armor);
        player.updateInventory();

        this.hasTakenFallDamage.put(player.getUniqueId(), false);

        PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

        return this.players.add(player);
    }

    @Override
    public void startGame() {
        this.spawn = new Location(PotPvP.getInstance().getServer().getWorld("world"), 234.5, 79.5, -5217.5);
    }

    @Override
    public void handleDeath(Player player) {
        // Emulate dropping items on death
        Location location = player.getLocation();
        for (ItemStack item : player.getInventory().getContents()) {
            if (SGFFAWorld.random.nextInt(3) != 0) {
                continue;
            }

            if (item == null) {
                continue;
            }

            if (item.getType() == Material.AIR || item.getType() == Material.ENDER_PEARL) {
                continue;
            }

            location.getWorld().dropItemNaturally(location, item);
        }

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) {
                continue;
            }

            if (item.getType() == Material.AIR) {
                continue;
            }

            location.getWorld().dropItemNaturally(location, item);
        }

        Player killer = GroupStateMachine.ffaState.handleScoreboardDeath(player, this);
        if (killer != null) {
            killer.removePotionEffect(PotionEffectType.REGENERATION);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + 6/*Number of 1/2 hearts to heal*/ * 25, 1));
        }

        this.hasTakenFallDamage.put(player.getUniqueId(), false);
    }

    @Override
    public Location handleRespawn(Player player) {
        PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));
        PlayerHelper.healAndPrepPlayerForBattle(player);

	    player.getInventory().setContents(this.inventory);
	    player.getInventory().setArmorContents(this.armor);
        player.updateInventory();
        return this.spawn;
    }

    /**
     * Handle when someone quits or /spawn's
     */
    public boolean handleQuit(Player player, String reason) {
        ProtectedRegion region = PotPvP.getInstance().getgGuardPlugin().getProtectedRegion(player.getLocation(),
                PotPvP.getInstance().getgGuardPlugin().getProtectedRegions());

        if (region != null && region.getRegionName().startsWith("ffa")) {
            return true; // Let the state machine do it's thing
        } else if (reason.equals("spawn")) {
            if (FFAState.lastDamageTime.containsKey(player.getName())
                    && FFAState.lastDamageTime.get(player.getName()) + FFAWorld.COMBAT_TAG_TIME >= System.currentTimeMillis()) {
                long timeRemaining = FFAState.lastDamageTime.get(player.getName()) + FFAWorld.COMBAT_TAG_TIME - System.currentTimeMillis();
                player.sendMessage(ChatColor.RED + "Cannot use /spawn when in combat on the FFA World. You have " + ((double)Math.round(((double)timeRemaining / 1000) * 10) / 10) + " seconds remaining.");
                return false; // Don't change states
            }

            player.teleport(this.spawn);
            PlayerHelper.healAndPrepPlayerForBattle(player);
            this.hasTakenFallDamage.put(player.getUniqueId(), false);

	        player.getInventory().setContents(this.inventory);
	        player.getInventory().setArmorContents(this.armor);
            player.updateInventory();
            return false; // Don't change states
        }

        // They logged off out of safe zone, give them a death
        GroupStateMachine.ffaState.handleScoreboardDeath(player, this);
        return true;
    }

    @EventHandler
    public void onPlayerTakeFallDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (this.players.contains(player)) {
                    // Check if they haven't taken damage yet since last death
                    if (!this.hasTakenFallDamage.get(player.getUniqueId())) {
                        player.getInventory().remove(Material.ENDER_PEARL);
	                    if (player.getOpenInventory() != null) {
		                    Inventory inventory = player.getOpenInventory().getTopInventory();
		                    if (inventory != null && inventory.getType() == InventoryType.CRAFTING) {
			                    inventory.clear();
		                    }
	                    }
                        player.updateInventory();
                        event.setCancelled(true);
                        this.hasTakenFallDamage.put(player.getUniqueId(), true);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEnderChestOpened(PlayerInteractEvent event) {
        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (this.players.contains(event.getPlayer())) {
                if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                    try {
                        event.getPlayer().openInventory(this.inventoriesTier2.get(event.getClickedBlock()));
                    } catch (NullPointerException e) {
                        Bukkit.getLogger().info("location " + event.getClickedBlock());
                        e.printStackTrace();
                    }
                } else if (event.getClickedBlock().getType() == Material.CHEST) {
                    event.setCancelled(true);
                    event.getPlayer().openInventory(this.inventoriesTier1.get(event.getClickedBlock()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {this.hasTakenFallDamage.remove(event.getPlayer().getUniqueId());} catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPearlDamage(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (this.players.contains(event.getPlayer())) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
                this.hasTakenFallDamage.put(player.getUniqueId(), true);
                player.teleport(event.getTo());
            }
        }
    }

    @EventHandler
    public void onDiamondArmorCrafted(CraftItemEvent event) {
        if (this.players.contains((Player) event.getWhoClicked())) {
            if (event.getRecipe().getResult().getType() == Material.DIAMOND_HELMET
                    || event.getRecipe().getResult().getType() == Material.DIAMOND_CHESTPLATE
                    || event.getRecipe().getResult().getType() == Material.DIAMOND_LEGGINGS
                    || event.getRecipe().getResult().getType() == Material.DIAMOND_BOOTS) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Cannot craft diamond armor in SG FFA World.");
            }
        }
    }

    public static ItemStack getTier1Item() {
        int rarity = random.nextInt(100);

        if (rarity < 40) {
            int i = random.nextInt(6);
            switch (i) {
                case 0:
                    return new ItemStack(Material.BREAD, random.nextInt(2) + 1);
                case 1:
                    return new ItemStack(Material.PUMPKIN_PIE, random.nextInt(2) + 1);
                case 2:
                    return new ItemStack(Material.COOKIE, random.nextInt(2) + 1);
                case 3:
                    return new ItemStack(Material.CARROT_ITEM, random.nextInt(2) + 1);
                case 4:
                    return new ItemStack(Material.GOLD_SWORD);
                case 5:
                    return new ItemStack(Material.WOOD_SWORD);
            }
        } else if (rarity < 75) {
            int i = random.nextInt(8);
            switch (i) {
                case 0:
                    return new ItemStack(Material.ARROW, 2);
                case 1:
                    return new ItemStack(Material.IRON_INGOT, 1);
                case 2:
                    return new ItemStack(Material.FLINT, random.nextInt(3) + 1);
                case 3:
                    return new ItemStack(Material.FEATHER, random.nextInt(3) + 1);
                case 4:
                    return new ItemStack(Material.STICK, random.nextInt(3) + 1);
                case 5:
                    return new ItemStack(Material.LEATHER_HELMET);
                case 6:
                    return new ItemStack(Material.LEATHER_BOOTS);
                case 7:
                    return new ItemStack(Material.BOW);
            }
        } else {
            int i = random.nextInt(4);
            switch (i) {
                case 0:
                    return new ItemStack(Material.FISHING_ROD);
                case 1:
                    return new ItemStack(Material.STONE_SWORD);
                case 2:
                    return new ItemStack(Material.LEATHER_CHESTPLATE);
                case 3:
                    return new ItemStack(Material.LEATHER_LEGGINGS);
            }
        }

        return null;
    }

    public static ItemStack getTier2Item() {
        int rarity = random.nextInt(100);

        if (rarity < 40) {
            int i = random.nextInt(11);
            switch (i) {
                case 0:
                    return new ItemStack(Material.GRILLED_PORK);
                case 1:
                    return new ItemStack(Material.GOLDEN_CARROT);
                case 2:
                    return new ItemStack(Material.GOLD_HELMET);
                case 3:
                    return new ItemStack(Material.GOLD_CHESTPLATE);
                case 4:
                    return new ItemStack(Material.GOLD_LEGGINGS);
                case 5:
                    return new ItemStack(Material.GOLD_BOOTS);
                case 6:
                    return new ItemStack(Material.STONE_SWORD);
                case 7:
                    return new ItemStack(Material.ARROW, 4);
                case 8:
                    return new ItemStack(Material.BOW);
                case 9:
                    return new ItemStack(Material.STICK, 2);
                case 10:
                    return new ItemStack(Material.IRON_INGOT);
            }
        } else if (rarity < 75) {
            int i = random.nextInt(4);
            switch (i) {
                case 0:
                    return new ItemStack(Material.CHAINMAIL_HELMET);
                case 1:
                    return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                case 2:
                    return new ItemStack(Material.CHAINMAIL_LEGGINGS);
                case 3:
                    return new ItemStack(Material.CHAINMAIL_BOOTS);
            }
        } else if (rarity < 90) {
            int i = random.nextInt(5);
            switch (i) {
                case 0:
                    return new ItemStack(Material.IRON_HELMET);
                case 1:
                    return new ItemStack(Material.IRON_CHESTPLATE);
                case 2:
                    return new ItemStack(Material.IRON_LEGGINGS);
                case 3:
                    return new ItemStack(Material.IRON_BOOTS);
                case 4:
                    return new ItemStack(Material.FLINT_AND_STEEL);
            }
        } else {int i = random.nextInt(2);
            switch (i) {
                case 0:
                    return new ItemStack(Material.DIAMOND);
                case 1:
                    return new ItemStack(Material.GOLDEN_APPLE);
            }
        }

        return null;
    }

    public static boolean isChestEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public static void addItemsToChest(Inventory inventory, int tier) {
        int numOfItems = tier == 1 ? random.nextInt(4) + 4 : random.nextInt(2) + 3;
        Set<Integer> items = new HashSet<>();

        for (int i = 0; i < numOfItems; i++) {
            int slot = 0;
            do {
                slot = random.nextInt(27);
            } while (items.contains(slot));

            items.add(slot);
            inventory.setItem(slot, tier == 1 ? SGFFAWorld.getTier1Item() : SGFFAWorld.getTier2Item());
        }
    }

    public class FillChestTask extends BukkitRunnable {

        @Override
        public void run() {
            if (SGFFAWorld.this.chestTier1Count == SGFFAWorld.this.chestsTier1.size()) {
                SGFFAWorld.this.chestTier1Count = 0;
            }

            if (SGFFAWorld.this.chestTier2Count == SGFFAWorld.this.chestsTier2.size()) {
                SGFFAWorld.this.chestTier2Count = 0;
            }

            // Distribute on ticks
            Block block = SGFFAWorld.this.chestsTier1.get(SGFFAWorld.this.chestTier1Count++);
            Inventory inventory = SGFFAWorld.this.inventoriesTier1.get(block);
            if (SGFFAWorld.isChestEmpty(inventory)) {
                SGFFAWorld.addItemsToChest(inventory, 1);
            }

            // Only every other tick do tier 2's respawn
            long currentTick = PotPvP.getInstance().getServer().getCurrentTick();
            if (currentTick % 3 == 0) {
                return;
            }

            block = SGFFAWorld.this.chestsTier2.get(SGFFAWorld.this.chestTier2Count++);
            inventory = SGFFAWorld.this.inventoriesTier2.get(block);
            if (SGFFAWorld.isChestEmpty(inventory)) {
                SGFFAWorld.addItemsToChest(inventory, 2);
            }
        }

    }

}
