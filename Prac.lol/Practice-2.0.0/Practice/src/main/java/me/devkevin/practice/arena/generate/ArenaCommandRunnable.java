package me.devkevin.practice.arena.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.location.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class ArenaCommandRunnable implements Runnable {

    private final Practice plugin;
    private final Arena copiedArena;

    private int times;

    @Override
    public void run() {
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }

    private void duplicateArena(Arena arena, int offsetX, int offsetZ) {
        new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
            @Override
            public void onComplete() {
                double minX = arena.getMin().getX() + this.getOffsetX();
                double minZ = arena.getMin().getZ() + this.getOffsetZ();
                double maxX = arena.getMax().getX() + this.getOffsetX();
                double maxZ = arena.getMax().getZ() + this.getOffsetZ();

                double aX = arena.getA().getX() + this.getOffsetX();
                double aZ = arena.getA().getZ() + this.getOffsetZ();
                double bX = arena.getB().getX() + this.getOffsetX();
                double bZ = arena.getB().getZ() + this.getOffsetZ();

                CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
                CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ, arena.getB().getYaw(), arena.getB().getPitch());

                StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max);

                arena.addStandaloneArena(standaloneArena);
                arena.addAvailableArena(standaloneArena);

                String arenaPasteMessage = "[Standalone Arena] - " + arena.getName() + " placed at " + (int) minX + ", " + (int) minZ + ". " + ArenaCommandRunnable.this.times + " copies remaining.";

                if (--ArenaCommandRunnable.this.times > 0) {
                    ArenaCommandRunnable.this.plugin.getServer().getLogger().info(arenaPasteMessage);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage(ChatColor.GREEN + arenaPasteMessage);
                        }
                    }
                    ArenaCommandRunnable.this.duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage(ChatColor.GREEN + "All the copies for " + ArenaCommandRunnable.this.copiedArena.getName() + " have been pasted successfully!");
                        }
                    }
                    ArenaCommandRunnable.this.plugin.getServer().getLogger().info("All the copies for " + ArenaCommandRunnable.this.copiedArena.getName() + " have been pasted successfully!");
                    ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnable(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnable() - 1);
                    this.getPlugin().getArenaManager().reloadArenas();
                }
            }
        }.run();
    }
}