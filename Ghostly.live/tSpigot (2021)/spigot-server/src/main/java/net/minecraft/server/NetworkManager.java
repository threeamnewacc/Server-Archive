package net.minecraft.server;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.enzol.spigot.TrainingSpigot;
import me.enzol.spigot.handler.PacketHandler;

public class NetworkManager extends SimpleChannelInboundHandler<Packet>
{
    private static final Logger g;
    public static final Marker a;
    public static final Marker b;
    public static final AttributeKey<EnumProtocol> c;
    public static final LazyInitVar<NioEventLoopGroup> d;
    public static final LazyInitVar<EpollEventLoopGroup> e;
    public static final LazyInitVar<LocalEventLoopGroup> f;
    private final EnumProtocolDirection h;
    private final Queue<QueuedPacket> packetQueue;
    private final ReentrantReadWriteLock j;
    public Channel channel;
    public SocketAddress l;
    public UUID spoofedUUID;
    public Property[] spoofedProfile;
    public boolean preparing;
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;
    protected String payload;
    private boolean openedBook;
    
    public static Channel getChannel(final NetworkManager nm) {
        return nm.channel;
    }
    
    public NetworkManager(final EnumProtocolDirection enumprotocoldirection) {
        this.packetQueue = Queues.newConcurrentLinkedQueue();
        this.j = new ReentrantReadWriteLock();
        this.preparing = true;
        this.h = enumprotocoldirection;
    }
    
    public void channelActive(final ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        this.preparing = false;
        try {
            this.a(EnumProtocol.HANDSHAKING);
        }
        catch (Throwable throwable) {
            NetworkManager.g.fatal((Object)throwable);
        }
    }
    
    public void a(final EnumProtocol enumprotocol) {
        this.channel.attr((AttributeKey)NetworkManager.c).set((Object)enumprotocol);
        this.channel.config().setAutoRead(true);
        NetworkManager.g.debug("Enabled auto read");
    }
    
    public void channelInactive(final ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }
    
