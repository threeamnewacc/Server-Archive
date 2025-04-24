// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.oitc;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.events.EventCountdownTask;

public class OITCCountdownTask extends EventCountdownTask
{
    public OITCCountdownTask(final PracticeEvent event) {
        super(event, 120);
    }
    
    @Override
    public boolean shouldAnnounce(final int timeUntilStart) {
        return true;
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
