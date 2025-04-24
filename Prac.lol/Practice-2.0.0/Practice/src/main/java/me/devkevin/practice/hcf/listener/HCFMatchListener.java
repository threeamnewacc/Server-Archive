package me.devkevin.practice.hcf.listener;

import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:19
 * HCFMatchListener / me.devkevin.practice.hcf.listener / Practice
 */
public class HCFMatchListener implements Listener {
    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            return;
        }

        Profile data = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (data == null) {
            return;
        }
        if (!data.isFighting()) {
            return;
        }

        Match match = this.plugin.getMatchManager().getMatch(data);
        if (match == null) {
            return;
        }
        if (!match.getKit().getName().equalsIgnoreCase("HCF")) {
            return;
        }

        for (PotionEffect Effect : player.getActivePotionEffects()) {
            if (Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                final double damageVal = (Effect.getAmplifier() + 1) * 1.3 + 1.0;
                int newDamage;
                if (event.getDamage() / damageVal <= 1.0) {
                    newDamage = (Effect.getAmplifier() + 1) * 3 + 1;
                } else {
                    newDamage = (int) (event.getDamage() / damageVal) + (Effect.getAmplifier() + 1) * 3;
                }

                event.setDamage(newDamage);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());


        Match match = this.plugin.getMatchManager().getMatch(profile);

        if (match != null) {
            if (match.getKit().isHcf()) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    switch (event.getClickedBlock().getType()) {
                        case FENCE_GATE:
                        case ACACIA_FENCE_GATE:
                        case BIRCH_FENCE_GATE:
                        case DARK_OAK_FENCE_GATE:
                        case SPRUCE_FENCE_GATE:
                        case JUNGLE_FENCE_GATE:
                            if (match.getTeams().get(profile.getTeamID()).getTeamID() == 1) {
                                event.setCancelled(true);
                            }
                            break;
                    }
                }
            }
        }
    }
}
