package cc.patrone.practice.manager.impl;

import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.util.ScoreHelper;
import io.netty.channel.Channel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardManager extends Manager {

    public ScoreboardManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public void update(Player player) {
        try {
            if (!managerHandler.getProfileManager().hasProfile(player) || !ScoreHelper.hasScore(player)) {
                return;
            }
            PracticeProfile practiceProfile = this.managerHandler.getProfileManager().getProfile(player);
            ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);

            List<String> slots = new ArrayList<>();

            scoreHelper.setTitle("&5&lPatrone &8┃ &dPractice");

            slots.add("&8&m-------------------------");
            switch (practiceProfile.getPlayerState()) {
                case LOBBY: {
                    slots.add("Online: &5" + this.managerHandler.getPlugin().getServer().getOnlinePlayers().size());
                    slots.add("In Fights: &5" + Kit.getFighting());
                    break;
                }
                case QUEUE: {
                    slots.add("Queued for: &5" + practiceProfile.getQueue().getKit().getName());
                    if (practiceProfile.getQueue().isRanked()) slots.add("Searching: &5" + practiceProfile.getQueue().getMinElo() + " - " + practiceProfile.getQueue().getMaxElo());
                    break;
                }
                case MATCH: {
                    Match match = practiceProfile.getMatch();
                    Player opponent = match.getP1() == player ? match.getP2() : match.getP1();
                    PracticeProfile opponentProfile = match.getP1() == player ? match.getProfile2() : match.getProfile1();
                    slots.add("Opponent: &5" + opponent.getName());
                    slots.add("Hits: &5" + practiceProfile.getHits() + " &8&l┃ &5" + opponentProfile.getHits());
                    slots.add("Combo: &5" + practiceProfile.getCombo() + " &8&l┃ &5" + opponentProfile.getCombo());
                    break;
                }
                case EDITING: {
                    slots.add("Editing: &5" + practiceProfile.getEditing().getName());
                    break;
                }
                case SPECTATING: {
                    Match match = practiceProfile.getSpectating();
                    slots.add(ChatColor.AQUA + match.getP1().getName());
                    slots.add("vs");
                    slots.add(ChatColor.RED + match.getP2().getName());
                    break;
                }
            }
            slots.add("");
            slots.add("&7patrone.cc");
            slots.add("&8&m-------------------------");
            if (practiceProfile.isScoreboardToggled()) {
                slots.clear();
            }
            if (slots.size() < 15) {
                for (int i = slots.size() + 1; i <= 15; i++) {
                    scoreHelper.removeSlot(i);
                }
            }
            Collections.reverse(slots);
            scoreHelper.setSlotsFromList(slots);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
