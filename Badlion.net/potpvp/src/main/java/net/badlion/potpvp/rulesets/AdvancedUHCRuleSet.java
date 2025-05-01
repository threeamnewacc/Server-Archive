package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class AdvancedUHCRuleSet extends KitRuleSet {

    public AdvancedUHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.SKULL_ITEM, 1, (short) 3), ArenaManager.ArenaType.NON_PEARL, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
        }

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

        this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 2);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 16);
        this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[4] = ItemStackUtil.createGoldenHead();
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 64);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Advance UHC";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dIron Armor";
        this.info4Sign[1] = "ProtIIHelm/Legs";
        this.info4Sign[2] = "Proj Prot II";
        this.info4Sign[3] = "Chest/Boots";

        this.info5Sign[0] = "§dIron Sword";
        this.info5Sign[1] = "Sharpness II";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dBow";
        this.info6Sign[1] = "Power II";
        this.info6Sign[2] = "";
        this.info6Sign[3] = "";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "16 Steak";
        this.info8Sign[2] = "1 Gold Apple";
        this.info8Sign[3] = "1 Gold Head";
    }

    @EventHandler
    public void healthRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    e.setCancelled(true);
                }
            }
        }
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
    public void onEatGoldenHead(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        final Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
            if (item.getType().equals(Material.GOLDEN_APPLE)) {
               BukkitUtil.runTaskNextTick(new Runnable() {
                    @Override
                    public void run() {
                        player.removePotionEffect(PotionEffectType.ABSORPTION);
                    }
                });
            }
        }
    }

}
