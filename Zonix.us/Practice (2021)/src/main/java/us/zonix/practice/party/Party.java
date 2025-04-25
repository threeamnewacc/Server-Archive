package us.zonix.practice.party;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.selection.ClassSelectionMenu;

public class Party {
    public static final ClassSelectionMenu CLASS_SELECTION_MENU = new ClassSelectionMenu();
    private final Practice plugin = Practice.getInstance();
    private final UUID leader;
    private final List<UUID> members = new ArrayList<>();
    private int limit = 50;
    private boolean open;
    private final Set<UUID> bards = new HashSet<>();
    private final Set<UUID> archers = new HashSet<>();

    public Party(UUID leader) {
        this.leader = leader;
        this.members.add(leader);
    }

    public void addMember(UUID uuid) {
        this.members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    public void broadcast(String message) {
        this.members().forEach(member -> member.sendMessage(message));
    }

    public MatchTeam[] split() {
        List<UUID> teamA = new ArrayList<>();
        List<UUID> teamB = new ArrayList<>();

        for (UUID member : this.members) {
            if (teamA.size() == teamB.size()) {
                teamA.add(member);
            } else {
                teamB.add(member);
            }
        }

        return new MatchTeam[]{new MatchTeam(teamA.get(0), teamA, 0), new MatchTeam(teamB.get(0), teamB, 1)};
    }

    public List<Player> diamonds() {
        List<Player> available = new ArrayList<>();

        for (UUID uuid : this.members) {
            if (!this.archers.contains(uuid) && !this.bards.contains(uuid) && Bukkit.getPlayer(uuid) != null) {
                available.add(Bukkit.getPlayer(uuid));
            }
        }

        return available;
    }

    public void addArcher(Player player) {
        this.archers.add(player.getUniqueId());
    }

    public void addBard(Player player) {
        this.bards.add(player.getUniqueId());
    }

    public Stream<Player> members() {
        return this.members.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int getMaxArchers() {
        if (this.members.size() <= 9) {
            return 1;
        } else if (this.members.size() <= 15) {
            return 2;
        } else {
            return this.members.size() > 16 ? 3 : 0;
        }
    }

    public int getMaxBards() {
        if (this.members.size() <= 8) {
            return 1;
        } else if (this.members.size() <= 9) {
            return 2;
        } else {
            return this.members.size() > 10 ? 3 : 0;
        }
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public List<UUID> getMembers() {
        return this.members;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean isOpen() {
        return this.open;
    }

    public Set<UUID> getBards() {
        return this.bards;
    }

    public Set<UUID> getArchers() {
        return this.archers;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
