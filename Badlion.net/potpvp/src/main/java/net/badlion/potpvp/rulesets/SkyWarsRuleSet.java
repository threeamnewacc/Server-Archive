package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.SkyWarsArena;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkyWarsRuleSet extends KitRuleSet {

    private List<Material> blacklistedBlocks = new ArrayList<>();

    public SkyWarsRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.EYE_OF_ENDER), ArenaManager.ArenaType.SKYWARS, false, false);

	    this.enabledInDuels = true;
	    this.enabledInEvents = false;

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.ThreeVsThreeRanked, true, true));
        }

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
        this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);
        this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[3] = new ItemStack(Material.DIAMOND_PICKAXE);
        this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_APPLE, 2);
        this.defaultInventoryKit[5] = new ItemStack(Material.SNOW_BALL, 16);
        this.defaultInventoryKit[6] = new ItemStack(Material.COBBLESTONE, 64);
        this.defaultInventoryKit[7] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 64);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5SkyWars";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dDiamond Armor";
        this.info4Sign[1] = "Chestplate";
        this.info4Sign[2] = "Boots";
        this.info4Sign[3] = "";

        this.info5Sign[0] = "§Iron Armor";
        this.info5Sign[1] = "Helmet";
        this.info5Sign[2] = "Leggings";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dWeapons";
        this.info6Sign[1] = "Diamond Sword";
        this.info6Sign[2] = "Bow";
        this.info6Sign[3] = "";

        this.info7Sign[0] = "§dOther";
        this.info7Sign[1] = "D PickAxe";
        this.info7Sign[2] = "Snow Balls";
        this.info7Sign[3] = "Fishing Rod";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "64 Steak";
        this.info8Sign[2] = "2 Gold Apples";
        this.info8Sign[3] = "";

        this.info9Sign[0] = "§dBlocks";
        this.info9Sign[1] = "64 Cobble";
        this.info9Sign[2] = "";
        this.info9Sign[3] = "";

        this.blacklistedBlocks.add(Material.WOOL);
        this.blacklistedBlocks.add(Material.GLASS);
    }

	@Override
	public void sendMessages(Player player) {
		player.sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.DARK_AQUA + "The spawn islands and everything you build will disappear in 1 minute.");
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

    @EventHandler(priority = EventPriority.LAST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
	        if (!this.canBreakBlock(event.getBlock())) return;

            if (this.blacklistedBlocks.contains(event.getBlock().getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You're not allowed to break this block!");
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LAST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
	        if (!this.canBreakBlock(event.getBlock())) return;

            event.setCancelled(false);

            Game game = GameState.getGroupGame(group);
            if (game.getArena() instanceof SkyWarsArena) {
	            // Don't let them place blocks on the walls (red wool)
	            if (event.getBlockAgainst().getType() == Material.WOOL && event.getBlockAgainst().getData() == 14) {
		            player.sendMessage(ChatColor.RED + "You can't place a block here!");
		            event.setCancelled(true);
		            return;
	            }

                ((SkyWarsArena) game.getArena()).addToDestroy(event.getBlock().getLocation());
            }
        }
    }

}
