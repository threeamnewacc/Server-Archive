package me.enzol.spigot;

import me.enzol.spigot.handler.MovementHandler;
import me.enzol.spigot.handler.PacketHandler;

import java.util.HashSet;
import java.util.Set;

public enum TrainingSpigot {
    INSTANCE("INSTANCE", 0);

    public static final String JarName = "TrainingSpigot - API";
    private Set<PacketHandler> packetHandlers;
    private Set<MovementHandler> movementHandlers;

    private TrainingSpigot(final String s, final int n) {
        this.packetHandlers = new HashSet<PacketHandler>();
        this.movementHandlers = new HashSet<MovementHandler>();
    }

}
