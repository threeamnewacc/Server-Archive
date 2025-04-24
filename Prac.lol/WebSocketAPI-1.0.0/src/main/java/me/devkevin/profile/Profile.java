package me.devkevin.profile;

import club.udrop.core.Core;
import club.udrop.core.CoreAPI;
import club.udrop.core.api.player.PlayerData;
import club.udrop.core.api.rank.RankData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.WebSocketAPI;
import me.devkevin.socket.impl.GlobalRequest;
import me.devkevin.util.wrapper.BanWrapper;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 14:13
 */
@Setter
@Getter
@RequiredArgsConstructor
public class Profile {
    private final WebSocketAPI plugin = WebSocketAPI.getInstance();

    private final UUID uuid;
    private final String name;
    private final InetAddress ipAddress;
    private RankData rank;
    private BanWrapper banData;

    private int id;

    private boolean errorLoadingData;

    public BanWrapper fetchData() {
        PlayerData playerData = CoreAPI.INSTANCE.getPlayerData(getUuid());
        JsonElement data = this.plugin.getProcessor().sendRequest(new GlobalRequest(this.ipAddress, this.uuid, playerData.getPlayerName()));

        this.banData = this.parseProfileData(data);

        return banData;
    }

    private BanWrapper parseProfileData(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        JsonElement idElement = object.get("playerId");
        this.id = idElement.getAsInt();

        String rank = object.get("rank").getAsString();
        if (rank != null) {
            this.rank = Core.INSTANCE.getRankManagement().getRank(this.rank.getName());
        } else {
            this.rank = Core.INSTANCE.getRankManagement().getDefaultRank();
        }

        return new BanWrapper("", false);
    }

    public Player getPlayer() {
        return WebSocketAPI.getInstance().getServer().getPlayer(this.uuid);
    }
}
