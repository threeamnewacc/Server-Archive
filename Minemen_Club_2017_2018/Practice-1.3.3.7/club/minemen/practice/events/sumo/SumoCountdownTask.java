// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.sumo;

import club.minemen.core.util.finalutil.CC;
import java.util.Arrays;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.events.EventCountdownTask;

public class SumoCountdownTask extends EventCountdownTask
{
    public SumoCountdownTask(final PracticeEvent event) {
        super(event, 120);
    }
    
    @Override
    public boolean shouldAnnounce(final int timeUntilStart) {
        return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
    }
    
    @Override
    public boolean canStart() {
        return this.getEvent().getPlayers().size() >= 2;
    }
    
    @Override
    public void onCancel() {
        this.getEvent().sendMessage(CC.RED + "There were not enough players to start the event, so it has been canceled.");
        this.getEvent().end();
    }
}
