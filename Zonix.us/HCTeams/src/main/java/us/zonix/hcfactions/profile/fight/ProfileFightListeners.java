package us.zonix.hcfactions.profile.fight;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.killer.ProfileFightKiller;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.profile.fight.killstreaks.KillStreakType;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;

import java.math.BigDecimal;

public class ProfileFightListeners implements Listener {

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setNoDamageTicks(19);
                }
            }.runTaskLater(FactionsPlugin.getInstance(), 1L);

            if (player.getHealth() - event.getFinalDamage() <= 0) {
                Profile profile = Profile.getByPlayer(player);

                LivingEntity damager;

                if (event.getDamager() instanceof LivingEntity) {
                    damager = (LivingEntity) event.getDamager();
                } else if (event.getDamager() instanceof Projectile) {
                    if (((Projectile) event.getDamager()).getShooter() != null) {
                        damager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                    } else {
                        damager = null;
                    }
                } else {
                    damager = null;
                }

                if (damager == null) {
                    return;
                }

                if (profile.isCombatLogged()) {
                    return;
                }

                if (!(damager instanceof Player)) {
                    profile.getFights().add(new ProfileFight(player, new ProfileFightKiller(damager.getType(), damager.getType().getName())));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profile.save();
                        }
                    }.runTaskAsynchronously(FactionsPlugin.getInstance());
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByPlayer(player);
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if(profile.getKillStreak() > 0) {
            profile.setKillStreak(0);
        }

        int balance = profile.getBalance();

        if(balance > 0) {
            profile.setBalance(0);
        }

        if (profile.isCombatLogged()) {

            event.setDeathMessage(null);

            PlayerFaction playerFaction = PlayerFaction.getByPlayerName(player.getName());
            if (playerFaction != null) {
                playerFaction.setDeathsTillRaidable(playerFaction.getDeathsTillRaidable().add(BigDecimal.ONE));
            }

            return;
        }

        player.getWorld().strikeLightningEffect(player.getLocation());

        if (player.getKiller() != null) {
            ProfileFight fight = new ProfileFight(player, new ProfileFightPlayerKiller(player.getKiller()));
            profile.getFights().add(fight);

            Profile killerProfile = Profile.getByPlayer(player.getKiller());

            PlayerFaction killerFaction = killerProfile.getFaction();

            if(killerFaction != null && killerFaction.getFocusPlayer() != null && killerFaction.getFocusPlayer() == player.getUniqueId()) {
                killerFaction.setFocusPlayer(null);
                killerFaction.sendMessage(ChatColor.YELLOW + "Focus has been removed from " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");

                for(Player member : killerFaction.getOnlinePlayers()) {

                    Profile memberProfile = Profile.getByPlayer(member);

                    if(memberProfile != null) {
                        memberProfile.updateTab();
                    }
                }
            }

            killerProfile.getFights().add(fight);

            if(FactionsPlugin.getInstance().isKitmapMode()) {

                killerProfile.setKillStreak((killerProfile.getKillStreak() + 1));

                for (KillStreakType killStreakType : KillStreakType.values()) {
                    if (killerProfile.getKillStreak() == killStreakType.getCount()) {
                        Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KILL_STREAK.MESSAGE").replace("%PLAYER%", player.getKiller().getName()).replace("%COUNT%", killerProfile.getKillStreak() + ""));

                        for (ItemStack item : killStreakType.getItems()) {
                            player.getKiller().getInventory().addItem(item);
                        }
                    }
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + player.getKiller().getName() + " Kill 1");

            }

            if(FactionsPlugin.getInstance().isKitmapMode()) {
                int toGive = FactionsPlugin.getInstance().getMainConfig().getInt("KITMAP_KILL.MONEY");
                killerProfile.setBalance( (killerProfile.getBalance() + toGive) );
                player.getKiller().sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KITMAP_KILL.MESSAGE").replace("%MONEY%", toGive + "").replace("%PLAYER%", player.getName()));
            } else {

                if(balance > 0) {
                    killerProfile.setBalance( (killerProfile.getBalance() + balance) );
                    player.getKiller().sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KITMAP_KILL.MESSAGE").replace("%MONEY%", balance + "").replace("%PLAYER%", player.getName()));
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
            return;
        }

        if (damageEvent == null) {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
            return;
        }

        DamageCause cause = damageEvent.getCause();

        if (cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.POISON || cause == DamageCause.MAGIC || cause == DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        try {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase()))));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
        } catch (Exception ignored) {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
        }
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof FishHook) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(1.025));
        }
    }

}
