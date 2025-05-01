package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MineZRuleSet extends KitRuleSet {

    private HashSet<Material> types = new HashSet<>();
    private Map<Material, Integer> mappedStacks = new HashMap<>();

    public MineZRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BREAD), ArenaManager.ArenaType.NON_PEARL, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

        this.types.add(Material.ARROW);
        this.types.add(Material.BREAD);
        this.types.add(Material.GOLDEN_APPLE);
        this.mappedStacks.put(Material.ARROW, 15);
        this.mappedStacks.put(Material.BREAD, 3);
        this.mappedStacks.put(Material.GOLDEN_APPLE, 1);
        this.mappedStacks.put(Material.WEB, 1);

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        this.defaultInventoryKit[2] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[3] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[4] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[5] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[6] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[7] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[8] = ItemStackUtil.HEALING_POTION_II;

        this.defaultInventoryKit[27] = new ItemStack(Material.ARROW, 15);

        this.defaultInventoryKit[28] = new ItemStack(Material.BOW);

        this.defaultInventoryKit[29] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[30] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[31] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[32] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[33] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[34] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[35] = ItemStackUtil.HEALING_POTION_II;

        this.defaultInventoryKit[18] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[19] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[20] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[21] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[22] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[23] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[24] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[25] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[26] = ItemStackUtil.HEALING_POTION_II;

	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[10] = new ItemStack(Material.WEB);
        this.defaultInventoryKit[11] = new ItemStack(Material.WEB);
        this.defaultInventoryKit[12] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[13] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[14] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[15] = new ItemStack(Material.BREAD, 3);
        this.defaultInventoryKit[16] = new ItemStack(Material.BREAD, 3);
        this.defaultInventoryKit[17] = new ItemStack(Material.BREAD, 3);

        // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5MineZ";
	    this.info2Sign[2] = "";
	    this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "FF 4 Boots";
	    this.info4Sign[2] = "";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§Diamond Sword";
	    this.info5Sign[1] = "No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "2 Cobwebs";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "1x Infinity I";
	    this.info6Sign[2] = "1x Power II";
	    this.info6Sign[3] = "45 Arrows";

	    this.info8Sign[0] = "§dPotions & Food";
	    this.info8Sign[1] = "12DrinkHealthII";
	    this.info8Sign[2] = "9SplashHealthII";
	    this.info8Sign[3] = "12Bread/3GApple";
    }

    @EventHandler
    public void healthRegen(EntityRegainHealthEvent event) {
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

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (this.canBreakBlock(event.getBlock())) {
				if (event.getBlock().getType() == Material.WEB) {
					event.setCancelled(false);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (this.canBreakBlock(event.getBlock())) {
				event.setCancelled(false);
			}
		}
	}

    @EventHandler(ignoreCancelled=true)
    public void onItemStacked(InventoryClickEvent event) {
        // Clicking out of bounds
        if (event.getClickedInventory() == null) {
            return;
        }

        InventoryHolder ih = event.getClickedInventory().getHolder();

        if (ih instanceof Player) {
            final Player player = (Player) ih;
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)
                    || (GroupStateMachine.kitCreationState.getKitCreator(player) != null &&
                        GroupStateMachine.kitCreationState.getKitCreator(player).getKitRuleSet() == this)) {
                ItemStack current = event.getCurrentItem();
                ItemStack cursor = event.getCursor();

                if (current != null && cursor != null) {
                    // Try something different an less efficient cuz i don't got time for this
                    PotPvP.getInstance().getServer().getScheduler().runTaskLater(PotPvP.getInstance(), new Runnable() {
                        public void run() {
                            // Initialize temp storage
                            ArrayList<Integer> emptySlots = new ArrayList<Integer>();
                            Map<Material, Integer> excess = new HashMap<Material, Integer>();
                            for (Material m : MineZRuleSet.this.types) {
                                excess.put(m, 0);
                            }

                            int i = 0;
                            for (ItemStack item : player.getInventory()) {
                                if (item == null || item.getType() == Material.AIR) {
                                    emptySlots.add(i++);
                                    continue;
                                }

                                // Check ofr anything that shouldn't be stacked past a point
                                if (MineZRuleSet.this.types.contains(item.getType())) {
                                    if (item.getAmount() > MineZRuleSet.this.mappedStacks.get(item.getType())) {
                                        int diff =  item.getAmount() - MineZRuleSet.this.mappedStacks.get(item.getType());
                                        excess.put(item.getType(), excess.get(item.getType()) + diff);
                                        item.setAmount(MineZRuleSet.this.mappedStacks.get(item.getType()));
                                    }
                                }

                                i++;
                            }

                            for (Map.Entry<Material, Integer> entry : excess.entrySet()) {
                                if (entry.getValue() > 0) {
                                    if (emptySlots.size() == 0) {
                                        continue;
                                    }

                                    Integer tmp = emptySlots.remove(0);
                                    if (tmp != null) {
                                        // Less than our max value
                                        if (MineZRuleSet.this.mappedStacks.get(entry.getKey()) >= entry.getValue()) {
                                            player.getInventory().setItem(tmp, new ItemStack(entry.getKey(), entry.getValue()));
                                        } else if (entry.getValue() > MineZRuleSet.this.mappedStacks.get(entry.getKey())) {
                                            // Do it as many times as we need to to get rid of this inventory...
                                            int total = entry.getValue();
                                            boolean hasDone = false;
                                            do {
                                                if (hasDone) {
                                                    if (emptySlots.size() > 0) {
                                                        tmp = emptySlots.remove(0);
                                                    } else {
                                                        return; // glitch?
                                                    }
                                                }

                                                // Ok we can start getting new ones on the next iteration
                                                hasDone = true;

                                                // Make sure we have a slot
                                                if (tmp != null) {
                                                    // If we have more than 16 arrows, then only do 16 in this area, if we have less than only do that amount
                                                    int remaining = total > MineZRuleSet.this.mappedStacks.get(entry.getKey()) ? MineZRuleSet.this.mappedStacks.get(entry.getKey()) : total;
                                                    player.getInventory().setItem(tmp, new ItemStack(entry.getKey(), remaining));
                                                    total -= remaining;
                                                } else {
                                                    return; // out of spots?
                                                }
                                            } while (total > 0);
                                        }
                                    } else {
                                        // Done..no more slots?
                                        return;
                                    }
                                }
                            }

	                        player.updateInventory();
                        }
                    }, 1);
                }
            }
        }
    }

}
