package us.zonix.practice.pvpclasses.pvpclasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.party.Party;
import us.zonix.practice.pvpclasses.PvPClass;
import us.zonix.practice.pvpclasses.PvPClassHandler;
import us.zonix.practice.util.Pair;
import us.zonix.practice.util.TimeUtils;

public class ArcherClass extends PvPClass {
    public static final int MARK_SECONDS = 10;
    private static Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static Map<String, Long> lastJumpUsage = new HashMap<>();
    private static Map<String, Long> markedPlayers = new ConcurrentHashMap<>();
    private static Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();

    public static Map<String, Long> getMarkedPlayers() {
        return markedPlayers;
    }

    public static Map<String, Set<Pair<String, Long>>> getMarkedBy() {
        return markedBy;
    }

    public ArcherClass() {
        super("Archer", 15, "LEATHER_", Arrays.asList(Material.SUGAR, Material.FEATHER));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

        super.tick(player);
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(Practice.getInstance(), event.getProjectile().getLocation()));
        }
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow)event.getDamager();
            final Player player = (Player)event.getEntity();
            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player)arrow.getShooter();
            float pullback = ((MetadataValue)arrow.getMetadata("Pullback").get(0)).asFloat();
            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            int damage = isMarked(player) ? 4 : 3;
            if (pullback < 0.5F) {
                damage = 2;
            }

            if (player.getHealth() - (double)damage <= 0.0) {
                event.setCancelled(true);
            } else {
                event.setDamage(0.0);
            }

            Location shotFrom = (Location)((MetadataValue)arrow.getMetadata("ShotFromDistance").get(0)).value();
            double distance = shotFrom.distance(player.getLocation());
            player.setHealth(Math.max(0.0, player.getHealth() - (double)damage));
            if (PvPClassHandler.hasKitOn(player, this)) {
                shooter.sendMessage(
                    ChatColor.YELLOW
                        + "("
                        + (int)distance
                        + ") "
                        + ChatColor.RED
                        + "You can't mark other archers. "
                        + ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + "("
                        + damage / 2
                        + " heart"
                        + (damage / 2 == 1 ? "" : "s")
                        + ")"
                );
            } else if (pullback >= 0.5F) {
                shooter.sendMessage(
                    ChatColor.YELLOW
                        + "("
                        + (int)distance
                        + ") "
                        + ChatColor.GREEN
                        + "You have marked a player for 10 seconds. "
                        + ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + "("
                        + damage / 2
                        + " heart"
                        + (damage / 2 == 1 ? "" : "s")
                        + ")"
                );
                if (!isMarked(player)) {
                    player.sendMessage(ChatColor.RED.toString() + "You have been shot by an archer. (+25% damage) for 10 seconds.");
                }

                PotionEffect invis = null;

                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                        invis = potionEffect;
                        break;
                    }
                }

                if (invis != null) {
                    PvPClass playerClass = PvPClassHandler.getPvPClass(player);
                    player.removePotionEffect(invis.getType());
                    final PotionEffect invisFinal = invis;
                    (new BukkitRunnable() {
                        public void run() {
                            if (invisFinal.getDuration() <= 1000000) {
                                player.addPotionEffect(invisFinal);
                            }
                        }
                    }).runTaskLater(Practice.getInstance(), 205L);
                }

                getMarkedPlayers().put(player.getName(), System.currentTimeMillis() + 10000L);
                getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
                getMarkedBy().get(shooter.getName()).add(new Pair<>(player.getName(), System.currentTimeMillis() + 10000L));
            } else {
                shooter.sendMessage(
                    ChatColor.YELLOW
                        + "("
                        + (int)distance
                        + ") "
                        + ChatColor.RED
                        + "The bow was not fully charged. "
                        + ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + "("
                        + damage / 2
                        + " heart"
                        + (damage / 2 == 1 ? "" : "s")
                        + ")"
                );
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (isMarked(player)) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = (Player)event.getDamager();
                } else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player)((Projectile)event.getDamager()).getShooter();
                }

                if (damager != null && !this.canUseMark(damager, player)) {
                    return;
                }

                event.setDamage(event.getDamage() * 1.25);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        event.getProjectile().setMetadata("Pullback", new FixedMetadataValue(Practice.getInstance(), event.getForce()));
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
                player.sendMessage(ChatColor.RED + "You can't use this for another §f" + msg + "§c.");
                return false;
            } else {
                lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3), true);
                return true;
            }
        } else if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
            long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
            String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
            player.sendMessage(ChatColor.RED + "You can't use this for another §f" + msg + "§c.");
            return false;
        } else {
            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 4));
            return false;
        }
    }

    public static boolean isMarked(Player player) {
        return getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis();
    }

    private boolean canUseMark(Player player, Player victim) {
        if (Practice.getInstance().getPartyManager().getParty(player.getUniqueId()) != null) {
            Party team = Practice.getInstance().getPartyManager().getParty(player.getUniqueId());
            int amount = 0;

            for (Player member : team.members().collect(Collectors.toList())) {
                if (PvPClassHandler.hasKitOn(member, this) && ++amount > 3) {
                    break;
                }
            }

            if (amount > 3) {
                player.sendMessage(ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
                return false;
            }
        }

        if (markedBy.containsKey(player.getName())) {
            for (Pair<String, Long> pair : markedBy.get(player.getName())) {
                if (victim.getName().equals(pair.first) && pair.second > System.currentTimeMillis()) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
