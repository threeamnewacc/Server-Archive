package us.zonix.practice.runnable;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.boydti.fawe.util.task.Task;
import com.boydti.fawe.util.task.TaskBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;

public class MatchResetRunnable extends BukkitRunnable {
    private final Practice plugin = Practice.getInstance();
    private final Match match;

    public void run() {
        if ((this.match.getKit().isBuild() || this.match.getKit().isSpleef()) && this.match.getPlacedBlockLocations().size() > 0) {
            new TaskBuilder()
                .async(
                    previousResult -> {
                        EditSession editSession = new EditSessionBuilder(this.match.getArena().getA().getWorld())
                            .fastmode(true)
                            .allowedRegionsEverywhere()
                            .autoQueue(false)
                            .limitUnlimited()
                            .build();

                        for (Location location : this.match.getPlacedBlockLocations()) {
                            try {
                                editSession.setBlock(new Vector((double)location.getBlockX(), (double)location.getBlockY(), location.getZ()), new BaseBlock(0));
                            } catch (Exception var6) {
                            }
                        }

                        editSession.flushQueue();
                        return null;
                    }
                )
                .sync(new Task() {
                    public Object run(Object previousResult) {
                        MatchResetRunnable.this.match.getPlacedBlockLocations().clear();
                        MatchResetRunnable.this.match.getArena().addAvailableArena(MatchResetRunnable.this.match.getStandaloneArena());
                        MatchResetRunnable.this.plugin.getArenaManager().removeArenaMatchUUID(MatchResetRunnable.this.match.getStandaloneArena());
                        MatchResetRunnable.this.cancel();
                        return null;
                    }
                })
                .build();
        } else if (this.match.getOriginalBlockChanges().size() > 0) {
            TaskManager.IMP
                .async(
                    () -> {
                        EditSession editSession = new EditSessionBuilder(this.match.getArena().getA().getWorld())
                            .fastmode(true)
                            .allowedRegionsEverywhere()
                            .autoQueue(false)
                            .limitUnlimited()
                            .build();

                        for (BlockState blockState : this.match.getOriginalBlockChanges()) {
                            try {
                                editSession.setBlock(
                                    new Vector(
                                        (double)blockState.getLocation().getBlockX(),
                                        (double)blockState.getLocation().getBlockY(),
                                        blockState.getLocation().getZ()
                                    ),
                                    new BaseBlock(blockState.getTypeId(), blockState.getRawData())
                                );
                            } catch (Exception var5) {
                            }
                        }

                        editSession.flushQueue();
                        TaskManager.IMP.task(() -> {
                            if (this.match.getKit().isSpleef()) {
                                this.match.getOriginalBlockChanges().clear();
                                this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
                                this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
                            }

                            this.cancel();
                        });
                    }
                );
        } else {
            this.cancel();
        }
    }

    public MatchResetRunnable(Match match) {
        this.match = match;
    }
}
