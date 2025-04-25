package us.zonix.practice.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.bots.ZonixBot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class BotManager {
    private final Practice plugin = Practice.getInstance();
    private HashMap<UUID, ZonixBot> npcRegistry = new HashMap<>();

    public void createMatch(Player player, Kit kit, ZonixBot.BotDifficulty difficulty) {
        Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Starting training match. " + ChatColor.GREEN + "(" + player.getName() + " vs Zeus)");
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Zeus");
        npc.data().set("player-skin-name", "Emilio");
        npc.spawn(arena.getA().toBukkitLocation());
        player.teleport(arena.getB().toBukkitLocation());
        ZonixBot bot = new ZonixBot();
        bot.setBotDifficulty(difficulty);
        bot.setKit(kit);
        bot.setArena(arena);
        bot.setDestroyed(false);
        bot.setNpc(npc);
        bot.startMechanics(Collections.singletonList(player.getUniqueId()), difficulty);
        playerData.setPlayerState(PlayerState.TRAINING);
        kit.applyToPlayer(player);

        for (Player online : this.plugin.getServer().getOnlinePlayers()) {
            online.hidePlayer(player);
            player.hidePlayer(online);
        }

        for (Player online : this.plugin.getServer().getOnlinePlayers()) {
            online.hidePlayer(bot.getBukkitEntity());
            bot.getBukkitEntity().hidePlayer(online);
        }

        player.showPlayer(bot.getBukkitEntity());
        bot.getBukkitEntity().showPlayer(player);
        this.npcRegistry.put(player.getUniqueId(), bot);
    }

    public void removeMatch(Player player, boolean won) {
        if (this.isTraining(player)) {
            ZonixBot bot = this.npcRegistry.get(player.getUniqueId());
            if (bot.getBotMechanics() != null) {
                bot.getBotMechanics().cancel();
            }

            bot.destroy();
            this.npcRegistry.remove(player.getUniqueId());
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            player.sendMessage(
                ChatColor.YELLOW
                    + "You "
                    + (won ? ChatColor.GREEN.toString() + ChatColor.BOLD + "WON" : ChatColor.RED.toString() + ChatColor.BOLD + "LOST")
                    + ChatColor.YELLOW.toString()
                    + " against the training bot."
            );
        }
    }

    public void forceRemoveMatch(Player player) {
        if (this.isTraining(player)) {
            ZonixBot bot = this.npcRegistry.get(player.getUniqueId());
            if (bot.getBotMechanics() != null) {
                bot.getBotMechanics().cancel();
            }

            bot.destroy();
            this.npcRegistry.remove(player.getUniqueId());
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        }
    }

    public void removeMatch(NPC npc) {
        UUID uuid = this.getPlayerMatch(npc);
        if (uuid != null) {
            if (this.npcRegistry.containsKey(uuid)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    Bukkit.getScheduler()
                        .runTaskLater(
                            this.plugin,
                            () -> {
                                this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                                player.sendMessage(
                                    ChatColor.YELLOW
                                        + "You "
                                        + ChatColor.GREEN.toString()
                                        + ChatColor.BOLD
                                        + "WON"
                                        + ChatColor.YELLOW.toString()
                                        + " against the training bot."
                                );
                            },
                            10L
                        );
                }

                ZonixBot bot = this.npcRegistry.get(uuid);
                if (bot.getBotMechanics() != null) {
                    bot.getBotMechanics().cancel();
                }

                bot.destroy();
                this.npcRegistry.remove(uuid);
            }
        }
    }

    public boolean isTraining(Player player) {
        return this.npcRegistry.containsKey(player.getUniqueId());
    }

    public UUID getPlayerMatch(NPC npcMatching) {
        for (Entry<UUID, ZonixBot> map : this.npcRegistry.entrySet()) {
            if (map.getValue().getNpc().getUniqueId() == npcMatching.getUniqueId()) {
                return map.getKey();
            }
        }

        return null;
    }

    public ZonixBot getBotFromNPC(NPC npc) {
        for (Entry<UUID, ZonixBot> map : this.npcRegistry.entrySet()) {
            if (map.getValue().getNpc().getUniqueId() == npc.getUniqueId()) {
                return map.getValue();
            }
        }

        return null;
    }

    public ZonixBot getBotFromPlayer(Player player) {
        for (Entry<UUID, ZonixBot> map : this.npcRegistry.entrySet()) {
            if (map.getKey() == player.getUniqueId()) {
                return map.getValue();
            }
        }

        return null;
    }

    public HashMap<UUID, ZonixBot> getNpcRegistry() {
        return this.npcRegistry;
    }
}
