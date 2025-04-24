package me.devkevin.landcore.utils.packet;


import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.devkevin.landcore.utils.FieldBoundReflections;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketInterceptor extends ChannelDuplexHandler {

    private static final FieldBoundReflections CHANNEL_FIELD = FieldBoundReflections.access(NetworkManager.class, "m");

    private static final String HANDLER_BASE_NAME = "packet_handler";
    private static final String HANDLER_NAME = "packet_handler_core";
    private final Player player;
    private final Channel channel;
    private final PacketListener packetListener;

    public PacketInterceptor(Player player, PacketListener packetListener) {
        this.player = player;
        this.packetListener = packetListener;

        NetworkManager networkManager = ((CraftPlayer) player).getHandle().playerConnection.networkManager;
        this.channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

    public void attach() {
        if (!isAttached()) {
            channel.pipeline().addBefore(HANDLER_BASE_NAME, HANDLER_NAME, this);
        }
    }

    public void detach() {
        if (!isAttached()) {
            return;
        }

        channel.pipeline().remove(HANDLER_NAME);
    }


    public boolean isAttached() {
        return channel.pipeline().get(HANDLER_NAME) != null;
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {

        if (object != null && object instanceof Packet) {
            object = packetListener.handleOutgoingPacket(player, (Packet) object);
        }

        super.write(channelHandlerContext, object, channelPromise);
    }


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {

        if (object != null && object instanceof Packet) {
            object = packetListener.handleIncomingPacket(player, (Packet) object);
        }

        super.channelRead(channelHandlerContext, object);
    }

}
