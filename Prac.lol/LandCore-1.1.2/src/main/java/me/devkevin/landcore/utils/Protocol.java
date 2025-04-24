package me.devkevin.landcore.utils;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 2:56
 * Protocol / me.devkevin.landcore.utils / LandCore
 */
public class Protocol {
    public EntityPlayer getEntity(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public void sendPacket(Player player, Packet<?> packet) {
        getEntity(player).playerConnection.sendPacket(packet);
    }

    public void broadcastWorld(Packet<?> packet, Player player) {
        for (Player online : player.getWorld().getPlayers()) {
            if ((online != null) && (online.getEntityId() != player.getEntityId()) && (online.canSee(player)))
                sendPacket(online, packet);
        }
    }

    public void broadcastServer(Packet<?> packet, Player player) {
        for (Player all : Bukkit.getServer().getOnlinePlayers())
            sendPacket(all, packet);
    }
}
