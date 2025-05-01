package net.kohi.vaultbattle.manager;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.Permission;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final VaultBattlePlugin plugin;
    private final Team spectatorTeam;

    Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerDataManager(VaultBattlePlugin plugin) {
        this.plugin = plugin;
        spectatorTeam = plugin.getScoreboard().registerNewTeam("spect");
        spectatorTeam.setPrefix(ChatColor.GRAY.toString());
    }

    public PlayerData get(Player player) {
        if (dataMap.get(player.getUniqueId()) != null) {
            return dataMap.get(player.getUniqueId());
        } else {
            dataMap.put(player.getUniqueId(), new PlayerData());
            return dataMap.get(player.getUniqueId());
        }
    }

    public boolean isSpectator(Player player) {
        PlayerData playerData = get(player);
        return playerData.isSpectating();
    }

    public void teleportToSpawn(Player player) {
        player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
    }

    public void makeSpectator(Player player) {
        PlayerData playerData = get(player);
        if (playerData.isSpectating()) {
            return;
        }
        plugin.getTeamManager().getTeams().stream().filter(team -> team.getScoreBoardTeam().hasEntry(player.getName())).forEach(team -> {
            team.getScoreBoardTeam().removeEntry(player.getName());
        });
        playerData.setSpectating(true);
        spectatorTeam.addEntry(player.getName());
        player.spigot().setCollidesWithEntities(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true), true);
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (get(other).isSpectating()) {
                if (other.hasPermission("vaultbattle.hiddenspectator")) {
                    player.hidePlayer(other);
                } else {
                    player.showPlayer(other);
                }
            } else {
                other.hidePlayer(player);
            }
        }
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setLevel(0);
        if (player.hasPermission(Permission.ADMIN)) {
            player.getInventory().addItem(ItemStackUtil.createItem(Material.COMPASS, ChatColor.YELLOW + "Teleporter")); // TODO: mods?
            player.getInventory().addItem(ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.YELLOW + "Players")); // TODO: mods?
        }
        player.getInventory().addItem(ItemStackUtil.createItem(Material.WATCH, ChatColor.YELLOW + "Warps"));
        // allow worldedit compass
        player.addAttachment(plugin, "worldedit.navigation.jumpto.tool", true);
        player.addAttachment(plugin, "worldedit.navigation.thru.tool", true);
    }
}
