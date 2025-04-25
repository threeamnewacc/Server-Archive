package me.enzol.spigot;

import me.enzol.spigot.command.KillEntitiesCommand;
import me.enzol.spigot.command.PingCommand;
import me.enzol.spigot.handler.MovementHandler;
import me.enzol.spigot.handler.PacketHandler;
import org.bukkit.*;
import org.bukkit.command.*;
import net.minecraft.server.*;
import java.util.*;

public enum TrainingSpigot
{
    INSTANCE("INSTANCE", 0);

    public static final String Aqua;
    public static final String Dark_Aqua;
    public static final String Gray;
    public static final String Separador;
    private TrainingSpigotConfig config;
    private Set<PacketHandler> packetHandlers;
    private Set<MovementHandler> movementHandlers;

    static {
        Aqua = ChatColor.AQUA.toString();
        Dark_Aqua = ChatColor.DARK_AQUA.toString();
        Gray = ChatColor.GRAY.toString();
        //Separador para nada copiado del foxspigot XDDDDDDDDDDDD
        Separador = String.valueOf(ChatColor.DARK_GRAY.toString()) + ChatColor.STRIKETHROUGH.toString() + " ";
    }

    private TrainingSpigot(final String s, final int n) {
        this.packetHandlers = new HashSet<PacketHandler>();
        this.movementHandlers = new HashSet<MovementHandler>();
    }

    public TrainingSpigotConfig getConfig() {
        return this.config;
    }

    public Set<PacketHandler> getPacketHandlers() {
        return this.packetHandlers;
    }

    public Set<MovementHandler> getMovementHandlers() {
        return this.movementHandlers;
    }

    public void setConfig(final TrainingSpigotConfig config) {
        this.config = config;
    }

    public void addPacketHandler(final PacketHandler handler) {
        this.packetHandlers.add(handler);
    }

    public void addMovementHandler(final MovementHandler handler) {
        this.movementHandlers.add(handler);
    }

    public void registerCommands() {
        final Map<String, Command> commands = new HashMap<String, Command>();
        MinecraftServer.getServer().server.getCommandMap().register("tspigot", new PingCommand());
        if (TrainingSpigot.INSTANCE.getConfig().isPingCommand()) {
            commands.put("ping", new PingCommand());
        }
        commands.put("killentities", new KillEntitiesCommand("killentities"));
        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "tspigot", entry.getValue());
        }
    }
}
