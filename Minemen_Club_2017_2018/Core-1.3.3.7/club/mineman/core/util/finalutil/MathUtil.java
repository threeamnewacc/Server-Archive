// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

public final class MathUtil
{
    private MathUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static double roundOff(final double x, final int places) {
        final double pow = Math.pow(10.0, places);
        return Math.round(x * pow) / pow;
    }
}
