package me.devkevin.landcore.utils.packet;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PacketAdapter<E extends Packet<?>> {

    Packet<?> handle(Player player, E packet);

    default Packet<?> handleAny(Player player, Packet<?> packet) {
        return handle(player, (E) packet);
    }

}