    public void exceptionCaught(final ChannelHandlerContext channelhandlercontext, final Throwable throwable) throws Exception {
        ChatMessage chatmessage;
        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        }
        else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable });
        }
        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) {
            throwable.printStackTrace();
        }
    }
    
    protected void a(final ChannelHandlerContext channelhandlercontext, final Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            if (packet instanceof PacketPlayInCustomPayload) {
                final PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload)packet;
                final String name = payload.a();
                if ((name.equalsIgnoreCase("MC|BSign") || name.equalsIgnoreCase("MC|BEdit")) && this.m instanceof PlayerConnection) {
                    final byte[] data = payload.b().array();
                    if (data.length > 15000) {
                        this.close(new ChatMessage("Invalid book packet", new Object[0]));
                        return;
                    }
                    if (!this.openedBook) {
                        this.close(new ChatMessage("Invalid book packet", new Object[0]));
                        return;
                    }
                    this.openedBook = false;
                }
            }
            else if (packet instanceof PacketPlayInBlockPlace) {
                final ItemStack stack = ((PacketPlayInBlockPlace)packet).getItemStack();
                if (stack != null && stack.getItem() != null && stack.getItem().getName().equalsIgnoreCase("item.writingBook")) {
                    this.openedBook = true;
                }
            }
            try {
                packet.a(this.m);
            }
            catch (CancelledPacketHandleException ex) {}
            if (this.m instanceof PlayerConnection) {
                try {
                    for (final PacketHandler handler : TrainingSpigot.INSTANCE.getPacketHandlers()) {
                        handler.handleReceivedPacket((PlayerConnection)this.m, packet);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void a(final PacketListener packetlistener) {
        Validate.notNull((Object)packetlistener, "packetListener", new Object[0]);
        NetworkManager.g.debug("Set listener of {} to {}", new Object[] { this, packetlistener });
        this.m = packetlistener;
    }
    
    public void handle(final Packet packet) {
        if (this.g()) {
            this.m();
            this.a(packet, null);
        }
        else {
            this.j.writeLock().lock();
            try {
                this.packetQueue.add(new QueuedPacket(packet, (GenericFutureListener<? extends Future<? super Void>>[])new GenericFutureListener[0]));
            }
            finally {
                this.j.writeLock().unlock();
            }
        }
    }
    
    public void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, final GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        if (this.g()) {
            this.m();
            this.a(packet, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add((Object[])agenericfuturelistener, 0, (Object)genericfuturelistener));
        }
        else {
            this.j.writeLock().lock();
            try {
                this.packetQueue.add(new QueuedPacket(packet, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add((Object[])agenericfuturelistener, 0, (Object)genericfuturelistener)));
            }
            finally {
                this.j.writeLock().unlock();
            }
        }
    }
    
    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] listeners) {
        final EnumProtocol packetProtocol = EnumProtocol.a(packet);
        final EnumProtocol channelProtocol = (EnumProtocol)this.channel.attr((AttributeKey)NetworkManager.c).get();
        if (channelProtocol != packetProtocol) {
            NetworkManager.g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (packetProtocol != channelProtocol) {
                this.a(packetProtocol);
            }
            final ChannelFuture channelfuture = this.channel.writeAndFlush((Object)packet);
            if (listeners != null) {
                channelfuture.addListeners((GenericFutureListener[])listeners);
            }
            channelfuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else {
            this.channel.eventLoop().execute((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (packetProtocol != channelProtocol) {
                        NetworkManager.this.a(packetProtocol);
                    }
                    final ChannelFuture channelfuture = NetworkManager.this.channel.writeAndFlush((Object)packet);
                    if (listeners != null) {
                        channelfuture.addListeners(listeners);
                    }
                    channelfuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }
    
    private void m() {
        if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();
            try {
                while (!this.packetQueue.isEmpty()) {
                    final QueuedPacket queuedPacket = this.packetQueue.poll();
                    this.a(queuedPacket.a, queuedPacket.b);
                }
            }
            finally {
                this.j.readLock().unlock();
            }
        }
    }
    
    public void a() {
        this.m();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox)this.m).c();
        }
        this.channel.flush();
    }
    
    public SocketAddress getSocketAddress() {
        return this.l;
    }
    
    public void close(final IChatBaseComponent ichatbasecomponent) {
        this.preparing = false;
        if (this.channel.isOpen()) {
            this.channel.close();
            this.n = ichatbasecomponent;
        }
    }
    
    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }
    
    public void a(final SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }
    
    public boolean g() {
        return this.channel != null && this.channel.isOpen();
    }
    
    public boolean h() {
        return this.channel == null;
    }
    
    public PacketListener getPacketListener() {
        return this.m;
    }
    
    public IChatBaseComponent j() {
        return this.n;
    }
    
    public void k() {
        this.channel.config().setAutoRead(false);
    }
    
    public void a(final int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor)this.channel.pipeline().get("decompress")).a(i);
            }
            else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new PacketDecompressor(i));
            }
            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor)this.channel.pipeline().get("decompress")).a(i);
            }
            else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new PacketCompressor(i));
            }
        }
        else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }
    }
    
    public void l() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.p) {
                this.p = true;
                if (this.j() != null) {
                    this.getPacketListener().a(this.j());
                }
                else if (this.getPacketListener() != null) {
                    this.getPacketListener().a(new ChatComponentText("Disconnected"));
                }
                this.packetQueue.clear();
            }
            else {
                NetworkManager.g.warn("handleDisconnection() called twice");
            }
        }
    }
    
    public EnumProtocol getProtocol() {
        return (EnumProtocol)this.channel.attr((AttributeKey)NetworkManager.c).get();
    }
    
    protected void channelRead0(final ChannelHandlerContext channelhandlercontext, final Packet object) throws Exception {
        this.a(channelhandlercontext, object);
    }
    
    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }
    
    static {
        g = LogManager.getLogger();
        a = MarkerManager.getMarker("NETWORK");
        b = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.a);
        c = AttributeKey.valueOf("protocol");
        d = new LazyInitVar<NioEventLoopGroup>() {
            @Override
            protected NioEventLoopGroup init() {
                return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
            }
        };
        e = new LazyInitVar<EpollEventLoopGroup>() {
            @Override
            protected EpollEventLoopGroup init() {
                return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
            }
        };
        f = new LazyInitVar<LocalEventLoopGroup>() {
            @Override
            protected LocalEventLoopGroup init() {
                return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
            }
        };
    }
    
    static class QueuedPacket
    {
        private final Packet a;
        private final GenericFutureListener<? extends Future<? super Void>>[] b;
        
        public QueuedPacket(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }
}
