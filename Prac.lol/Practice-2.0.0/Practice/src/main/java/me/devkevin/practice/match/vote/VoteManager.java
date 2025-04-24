package me.devkevin.practice.match.vote;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.ChatComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/01/2023 @ 16:38
 * VoteManager / me.devkevin.practice.match.vote / Practice
 */
@Getter
@RequiredArgsConstructor
public class VoteManager {
    private final Practice plugin;
    private final List<UUID> pendingVotes = new ArrayList<>();

    public boolean hasVoted(Player player, Arena arena) {
        if (Thread.currentThread() == MinecraftServer.getServer().primaryThread) {
            return false;
        }
        if (arena == null) {
            return false;
        }
        return this.plugin.getPracticeDatabase().getVotesCollection().find(Filters.and(Filters.eq("uuid", (Object)player.getUniqueId().toString()), Filters.eq("arena", (Object) arena.getName()))).first() != null;
    }

    public void sendVoteMessage(Player player, Arena arena) {
        if (this.hasVoted(player, arena)) {
            return;
        }

        ChatComponentBuilder builder = new ChatComponentBuilder("");
        builder.append(CC.AQUA + "Give us some feedback about ");
        builder.append(CC.GOLD + arena.getName());
        builder.append(CC.PINK + " by clicking one of the stars: ");

        Arrays.stream(Vote.values()).forEach(rating -> builder.append(rating.getDisplayName() + " ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rate " + rating.name()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.GREEN + "Click to rate " + rating.getDisplayName() + CC.GREEN + ".").create())));

        player.sendMessage(builder.create());
    }

    public void attemptVote(Player player, Arena arena, Vote vote) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile.getLastArenaPlayed() == null) {
            return;
        }

        if (!profile.getLastArenaPlayed().getName().equals(arena.getName())) {
            player.sendMessage(CC.RED + "You can not vote for that arena.");
            return;
        }

        if (this.hasVoted(player, arena)) {
            player.sendMessage(CC.RED + "You have already voted for that arena.");
            return;
        }

        if (this.pendingVotes.contains(player.getUniqueId())) {
            return;
        }

        this.pendingVotes.add(player.getUniqueId());
        this.plugin.getPracticeDatabase().getVotesCollection().insertOne(new Document("uuid", player.getUniqueId().toString()).append("arena", arena.getName()).append("vote", vote.name()));
        player.sendMessage(CC.YELLOW + "You rated " + CC.GOLD + arena.getName() + CC.YELLOW + " with " + vote.getDisplayName() + CC.YELLOW + ". Thank you for your feedback.");
        this.pendingVotes.remove(player.getUniqueId());
    }
}
