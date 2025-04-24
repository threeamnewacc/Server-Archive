// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.sumo;

import club.minemen.practice.events.PracticeEvent;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;
import club.minemen.practice.events.EventPlayer;

public class SumoPlayer extends EventPlayer
{
    private SumoState state;
    private BukkitTask fightTask;
    private SumoPlayer fighting;
    
    public SumoPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = SumoState.WAITING;
    }
    
    public void setState(final SumoState state) {
        this.state = state;
    }
    
    public void setFightTask(final BukkitTask fightTask) {
        this.fightTask = fightTask;
    }
    
    public void setFighting(final SumoPlayer fighting) {
        this.fighting = fighting;
    }
    
    public SumoState getState() {
        return this.state;
    }
    
    public BukkitTask getFightTask() {
        return this.fightTask;
    }
    
    public SumoPlayer getFighting() {
        return this.fighting;
    }
    
    public enum SumoState
    {
        WAITING, 
        FIGHTING, 
        FOUGHT;
    }
}
