package cc.patrone.practice.match;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.arena.Arena;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.util.EloUtil;
import cc.patrone.practice.util.InventorySnapshot;
import cc.patrone.practice.util.Items;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Match {

    private final PracticePlugin plugin;
    private final Player p1;
    private final Player p2;
    private final Kit kit;
    private final boolean ranked;
    private Arena arena;
    private PracticeProfile profile1;
    private PracticeProfile profile2;
    private List<UUID> spectators = new ArrayList<>();
    private MatchState matchState = MatchState.STARTING;
    private List<Item> droppedItems = new ArrayList<>();

    public void start() {
        arena = this.plugin.getManagerHandler().getArenaManager().randomArena(kit.isSumo());
        profile1 = this.plugin.getManagerHandler().getProfileManager().getProfile(p1);
        profile2 = this.plugin.getManagerHandler().getProfileManager().getProfile(p2);
        this.plugin.getManagerHandler().getPlayerManager().hideAll(p1);
        this.plugin.getManagerHandler().getPlayerManager().hideAll(p2);
        p1.sendMessage(ChatColor.LIGHT_PURPLE + "Found a match against " + ChatColor.DARK_PURPLE + p2.getName() + (ranked ? " (" + profile2.getElo(kit) + ")" : ""));
        p2.sendMessage(ChatColor.LIGHT_PURPLE + "Found a match against " + ChatColor.DARK_PURPLE + p1.getName() + (ranked ? " (" + profile1.getElo(kit) + ")" : ""));
        p1.sendMessage(ChatColor.LIGHT_PURPLE + "Arena: " + ChatColor.DARK_PURPLE + arena.getName());
        p2.sendMessage(ChatColor.LIGHT_PURPLE + "Arena: " + ChatColor.DARK_PURPLE + arena.getName());
        arena.getSpawn1().getChunk().load();
        arena.getSpawn2().getChunk().load();

        this.plugin.getManagerHandler().getPlayerManager().hideAllPlayers(p1);
        this.plugin.getManagerHandler().getPlayerManager().hideAllPlayers(p2);

        p1.teleport(arena.getSpawn1());
        p2.teleport(arena.getSpawn2());

        this.plugin.getManagerHandler().getPlayerManager().resetPlayer(p1);
        if (profile1.getCustomKit(kit) == null) {
            p1.getInventory().setContents(kit.getInventory().getContents());
            p1.getInventory().setArmorContents(kit.getArmor());
        } else {
            p1.getInventory().setItem(0, Items.CUSTOM_KIT.getItem());
            p1.getInventory().setItem(8, Items.DEFAULT_KIT.getItem());
        }
        p1.updateInventory();

        this.plugin.getManagerHandler().getPlayerManager().resetPlayer(p2);
        if (profile2.getCustomKit(kit) == null) {
            p2.getInventory().setContents(kit.getInventory().getContents());
            p2.getInventory().setArmorContents(kit.getArmor());
        } else {
            p2.getInventory().setItem(0, Items.CUSTOM_KIT.getItem());
            p2.getInventory().setItem(8, Items.DEFAULT_KIT.getItem());
        }
        p2.updateInventory();


        p1.showPlayer(p2);
        p2.showPlayer(p1);

        profile1.setMatch(this);
        profile2.setMatch(this);
        if (ranked) {
            kit.getRankedQueue().remove(p1.getUniqueId());
            kit.getRankedQueue().remove(p2.getUniqueId());
            kit.getRankedMatch().add(p1.getUniqueId());
            kit.getRankedMatch().add(p2.getUniqueId());
        } else {
            kit.getUnrankedQueue().remove(p1.getUniqueId());
            kit.getUnrankedQueue().remove(p2.getUniqueId());
            kit.getUnrankedMatch().add(p1.getUniqueId());
            kit.getUnrankedMatch().add(p2.getUniqueId());
        }
        profile1.setPlayerState(PlayerState.MATCH);
        profile2.setPlayerState(PlayerState.MATCH);
        new BukkitRunnable() {
            int i = 5;

            public void run() {
                if (matchState != MatchState.STARTING) {
                    this.cancel();
                    return;
                }
                if (i > 0) {
                    broadcast(ChatColor.DARK_PURPLE + "Starting in " + ChatColor.LIGHT_PURPLE + i + "...");
                    playSound(Sound.NOTE_STICKS);
                    i--;
                } else {
                    broadcast(ChatColor.GREEN + "The match has started!");
                    playSound(Sound.NOTE_PLING);
                    this.cancel();
                    matchState = MatchState.STARTED;
                    return;
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    public void end(Player loser) {
        if (matchState == MatchState.ENDING) {
            return;
        }

        droppedItems.stream().forEach(d -> d.remove());

        matchState = MatchState.ENDING;

        new InventorySnapshot(p1, p2);
        new InventorySnapshot(p2, p1);
        Player winner = loser == p1 ? p2 : p1;
        winner.hidePlayer(loser);
        this.plugin.getManagerHandler().getPlayerManager().resetPlayer(p1);
        this.plugin.getManagerHandler().getPlayerManager().resetPlayer(p2);

        TextComponent winnerComponent = new TextComponent(ChatColor.DARK_PURPLE + "Winner: ");
        TextComponent winnerName = new TextComponent(ChatColor.GREEN + winner.getName());
        winnerName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + winner.getName()));
        winnerComponent.addExtra(winnerName);

        TextComponent loserComponent = new TextComponent(ChatColor.DARK_PURPLE + "Loser: ");
        TextComponent loserName = new TextComponent(ChatColor.RED + loser.getName());
        loserName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + loser.getName()));
        loserComponent.addExtra(loserName);

        broadcast(ChatColor.LIGHT_PURPLE + "Post match inventories " + ChatColor.GRAY + "(Click to view)");
        broadcast(winnerComponent);
        broadcast(loserComponent);

        if (ranked) {
            PracticeProfile winnerProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(winner);
            PracticeProfile loserProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(loser);
            int winnerElo = winnerProfile.getElo(kit);
            int loserElo = loserProfile.getElo(kit);
            int[] newElo = EloUtil.getNewRankings(winnerElo, loserElo, true);
            int eloDifference = Math.abs(newElo[0] - winnerElo);
            winnerProfile.setElo(kit, newElo[0]);
            loserProfile.setElo(kit, newElo[1]);
            String eloChanges = ChatColor.DARK_PURPLE + "Elo Changes" + ChatColor.GRAY + ": " + ChatColor.GREEN + winner.getName() + " +" + eloDifference + " (" + newElo[0] + ") " + ChatColor.RED + loser.getName() + " -" + eloDifference + " (" + newElo[1] + ")";
            broadcast(eloChanges);
        }
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            this.plugin.getManagerHandler().getPlayerManager().giveItems(p1, true);
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(p1);
            this.plugin.getManagerHandler().getPlayerManager().giveItems(p2, true);
            this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(p2);
            if (ranked) {
                kit.getRankedMatch().remove(p1.getUniqueId());
                kit.getRankedMatch().remove(p2.getUniqueId());
            } else {
                kit.getUnrankedMatch().remove(p1.getUniqueId());
                kit.getUnrankedMatch().remove(p2.getUniqueId());
            }
            spectators.stream().forEach(u -> {
                Player spectator = this.plugin.getServer().getPlayer(u);
                PracticeProfile spectatorProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(spectator);
                this.plugin.getManagerHandler().getPlayerManager().giveItems(spectator, false);
                this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(spectator);
                spectatorProfile.setSpectating(null);
            });

            profile1.setMatch(null);
            profile1.setHits(0);
            profile1.setCombo(0);
            profile1.setThrownPots(0);
            profile1.setLastPearl(0);
            profile2.setFullyLandedPots(0);
            profile2.setMatch(null);
            profile2.setHits(0);
            profile2.setCombo(0);
            profile2.setThrownPots(0);
            profile2.setFullyLandedPots(0);
            profile2.setLastPearl(0);

        }, 60L);
    }

    public void broadcast(String message) {
        p1.sendMessage(message);
        p2.sendMessage(message);
        spectators.stream().forEach(u -> this.plugin.getServer().getPlayer(u).sendMessage(message));
    }

    public void broadcast(TextComponent message) {
        p1.sendMessage(message);
        p2.sendMessage(message);
        spectators.stream().forEach(u -> this.plugin.getServer().getPlayer(u).sendMessage(message));
    }

    public void playSound(Sound sound) {
        p1.playSound(p1.getLocation(), sound, 20L, 20L);
        p2.playSound(p2.getLocation(), sound, 20L, 20L);
        spectators.stream().forEach(u -> this.plugin.getServer().getPlayer(u).playSound(this.plugin.getServer().getPlayer(u).getLocation(), sound, 20L, 20L));
    }
}
