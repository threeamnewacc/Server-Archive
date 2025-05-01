package net.kohi.vaultbattle.type;

import net.kohi.sidebar.item.SidebarItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private String name;

    private TeamColor color;

    private ItemStack joinItem;

    private transient List<UUID> players = new ArrayList<>();

    private transient double bank = 0;

    private transient List<Location> bankBlocks = new ArrayList<>();

    private org.bukkit.scoreboard.Team scoreBoardTeam;

    private boolean eliminated = false;

    private SidebarItem playerCountSidebar = new SidebarItem(10) {
        @Override
        public String getText() {
            return name + " Players: " + ChatColor.WHITE + players.size();
        }
    };

    private SidebarItem bankSidebar = new SidebarItem(10) {
        @Override
        public String getText() {
            return name + " Bank: " + ChatColor.WHITE + getBankRounded();
        }
    };

    public int getBankRounded() {
        return (int) Math.ceil(bank);
    }

    public List<Player> getOnlinePlayers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                online.add(player);
            }
        }
        return online;
    }

    public int size() {
        return players.size();
    }

    public boolean canRespawn() {
        if (bank >= 6) {
            return true;
        }
        return false;
    }

    public void destroyBankBlock() {
        bank--;
    }

    public void broadcastSound(Sound sound, float volume, float pitch) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public TeamColor getColor() {
        return this.color;
    }

    public ItemStack getJoinItem() {
        return this.joinItem;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public double getBank() {
        return this.bank;
    }

    public List<Location> getBankBlocks() {
        return this.bankBlocks;
    }

    public org.bukkit.scoreboard.Team getScoreBoardTeam() {
        return this.scoreBoardTeam;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public SidebarItem getPlayerCountSidebar() {
        return this.playerCountSidebar;
    }

    public SidebarItem getBankSidebar() {
        return this.bankSidebar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(TeamColor color) {
        this.color = color;
    }

    public void setJoinItem(ItemStack joinItem) {
        this.joinItem = joinItem;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public void setBankBlocks(List<Location> bankBlocks) {
        this.bankBlocks = bankBlocks;
    }

    public void setScoreBoardTeam(org.bukkit.scoreboard.Team scoreBoardTeam) {
        this.scoreBoardTeam = scoreBoardTeam;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    public void setPlayerCountSidebar(SidebarItem playerCountSidebar) {
        this.playerCountSidebar = playerCountSidebar;
    }

    public void setBankSidebar(SidebarItem bankSidebar) {
        this.bankSidebar = bankSidebar;
    }
}
