package us.zonix.hcfactions.profile.cooldown;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import us.zonix.hcfactions.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.zonix.hcfactions.profile.Profile;

public class ProfileCooldownListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.getGameMode() == GameMode.CREATIVE) return;

                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ENDER_PEARL);

                if (cooldown != null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(cooldown.getType().getMessage().replace("%TIME%", cooldown.getTimeLeft()));
                    return;
                }

                profile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.ENDER_PEARL, ProfileCooldownType.ENDER_PEARL.getDuration()));
                profile.setPearlLocation(player.getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {


        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 1) {

            if (player.getGameMode() == GameMode.CREATIVE) return;

            ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.GOLDEN_APPLE);

            if (cooldown != null) {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(cooldown.getType().getMessage().replace("%TIME%", cooldown.getTimeLeft()));
                return;
            }

            profile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.GOLDEN_APPLE, ProfileCooldownType.GOLDEN_APPLE.getDuration()));
            profile.setPearlLocation(player.getLocation());

        }

    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Profile.getByPlayer(event.getEntity()).getCooldowns().clear();
    }

}
