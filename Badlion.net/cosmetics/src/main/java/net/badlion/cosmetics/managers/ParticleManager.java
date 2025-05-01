package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.particles.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleManager {

    public static Map<String, Particle> particles = new LinkedHashMap<>();

    static {
        ParticleManager.particles.put("flame_trail", new FlameTrail());
        ParticleManager.particles.put("music_trail", new MusicTrail());
        ParticleManager.particles.put("thunder_cloud_trail", new ThunderCloudTrail());
        ParticleManager.particles.put("love_trail", new LoveTrail());
        ParticleManager.particles.put("snow_trail", new SnowTrail());
        ParticleManager.particles.put("spell_trail", new SpellTrail());
        //ParticleManager.particles.put("arc_trail", new ArcTrail());
        ParticleManager.particles.put("smoke_trail", new SmokeTrail());
        ParticleManager.particles.put("spark_trail", new SparkTrail());
        ParticleManager.particles.put("lava_trail", new LavaTrail());
        ParticleManager.particles.put("aqua_trail", new AquaTrail());
        ParticleManager.particles.put("nyan_trail", new NyanTrail());
        ParticleManager.particles.put("nether_trail", new NetherTrail());
        ParticleManager.particles.put("portal_trail", new PortalTrail());
        ParticleManager.particles.put("glyph_trail", new GlyphTrail());
        ParticleManager.particles.put("magic_trail", new MagicTrail());
        ParticleManager.particles.put("cloud_trail", new CloudTrail());
        ParticleManager.particles.put("flame_ring_trail", new FlameRingTrail());
        ParticleManager.particles.put("cage_trail", new CageTrail());
        ParticleManager.particles.put("halo_trail", new HaloTrail());
        ParticleManager.particles.put("helix_trail", new HelixTrail());
        ParticleManager.particles.put("christmas_helix_trail", new ChristmasHelixTrail());
        ParticleManager.particles.put("green_ring_trail", new GreenRingTrail());
        ParticleManager.particles.put("owner_trail", new OwnerTrail());
    }

    public static Particle getParticle(String particleName) {
        return ParticleManager.particles.get(particleName.toLowerCase());
    }

    public static void addParticle(final CommandSender sender, final Player player, final String particleName) {
        ParticleManager.addParticle(sender, player, particleName, true);
    }

    public static void addParticle(final CommandSender sender, final Player player, final String particleName, final boolean verbose) {
        final Particle particle = ParticleManager.getParticle(particleName);

        // Is particle valid?
        if (particle == null) {
            sender.sendMessage(ChatColor.RED + "Particle " + particleName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have the particle?
        if (cosmeticsSettings.hasParticle(particle)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the particle " + particle.getName());
            return;
        }

        cosmeticsSettings.addParticle(particle);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave particle " + particle.getName() + " to " + player.getName());
        }
    }

    public static void equipParticle(Player player, String particleName) {
        Particle particle = ParticleManager.getParticle(particleName);
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Is particle valid?
        if (particle == null) {
            player.sendMessage(ChatColor.RED + "Particle " + particleName + " not found.");
            return;
        }

        // Do they already have the particle equipped?
        if (cosmeticsSettings.getActiveParticle() == particle) {
            player.sendMessage(ChatColor.RED + "You already have + " + particle.getName() + " equipped!");
            return;
        }

        cosmeticsSettings.setActiveParticle(particle, true);
    }

    public static Map<String, Particle> getParticles() {
        return particles;
    }

}
