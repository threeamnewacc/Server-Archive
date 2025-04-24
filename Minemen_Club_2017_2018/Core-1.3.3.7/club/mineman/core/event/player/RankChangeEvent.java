// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event.player;

import club.mineman.core.mineman.Mineman;
import club.mineman.core.rank.Rank;
import club.mineman.core.event.MinemanPlayerEvent;

public class RankChangeEvent extends MinemanPlayerEvent
{
    private Rank from;
    private Rank to;
    
    public RankChangeEvent(final Mineman mineman, final Rank from, final Rank to) {
        super(mineman);
        this.from = from;
        this.to = to;
    }
    
    public Rank getFrom() {
        return this.from;
    }
    
    public Rank getTo() {
        return this.to;
    }
}
