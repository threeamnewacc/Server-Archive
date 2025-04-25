package us.zonix.practice.arena;

import java.util.List;
import org.bukkit.Location;
import us.zonix.practice.CustomLocation;

public class Arena {
    private final String name;
    private List<StandaloneArena> standaloneArenas;
    private List<StandaloneArena> availableArenas;
    private CustomLocation a;
    private CustomLocation b;
    private CustomLocation min;
    private CustomLocation max;
    private boolean enabled;

    public Arena(
        String name,
        List<StandaloneArena> standaloneArenas,
        List<StandaloneArena> availableArenas,
        CustomLocation a,
        CustomLocation b,
        CustomLocation min,
        CustomLocation max,
        boolean enabled
    ) {
        this.name = name;
        this.standaloneArenas = standaloneArenas;
        this.availableArenas = availableArenas;
        this.a = a;
        this.b = b;
        this.min = min;
        this.max = max;
        this.enabled = enabled;
        this.loadChunks();
    }

    private void loadChunks() {
        this.standaloneArenas.forEach(standaloneArena -> {
            Location locAx = standaloneArena.getA().toBukkitLocation();
            Location locBx = standaloneArena.getB().toBukkitLocation();
            if (!locAx.isChunkLoaded()) {
                locAx.getChunk().load();
            }

            if (!locBx.isChunkLoaded()) {
                locBx.getChunk().load();
            }
        });
        this.availableArenas.forEach(standaloneArena -> {
            Location locAx = standaloneArena.getA().toBukkitLocation();
            Location locBx = standaloneArena.getB().toBukkitLocation();
            if (!locAx.isChunkLoaded()) {
                locAx.getChunk().load();
            }

            if (!locBx.isChunkLoaded()) {
                locBx.getChunk().load();
            }
        });
        Location locA = this.a.toBukkitLocation();
        Location locB = this.b.toBukkitLocation();
        if (!locA.isChunkLoaded()) {
            locA.getChunk().load();
        }

        if (!locB.isChunkLoaded()) {
            locB.getChunk().load();
        }
    }

    public StandaloneArena getAvailableArena() {
        StandaloneArena arena = this.availableArenas.get(0);
        this.availableArenas.remove(0);
        return arena;
    }

    public void addStandaloneArena(StandaloneArena arena) {
        this.standaloneArenas.add(arena);
    }

    public void addAvailableArena(StandaloneArena arena) {
        this.availableArenas.add(arena);
    }

    public String getName() {
        return this.name;
    }

    public List<StandaloneArena> getStandaloneArenas() {
        return this.standaloneArenas;
    }

    public List<StandaloneArena> getAvailableArenas() {
        return this.availableArenas;
    }

    public CustomLocation getA() {
        return this.a;
    }

    public CustomLocation getB() {
        return this.b;
    }

    public CustomLocation getMin() {
        return this.min;
    }

    public CustomLocation getMax() {
        return this.max;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setStandaloneArenas(List<StandaloneArena> standaloneArenas) {
        this.standaloneArenas = standaloneArenas;
    }

    public void setAvailableArenas(List<StandaloneArena> availableArenas) {
        this.availableArenas = availableArenas;
    }

    public void setA(CustomLocation a) {
        this.a = a;
    }

    public void setB(CustomLocation b) {
        this.b = b;
    }

    public void setMin(CustomLocation min) {
        this.min = min;
    }

    public void setMax(CustomLocation max) {
        this.max = max;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Arena(String name) {
        this.name = name;
    }
}
