package club.minemen.hcfactions.modmode.profile;

import club.minemen.core.util.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Profile {

    @Getter private static Map<UUID, Profile> profiles = new HashMap<>();

    @Getter private final UUID uuid;
    @Getter private boolean vanished;
    @Getter @Setter private ItemStack[] contents, armor;
    @Getter @Setter private GameMode gamemode;

    public Profile(UUID uuid) {
        this.uuid = uuid;

        Player player = this.getPlayer();

        this.contents = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.gamemode = player.getGameMode();

        setVanished(true);
        setup();

        profiles.put(this.uuid, this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void setup() {
        Player player = this.getPlayer();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).name(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Teleport Tool").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.BOOK).name(ChatColor.AQUA + ChatColor.BOLD.toString() + "Inventory Viewer").build());
        player.getInventory().setItem(2, new ItemBuilder(Material.CARPET).name(" ").build());

        if (player.hasPermission("modmode.admin")) {
            player.getInventory().setItem(4, new ItemBuilder(Material.WOOD_AXE).name(ChatColor.RED + ChatColor.BOLD.toString() + "WorldEdit Wand").build());
        }

        player.getInventory().setItem(6, new ItemBuilder(Material.DIAMOND_PICKAXE).name(ChatColor.AQUA + ChatColor.BOLD.toString() + "X-Ray Finder").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.SKULL_ITEM).durability(3).name(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Online Staff").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK).durability(8).name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Become Visible").build());

        player.setGameMode(GameMode.CREATIVE);
        player.updateInventory();
    }

    public void setVanished(boolean vanished) {
        Player player = this.getPlayer();

        this.vanished = vanished;

        if (vanished) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!(other.hasPermission("modmode.staff"))) {
                    if (!(other.equals(player))) {
                        other.hidePlayer(player);
                    }
                }
                else {
                    Scoreboard scoreboard = other.getScoreboard();

                    if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        other.setScoreboard(scoreboard);
                    }

                    Team team = scoreboard.getTeam("hidden");

                    if (team == null) {
                        team = scoreboard.registerNewTeam("hidden");
                        team.setPrefix(ChatColor.GRAY + "");
                        team.setCanSeeFriendlyInvisibles(true);
                    }

                    team.addEntry(player.getName());
                    other.showPlayer(player);
                }
            }
        }
        else {
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(player);

                Scoreboard scoreboard = other.getScoreboard();

                if (!(scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard()))) {
                    Team team = scoreboard.getTeam("hidden");

                    if (team == null) {
                        continue;
                    }

                    team.removeEntry(player.getName());
                }
            }
        }
    }

    public static Profile getByUuid(UUID uuid) {
        return profiles.get(uuid);
    }

}
