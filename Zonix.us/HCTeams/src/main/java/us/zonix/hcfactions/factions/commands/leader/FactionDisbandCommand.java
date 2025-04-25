package us.zonix.hcfactions.factions.commands.leader;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.claimwall.ClaimWall;
import us.zonix.hcfactions.claimwall.ClaimWallType;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.events.player.PlayerDisbandFactionEvent;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.claimwall.ClaimWallType;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.PlayerUtility;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class FactionDisbandCommand extends FactionCommand {
    @Command(name = "f.disband", aliases = {"faction.disband", "factions.disband"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction;

        if (command.getArgs().length >= 1 && us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.DEVELOPER)) {
            String name = command.getArgs(0);


            Faction faction = PlayerFaction.getAnyByString(name);
            if (faction != null) {
                if (faction instanceof PlayerFaction) {
                    playerFaction = (PlayerFaction) faction;
                } else {
                    player.sendMessage(langConfig.getString("SYSTEM_FACTION.DELETED").replace("%NAME%", faction.getName()));

                    Faction.getFactions().remove(faction);

                    Set<Claim> claims = new HashSet<>(faction.getClaims());
                    for (Claim claim : claims) {
                        claim.remove();
                    }

                    main.getFactionsDatabase().getDatabase().getCollection("systemFactions").deleteOne(eq("uuid", faction.getUuid().toString()));
                    return;
                }
            } else {
                player.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
                return;
            }
        } else {
            playerFaction = profile.getFaction();

            if (playerFaction == null) {
                player.sendMessage(langConfig.getString("ERROR.NOT_IN_FACTION"));
                return;
            }

            if (!playerFaction.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(langConfig.getString("ERROR.NOT_LEADER"));
                return;
            }
        }

        for (UUID member : playerFaction.getAllPlayerUuids()) {
            Profile memberProfile = Profile.getByUuid(member);

            if (memberProfile != null && memberProfile.getFaction().equals(playerFaction)) {
                memberProfile.setFaction(null);
            }
        }

        profile.setBalance(profile.getBalance() + playerFaction.getBalance());

        Bukkit.getPluginManager().callEvent(new PlayerDisbandFactionEvent(player, playerFaction));

        Bukkit.broadcastMessage(langConfig.getString("ANNOUNCEMENTS.FACTION_DISBANDED").replace("%PLAYER%", player.getName()).replace("%NAME%", playerFaction.getName()));


        Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                main.getFactionsDatabase().getDatabase().getCollection("playerFactions").deleteOne(eq("uuid", playerFaction.getUuid().toString()));
            }
        });

        for (PlayerFaction ally : playerFaction.getAllies()) {
            ally.getAllies().remove(playerFaction);
        }

        Set<Claim> claims = new HashSet<>(playerFaction.getClaims());

        this.removeWalls(claims);

        for (Claim claim : claims) {
            claim.remove();
        }

        Faction.getFactions().remove(playerFaction);
    }

    private void removeWalls(Set<Claim> claims) {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);

                    for(Claim claim : claims) {

                        for (ClaimWallType type : ClaimWallType.values()) {
                            if (claim == null) {
                                continue;
                            }

                            if (!(type.isValid(claim))) {
                                continue;
                            }

                            if (claim.getBorder() == null) {
                                continue;
                            }

                            int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                            int max = player.getLocation().getBlockY() + (type.getRange() / 2);

                            for (Location location : claim.getBorder()) {
                                for (int i = min - (20); i < max + (20); i++) {
                                    location.setY(i);
                                    if (location.getBlock().isEmpty()) {
                                        if (profile.getWalls().containsKey(location)) {
                                            profile.getWalls().get(location).hide(player);
                                            profile.getWalls().remove(location);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(this.main);
    }
}
