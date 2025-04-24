// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.util;

import java.util.HashSet;
import org.bukkit.World;
import org.bukkit.Location;
import java.util.Set;

public class BlockUtil
{
    private static Set<Byte> blockSolidPassSet;
    private static Set<Byte> blockStairsSet;
    private static Set<Byte> blockLiquidsSet;
    private static Set<Byte> blockWebsSet;
    private static Set<Byte> blockIceSet;
    
    public static boolean isOnGround(final Location location, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracX > 0.7) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracZ < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        }
        else if (fracZ > 0.7 && !BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }
    
    public static boolean isOnStairs(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockStairsSet, down);
    }
    
    private static boolean isUnderBlock(final Location location, final Set<Byte> itemIDs, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracX > 0.7) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracZ < 0.3) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        }
        else if (fracZ > 0.7 && itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }
    
    public static boolean isOnLiquid(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockLiquidsSet, down);
    }
    
    public static boolean isOnWeb(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockWebsSet, down);
    }
    
    public static boolean isOnIce(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockIceSet, down);
    }
    
    static {
        BlockUtil.blockSolidPassSet = new HashSet<Byte>();
        BlockUtil.blockStairsSet = new HashSet<Byte>();
        BlockUtil.blockLiquidsSet = new HashSet<Byte>();
        BlockUtil.blockWebsSet = new HashSet<Byte>();
        BlockUtil.blockIceSet = new HashSet<Byte>();
        BlockUtil.blockSolidPassSet.add((Byte)0);
        BlockUtil.blockSolidPassSet.add((Byte)6);
        BlockUtil.blockSolidPassSet.add((Byte)8);
        BlockUtil.blockSolidPassSet.add((Byte)9);
        BlockUtil.blockSolidPassSet.add((Byte)10);
        BlockUtil.blockSolidPassSet.add((Byte)11);
        BlockUtil.blockSolidPassSet.add((Byte)27);
        BlockUtil.blockSolidPassSet.add((Byte)28);
        BlockUtil.blockSolidPassSet.add((Byte)30);
        BlockUtil.blockSolidPassSet.add((Byte)31);
        BlockUtil.blockSolidPassSet.add((Byte)32);
        BlockUtil.blockSolidPassSet.add((Byte)37);
        BlockUtil.blockSolidPassSet.add((Byte)38);
        BlockUtil.blockSolidPassSet.add((Byte)39);
        BlockUtil.blockSolidPassSet.add((Byte)40);
        BlockUtil.blockSolidPassSet.add((Byte)50);
        BlockUtil.blockSolidPassSet.add((Byte)51);
        BlockUtil.blockSolidPassSet.add((Byte)55);
        BlockUtil.blockSolidPassSet.add((Byte)59);
        BlockUtil.blockSolidPassSet.add((Byte)63);
        BlockUtil.blockSolidPassSet.add((Byte)66);
        BlockUtil.blockSolidPassSet.add((Byte)68);
        BlockUtil.blockSolidPassSet.add((Byte)69);
        BlockUtil.blockSolidPassSet.add((Byte)70);
        BlockUtil.blockSolidPassSet.add((Byte)72);
        BlockUtil.blockSolidPassSet.add((Byte)75);
        BlockUtil.blockSolidPassSet.add((Byte)76);
        BlockUtil.blockSolidPassSet.add((Byte)77);
        BlockUtil.blockSolidPassSet.add((Byte)78);
        BlockUtil.blockSolidPassSet.add((Byte)83);
        BlockUtil.blockSolidPassSet.add((Byte)90);
        BlockUtil.blockSolidPassSet.add((Byte)104);
        BlockUtil.blockSolidPassSet.add((Byte)105);
        BlockUtil.blockSolidPassSet.add((Byte)115);
        BlockUtil.blockSolidPassSet.add((Byte)119);
        BlockUtil.blockSolidPassSet.add((Byte)(-124));
        BlockUtil.blockSolidPassSet.add((Byte)(-113));
        BlockUtil.blockSolidPassSet.add((Byte)(-81));
        BlockUtil.blockStairsSet.add((Byte)53);
        BlockUtil.blockStairsSet.add((Byte)67);
        BlockUtil.blockStairsSet.add((Byte)108);
        BlockUtil.blockStairsSet.add((Byte)109);
        BlockUtil.blockStairsSet.add((Byte)114);
        BlockUtil.blockStairsSet.add((Byte)(-128));
        BlockUtil.blockStairsSet.add((Byte)(-122));
        BlockUtil.blockStairsSet.add((Byte)(-121));
        BlockUtil.blockStairsSet.add((Byte)(-120));
        BlockUtil.blockStairsSet.add((Byte)(-100));
        BlockUtil.blockStairsSet.add((Byte)(-93));
        BlockUtil.blockStairsSet.add((Byte)(-92));
        BlockUtil.blockStairsSet.add((Byte)126);
        BlockUtil.blockStairsSet.add((Byte)(-76));
        BlockUtil.blockLiquidsSet.add((Byte)8);
        BlockUtil.blockLiquidsSet.add((Byte)9);
        BlockUtil.blockLiquidsSet.add((Byte)10);
        BlockUtil.blockLiquidsSet.add((Byte)11);
        BlockUtil.blockWebsSet.add((Byte)30);
        BlockUtil.blockIceSet.add((Byte)79);
        BlockUtil.blockIceSet.add((Byte)(-82));
    }
}
