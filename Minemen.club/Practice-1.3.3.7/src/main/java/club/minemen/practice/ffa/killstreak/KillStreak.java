package club.minemen.practice.ffa.killstreak;

import java.util.List;
import org.bukkit.entity.Player;

public interface KillStreak {

	void giveKillStreak(Player player);

	List<Integer> getStreaks();

}
