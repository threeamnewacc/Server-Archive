package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.helpers.PotionFixHelper;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BadlionFactionsRuleSet extends KitRuleSet {

    public BadlionFactionsRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.REDSTONE_BLOCK), ArenaManager.ArenaType.PEARL, true, false);

        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Initialize valid enchants
	    this.validEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
	    this.validEnchantments.put(Enchantment.PROTECTION_FALL, 4);
	    this.validEnchantments.put(Enchantment.DAMAGE_ALL, 5);
	    this.validEnchantments.put(Enchantment.KNOCKBACK, 2);
	    this.validEnchantments.put(Enchantment.FIRE_ASPECT, 2);
	    this.validEnchantments.put(Enchantment.DURABILITY, 3);
	    this.validEnchantments.put(Enchantment.ARROW_DAMAGE, 5);
	    this.validEnchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
	    this.validEnchantments.put(Enchantment.ARROW_FIRE, 1);
	    this.validEnchantments.put(Enchantment.ARROW_INFINITE, 1);

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 5);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_FIRE, 1);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 5);

        this.defaultInventoryKit[2] = new ItemStack(Material.ENDER_PEARL, 16);
        this.defaultInventoryKit[3] = new ItemStack(Material.GOLDEN_APPLE, 64);
        this.defaultInventoryKit[4] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[5] = ItemStackUtil.FIRE_RESISTANCE_POTION_EXT;
        this.defaultInventoryKit[6] = ItemStackUtil.REGENERATION_POTION_EXT;
        this.defaultInventoryKit[7] = ItemStackUtil.STRENGTH_POTION_II;
        this.defaultInventoryKit[8] = ItemStackUtil.SWIFTNESS_POTION_II;

        this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 64);
        this.defaultInventoryKit[10] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[11] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[12] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[13] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[14] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[15] = ItemStackUtil.WEAKNESS_SPLASH_EXT;
        this.defaultInventoryKit[16] = ItemStackUtil.SLOWNESS_SPLASH_EXT;
        this.defaultInventoryKit[17] = ItemStackUtil.POISON_SPLASH_EXT;

        this.defaultInventoryKit[18] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[19] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[20] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[21] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[22] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[23] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[24] = ItemStackUtil.REGENERATION_POTION_EXT;
        this.defaultInventoryKit[25] = ItemStackUtil.STRENGTH_POTION_II;
        this.defaultInventoryKit[26] = ItemStackUtil.SWIFTNESS_POTION_II;

        this.defaultInventoryKit[27] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);
        this.defaultInventoryKit[28] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[29] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[30] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[31] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[32] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[33] = ItemStackUtil.REGENERATION_POTION_EXT;
        this.defaultInventoryKit[34] = ItemStackUtil.STRENGTH_POTION_II;
        this.defaultInventoryKit[35] = ItemStackUtil.SWIFTNESS_POTION_II;

        // Set up kit creation chest

        this.kitCreationInventory.setItem(0, ItemStackUtil.DIAMOND_HELMET);
        this.kitCreationInventory.setItem(9, ItemStackUtil.DIAMOND_CHESTPLATE);
        this.kitCreationInventory.setItem(18, ItemStackUtil.DIAMOND_LEGGINGS);
        this.kitCreationInventory.setItem(27, ItemStackUtil.DIAMOND_BOOTS);
        this.kitCreationInventory.setItem(36, ItemStackUtil.DIAMOND_SWORD);
        this.kitCreationInventory.setItem(45, ItemStackUtil.DIAMOND_AXE);

        this.kitCreationInventory.setItem(1, ItemStackUtil.BOW);
        this.kitCreationInventory.setItem(10, ItemStackUtil.ARROW);
        this.kitCreationInventory.setItem(19, ItemStackUtil.FISHING_ROD);
        this.kitCreationInventory.setItem(37, ItemStackUtil.APPLE);
        this.kitCreationInventory.setItem(46, ItemStackUtil.BAKED_POTATO);

        this.kitCreationInventory.setItem(2, ItemStackUtil.ENDER_PEARL);
        this.kitCreationInventory.setItem(11, ItemStackUtil.MILK_BUCKET);
        this.kitCreationInventory.setItem(38, ItemStackUtil.COOKED_BEEF);
        this.kitCreationInventory.setItem(47, ItemStackUtil.COOKED_CHICKEN);

        this.kitCreationInventory.setItem(3, ItemStackUtil.HEALING_POTION_II);
        this.kitCreationInventory.setItem(12, ItemStackUtil.HEALING_SPLASH_II);
        this.kitCreationInventory.setItem(21, ItemStackUtil.GOD_APPLE);
        this.kitCreationInventory.setItem(30, ItemStackUtil.GOLDEN_APPLE);
        this.kitCreationInventory.setItem(39, ItemStackUtil.COOKIE);
        this.kitCreationInventory.setItem(48, ItemStackUtil.COOKED_FISH);

        this.kitCreationInventory.setItem(4, ItemStackUtil.REGENERATION_POTION_EXT);
        this.kitCreationInventory.setItem(13, ItemStackUtil.REGENERATION_POTION_II);
        this.kitCreationInventory.setItem(22, ItemStackUtil.REGENERATION_SPLASH_EXT);
        this.kitCreationInventory.setItem(31, ItemStackUtil.REGENERATION_SPLASH_II);
        this.kitCreationInventory.setItem(40, ItemStackUtil.MELON);
        this.kitCreationInventory.setItem(49, ItemStackUtil.PUMPKIN_PIE);

        this.kitCreationInventory.setItem(5, ItemStackUtil.STRENGTH_POTION_EXT);
        this.kitCreationInventory.setItem(14, ItemStackUtil.STRENGTH_POTION_II);
        this.kitCreationInventory.setItem(23, ItemStackUtil.STRENGTH_SPLASH_EXT);
        this.kitCreationInventory.setItem(32, ItemStackUtil.STRENGTH_SPLASH_II);
        this.kitCreationInventory.setItem(41, ItemStackUtil.GRILLED_PORK);
        this.kitCreationInventory.setItem(50, ItemStackUtil.BREAD);

        this.kitCreationInventory.setItem(6, ItemStackUtil.SWIFTNESS_POTION_EXT);
        this.kitCreationInventory.setItem(15, ItemStackUtil.SWIFTNESS_POTION_II);
        this.kitCreationInventory.setItem(24, ItemStackUtil.SWIFTNESS_SPLASH_EXT);
        this.kitCreationInventory.setItem(33, ItemStackUtil.SWIFTNESS_SPLASH_II);

        this.kitCreationInventory.setItem(7, ItemStackUtil.FIRE_RESISTANCE_POTION_EXT);
        this.kitCreationInventory.setItem(16, ItemStackUtil.FIRE_RESISTANCE_SPLASH_EXT);

        this.kitCreationInventory.setItem(8, ItemStackUtil.SLOWNESS_SPLASH_EXT);
        this.kitCreationInventory.setItem(17, ItemStackUtil.WEAKNESS_SPLASH_EXT);
        this.kitCreationInventory.setItem(26, ItemStackUtil.POISON_SPLASH_EXT);
        this.kitCreationInventory.setItem(35, ItemStackUtil.POISON_SPLASH_II);
        this.kitCreationInventory.setItem(53, SmellyInventory.getCloseInventoryItem());

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Badlion";
	    this.info2Sign[2] = "§5Factions";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection IV";
	    this.info4Sign[2] = "Unbreaking III";
	    this.info4Sign[3] = "FF IV";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness V";
	    this.info5Sign[2] = "Fire Aspect II";
	    this.info5Sign[3] = "Unb III / KB II";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power V";
	    this.info6Sign[2] = "Flame/Infinity I";
	    this.info6Sign[3] = "Unbreaking III";

	    this.info7Sign[0] = "§dPotions(Buff)";
	    this.info7Sign[1] = "Health II";
	    this.info7Sign[2] = "Str II (1.5)";
	    this.info7Sign[3] = "Speed/RegenI/II";

	    this.info8Sign[0] = "§d(Debuffs)";
	    this.info8Sign[1] = "Poison I / II";
	    this.info8Sign[2] = "Slow/Weak 3:00";
	    this.info8Sign[3] = "Regeneration II";

	    this.info9Sign[0] = "§dAllowed Items";
	    this.info9Sign[1] = "All Food";
	    this.info9Sign[2] = "Pearls";
	    this.info9Sign[3] = "1 GodApple";
    }

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				}
			}
		}
	}

    @EventHandler
    public void onPlayerEatGApple(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
            Game game = GameState.getGroupGame(group);
            Map<String, Long> goldenAppleCooldowns = game.getGodAppleCooldowns();

            if ((event.getItem().getType() == Material.GOLDEN_APPLE) && (event.getItem().getData().getData() == 1)) {
                long lastConsume = goldenAppleCooldowns.containsKey(player.getName()) ? goldenAppleCooldowns.get(player.getName()) : 0L;
                long currentTime = System.currentTimeMillis();

                long diff = currentTime - lastConsume;
                if (diff < 12000000L) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Only 1 God Apple allowed per match/event.");
                } else {
                    goldenAppleCooldowns.put(player.getName(), currentTime);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player)) {
            Player player = (Player) event.getDamager();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                PotionFixHelper.modifyDamage(player, event, 6);
            }
        }
    }

    @EventHandler
    public void onPlayerHitByArrow(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Group group = PotPvP.getInstance().getPlayerGroup((Player) event.getEntity());

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                Location location = event.getEntity().getLocation();
                // The target is in the water
                if (location.getBlock().getType() == Material.STATIONARY_WATER || location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA
                        || location.getBlock().getType() == Material.STATIONARY_LAVA) {
                    // Easy way to cancel the knockback
                    Arrow arrow = (Arrow) event.getDamager();
                    arrow.setKnockbackStrength(0);
                }
            }
        }
    }

}