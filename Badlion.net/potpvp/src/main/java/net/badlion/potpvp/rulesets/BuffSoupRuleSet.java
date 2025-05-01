package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PotionFixHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuffSoupRuleSet extends SoupRuleSet {

    public BuffSoupRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BOWL));

	    Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
	    Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Potion effects
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Speed 2
	    this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1)); // Strength 2

        // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    for (int i = 1; i < 9; i++ ) {
		    this.defaultInventoryKit[i] = new ItemStack(Material.MUSHROOM_SOUP);
	    }
	    for (int i = 27; i < 36; i++ ) {
		    this.defaultInventoryKit[i] = new ItemStack(Material.MUSHROOM_SOUP);
	    }

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Buff Soup";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection 1";
	    this.info4Sign[2] = "FF 4";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dSoups";
	    this.info6Sign[1] = "18 Soups";
	    this.info6Sign[2] = "";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dPotions";
	    this.info8Sign[1] = "Str II (1.5)";
	    this.info8Sign[2] = "Speed II";
	    this.info8Sign[3] = "";
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
	public void onPlayerHeal(EntityRegainHealthEvent event) {
		if ((event.getEntity() instanceof Player))
		{
			Player player = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
				PotionFixHelper.modifyHealPotion(event, 6);
				PotionFixHelper.modifyRegenPotion(event);
			}
		}
	}

}
