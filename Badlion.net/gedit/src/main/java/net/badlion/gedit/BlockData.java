package net.badlion.gedit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

public class BlockData {

    private boolean fromSchematic;

    private Block block;

    private BlockVector blockVector;

    private int typeId;
    private byte data;

    private BlockState blockState;

    public BlockData(Block block) {
        this.block = block;
        this.typeId = block.getTypeId();
        this.data = block.getData();
        this.blockState = block.getState();
        this.fromSchematic = false;
    }

    public BlockData(Block block, BlockVector blockVector) {
        this.block = block;
        this.blockVector = blockVector;
        this.typeId = block.getTypeId();
        this.data = block.getData();
        this.blockState = block.getState();
        this.fromSchematic = false;
    }

    public BlockData(BlockVector blockVector, int typeId, byte data) {
        this.blockVector = blockVector;

        this.typeId = typeId;
        this.data = data;
        this.fromSchematic = true;
    }

    public BlockVector getBlockVector() {
        return blockVector;
    }

    public Block getBlock() {
        return block;
    }

    public int getTypeId() {
        return typeId;
    }

    public byte getData() {
        return data;
    }

    // TODO: Implement this
    public BlockState getBlockState() {
        return blockState;
    }

    public boolean isFromSchematic() {
        return fromSchematic;
    }
}
