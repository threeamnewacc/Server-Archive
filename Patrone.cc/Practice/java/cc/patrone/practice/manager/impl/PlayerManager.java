package cc.patrone.practice.manager.impl;

import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.util.ItemBuilder;
import cc.patrone.practice.util.Items;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class PlayerManager extends Manager {

    public PlayerManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public void resetPlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }
        PracticeProfile practiceProfile = this.managerHandler.getProfileManager().getProfile(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        player.setExhaustion(0);
        player.setSaturation(5);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        player.getActivePotionEffects().stream().forEach(p -> player.removePotionEffect(p.getType()));
        player.setFireTicks(0);
        player.setFlySpeed(0.4F);
        player.setMaximumNoDamageTicks(19);
    }

    public void giveItems(Player player, boolean rematch) {
        if (!player.isOnline()) {
            return;
        }
        resetPlayer(player);
        player.getInventory().setItem(0, Items.UNRANKED.getItem());
        player.getInventory().setItem(1, Items.RANKED.getItem());
        player.getInventory().setItem(7, Items.STATS.getItem());
        player.getInventory().setItem(8, Items.EDIT_KIT.getItem());
        player.updateInventory();
    }

    public void teleportSpawn(Player player) {
        if (!player.isOnline()) {
            return;
        }
        PracticeProfile practiceProfile = this.managerHandler.getProfileManager().getProfile(player);
        practiceProfile.setPlayerState(PlayerState.LOBBY);
        player.teleport(this.managerHandler.getSettingsManager().getSpawn());
        hideAllPlayers(player);
        if (player.hasPermission("core.donor")) {
            player.setAllowFlight(true);
        }
        showAll(player);

    }

    public void giveLeaveQueueItems(Player player) {
        if (!player.isOnline()) {
            return;
        }
        resetPlayer(player);
        player.getInventory().setItem(0, Items.VIEW_QUEUE.getItem());
        player.getInventory().setItem(8, Items.LEAVE_QUEUE.getItem());
        player.updateInventory();
    }

    public void removeFromQueue(Player player) {
        Arrays.stream(Kit.values()).forEach(k -> {
            k.getUnrankedQueue().remove(player.getUniqueId());
            k.getRankedQueue().remove(player.getUniqueId());
        });
    }

    public void hideAllPlayers(Player player) {
        if (!player.isOnline()) {
            return;
        }
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            p.hidePlayer(player);
            player.hidePlayer(p);
        });
    }

    public void showStats(Player player) {
        Inventory inventory = this.managerHandler.getPlugin().getServer().createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Your Stats");
        PracticeProfile practiceProfile = this.managerHandler.getProfileManager().getProfile(player);
        Arrays.stream(Kit.values()).forEach(k -> inventory.setItem(k.getId(), new ItemBuilder(k.getDisplay()).setName(ChatColor.LIGHT_PURPLE + k.getName()).setLore(ChatColor.DARK_PURPLE + "Your elo: " + ChatColor.LIGHT_PURPLE + practiceProfile.getElo(k)).toItemStack()));
        player.openInventory(inventory);
    }

    public void giveSpectateItems(Player player) {
        if (!player.isOnline()) {
            return;
        }
        resetPlayer(player);
        player.getInventory().setItem(0, Items.MATCH_INFO.getItem());
        player.getInventory().setItem(8, Items.STOP_SPECTATING.getItem());
        player.updateInventory();
    }

    public void hideAll(Player player) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            player.hidePlayer(p);
            ((Player) p).hidePlayer(player);
        });
    }

    public void showAll(Player player) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            player.showPlayer(p);
            ((Player) p).showPlayer(player);
        });
    }
}
