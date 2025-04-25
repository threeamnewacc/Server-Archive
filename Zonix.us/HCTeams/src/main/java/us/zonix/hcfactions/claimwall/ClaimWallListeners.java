package us.zonix.hcfactions.claimwall;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldown;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.factions.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.profile.Profile;

import java.util.Set;

public class ClaimWallListeners implements Listener {

    private FactionsPlugin main;

    public ClaimWallListeners(FactionsPlugin main) {
        this.main = main;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);

                    for (ClaimWallType type : ClaimWallType.values()) {
                        if ((profile.getProtection() == null && type == ClaimWallType.PVP_PROTECTION) || (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) == null && type == ClaimWallType.SPAWN_TAG)) {
                            if ( (profile.getProtection() == null) && profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) == null && !profile.getWalls().isEmpty()) {
                                int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                                int max = player.getLocation().getBlockY() + (type.getRange() / 2);
                                Set<Claim> nearbyClaims = Claim.getNearbyClaimsAt(player.getLocation(),type.getRange() * 2);

                                if (!(nearbyClaims.isEmpty())) {

                                    for (Claim claim : nearbyClaims) {

                                        if(claim == null) {
                                            continue;
                                        }

                                        if (!(type.isValid(claim))) {
                                            continue;
                                        }

                                        if (claim.getBorder() == null) {
                                            continue;
                                        }

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

                            continue;
                        }

                        int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                        int max = player.getLocation().getBlockY() + (type.getRange() / 2);
                        Set<Claim> nearbyClaims = Claim.getNearbyClaimsAt(player.getLocation(),type.getRange() * 2);

                        if (!(nearbyClaims.isEmpty())) {
                            for (Claim claim : nearbyClaims) {

                                if(claim == null) {
                                    continue;
                                }

                                if (!(type.isValid(claim))) {
                                    continue;
                                }

                                if (claim.getBorder() == null) {
                                    continue;
                                }

                                for (Location location : claim.getBorder()) {
                                    for (int i = min - (20); i < max + (20); i++) {
                                        location.setY(i);
                                        if (location.getBlock().isEmpty()) {
                                            if (location.distance(player.getLocation()) <= type.getRange() && !claim.isInside(player.getLocation()) && i < max && i > min) {
                                                if (profile.getProtection() == null && profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) == null && !profile.getWalls().isEmpty()) {
                                                    if (profile.getWalls().containsKey(location)) {
                                                        profile.getWalls().get(location).hide(player);
                                                        profile.getWalls().remove(location);
                                                    }
                                                } else {
                                                    profile.getWalls().put(location, new ClaimWall(type, location).show(player));
                                                }
                                            } else {
                                                if (profile.getWalls().containsKey(location)) {
                                                    profile.getWalls().get(location).hide(player);
                                                    profile.getWalls().remove(location);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!(profile.getWalls().isEmpty())) {
                                for (Location location : profile.getWalls().keySet()) {
                                    if (profile.getWalls().containsKey(location)) {
                                        profile.getWalls().get(location).hide(player);
                                    }
                                }
                                profile.getWalls().clear();
                            }
                        }

                    }

                }
            }
        }.runTaskTimerAsynchronously(main, 2L, 2L);
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) != null) {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) {
                if (ClaimWallType.SPAWN_TAG.isValid(entering)) {

                    if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                        player.sendMessage(ProfileCooldownType.SPAWN_TAG.getMessage());
                        player.sendMessage(main.getLanguageConfig().getString("SPAWN_TAG.PEARL_REFUNDED"));

                        ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ENDER_PEARL);
                        if (cooldown != null) {
                            profile.getCooldowns().remove(cooldown);
                            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                        }

                    }

                    event.setCancelled(true);
                }
            }
        }

        if (profile.getProtection() != null) {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) {
                if (ClaimWallType.PVP_PROTECTION.isValid(entering)) {
                    event.setCancelled(true);
                    player.sendMessage(main.getLanguageConfig().getString("PVP_PROTECTION.CANT_ENTER").replace("%FACTION%", entering.getFaction().getName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
                }
            }
        }
    }

}
