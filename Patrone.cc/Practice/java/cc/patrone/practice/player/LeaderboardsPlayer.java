package cc.patrone.practice.player;

import cc.patrone.practice.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class LeaderboardsPlayer {

    private UUID uuid;
    private Kit kit;
    private int elo;

}
