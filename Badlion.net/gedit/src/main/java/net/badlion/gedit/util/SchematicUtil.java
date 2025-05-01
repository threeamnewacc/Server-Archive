package net.badlion.gedit.util;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import net.badlion.gedit.BlockData;
import net.badlion.gedit.GEdit;
import net.badlion.gedit.sessions.Session;
import net.badlion.gedit.sessions.SessionManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SchematicUtil {

    public static void saveSession(Player player, String name) throws Exception {
        Session session = SessionManager.getSession(player);
        SchematicUtil.saveSession(session.getWandSelection(), player.getLocation(), name);
    }

    public static void saveSession(WandSelection wandSelection, Location origin, String name) throws Exception {
        CompoundTag data = new CompoundTag("Schematic", new CompoundMap());

        if (wandSelection.getAllBlocks().isEmpty()) {
            throw new Exception("Session is empty!");
        }

        data.getValue().put(new StringTag("Materials", "Alpha"));

        // Offset used by world edit, this position is where your are located when you copy the selection
        IntTag offsetX = new IntTag("WEOffsetX", origin.getBlockX());
        IntTag offsetY = new IntTag("WEOffsetY", origin.getBlockY());
        IntTag offsetZ = new IntTag("WEOffsetZ", origin.getBlockZ());

        data.getValue().put(offsetX);
        data.getValue().put(offsetY);
        data.getValue().put(offsetZ);

        int width = wandSelection.getWidth();
        int height = wandSelection.getHeight();
        int length = wandSelection.getLength();

        ShortTag widthTag = new ShortTag("Width", (short) width);
        ShortTag heightTag = new ShortTag("Height", (short) height);
        ShortTag lengthTag = new ShortTag("Length", (short) length);

        data.getValue().put(widthTag);
        data.getValue().put(lengthTag);
        data.getValue().put(heightTag);

        byte[] blockIds = new byte[width * height * length];
        byte[] blockDatas = new byte[width * height * length];

        BlockData[][][] blocks = wandSelection.getBlockDataArray();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    BlockData blockData = blocks[x][y][z];
                    blockIds[index] = (byte) blockData.getTypeId();
                    blockDatas[index] = blockData.getData();
                }
            }
        }

        // From MC Wiki: Block IDs defining the terrain. 8 bits per block. Sorted by height (bottom to top) then length then width -- the index of the block at X,Y,Z is (Y×length + Z)×width + X.
        ByteArrayTag blockTag = new ByteArrayTag("Blocks", blockIds);

        // From MC Wiki: Block data additionally defining parts of the terrain. Only the lower 4 bits of each byte are used. (Unlike in the chunk format, the block data in the schematic format occupies a full byte per block.)
        ByteArrayTag blockDataTag = new ByteArrayTag("Data", blockDatas);

        data.getValue().put(blockTag);
        data.getValue().put(blockDataTag);

        File file = new File(GEdit.schematics, name + ".schematic");
        NBTOutputStream nbtout = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(file)), false);
        nbtout.writeTag(data);
        nbtout.close();
    }


    public static void loadSession(final File file, final Player player) throws Exception {
        final Long start = System.currentTimeMillis();
        SessionManager.getSession(player).setLoadingSchematic(true);
        Bukkit.getLogger().log(Level.INFO, "LOADING SCHEMATIC Start: " + start);
        new BukkitRunnable() {
            @Override
            public void run() {
                try (NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)), false)) {
                    final Tag schematicTag = nbtStream.readTag();
                    nbtStream.close();
                    Bukkit.getLogger().log(Level.INFO, "LOADING SCHEMATIC Async Done: " + (System.currentTimeMillis() - start));

                    if (!schematicTag.getName().equals("Schematic")) {
                        new Exception("This file is not in MCEDIT Schematic Format.").printStackTrace();
                        return;
                    }
                    CompoundMap schematicData = (CompoundMap) schematicTag.getValue();
                    if (!schematicData.containsKey("Blocks")) {
                        new Exception("This schematic file has no blocks tag").printStackTrace();
                        return;
                    }

                    // From MC Wiki: This will be "Classic" for schematics exported from Minecraft Classic levels, and "Alpha" for those from Minecraft Alpha and newer levels.
                    String materials = ((StringTag) schematicData.get("Materials")).getValue();
                    if (!materials.equals("Alpha")) {
                        new Exception("This schematic is not from an Alpha or newer level").printStackTrace();
                        return;
                    }

                    short width = ((ShortTag) schematicData.get("Width")).getValue();
                    short length = ((ShortTag) schematicData.get("Length")).getValue();
                    short height = ((ShortTag) schematicData.get("Height")).getValue();
                    int offsetX = ((IntTag) schematicData.get("WEOffsetX")).getValue();
                    int offsetY = ((IntTag) schematicData.get("WEOffsetY")).getValue();
                    int offsetZ = ((IntTag) schematicData.get("WEOffsetZ")).getValue();

                    // From MC Wiki: Block IDs defining the terrain. 8 bits per block. Sorted by height (bottom to top) then length then width -- the index of the block at X,Y,Z is (Y×length + Z)×width + X.
                    byte[] blockIds = ((ByteArrayTag) schematicData.get("Blocks")).getValue();

                    // From MC Wiki: Block data additionally defining parts of the terrain. Only the lower 4 bits of each byte are used. (Unlike in the chunk format, the block data in the schematic format occupies a full byte per block.)
                    byte[] blockData = ((ByteArrayTag) schematicData.get("Data")).getValue();

                    Session session = SessionManager.getSession(player);
                    session.setxOffSet(offsetX);
                    session.setyOffSet(offsetY);
                    session.setzOffSet(offsetZ);
                    int airLoaded = 0;
                    int blocksLoaded = 0;
                    session.getBlockStates().clear();
                    short[] blocks = new short[blockIds.length];
                    for (int index = 0; index < blockIds.length; index++) {
                        blocks[index] = (short) (blockIds[index] & 0xFF);
                    }
                    for (int x = 0; x < width; ++x) {
                        for (int y = 0; y < height; ++y) {
                            for (int z = 0; z < length; ++z) {
                                int index = y * width * length + z * width + x;
                                BlockVector blockVector = new BlockVector(x, y, z);
                                if (blocks[index] == 0) {
                                    airLoaded++;
                                } else {
                                    blocksLoaded++;
                                }
                                session.getBlockStates().add(new BlockData(blockVector, blocks[index], blockData[index]));
                            }
                        }
                    }
                    session.setLoadingSchematic(false);
                    player.sendMessage(ChatColor.GREEN + "Schematic loaded!");
                    player.sendMessage(ChatColor.GOLD + "AIR: " + ChatColor.GREEN + airLoaded);
                    player.sendMessage(ChatColor.GOLD + "BLOCKS: " + ChatColor.GREEN + blocksLoaded);
                    Bukkit.getLogger().log(Level.INFO, "LOADING SCHEMATIC async Finish: " + (System.currentTimeMillis() - start));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(GEdit.getInstance());
    }

    public static List<BlockData> getBlockDataFromSchematic(final File file) throws Exception {
        try (NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)), false)) {
            final Tag schematicTag = nbtStream.readTag();
            nbtStream.close();

            if (!schematicTag.getName().equals("Schematic")) {
                new Exception("This file is not in MCEDIT Schematic Format.").printStackTrace();
                return null;
            }
            CompoundMap schematicData = (CompoundMap) schematicTag.getValue();
            if (!schematicData.containsKey("Blocks")) {
                new Exception("This schematic file has no blocks tag").printStackTrace();
                return null;
            }

            // From MC Wiki: This will be "Classic" for schematics exported from Minecraft Classic levels, and "Alpha" for those from Minecraft Alpha and newer levels.
            String materials = ((StringTag) schematicData.get("Materials")).getValue();
            if (!materials.equals("Alpha")) {
                new Exception("This schematic is not from an Alpha or newer level").printStackTrace();
                return null;
            }

            short width = ((ShortTag) schematicData.get("Width")).getValue();
            short length = ((ShortTag) schematicData.get("Length")).getValue();
            short height = ((ShortTag) schematicData.get("Height")).getValue();

            // From MC Wiki: Block IDs defining the terrain. 8 bits per block. Sorted by height (bottom to top) then length then width -- the index of the block at X,Y,Z is (Y×length + Z)×width + X.
            byte[] blockIds = ((ByteArrayTag) schematicData.get("Blocks")).getValue();

            // From MC Wiki: Block data additionally defining parts of the terrain. Only the lower 4 bits of each byte are used. (Unlike in the chunk format, the block data in the schematic format occupies a full byte per block.)
            byte[] blockData = ((ByteArrayTag) schematicData.get("Data")).getValue();

            short[] blocks = new short[blockIds.length];
            for (int index = 0; index < blockIds.length; index++) {
                blocks[index] = (short) (blockIds[index] & 0xFF);
            }
            List<BlockData> blockStates = new ArrayList<>();
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        int index = y * width * length + z * width + x;
                        BlockVector blockVector = new BlockVector(x, y, z);
                        blockStates.add(new BlockData(blockVector, blocks[index], blockData[index]));
                    }
                }
            }
            return blockStates;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
