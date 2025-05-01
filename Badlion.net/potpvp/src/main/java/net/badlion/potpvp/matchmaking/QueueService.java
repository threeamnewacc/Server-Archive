package net.badlion.potpvp.matchmaking;

import com.google.common.base.Joiner;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.MatchMakingManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class QueueService extends MatchMakingService {

    private Map<Group, Integer> ratings = new HashMap<>();
    private Queue<Group> queue = new LinkedList<>();
    private int numOfGroupsWanted = 2;
    private Ladder ladder;

    public QueueService(int numOfGroupsWanted) {
        this.numOfGroupsWanted = numOfGroupsWanted;
    }

    /**
     * Add group to matchmaking service
     */
    public void addGroup(Group group, int rating) {
        super.addGroup(group, rating);
        this.queue.add(group);
        this.ratings.put(group, rating);

	    // Update inventory
		this.ladder.getKitRuleSet().updateTotalPlayersForLadder(this.ladder.getLadderType());
    }

    /**
     * Remove group from matchmaking service
     */
    public boolean removeGroup(Group group) {
	    boolean bool = this.queue.remove(group);
        this.ratings.remove(group);

	    // Update inventory
	    if (bool) {
		 	this.ladder.getKitRuleSet().updateTotalPlayersForLadder(this.ladder.getLadderType());
	    }

        return bool;
    }

    /**
     * Request groups to start a new match (might return empty list)
     */
    public List<Group.GroupRating> requestGroups() {
        List<Group.GroupRating> groups = new ArrayList<>();

        if (this.queue.size() >= this.numOfGroupsWanted) {
            for (int i = 0; i < this.numOfGroupsWanted; i++) {
                // Make sure to call the Manager which will call our removeGroup() function instead
                // This fixes the negative player count bug (i think)
                Group group = this.queue.peek();
                Integer rating = this.ratings.get(group);

                if (rating == null) {
                    Bukkit.getLogger().info("Error retrieving rating for group " + group.toString());
                    group.sendMessage(ChatColor.RED + "Something went wrong. Please report this to a staff member and relog.");

                    // Sanity check
                    if (!MatchMakingManager.removeFromMatchMaking(group)) {
                        this.removeGroup(group);
                    }
                    continue;
                }

                // Sanity check
                if (!MatchMakingManager.removeFromMatchMaking(group)) {
                    Bukkit.getLogger().info("Error removing group from service " + group.toString());
                    group.sendMessage(ChatColor.RED + "Something went wrong. Please report this to a staff member and relog.");
                    this.removeGroup(group);
                    continue;
                }
                groups.add(new Group.GroupRating(group, rating));
            }
        }

        return Collections.unmodifiableList(groups);
    }

    public Ladder getLadder() {
        return this.ladder;
    }

    public void setLadder(Ladder ladder) {
        this.ladder = ladder;
    }

    public String getServiceInfo() {
        String ret = this.ladder.isRanked() ? "Ranked " : " Unranked ";
        if (this.ladder.getLadderType() == Ladder.LadderType.FiveVsFiveRanked) {
            ret += "5v5 ";
        } else if (this.ladder.getLadderType() == Ladder.LadderType.ThreeVsThreeRanked) {
            ret += "3v3 ";
        } else if (this.ladder.getLadderType() == Ladder.LadderType.TwoVsTwoRanked) {
            ret += "2v2 ";
        } else {
            ret += "1v1 ";
        }

        List<String> names = new ArrayList<>();
        for (KitRuleSet kitRuleSet : this.ladder.getAllKitRuleSets()) {
            names.add(kitRuleSet.getName());
        }

        return ret + Joiner.on("/").skipNulls().join(names);
    }

	public int getNumberInQueue() {
		return this.queue.size();
	}

}
