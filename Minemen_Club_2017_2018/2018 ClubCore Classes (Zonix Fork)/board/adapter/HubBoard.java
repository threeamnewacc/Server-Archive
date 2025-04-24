package us.zonix.core.board.adapter;


import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import us.zonix.core.CorePlugin;
import us.zonix.core.board.Board;
import us.zonix.core.board.BoardAdapter;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.redis.queue.Queue;
import us.zonix.core.server.ServerType;
import us.zonix.core.util.CC;

public class HubBoard implements BoardAdapter {

    private final CorePlugin plugin = CorePlugin.getInstance();

    private int online;

    @Override
    public String getTitle(Player player) {
        return CC.DARK_RED + CC.BOLD + "Zonix Network";
    }

    @Override
    public void preLoop() {
        this.online = this.plugin.getRedisManager().getTotalPlayersOnline();
    }

    @Override
    public List<String> getScoreboard(Player player, Board board) {
        List<String> strings = new LinkedList<>();

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getTwoFactorAuthentication() != null && !profile.isAuthenticated()) {
            strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
            strings.add(CC.DARK_RED + CC.BOLD + "AUTHENTICATE");
            strings.add(CC.GRAY + "Usage: /auth <token>");
            strings.add("   ");
            strings.add(CC.RED + "Issues? Contact a Manager");
            strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
            return strings;
        }

        Punishment ban = profile.getBannedPunishment();

        if (ban != null && CorePlugin.getInstance().getServerType() == ServerType.HUB) {
            strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
            strings.add(CC.DARK_RED + CC.BOLD + "BANNED");
            strings.add(CC.GRAY + ban.getTimeLeft());
            strings.add("   ");
            strings.add(CC.RED + "Appeal at www.zonix.us");
            strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
            return strings;
        }

        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");
        strings.add(CC.RED + CC.BOLD + "Online" + CC.GRAY + ":");
        strings.add(CC.WHITE + this.online);
        strings.add(" ");
        strings.add(CC.RED + CC.BOLD + "Rank" + CC.GRAY + ":");
        strings.add(profile.getRank().getColor() + profile.getRank().getName());
        strings.add("  ");

        Queue queue = this.plugin.getQueueManager().getQueue(player);

        if (queue != null) {
            strings.add(CC.DARK_RED + CC.BOLD + "Queue" + CC.GRAY + ":");
            strings.add(CC.RED + CC.WHITE + queue.getServerName().replace("_", "-"));
            strings.add(CC.RED + CC.WHITE + "Position: #" + queue.getPosition(player) + " of " + queue.getPlayers().size());
            strings.add("  ");
        }

        strings.add(CC.RED + "www.zonix.us");
        strings.add(CC.GRAY + CC.STRIKE_THROUGH + "--------------------");

        return strings;
    }

    @Override
    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {
    }

    @Override
    public long getInterval() {
        return 20L;
    }

}
