package us.zonix.practice.ffa.killstreak;

import java.util.List;
import org.bukkit.entity.Player;

public interface KillStreak {
    void giveKillStreak(Player var1);

    List<Integer> getStreaks();
}
