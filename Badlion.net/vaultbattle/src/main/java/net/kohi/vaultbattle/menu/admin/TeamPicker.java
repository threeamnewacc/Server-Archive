package net.kohi.vaultbattle.menu.admin;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.menu.AbstractMenu;
import net.kohi.vaultbattle.type.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class TeamPicker extends AbstractMenu {

    private final Team[] teams;
    private EditType type;
    private Location location;
    private Region region;

    public TeamPicker(VaultBattlePlugin plugin, EditType type, Location location, Region region) {
        super(plugin, 9, "Team Picker Menu");
        this.teams = plugin.getTeamManager().getTeams().toArray(new Team[0]);
        if (region != null && !type.equals(EditType.SETSPAWN)) {
            this.region = region;
            this.type = type;
        } else if (location != null && type.equals(EditType.SETSPAWN)) {
            this.type = type;
            this.location = location;
        } else {
            return;
        }
        for (int i = 0; i < teams.length; i++) {
            ItemStack item = ItemStackUtil.createItem(Material.STAINED_CLAY, teams[i].getName());
            inventory.setItem(i, item);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if (inventory.equals(event.getClickedInventory())) {
            if (event.getSlot() < teams.length && event.getSlot() >= 0) {
                Team team = teams[event.getSlot()];
                GameMap map = plugin.getGameMapManager().getMapEditing(player);
                if (map != null) {
                    if (type.equals(EditType.SETSPAWN) && location != null) {
                        if (map.getTeamSpawns().containsKey(team.getColor())) {
                            map.getTeamSpawns().remove(team.getColor());
                        }
                        map.getTeamSpawns().put(team.getColor(), new SimpleLocation(location));
                        player.sendMessage(ChatColor.GREEN + "Set team spawnpoint for team " + team.getName());

                    } else if (type.equals(EditType.SETBANK) && region != null) {
                        if (map.getBanks().containsKey(team.getColor())) {
                            map.getBanks().remove(team.getColor());
                        }
                        map.getBanks().put(team.getColor(), region);
                        player.sendMessage(ChatColor.GREEN + "Set bank region for team " + team.getName());
                    }
                    player.closeInventory();
                    plugin.getGameMapManager().save();
                } else {
                    player.sendMessage(ChatColor.RED + "You are not editing any map.");
                    return;
                }
            }
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}
