package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.ffaworlds.SGFFAWorld;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SGRuleSet extends KitRuleSet {

    public SGRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.FISHING_ROD), ArenaManager.ArenaType.NON_PEARL, false, false);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
        Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.ThreeVsThreeRanked, true, true));

	    // Initialize FFAWorld for this rule set
	    ItemStack ffaItem = ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + "Join SG FFA", ChatColor.YELLOW + "Players: 0");

	    SGFFAWorld ffa = new SGFFAWorld(ffaItem, this);
	    PotPvP.getInstance().getServer().getPluginManager().registerEvents(ffa, PotPvP.getInstance());

	    // Create TDM kill reward items
	    TDMGame.getKitRuleSets().add(this);
	    this.tdmKillRewardItems.add(ItemStackUtil.GOLDEN_APPLE);
	    this.tdmKillRewardItems.add(new ItemStack(Material.ARROW, 16));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.GOLD_HELMET);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[3]);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[2]);
	    this.defaultArmorKit[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[1]);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[0]);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.STONE_SWORD);
	    this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);
	    this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[3] = new ItemStack(Material.GOLDEN_APPLE);
	    this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_CARROT);
	    this.defaultInventoryKit[5] = new ItemStack(Material.PUMPKIN_PIE, 2);
	    this.defaultInventoryKit[6] = new ItemStack(Material.MELON, 2);
	    this.defaultInventoryKit[7] = new ItemStack(Material.BREAD);
        this.defaultInventoryKit[8] = new ItemStack(Material.FLINT_AND_STEEL);
	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 8);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5SG";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dArmor";
	    this.info4Sign[1] = "Gold Helmet";
	    this.info4Sign[2] = "Iron Chest/Boots";
	    this.info4Sign[3] = "Chain Leggings";

	    this.info5Sign[0] = "§dWeapons";
	    this.info5Sign[1] = "Stone Sword";
	    this.info5Sign[2] = "Fishing Rod";
	    this.info5Sign[3] = "Bow - 8 Arrows";

	    this.info6Sign[0] = "§dFood";
	    this.info6Sign[1] = "1 Golden Carrot";
	    this.info6Sign[2] = "2 Melon/Pie";
	    this.info6Sign[3] = "1 Bread";
    }

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				} else if (itemStack.getType() == Material.FLINT_AND_STEEL) {
					// Reset durability since now we do the 1/3rd thing
					itemStack.setDurability((short) 0);
				}
			}

			ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onFlintAndSteelEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			// Are they using a flint and steel?
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null
					&& event.getItem().getType() == Material.FLINT_AND_STEEL) {

				// Two uses per flint and steel, reduce durability by 32 per use
				event.getItem().setDurability((short) (event.getItem().getDurability() + 32));
			}
		}
	}

}
