package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.Empty;
import net.badlion.shards.grpc.MasterServerCheckGrpc;
import net.badlion.shards.grpc.MasterServerCheckReply;
import org.bukkit.Bukkit;

public class MasterServerCheck extends MasterServerCheckGrpc.MasterServerCheckImplBase {

    private final ShardPlugin plugin;

    public MasterServerCheck(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void masterServerCheck(Empty request, StreamObserver<MasterServerCheckReply> replyStreamObserver) {
        // Check if the server is master, if it is sync some data to the server requesting
        if (plugin.isMaster()) {
            replyStreamObserver.onNext(MasterServerCheckReply.newBuilder()
                    .setIsmaster(true)
                    .setWorldtime(Bukkit.getServer().getWorlds().get(0).getFullTime())
                    .setMasterconf(this.plugin.getGsonSmall().toJson(this.plugin.getMasterConf())).build());
        } else {
            replyStreamObserver.onNext(MasterServerCheckReply.newBuilder().setIsmaster(false).build());
        }
        replyStreamObserver.onCompleted();
    }

}
