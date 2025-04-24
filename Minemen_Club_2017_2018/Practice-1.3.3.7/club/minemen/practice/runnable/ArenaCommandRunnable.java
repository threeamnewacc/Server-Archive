// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.beans.ConstructorProperties;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.arena.StandaloneArena;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.Practice;

public class ArenaCommandRunnable implements Runnable
{
    private final Practice plugin;
    private final Arena copiedArena;
    private int times;
    
    @Override
    public void run() {
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }
    
    private void duplicateArena(final Arena arena, final int offsetX, final int offsetZ) {
        new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
            @Override
            public void onComplete() {
                final double minX = arena.getMin().getX() + this.getOffsetX();
                final double minZ = arena.getMin().getZ() + this.getOffsetZ();
                final double maxX = arena.getMax().getX() + this.getOffsetX();
                final double maxZ = arena.getMax().getZ() + this.getOffsetZ();
                final double aX = arena.getA().getX() + this.getOffsetX();
                final double aZ = arena.getA().getZ() + this.getOffsetZ();
                final double bX = arena.getB().getX() + this.getOffsetX();
                final double bZ = arena.getB().getZ() + this.getOffsetZ();
                final CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ);
                final CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ);
                final CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ);
                final CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ);
                final StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max);
                arena.addStandaloneArena(standaloneArena);
                arena.addAvailableArena(standaloneArena);
                if (--ArenaCommandRunnable.this.times > 0) {
                    ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(CC.PRIMARY + "Placed a standalone arena of " + CC.SECONDARY + arena.getName() + CC.PRIMARY + " at " + CC.SECONDARY + minX + CC.PRIMARY + ", " + CC.SECONDARY + minZ + CC.PRIMARY + ". " + CC.SECONDARY + ArenaCommandRunnable.this.times + CC.PRIMARY + " arenas remaining.");
                    ArenaCommandRunnable.this.duplicateArena(arena, (int)Math.round(maxX), (int)Math.round(maxZ));
                }
                else {
                    ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(CC.PRIMARY + "Finished pasting " + CC.SECONDARY + ArenaCommandRunnable.this.copiedArena.getName() + CC.PRIMARY + "'s standalone arenas.");
                    ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnables(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
                }
            }
        }.run();
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
    
    @ConstructorProperties({ "plugin", "copiedArena", "times" })
    public ArenaCommandRunnable(final Practice plugin, final Arena copiedArena, final int times) {
        this.plugin = plugin;
        this.copiedArena = copiedArena;
        this.times = times;
    }
}
