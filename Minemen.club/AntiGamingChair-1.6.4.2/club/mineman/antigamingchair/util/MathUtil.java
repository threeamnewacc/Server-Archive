// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.util;

import java.util.Iterator;
import org.bukkit.entity.Entity;
import club.mineman.antigamingchair.location.CustomLocation;
import org.bukkit.entity.Player;

public class MathUtil
{
    public static CustomLocation getLocationInFrontOfPlayer(final Player player, final double distance) {
        return new CustomLocation(0.0, 0.0, 0.0, 0.0f, 0.0f);
    }
    
    public static CustomLocation getLocationInFrontOfLocation(final double x, final double y, final double z, final float yaw, final float pitch, final double distance) {
        return new CustomLocation(0.0, 0.0, 0.0, 0.0f, 0.0f);
    }
    
    public static boolean isMouseOverEntity(final Player player) {
        return rayTrace(player, 6.0) != null;
    }
    
    public static Entity rayTrace(final Player player, final double distance) {
        final CustomLocation playerLocation = CustomLocation.fromBukkitLocation(player.getLocation());
        Entity currentTarget = null;
        float lowestFov = Float.MAX_VALUE;
        for (final Entity entity : player.getNearbyEntities(distance, distance, distance)) {
            final CustomLocation entityLocation = CustomLocation.fromBukkitLocation(entity.getLocation());
            final float fov = getRotationFromPosition(playerLocation, entityLocation)[0] - playerLocation.getYaw();
            final double groundDistance = playerLocation.getGroundDistanceTo(entityLocation);
            if (lowestFov < fov && fov < groundDistance + 2.0) {
                currentTarget = entity;
                lowestFov = fov;
            }
        }
        return currentTarget;
    }
    
    public static float[] getRotationFromPosition(final CustomLocation playerLocation, final CustomLocation targetLocation) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getX:()D
        //     4: aload_0         /* playerLocation */
        //     5: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getX:()D
        //     8: dsub           
        //     9: dstore_2        /* xDiff */
        //    10: aload_1         /* targetLocation */
        //    11: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getZ:()D
        //    14: aload_0         /* playerLocation */
        //    15: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getZ:()D
        //    18: dsub           
        //    19: dstore          zDiff
        //    21: aload_1         /* targetLocation */
        //    22: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getY:()D
        //    25: aload_0         /* playerLocation */
        //    26: invokevirtual   club/mineman/antigamingchair/location/CustomLocation.getY:()D
        //    29: ldc             0.2
        //    31: dadd           
        //    32: dsub           
        //    33: dstore          yDiff
        //    35: dload_2         /* xDiff */
        //    36: dload_2         /* xDiff */
        //    37: dmul           
        //    38: dload           zDiff
        //    40: dload           zDiff
        //    42: dmul           
        //    43: dadd           
        //    44: invokestatic    net/minecraft/server/v1_8_R3/MathHelper.sqrt:(D)F
        //    47: f2d            
        //    48: dstore          dist
        //    50: dload           zDiff
        //    52: dload_2         /* xDiff */
        //    53: invokestatic    java/lang/Math.atan2:(DD)D
        //    56: ldc2_w          180.0
        //    59: dmul           
        //    60: ldc2_w          3.141592653589793
        //    63: ddiv           
        //    64: d2f            
        //    65: ldc             45.0
        //    67: fsub           
        //    68: fstore          yaw
        //    70: dload           yDiff
        //    72: dload           dist
        //    74: invokestatic    java/lang/Math.atan2:(DD)D
        //    77: ldc2_w          180.0
        //    80: dmul           
        //    81: ldc2_w          3.141592653589793
        //    84: ddiv           
        //    85: dneg           
        //    86: d2f            
        //    87: fstore          pitch
        //    89: iconst_2       
        //    90: newarray        F
        //    92: dup            
        //    93: iconst_0       
        //    94: fload           yaw
        //    96: fastore        
        //    97: dup            
        //    98: iconst_1       
        //    99: fload           pitch
        //   101: fastore        
        //   102: areturn        
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException: -1
        //     at java.util.ArrayList.elementData(ArrayList.java:424)
        //     at java.util.ArrayList.remove(ArrayList.java:501)
        //     at com.strobel.assembler.ir.StackMappingVisitor.pop(StackMappingVisitor.java:267)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:629)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static double getDistanceBetweenAngles(final float angle1, final float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }
        return distance;
    }
    
    public static int pingFormula(final long ping) {
        return (int)Math.ceil(ping / 50.0);
    }
}
