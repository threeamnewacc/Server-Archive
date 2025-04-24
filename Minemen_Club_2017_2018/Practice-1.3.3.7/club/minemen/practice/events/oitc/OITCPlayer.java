// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.oitc;

import club.minemen.practice.events.PracticeEvent;
import java.util.UUID;
import club.minemen.practice.events.EventPlayer;

public class OITCPlayer extends EventPlayer
{
    private int score;
    
    public OITCPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.score = 0;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getScore() {
        return this.score;
    }
}
