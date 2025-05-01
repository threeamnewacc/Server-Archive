package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ffaworlds.UHCFFAWorld;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class UHCRuleSet extends KitRuleSet {

    public UHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLDEN_APPLE), ArenaManager.ArenaType.NON_PEARL, false, false);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.ThreeVsThreeRanked, true, true));
        }

	    // Initialize FFAWorld for this rule set
	    ItemStack ffaItem = ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + "Join UHC FFA", ChatColor.YELLOW + "Players: 0");

	    UHCFFAWorld ffa = new UHCFFAWorld(ffaItem, this);
        PotPvP.getInstance().getServer().getPluginManager().registerEvents(ffa, PotPvP.getInstance());

	    // Create TDM kill reward items
	    TDMGame.getKitRuleSets().add(this);
	    this.tdmKillRewardItems.add(ItemStackUtil.createGoldenHead());

        // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 1);

        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 16);
        this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[4] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[9] = new ItemStack(Material.ARROW);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5UHC";
	    this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "ProtIIHelm/Legs";
	    this.info4Sign[2] = "Proj Prot II";
	    this.info4Sign[3] = "Chest/Boots";

	    this.info5Sign[0] = "§dIron Sword";
	    this.info5Sign[1] = "Sharpness I";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power I";
	    this.info6Sign[2] = "Infinity I";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "12 Steak";
	    this.info8Sign[2] = "2 Gold Apples";
	    this.info8Sign[3] = "";
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player damaged = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(damaged);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                    Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

	                if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
                        damager.sendMessage(ChatColor.GOLD + damaged.getName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
                                Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + ChatColor.DARK_RED + " ♥");
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

}
