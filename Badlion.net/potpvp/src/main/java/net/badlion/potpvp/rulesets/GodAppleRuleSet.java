package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GodAppleRuleSet extends KitRuleSet {

    public GodAppleRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), ArenaManager.ArenaType.NON_PEARL, false, true);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Speed 2 and strength 2
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	    this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));

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

	    // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 5);

	    this.defaultInventoryKit[1] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);

        this.defaultInventoryKit[2] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultInventoryKit[2].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[3] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultInventoryKit[3].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[4] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.defaultInventoryKit[4].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[5] = new ItemStack(Material.DIAMOND_BOOTS);
        this.defaultInventoryKit[5].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    // 1.9
	    if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		    this.defaultExtraItems[0] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);

		    this.defaultInventoryKit[1] = new ItemStack(Material.AIR);
	    }

        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_HELMET);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_CHESTPLATE);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_LEGGINGS);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_BOOTS);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_SWORD);
        this.kitCreationInventory.addItem(ItemStackUtil.GOD_APPLE);
        this.kitCreationInventory.addItem(ItemStackUtil.STRENGTH_POTION_II_EXT);
        this.kitCreationInventory.addItem(ItemStackUtil.SWIFTNESS_POTION_II_EXT);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Gapple";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection IV";
	    this.info4Sign[2] = "Unbreaking III";
	    this.info4Sign[3] = "Bring 2 Sets";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness V";
	    this.info5Sign[2] = "Fire Aspect II";
	    this.info5Sign[3] = "Unbreaking III";

	    this.info6Sign[0] = "§dPotions";
	    this.info6Sign[1] = "x2 Strength II";
	    this.info6Sign[2] = "x2 Speed II";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Gapples";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";

        // Knockback changes
        this.knockbackFriction = 2.0;
        this.knockbackHorizontal = 0.34;
        this.knockbackVertical = 0.34;
        this.knockbackVerticalLimit = 0.4;
        this.knockbackExtraHorizontal = 0.48;
        this.knockbackExtraVertical = 0.085;
    }

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				// Remove potions from their kit since we have permanent potion effects now
				if (itemStack.getType() == Material.POTION) {
					event.getPlayer().getInventory().remove(itemStack);
				}
			}

			ItemStackUtil.removeUnbreakingFromArmor(event.getPlayer());
		}
	}

	@EventHandler
	public void onEatGodAppleEvent(final PlayerItemConsumeEvent event) {
		if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			final Player player = event.getPlayer();
			ItemStack item = event.getItem();
			if (item.getType().equals(Material.GOLDEN_APPLE)) {
				if (item.getDurability() == 1) {
					Group group = PotPvP.getInstance().getPlayerGroup(player);
					if (GameState.getGroupGame(group) != null) {
						// TODO: DAFUQ?
						BukkitUtil.runTaskNextTick(new Runnable() {
							@Override
							public void run() {
								// Reset god apples to 1.7 behavior
								player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 4), true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0), true);
							}
						});
					}
				}
			}
		}
	}

}
