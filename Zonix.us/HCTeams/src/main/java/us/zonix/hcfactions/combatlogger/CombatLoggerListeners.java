package us.zonix.hcfactions.combatlogger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.deathsign.DeathSign;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.deathban.ProfileDeathban;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import us.zonix.hcfactions.profile.fight.killstreaks.KillStreakType;
import us.zonix.hcfactions.util.ItemNames;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.profile.deathmessage.ProfileDeathMessage;
import us.zonix.hcfactions.profile.deathmessage.ProfileDeathMessageTemplate;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;
import us.zonix.hcfactions.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.profile.deathban.ProfileDeathban;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.ItemNames;

import java.math.BigDecimal;
import java.util.*;

public class CombatLoggerListeners implements Listener {

    private FactionsPlugin main;

    public CombatLoggerListeners(FactionsPlugin main) {
        this.main = main;

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<CombatLogger, Long>> iterator = CombatLogger.getLoggersMap().entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<CombatLogger, Long> entry = iterator.next();
                    CombatLogger logger = entry.getKey();
                    long time = entry.getValue();

                    if (System.currentTimeMillis() - time > (main.getMainConfig().getInt("COMBAT_LOGGER.DESPAWN_TIME") * 1000)) {
                        logger.getEntity().remove();
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 20L, 20L);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Profile profile = Profile.getByPlayer(player);

        if (profile.getProtection() != null) {
            return;
        }

        for (Mode mode : Mode.getModes()) {
            if (mode.isSOTWActive()) {
                return;
            }
        }

        if (profile.isSafeLogout()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(player.getLocation());

        if (claim == null) {
            if(!FactionsPlugin.getInstance().isKitmapMode()) {
                new CombatLogger(player);
            }
            return;
        }

        Faction faction = claim.getFaction();

        if (faction instanceof SystemFaction) {
            SystemFaction systemFaction = (SystemFaction) faction;

            if (!systemFaction.isDeathban()) {
                return;
            }
        }

        if(!FactionsPlugin.getInstance().isKitmapMode()) {
            new CombatLogger(player);
        }
    }

    @EventHandler
    public void onEntityDamageEventPlayer(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByPlayer(player);

            if (profile.getCooldownByType(ProfileCooldownType.LOGOUT) != null) {
                player.sendMessage(main.getLanguageConfig().getString("COMBAT_LOGGER.LOGOUT_CANCELLED"));
                profile.getCooldowns().remove(profile.getCooldownByType(ProfileCooldownType.LOGOUT));
                profile.setLogoutLocation(null);
            }
        }
    }


    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType() == CombatLogger.ENTITY_TYPE) {
                if (entity instanceof LivingEntity) {
                    if (((LivingEntity) entity).getCustomName() != null) {
                        entity.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                CombatLogger logger = CombatLogger.getByEntity((LivingEntity) entity);

                if (logger != null) {
                    entity.remove();
                    CombatLogger.getLoggers().remove(logger);
                }

            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        CombatLogger logger = CombatLogger.getByPlayer(player);

        if (logger != null) {
            if (!(profile.isCombatLogged())) {
                event.getPlayer().setHealth(logger.getEntity().getHealth() / 5);
            }
            event.getPlayer().teleport(logger.getEntity().getLocation());
            logger.getEntity().remove();
            CombatLogger.getLoggers().remove(logger);
        }

        if (profile.isCombatLogged()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setExp(0);
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof LivingEntity) {
            CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getRightClicked());

            if (logger != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        CombatLogger logger = CombatLogger.getByEntity(entity);

        if (logger != null) {

            Profile profile = new Profile(logger.getUuid());

            for (ItemStack itemStack : logger.getArmor()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                }
            }

            for (ItemStack itemStack : logger.getContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                }
            }

            profile.getCooldowns().clear();

            event.getDrops().clear();

            PlayerFaction playerFaction = PlayerFaction.getByPlayerName(logger.getName());

            if (playerFaction != null) {
                playerFaction.freeze(FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.FREEZE_DURATION"));
                playerFaction.setDeathsTillRaidable(playerFaction.getDeathsTillRaidable().subtract(BigDecimal.ONE));
                playerFaction.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("ANNOUNCEMENTS.FACTION.PLAYER_DEATH").replace("%PLAYER%", logger.getName()).replace("%DTR%", playerFaction.getDeathsTillRaidable() + "").replace("%MAX_DTR%", playerFaction.getMaxDeathsTillRaidable() + ""));
            }

            EntityDamageEvent damageEvent = entity.getLastDamageCause();

            entity.getWorld().strikeLightningEffect(entity.getLocation());

            if (!main.isKitmapMode()) {
                profile.setDeathban(new ProfileDeathban(logger.getDeathbanDuration()));
            }

            profile.setCombatLogged(true);

            if (entity.getKiller() != null) {
                ProfileFight fight = new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getArmor(), logger.getHunger(), logger.getEffects(), new ProfileFightPlayerKiller(entity.getKiller()), entity.getLocation());
                profile.getFights().add(fight);

                Profile.getByPlayer(entity.getKiller()).getFights().add(fight);

                Player killer = entity.getKiller();
                entity.getWorld().dropItemNaturally(entity.getLocation(), new DeathSign(killer.getName(), logger.getName()).toItemStack());

                if (us.zonix.core.profile.Profile.getByUuid(killer.getUniqueId()).getRank().isAboveOrEqual(Rank.SILVER)) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(logger.getName()).build());
                }

                Profile killerProfile = Profile.getByPlayer(killer);

                killerProfile.setKillStreak((killerProfile.getKillStreak() + 1));

                for(KillStreakType killStreakType : KillStreakType.values()) {
                    if(killerProfile.getKillStreak() == killStreakType.getCount()) {
                        Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KILL_STREAK.MESSAGE").replace("%PLAYER%", killer.getName()).replace("%COUNT%", killerProfile.getKillStreak() + ""));

                        for(ItemStack item : killStreakType.getItems()) {
                            killer.getInventory().addItem(item);
                        }
                    }
                }

                String weapon = "their fists";

                if (profile.getLastDamager() != null && profile.getLastDamager().getKey() != null && profile.getLastDamager().getKey().equals(killer.getUniqueId())) {
                    ItemStack item = profile.getLastDamager().getValue();

                    if (item != null) {
                        if (item.getItemMeta().hasDisplayName()) {
                            weapon = item.getItemMeta().getDisplayName();
                        }
                        else {
                            weapon = ItemNames.lookup(item);
                        }
                    }
                } else {
                    if (killer.getItemInHand() != null && killer.getItemInHand().getType() != Material.AIR) {
                        if (killer.getItemInHand().getItemMeta().hasDisplayName()) {
                            weapon = killer.getItemInHand().getItemMeta().getDisplayName();
                        }
                        else {
                            weapon = ItemNames.lookup(killer.getItemInHand());
                        }
                    }
                }

                new ProfileDeathMessage(ProfileDeathMessageTemplate.LOGGER, profile, killerProfile, weapon);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        profile.save();
                        Profile.getProfilesMap().remove(profile.getUuid());
                    }
                }.runTaskAsynchronously(this.main);

                return;
            }

            if (damageEvent == null) {
                new ProfileDeathMessage(ProfileDeathMessageTemplate.CUSTOM, profile);
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getArmor(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));


                new BukkitRunnable() {

                    @Override
                    public void run() {
                        profile.save();
                        Profile.getProfilesMap().remove(profile.getUuid());
                    }
                }.runTaskAsynchronously(this.main);

                return;
            }

            EntityDamageEvent.DamageCause cause = damageEvent.getCause();

            if (cause == EntityDamageEvent.DamageCause.PROJECTILE || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                return;
            }

            try {
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getArmor(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase())), entity.getLocation()));
            }
            catch (Exception ignored) {
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getArmor(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));
            }

            ProfileDeathMessageTemplate template;

            try {
                template = ProfileDeathMessageTemplate.valueOf(cause.name());
            }
            catch (Exception exception) {
                return;
            }

            new ProfileDeathMessage(template, profile);

            profile.save();
            Profile.getProfilesMap().remove(profile.getUuid());
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            Player damager;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
                else {
                    return;
                }
            }
            else {
                return;
            }

            CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getEntity());

            if (logger != null) {
                PlayerFaction damagedFaction = PlayerFaction.getByPlayerName(logger.getName());
                PlayerFaction damagerFaction = PlayerFaction.getByPlayerName(damager.getName());

                if (damagedFaction == null || damagerFaction == null) {
                    CombatLogger.getLoggersMap().put(logger, System.currentTimeMillis());
                    return;
                }

                if (damagedFaction == damagerFaction) {
                    damager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("FACTION_OTHER.CANNOT_DAMAGE_FRIENDLY").replace("%PLAYER%", logger.getName()));
                    event.setCancelled(true);
                    return;
                }

                if (damagedFaction.getAllies().contains(damagerFaction) && !FactionsPlugin.getInstance().getMainConfig().getBoolean("ALLIES.DAMAGE_ALLIES")) {
                    damager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("FACTION_OTHER.CANNOT_DAMAGE_ALLY").replace("%PLAYER%", logger.getName()));
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getEntity());

            if (logger != null) {
                CombatLogger.getLoggersMap().put(logger, System.currentTimeMillis());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getEntity().setVelocity(new Vector());
                    }
                }.runTaskLaterAsynchronously(main, 1L);

                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setDamage(event.getFinalDamage() * 5);
                }
            }
        }
    }

}
