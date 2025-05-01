package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupRuleSet extends KitRuleSet {

    public SoupRuleSet(int id, String name, ItemStack itemStack) {
        super(id, name, itemStack,  ArenaManager.ArenaType.SOUP,  false, false);

        // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 32);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Iron";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "Feather";
	    this.info4Sign[2] = "Falling IV";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dIron Sword";
	    this.info5Sign[1] = "No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "No Enchants";
	    this.info6Sign[2] = "32 Arrows";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Steak";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";

	    // Speed 1 kb
	    this.knockbackFriction = 2.0;
	    this.knockbackHorizontal = 0.42;
	    this.knockbackVertical = 0.34;
	    this.knockbackVerticalLimit = 0.4;
	    this.knockbackExtraHorizontal = 0.513;
	    this.knockbackExtraVertical = 0.105;
    }

	@EventHandler
	public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
					if (!player.isDead()) {
						if (player.getHealth() < 20) {
							if (player.getHealth() + 7 < 20) {
								player.setHealth(player.getHealth() + 7);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							} else {
								player.setHealth(20);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
				event.setCancelled(true);
			}
		}
	}

}
