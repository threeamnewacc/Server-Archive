package me.devkevin.socket.impl;

import club.udrop.core.Core;
import club.udrop.core.api.rank.RankData;
import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;
import me.devkevin.util.MapUtil;
import me.devkevin.util.TimeUtil;

import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:59
 */
@RequiredArgsConstructor
public abstract class PlayerRequest implements Request {
    private final String path, name;

    @Override
    public String getPath() {
        return "/player/" + this.name + "/" + this.path;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    /**
     * Wrapper classes for various Player requests.
     */
    public static final class AltsRequest extends PlayerRequest {
        public AltsRequest(String name) {
            super("alts", name);
        }
    }

    public static final class BanInfoRequest extends PlayerRequest {
        public BanInfoRequest(String name) {
            super("ban-info", name);
        }
    }

    public static final class RankUpdateRequest extends PlayerRequest {
        private final RankData grant;
        private final long duration;
        private final int givenBy;

        public RankUpdateRequest(String name, RankData grant, long duration, int givenBy) {
            super("update-rank", name);

            this.grant = grant;
            this.duration = duration;
            this.givenBy = givenBy;
        }

        @Override
        public Map<String, Object> toMap() {
            return MapUtil.of(
                    "given-by", this.givenBy,
                    "grant", Core.INSTANCE.getRankManagement().getRank(grant.getName()).getName(),
                    "start-time", TimeUtil.getCurrentTimestamp(),
                    "end-time", this.duration == -1 ? "PERM" : TimeUtil.addDuration(this.duration)
            );
        }
    }
}
