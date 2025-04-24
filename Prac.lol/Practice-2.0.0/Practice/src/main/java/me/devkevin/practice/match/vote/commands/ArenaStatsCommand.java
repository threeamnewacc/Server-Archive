package me.devkevin.practice.match.vote.commands;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import club.inverted.chatcolor.CC;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.command.PracticeCommand;
import me.devkevin.practice.match.vote.Vote;
import me.devkevin.practice.util.TaskUtil;
import me.devkevin.practice.util.command.Command;
import me.devkevin.practice.util.command.CommandArgs;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/01/2023 @ 23:40
 * ArenaStatsCommand / me.devkevin.practice.match.vote.commands / Practice
 */
public class ArenaStatsCommand extends PracticeCommand {
    @Override @Command(name = "arenastats", permission = Rank.ADMIN)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "/arenastats <arena>");
            return;
        }

        Arena arena = getPlugin().getArenaManager().getArena(args[0]);

        if (arena == null) {
            player.sendMessage(CC.RED + "/arenastats <arena>");
            return;
        }

        TaskUtil.runAsync(() -> {
            AtomicInteger total = new AtomicInteger();
            HashMap<Vote, AtomicInteger> ratings = new HashMap<>();

            try (MongoCursor<Document> it = getPlugin().getPracticeDatabase().getVotesCollection().find(Filters.eq("arena", (Object) arena.getName())).iterator()) {
                it.forEachRemaining(document -> {
                    Vote rating = Vote.valueOf(document.getString("vote"));
                    ratings.computeIfAbsent(rating, i -> new AtomicInteger()).incrementAndGet();
                    total.incrementAndGet();
                });
            }

            player.sendMessage(CC.GOLD + "Ratings for " + CC.YELLOW + arena.getName() + CC.GOLD + ":");
            player.sendMessage(CC.GRAY + "Total votes: " + CC.WHITE + total.get());

            ratings.forEach((rating, number) -> {
                double decimalPercent = (double) number.get() / (double) total.get();
                double percent = decimalPercent == 1.0 ? decimalPercent : (1.0 - decimalPercent) * 100.0;
                double rounded = (double) Math.round(percent * 100.0) / 100.0;
                player.sendMessage(rating.getDisplayName() + CC.GRAY + ": " + CC.WHITE + number.get() + CC.GOLD + " (" + rounded + "%)");
            });
        });
    }
}
