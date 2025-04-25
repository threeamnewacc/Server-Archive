package me.enzol.spigot.handler;

import net.minecraft.server.Packet;
import net.minecraft.server.PacketListener;
import net.minecraft.server.PlayerConnection;

public interface PacketHandler {

    void handleReceivedPacket(PlayerConnection connection, Packet<PacketListener> packet);

    void handleSentPacket(PlayerConnection connection, Packet<PacketListener> packet);
}
