package cc.patrone.practice.tab;

import cc.patrone.core.player.CoreProfile;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.TabPlayer;
import io.github.thatkawaiisam.ziggurat.ZigguratAdapter;
import io.github.thatkawaiisam.ziggurat.ZigguratCommons;
import io.github.thatkawaiisam.ziggurat.utils.BufferedTabObject;
import io.github.thatkawaiisam.ziggurat.utils.TabColumn;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class TabAdapter implements ZigguratAdapter {

    private final PracticePlugin plugin;

    private List<TabPlayer> tabPlayers = new ArrayList<>();

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        Set<BufferedTabObject> toReturn = new HashSet<>();
        //    toReturn.add(new BufferedTabObject().text("&7&m----------------").slot(1).column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.defaultTexture));
        tabPlayers.clear();
        this.plugin.getServer().getOnlinePlayers().stream().forEach(o -> {
            CoreProfile coreProfile = this.plugin.getCorePlugin().getManagerHandler().getProfileManager().getProfile(o);
            if (o != null && coreProfile != null && coreProfile.getGroup() != null) {
                tabPlayers.add(new TabPlayer(o.getName(), coreProfile.getGroup(), coreProfile.getGroup().getWeight()));
            }
        });
        int row = 1;
        TabColumn lastColumn = TabColumn.RIGHT;
        TabColumn tabColumn = null;
        tabPlayers.sort(Comparator.comparing(TabPlayer::getWeight));
        Collections.reverse(tabPlayers);
        for (TabPlayer tabPlayer : tabPlayers) {
            if (lastColumn == TabColumn.LEFT) {
                tabColumn = TabColumn.MIDDLE;
            } else if (lastColumn == TabColumn.MIDDLE) {
                tabColumn = TabColumn.RIGHT;
            } else {
                tabColumn = TabColumn.LEFT;
            }
            toReturn.add(new BufferedTabObject().text(tabPlayer.getGroup().getTabColor() + tabPlayer.getName()).slot(row).column(tabColumn).ping(0).skin(ZigguratCommons.defaultTexture));
            if (tabColumn == TabColumn.RIGHT) {
                row += 1;
            }
            lastColumn = tabColumn;
        }
        return toReturn;
    }

    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public String getHeader() {
        return null;
    }
}
