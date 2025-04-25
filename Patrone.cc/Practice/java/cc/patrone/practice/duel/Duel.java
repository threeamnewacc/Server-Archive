package cc.patrone.practice.duel;

import cc.patrone.practice.kit.Kit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Duel {

    private final Player requester;
    private final Kit kit;
    private long timestamp = System.currentTimeMillis();

}
