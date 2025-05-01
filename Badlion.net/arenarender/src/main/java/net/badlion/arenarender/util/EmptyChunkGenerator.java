package net.badlion.arenarender.util;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {

	private static final EmptyChunkGenerator instance = new EmptyChunkGenerator();

	public static EmptyChunkGenerator getInstance(){
		return EmptyChunkGenerator.instance;
	}

	private final byte[] buf = new byte[0x10000];

	@Override
	public byte[] generate(World world, Random random, int x, int z) {
		return buf;
	}
}
