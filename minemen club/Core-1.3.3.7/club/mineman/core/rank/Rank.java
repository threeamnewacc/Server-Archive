// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.rank;

import java.util.Arrays;
import club.mineman.core.util.finalutil.CC;

public enum Rank
{
    NORMAL("", CC.GREEN, "Normal"), 
    MEMBER(CC.D_GRAY + "[" + CC.D_GREEN + "M" + CC.D_GRAY + "] ", CC.GREEN, "Member"), 
    CLUBBER(CC.D_GRAY + "[" + CC.D_PURPLE + "C\u2605" + CC.D_GRAY + "] ", CC.PINK, "Clubber"), 
    BARTENDER(CC.D_GRAY + "[" + CC.D_AQUA + "\u2721" + CC.D_GRAY + "] ", CC.AQUA, "Bartender"), 
    PARTYMAN(CC.D_GRAY + "[" + CC.GOLD + "\u272a" + CC.D_GRAY + "] ", CC.YELLOW, "Partyman"), 
    BUILDER(CC.D_GREEN, "Builder"), 
    YOUTUBER(CC.D_PURPLE, "YouTuber"), 
    VIP(CC.GOLD, "Famous"), 
    TRAINEE(CC.YELLOW, "Trainee"), 
    MOD(CC.D_AQUA, "Moderator"), 
    ADMIN(CC.RED, "Admin"), 
    DEVELOPER(CC.AQUA, "Developer"), 
    OWNER(CC.D_RED, "Owner");
    
    public static final Rank[] RANKS;
    private final String prefix;
    private final String color;
    private final String name;
    
    private Rank(final String prefix, final String color, final String name) {
        this.prefix = prefix;
        this.color = color;
        this.name = name;
    }
    
    private Rank(final String color, final String name) {
        this(CC.D_GRAY + "[" + color + name + CC.D_GRAY + "] ", color, name);
    }
    
    public static Rank getByName(final String name) {
        return Arrays.stream(Rank.RANKS).filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public int getPriority() {
        return this.ordinal();
    }
    
    public boolean hasRank(final Rank requiredRank) {
        return this.getPriority() >= requiredRank.getPriority();
    }
    
    public boolean isStaff() {
        return this.ordinal() >= Rank.TRAINEE.ordinal();
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public String getName() {
        return this.name;
    }
    
    static {
        RANKS = values();
    }
}
