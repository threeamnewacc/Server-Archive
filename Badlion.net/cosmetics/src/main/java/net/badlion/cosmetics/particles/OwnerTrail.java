package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OwnerTrail extends Particle {


    public static boolean enabled = true;
    private BufferedImage image = null;
    private ParticleLibrary blackType = new ParticleLibrary(ParticleLibrary.ParticleType.SMOKE_NORMAL, 0, 1, 0);
    private ParticleLibrary orangeType = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);

    public OwnerTrail() {
        super("owner_trail", ItemRarity.SPECIAL, ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Owner Trail", ChatColor.GRAY + "Met one of the Badlion owners in real life"));

        try {
            File file = new File(Cosmetics.getInstance().getDataFolder(), "badlion.png");
            this.image = ImageIO.read(ImageIO.createImageInputStream(file));
        } catch (IOException e) {
            System.out.println("badlion.png can't be found! Disabled Owner trail.");
            e.printStackTrace();
            return;
        }

        if (this.image == null) {
            Bukkit.getLogger().warning("badlion.png is null! Disabled Owner trail.");
            OwnerTrail.enabled = false;
            return;
        }

        this.speed = 2;
        this.allowedForAllPermissions = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @Override
    public void spawnParticle(Player player) {
        if (!enabled) {
            return;
        }

        Location location = player.getLocation().add(0.0D, 4.0D, 0.0D);
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());

        List<Location> blackLocations = new ArrayList<>();
        List<Location> orangeLocations = new ArrayList<>();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int clr = image.getRGB(x, y);
                Vector v = new Vector((float) image.getWidth() / 2 - x, (float) image.getHeight() / 2 - y, 0).multiply((float) 1 / 5);
                if (new Color(255, 205, 84).getRGB() == clr) {
                    orangeLocations.add(location.clone().add(v));
                } else if (new Color(0, 0, 0).getRGB() == clr) {
                    blackLocations.add(location.clone().add(v));
                }
            }
        }

        this.blackType.sendToLocation(player, blackLocations);
        this.orangeType.sendToLocation(player, orangeLocations);
    }
}
