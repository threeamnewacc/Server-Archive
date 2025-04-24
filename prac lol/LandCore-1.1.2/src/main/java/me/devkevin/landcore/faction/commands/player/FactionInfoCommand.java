package me.devkevin.landcore.faction.commands.player;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.member.FactionMember;
import me.devkevin.landcore.faction.member.FactionMemberType;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 13:32
 * FactionInfoCommand / me.devkevin.landcore.faction.commands.player / LandCore
 */
public class FactionInfoCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionInfoCommand(LandCore plugin) {
        super("faction.info");
        this.plugin = plugin;
        setAliases("f info");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

            if (factionProfile.getFaction() == null) {
                player.sendMessage(CC.RED + "You are not in a faction!");
                return;
            }
            describeFaction(player, factionProfile.getFaction());
            return;
        }

        String name = args[0];
        Faction faction = this.plugin.getFactionManager().getFaction(name);
        Faction playerFaction = playerFaction(name);

        if (faction == null && playerFaction == null) {
            player.sendMessage(CC.RED + "No faction found with the name or member with the name '" + name + "'");
            return;
        }
        if (playerFaction != null && faction == playerFaction) {
            describeFaction(player, faction);
            return;
        }
        if (faction != null) describeFaction(player, faction);
        if (playerFaction != null) describeFaction(player, playerFaction);
    }

    private void describeFaction(Player player, Faction faction) {
        List<FactionMember> factionMembers = new ArrayList<>();
        faction.getMembers().forEach(u -> factionMembers.add(new FactionMember(u, faction.getLeader().equals(u) ? FactionMemberType.LEADER : faction.getCaptains().contains(u) ? FactionMemberType.CAPTAIN : FactionMemberType.MEMBER)));
        factionMembers.sort(Comparator.comparing(cm -> cm.getType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        factionMembers.forEach(cm -> playerNames.add((faction.getLeader().equals(cm.getUuid()) ? CC.DARK_GREEN + "***" : faction.getCaptains().contains(cm.getUuid()) ? CC.DARK_GREEN + "*" : "") + colorName(cm.getUuid())));

        player.sendMessage(CC.GRAY + CC.BOARD_SEPARATOR);
        player.sendMessage(CC.DARK_GREEN + CC.B + faction.getName() + CC.GRAY + " [" + faction.getMembers().size() + "/12]");
        player.sendMessage(CC.YELLOW + "Description: " + CC.GRAY + faction.getDescription());
        player.sendMessage(CC.YELLOW + "Members: " + Strings.join(playerNames, CC.GRAY + ", "));
        player.sendMessage(CC.YELLOW + "Elo: " + CC.DARK_GREEN + (faction.getElo() == 0 ? "[N/A]" : faction.getElo()));
        player.sendMessage(CC.YELLOW + "Date Created: " + CC.GREEN + faction.getDateCreated());
        player.sendMessage(CC.GOLD + "View this faction on our site: " + CC.GRAY + "www.prac.lol/faction/" + faction.getName());
        player.sendMessage(CC.GRAY + CC.BOARD_SEPARATOR);
    }

    private Faction playerFaction(String name) {
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(name);
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction.getMembers().contains(offlinePlayer.getUniqueId())).findFirst().orElse(null);
    }

    private String colorName(UUID uuid) {
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
        return offlinePlayer.isOnline() ? CC.GREEN + offlinePlayer.getName() : CC.RED + offlinePlayer.getName();
    }
}
