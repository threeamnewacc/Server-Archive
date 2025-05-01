package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DiamondOCNRuleSet extends KitRuleSet {

    public DiamondOCNRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.DIAMOND_HELMET), ArenaManager.ArenaType.NON_PEARL, false, false);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
        this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[4] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 32);

	    if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		    this.defaultExtraItems[0] = new ItemStack(Material.SHIELD);

		    this.defaultInventoryKit[1] = new ItemStack(Material.DIAMOND_AXE);
	    }

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Diamond";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Feather";
	    this.info4Sign[2] = "Falling IV";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dDiamond Sword";
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
    }

	@EventHandler
	public void onRegenDrinkEvent(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == ItemStackUtil.REGENERATION_POTION_II.getType()
				&& event.getItem().getDurability() == ItemStackUtil.REGENERATION_POTION_II.getDurability()) {
			Player player = event.getPlayer();

		   	// Remove the regen potion effect and add ours
			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				if (potionEffect.getType().equals(PotionEffectType.REGENERATION)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1, true));
				}
			}
		}
	}

}
