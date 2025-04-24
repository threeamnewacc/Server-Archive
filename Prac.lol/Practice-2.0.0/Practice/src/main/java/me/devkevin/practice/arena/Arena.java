package me.devkevin.practice.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.location.CustomLocation;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Arena {

    @Getter private final String name;

    private List<StandaloneArena> standaloneArenas;
    private List<StandaloneArena> availableArenas;

    private String icon;
    private int iconData;

    private CustomLocation a;
    private CustomLocation b;

    private CustomLocation min;
    private CustomLocation max;

    private int buildMax;
    private int deadZone;
    private int portalProt;

    private boolean enabled;

    @Setter private boolean arenaSelected;
    @Setter private Arena selectedArena;

    //private String author = "Inverted Build Team.";

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
}