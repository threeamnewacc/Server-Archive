package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.customparticles.CustomHelixEffect;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZombieMorph extends Morph implements Listener, UnregistrableListener {

    public static ZombieMorph instance;

    public static Map<UUID, Long> zombieInfectParticlePlayers = new HashMap<>();
    public static Map<UUID, Long> zombieInfectParticle2Players = new HashMap<>();
    public static Map<UUID, Long> lastZombieInfectTimes = new HashMap<>();

    public static Particle zombieInfectParticle = new CustomHelixEffect(ParticleLibrary.ParticleType.HEART);

    public ZombieMorph() {
        super("zombie_morph", ItemRarity.SUPER_RARE, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 54, ChatColor.GREEN + "Zombie Morph", ChatColor.GRAY + "Left click player to infect."));
        this.morphType = MorphUtil.MorphType.ZOMBIE;


        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(this, Cosmetics.getInstance());

        ZombieMorph.instance = this;
    }

    @Override
    public void unregister() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.ZOMBIE, player).sendServerSetMorph();
    }

    // Zombie attack ability
    @EventHandler(priority = EventPriority.FIRST)
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!Cosmetics.getInstance().isMorphsEnabled() || CosmeticsManager.isCosmeticDisabled(event.getDamager().getUniqueId(), Cosmetics.CosmeticType.MORPH)) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        final Player target = (Player) event.getEntity();

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) {
            return;
        }

        Morph activeMorph = cosmeticsSettings.getActiveMorph();

        // Do they have an active morph?
        if (activeMorph == null) {
            return;
        }

        if (player.getItemInHand().getType() != Material.MONSTER_EGG) {
            return;
        }

        if (activeMorph.getMorphType() == MorphUtil.MorphType.ZOMBIE) {
            CosmeticsManager.CosmeticsSettings targetCosmeticsSettings = CosmeticsManager.getCosmeticsSettings(target.getUniqueId());
            // If the target is morphed, don't allow the Zombie transformation
            if (targetCosmeticsSettings.getActiveMorph() != null) {
                player.sendMessage(ChatColor.RED + "You can not infect this person!");
                return;
            }

            if (lastZombieInfectTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastZombieInfectTimes.get(player.getUniqueId()) <= 1000 * 20) {
                player.sendMessage(ChatColor.RED + "Please wait " + (20 - (Math.round(System.currentTimeMillis() - lastZombieInfectTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ZOMBIE_INFECT", "ENTITY_ZOMBIE_INFECT"), 1.0f, 1.0f);

            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    zombieInfectParticlePlayers.put(target.getUniqueId(), System.currentTimeMillis());
                }
            }, 20L * 10);

            player.sendMessage(ChatColor.GREEN + "You are infecting " + target.getName() + "!");
            target.sendMessage(ChatColor.GREEN + "You are being infected by " + player.getName() + "!");

            zombieInfectParticle2Players.put(target.getUniqueId(), System.currentTimeMillis());
            lastZombieInfectTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
