package net.badlion.cosmetics.customparticles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BadlionLogo {

    private static List<Location> blueLocations = new ArrayList<>();
    private static List<Location> orangeLocations = new ArrayList<>();
    private static List<Location> blackLocations = new ArrayList<>();
    private static List<Location> whiteLocations = new ArrayList<>();
    private static ParticleLibrary blackType = new ParticleLibrary(ParticleLibrary.ParticleType.SMOKE_NORMAL, 0, 1, 0);
    private static ParticleLibrary blueType = new ParticleLibrary(ParticleLibrary.ParticleType.WATER_DROP, 0, 1, 0);
    private static ParticleLibrary orangeType = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);
    private static ParticleLibrary whiteType = new ParticleLibrary(ParticleLibrary.ParticleType.FIREWORKS_SPARK, 0, 1, 0);

    public static void initialize() {
        if (Gberry.serverType != Gberry.ServerType.LOBBY) {
            return;
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new File("badlion.png"));
        } catch (IOException e) {
            System.out.println("Badlion.png can't be found! Disabled Badlion logo particles.");
            e.printStackTrace();
            return;
        }

        Location location = new Location(Bukkit.getWorld("world"), 0.5, 105, -11.5);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int clr = image.getRGB(x, y);
                Vector v = new Vector((float) image.getWidth() / 2 - x, (float) image.getHeight() / 2 - y, 0).multiply((float) 1 / 5);
                if (new Color(38, 126, 158).getRGB() == clr) {
                    blueLocations.add(location.clone().add(v));
                } else if (new Color(255, 205, 84).getRGB() == clr) {
                    orangeLocations.add(location.clone().add(v));
                } else if (new Color(0, 0, 0).getRGB() == clr) {
                    blackLocations.add(location.clone().add(v));
                }
            }
        }
    }

    public static void spawnParticle() {
        blackType.sendToLocation(blackLocations);
        blueType.sendToLocation(blueLocations);
        orangeType.sendToLocation(orangeLocations);
        whiteType.sendToLocation(whiteLocations);
    }
}
