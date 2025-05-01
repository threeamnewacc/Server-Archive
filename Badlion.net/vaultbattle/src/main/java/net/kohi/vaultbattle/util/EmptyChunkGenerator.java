package net.kohi.vaultbattle.util;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {

    private static final EmptyChunkGenerator instance = new EmptyChunkGenerator();

    private final byte[] buf = new byte[0x10000];

    public static EmptyChunkGenerator getInstance() {
        return EmptyChunkGenerator.instance;
    }

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return buf;
    }
}
