package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.Empty;
import net.badlion.shards.grpc.SlaveShutdownGrpc;

public class SlaveShutdown extends SlaveShutdownGrpc.SlaveShutdownImplBase {

    private final ShardPlugin plugin;

    public SlaveShutdown(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void slaveShutdown(Empty request, StreamObserver<Empty> replyStreamObserver) {
        replyStreamObserver.onNext(Empty.newBuilder().build());
        replyStreamObserver.onCompleted();
        this.plugin.getLogger().info("SHUTTING DOWN SLAVE");
        this.plugin.getServer().shutdown();
    }

}
