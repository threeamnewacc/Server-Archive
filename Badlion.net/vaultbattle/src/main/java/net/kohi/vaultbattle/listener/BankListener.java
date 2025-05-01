package net.kohi.vaultbattle.listener;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.Phase;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.Region;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class BankListener implements Listener {

    private final VaultBattlePlugin plugin;

    public BankListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameMapManager().isEditing(player)) {
            return;
        }
        if (!plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            event.setCancelled(true);
            return;
        }
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data.isSpectating()) {
            event.setCancelled(true);
            return;
        }
        if (event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            return;
        }
        handleOres(event);
        Team team = data.getTeam();
        if (team != null) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (block.getType().equals(Material.EMERALD_BLOCK)) {
                Region region = plugin.getGameManager().getMap().getBanks().get(team.getColor());
                if (region.isInside(location)) {
                    event.setCancelled(true);
                    player.sendFormattedMessage("{0}You can not destroy your own bank blocks.", ChatColor.RED);
                    return;
                }
                plugin.getGameManager().getMap().getBanks().entrySet().stream().filter(entry -> entry.getValue().isInside(location)).forEach(entry -> {
                    plugin.getTeamManager().getTeams().stream().filter(t -> t.getColor().equals(entry.getKey())).forEach(t -> {
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            player.sendFormattedMessage("{0}Your invisibility has been removed since you broke a bank block.", ChatColor.GOLD);
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                        int breakAmount = plugin.getGameManager().getBreakAmount();
                        event.setCancelled(true);
                        team.broadcastMessage(ChatColor.GREEN + player.getName() + " broke " + breakAmount + " block" + (breakAmount > 1 ? "s" : "") + " from " + t.getName() + "'s bank.");
                        t.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "IRONGOLEM_HIT", "ENTITY_IRONGOLEM_HURT"), 1.0F, 1.0F);
                        for (int i = 0; i < breakAmount; i++) {
                            Location bankDestroy = plugin.getGameManager().getMap().getBanks().get(t.getColor()).getBlock(t.getBankRounded());
                            bankDestroy.getBlock().setType(Material.AIR);
                            bankDestroy.getWorld().playEffect(bankDestroy, Effect.STEP_SOUND, 133);
                            t.destroyBankBlock();
                            if (t.getBank() <= 0) {
                                for (UUID memberId : t.getPlayers()) {
                                    Player member = Bukkit.getPlayer(memberId);
                                    if (member != null) {
                                        plugin.getPlayerDataManager().teleportToSpawn(member);
                                        member.getInventory().clear();
                                        member.getInventory().setHelmet(null);
                                        member.getInventory().setChestplate(null);
                                        member.getInventory().setLeggings(null);
                                        member.getInventory().setBoots(null);
                                        for (PotionEffect effect : member.getActivePotionEffects()) {
                                            member.removePotionEffect(effect.getType());
                                        }
                                        ChatSettingsManager.getChatSettings(member).setActiveChannel("G");
                                        member.sendFormattedMessage("{0}You are now in global chat.", ChatColor.GREEN);
                                        plugin.getPlayerDataManager().makeSpectator(member);
                                    }
                                }
                                Bukkit.broadcastMessage(t.getName() + " Team " + ChatColor.RED + " has been eliminated from the game");
                                t.setEliminated(true);
                                plugin.getGameManager().checkForWinner();
                                break;
                            }
                            player.getWorld().dropItem(player.getLocation().add(0, 1, 0), new ItemStack(Material.EMERALD_BLOCK));
                        }
                        event.setCancelled(true);
                    });
                });
            }
            if (!event.isCancelled()) {
                plugin.getGameManager().getMap().getBanks().values().forEach(bank -> {
                    if (bank.isInside(location, 1)) {
                        event.setCancelled(true);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameMapManager().isEditing(player)) {
            return;
        }
        if (!plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            event.setCancelled(true);
            return;
        }
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data.isSpectating()) {
            event.setCancelled(true);
            return;
        }
        Team team = data.getTeam();
        if (team != null) {
            plugin.getGameManager().getMap().getBanks().entrySet().stream().filter(entry -> entry.getValue().isInside(event.getBlock().getLocation(), 1)).forEach(entry -> {
                event.setCancelled(true);
                player.sendFormattedMessage("{0}You can not place blocks in a bank.", ChatColor.RED);
            });

        }
    }

    private void handleOres(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (item == null) {
            // wasn't going to drop anything anmyway
            return;
        }
        int fortune = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int amount;
        Material type;
        int exp = 0;
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                type = Material.DIAMOND;
                amount = fortune + 2;
                exp = 2;
                break;
            case IRON_ORE:
                type = Material.IRON_INGOT;
                amount = fortune + 2;
                exp = 1;
                break;
            case GOLD_ORE:
                type = Material.GOLD_INGOT;
                amount = fortune + 2;
                exp = 1;
                break;
            case COAL_ORE:
                type = Material.COAL;
                amount = fortune + 3;
                break;
            default:
                return;
        }
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        for (int i = 0; i < amount; i++) {
            loc.getWorld().dropItem(loc, new ItemStack(type));
        }
        ((ExperienceOrb) loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB)).setExperience(exp);
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        if (item.getType().getMaxDurability() == 0) {
            // not a "tool" type of item
            return;
        }
        // damage item
        item.setDurability((short) (item.getDurability() + 1));
        // fake break item if needed
        if (item.getDurability() == item.getType().getMaxDurability()) {
            event.getPlayer().setItemInHand(null);
        }
    }
}
