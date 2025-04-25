package us.zonix.hcfactions.event.utils;

import org.bukkit.block.*;

public enum CuboidDirection
{
    NORTH, 
    EAST, 
    SOUTH, 
    WEST, 
    UP, 
    DOWN, 
    HORIZONTAL, 
    VERTICAL, 
    BOTH, 
    UNKNOWN;
    
    public CuboidDirection opposite() {
        switch (this) {
            case NORTH: {
                return CuboidDirection.SOUTH;
            }
            case EAST: {
                return CuboidDirection.WEST;
            }
            case SOUTH: {
                return CuboidDirection.NORTH;
            }
            case WEST: {
                return CuboidDirection.EAST;
            }
            case HORIZONTAL: {
                return CuboidDirection.VERTICAL;
            }
            case VERTICAL: {
                return CuboidDirection.HORIZONTAL;
            }
            case UP: {
                return CuboidDirection.DOWN;
            }
            case DOWN: {
                return CuboidDirection.UP;
            }
            case BOTH: {
                return CuboidDirection.BOTH;
            }
            default: {
                return CuboidDirection.UNKNOWN;
            }
        }
    }
    
    public BlockFace toBukkitDirection() {
        switch (this) {
            case NORTH: {
                return BlockFace.NORTH;
            }
            case EAST: {
                return BlockFace.EAST;
            }
            case SOUTH: {
                return BlockFace.SOUTH;
            }
            case WEST: {
                return BlockFace.WEST;
            }
            case HORIZONTAL: {
                return null;
            }
            case VERTICAL: {
                return null;
            }
            case UP: {
                return BlockFace.UP;
            }
            case DOWN: {
                return BlockFace.DOWN;
            }
            case BOTH: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
}
