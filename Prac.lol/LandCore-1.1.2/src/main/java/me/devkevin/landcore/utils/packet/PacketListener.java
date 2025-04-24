package me.devkevin.landcore.utils.packet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PacketListener {

    private final Multimap<Class<?>, PacketAdapter<?>> registeredAdapters;

    public PacketListener() {
        this.registeredAdapters = HashMultimap.create();
    }

    public Packet<?> handleOutgoingPacket(Player player, Packet<?> packet) {
        return invokeAdapters(player, packet);
    }


    public Packet<?> handleIncomingPacket(Player player, Packet<?> packet) {
        return invokeAdapters(player, packet);
    }

    private Packet<?> invokeAdapters(Player player, Packet<?> packet) {
        if (packet == null) {
            return null;
        }

        Collection<PacketAdapter<?>> adapters = registeredAdapters.get(packet.getClass());

        for (PacketAdapter<?> adapter : adapters) {
            packet = adapter.handleAny(player, packet);
        }

        return packet;
    }

    public <E extends Packet<?>> void registerAdapter(Class<? extends E> classType, PacketAdapter<E> handler) {
        this.registeredAdapters.put(classType, handler);
    }


}

