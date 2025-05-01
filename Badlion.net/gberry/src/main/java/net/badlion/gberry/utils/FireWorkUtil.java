package net.badlion.gberry.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class FireWorkUtil {
    
    private static List<Color> colors = new ArrayList<>();

    public static void initialize() {
        FireWorkUtil.colors.add(Color.BLUE);
        FireWorkUtil.colors.add(Color.RED);
        FireWorkUtil.colors.add(Color.YELLOW);
        FireWorkUtil.colors.add(Color.GREEN);
        FireWorkUtil.colors.add(Color.PURPLE);
        FireWorkUtil.colors.add(Color.WHITE);
        FireWorkUtil.colors.add(Color.AQUA);
        FireWorkUtil.colors.add(Color.ORANGE);
    }

    public static void shootFirework(Location location) {
        FireWorkUtil.shootFirework(location, FireWorkUtil.colors);
    }

    public static void shootFirework(Location location, List<Color> colors) {
        FireWorkUtil.shootFirework(location, colors.get((int) (Math.random() * colors.size())));
    }

    public static void shootFirework(Location location, Color color) {
        FireWorkUtil.shootFirework(location, color, color);
    }

    public static void shootFirework(Location location, Color color, Color color2) {
        Firework fw1 = location.getWorld().spawn(location, Firework.class);
        FireworkMeta m1 = fw1.getFireworkMeta();
        m1.setPower(1);
        FireworkEffect effect1 = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(color).withFlicker().build();
        FireworkEffect effect2 = FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(color2).withFlicker().build();
        m1.addEffects(effect1, effect2);
        fw1.setFireworkMeta(m1);
    }

}
