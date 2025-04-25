package cc.patrone.practice.player;

import cc.patrone.practice.duel.Duel;
import cc.patrone.practice.kit.CustomKit;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.queue.Queue;
import cc.patrone.practice.util.InventorySnapshot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PracticeProfile {

    private final UUID uuid;
    private PlayerState playerState = PlayerState.LOBBY;
    private boolean scoreboardToggled;
    private Queue queue;
    private Map<Kit, Integer> eloMap = new HashMap<>();
    private Match match;
    private int hits;
    private int combo;
    private Match spectating;
    private Map<Kit, CustomKit> customKitMap = new HashMap<>();
    private Kit editing;
    private boolean buildMode;
    private List<Duel> duelList = new ArrayList<>();
    private Player dueling;
    private int longestCombo;
    private int thrownPots;
    private int fullyLandedPots;
    private InventorySnapshot openInventory;
    private long lastPearl;

    public int getElo(Kit kit) {
        return eloMap.getOrDefault(kit, 1000);
    }

    public void setElo(Kit kit, int elo) {
        eloMap.put(kit, elo);
    }

    public CustomKit getCustomKit(Kit kit) {
        return customKitMap.get(kit);
    }

    public void setCustomKit(Kit kit, CustomKit customKit) {
        customKitMap.put(kit, customKit);
    }

}
