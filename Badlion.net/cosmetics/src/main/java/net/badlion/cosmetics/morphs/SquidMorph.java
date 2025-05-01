package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SquidMorph extends Morph {

    public static ArrayList<UUID> squidSneakPlayers = new ArrayList<>();

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.WATER_SPLASH, 0, 1, 0);

    public SquidMorph() {
        super("squid_morph", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 94, ChatColor.GREEN + "Squid Morph", ChatColor.GRAY + "Shift to splash."));
        this.morphType = MorphUtil.MorphType.SQUID;

    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) {
            squidSneakPlayers.remove(player.getUniqueId());
            return;
        }
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                for (int i3 = 0; i3 < 5; i3++) {
                    locations.add(Particle.getParticleLocation(player.getLocation().add(player.getLocation().getDirection().add(new Vector(i2, 0.0D, i2))), 0.3D));
                    locations.add(Particle.getParticleLocation(player.getLocation().add(player.getLocation().getDirection().add(new Vector(-i2, 0.0D, -i2))), 0.3D));
                }
            }
        }
        this.particle.sendToLocation(player, locations);
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SPLASH", "ENTITY_GENERIC_SPLASH"), 1.0f, 1.0f);
        squidSneakPlayers.add(player.getUniqueId());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.SQUID, player).sendServerSetMorph();
    }
}
