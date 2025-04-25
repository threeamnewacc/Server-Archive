package us.zonix.practice.runnable;

import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.arena.StandaloneArena;

public class ArenaCommandRunnable implements Runnable {
    private final Practice plugin;
    private final Arena copiedArena;
    private int times;

    @Override
    public void run() {
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }

    private void duplicateArena(final Arena arena, int offsetX, int offsetZ) {
        (new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
                @Override
                public void onComplete() {
                    double minX = arena.getMin().getX() + (double)this.getOffsetX();
                    double minZ = arena.getMin().getZ() + (double)this.getOffsetZ();
                    double maxX = arena.getMax().getX() + (double)this.getOffsetX();
                    double maxZ = arena.getMax().getZ() + (double)this.getOffsetZ();
                    double aX = arena.getA().getX() + (double)this.getOffsetX();
                    double aZ = arena.getA().getZ() + (double)this.getOffsetZ();
                    double bX = arena.getB().getX() + (double)this.getOffsetX();
                    double bZ = arena.getB().getZ() + (double)this.getOffsetZ();
                    CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                    CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                    CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
                    CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ, arena.getB().getYaw(), arena.getB().getPitch());
                    StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max);
                    arena.addStandaloneArena(standaloneArena);
                    arena.addAvailableArena(standaloneArena);
                    if (--ArenaCommandRunnable.this.times > 0) {
                        ArenaCommandRunnable.this.plugin
                            .getServer()
                            .getLogger()
                            .info(
                                "Placed a standalone arena of "
                                    + arena.getName()
                                    + " at "
                                    + (int)minX
                                    + ", "
                                    + (int)minZ
                                    + ". "
                                    + ArenaCommandRunnable.this.times
                                    + " arenas remaining."
                            );
                        ArenaCommandRunnable.this.duplicateArena(arena, (int)Math.round(maxX), (int)Math.round(maxZ));
                    } else {
                        ArenaCommandRunnable.this.plugin
                            .getServer()
                            .getLogger()
                            .info("Finished pasting " + ArenaCommandRunnable.this.copiedArena.getName() + "'s standalone arenas.");
                        ArenaCommandRunnable.this.plugin
                            .getArenaManager()
                            .setGeneratingArenaRunnables(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
                        this.getPlugin().getArenaManager().reloadArenas();
                    }
                }
            })
            .run();
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public Arena getCopiedArena() {
        return this.copiedArena;
    }

    public int getTimes() {
        return this.times;
    }

    public ArenaCommandRunnable(Practice plugin, Arena copiedArena, int times) {
        this.plugin = plugin;
        this.copiedArena = copiedArena;
        this.times = times;
    }
}
