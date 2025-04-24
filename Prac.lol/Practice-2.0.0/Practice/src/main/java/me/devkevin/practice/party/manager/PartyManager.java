package me.devkevin.practice.party.manager;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.TaskUtil;
import me.devkevin.practice.util.TtlHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 28/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class PartyManager {

    private final Practice plugin = Practice.getInstance();

    private final Map<UUID, List<UUID>> partyInvites = new TtlHashMap<>(TimeUnit.SECONDS, 15);
    private final Map<UUID, Party> parties = new HashMap<>();
    private final Map<UUID, UUID> partyLeaders = new HashMap<>();

    public boolean isLeader(UUID uuid) {
        return this.parties.containsKey(uuid);
    }

    public boolean hasPartyInvite(UUID player, UUID other) {
        return this.partyInvites.get(player) != null && this.partyInvites.get(player).contains(other);
    }

    public void removePartyInvites(UUID uuid) {
        this.partyInvites.remove(uuid);
    }

    public void createPartyInvite(UUID requester, UUID requested) {
        this.partyInvites.computeIfAbsent(requested, k -> new ArrayList<>()).add(requester);
    }

    public boolean isInParty(UUID player, Party party) {
        Party targetParty = this.getParty(player);
        return targetParty != null && targetParty.getLeader() == party.getLeader();
    }

    private void givePartyItems(Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getHotbarItem().getPartyItems());
        player.updateInventory();
    }

    public Party getParty(UUID uuid) {
        if (this.parties.containsKey(uuid)) {
            return this.parties.get(uuid);
        }
        if (this.partyLeaders.containsKey(uuid)) {
            UUID leader = this.partyLeaders.get(uuid);
            return this.parties.get(leader);
        }
        return null;
    }

    public void createParty(Player player) {
        Party party = new Party(player.getUniqueId());
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        this.parties.put(player.getUniqueId(), party);
        this.plugin.getPartyMenu().addParty(player);
        this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(player);

        profile.setState(ProfileState.PARTY);

        givePartyItems(player);

        player.sendMessage(CC.YELLOW + "You have created a party.");
        player.sendMessage(CC.GRAY + CC.I + "Note: " + CC.RED + CC.I + "Party system is under development if you see any bug report or contact with any admin please.");
    }

    private void disbandParty(Party party, boolean tournament) {
        this.plugin.getPartyMenu().removeParty(party);
        this.parties.remove(party.getLeader());

        if (party.getBroadcastTask() != null) {
            party.getBroadcastTask().cancel();
        }

        party.broadcast(CC.RED + "Your party has been disbanded.");

        party.members().forEach(member -> {
            Profile profile = this.plugin.getProfileManager().getProfileData(member.getUniqueId());

            if (this.partyLeaders.get(profile.getUuid()) != null) {
                this.partyLeaders.remove(profile.getUuid());
            }

            if (profile.getState() == ProfileState.SPAWN) {
                this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(member);
            }
        });
    }

    public void joinParty(UUID leader, Player player) {
        Party party = this.getParty(leader);
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        this.partyLeaders.put(player.getUniqueId(), leader);
        party.addMember(player.getUniqueId());
        this.plugin.getPartyMenu().updateParty(party);
        this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(player);

        profile.setState(ProfileState.PARTY);
        givePartyItems(player);

        party.broadcast(LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GREEN + " has joined the party.");
    }

    public void leaveParty(Player player) {
        Party party = this.getParty(player.getUniqueId());

        if (party == null) {
            return;
        }

        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (this.parties.containsKey(player.getUniqueId())) {
            this.disbandParty(party, false);
        } else {
            party.broadcast(LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.RED + " has left the party");
            party.removeMember(player.getUniqueId());

            party.getBards().remove(player.getUniqueId());
            party.getArchers().remove(player.getUniqueId());

            this.partyLeaders.remove(player.getUniqueId());

            this.plugin.getPartyMenu().updateParty(party);

            if (profile.getState() == ProfileState.SPECTATING) {
                this.plugin.getProfileManager().sendToSpawn(player);
                return;
            } else {
                this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(player);
            }
        }

        switch (profile.getState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, profile, false);
                break;
            case SPECTATING:
                if(this.plugin.getEventManager().getSpectators().contains(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                } else {
                    this.plugin.getMatchManager().removeSpectator(player);
                }
                this.plugin.getProfileManager().sendToSpawn(player);
                break;
            case SPAWN:
                TaskUtil.runLater(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getInventory().getContents() == null) {
                            player.getInventory().setContents(plugin.getHotbarItem().getSpawnItems());
                        }
                        player.updateInventory();
                    }
                }, 3L);
                break;
        }

        party.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> {
            member.hidePlayer(player);
            player.hidePlayer(member);
        });

        this.plugin.getProfileManager().sendToSpawnAndResetButNotTP(player);
    }
}
