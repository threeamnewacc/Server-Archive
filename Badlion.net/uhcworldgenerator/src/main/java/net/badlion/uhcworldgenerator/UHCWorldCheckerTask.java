package net.badlion.uhcworldgenerator;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

public class UHCWorldCheckerTask extends BukkitRunnable {

    public static int RADIUS_OF_CENTER;
    public static int LIMITED_COUNT = 13000; // # of blocks (33% of 100x100)

    public static Integer number100;
    public static Integer number500;
    public static Integer number1K;
    public static Integer number2K;
    public static Integer number3K;
    public static Integer number4K;
    public static boolean isGenerating = false;
    public static Long lastAttemptTime = System.currentTimeMillis();
    public static World world;
    public static World worldNether;
    public static World worldEnd;

    public static int failedAttempts = 0;
    public static String newWorldName;

    public enum BIOME_THRESHOLD { DISALLOWED, LIMITED, ALLOWED }

    public static File folder = new File("worlds/"); // Current plugin directory by default

    public UHCWorldCheckerTask() {
        // Blow away any corrupt/missing worlds
        this.checkForCorruptWorlds(folder, "100");
        this.checkForCorruptWorlds(folder, "500");
        this.checkForCorruptWorlds(folder, "1k");
        this.checkForCorruptWorlds(folder, "2k");
        this.checkForCorruptWorlds(folder, "3k");
        this.checkForCorruptWorlds(folder, "4k");

        // Check how many worlds we have right now
	    for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("100_worlds"); i++) {
		    File file = new File(folder, "uhcworld_100_" + i);
		    if (!file.exists()) {
			    UHCWorldCheckerTask.number100 = i;
			    System.out.println("Next 100 world #: " + UHCWorldCheckerTask.number100);
			    break;
		    }
	    }
	    for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("500_worlds"); i++) {
		    File file = new File(folder, "uhcworld_500_" + i);
		    if (!file.exists()) {
			    UHCWorldCheckerTask.number500 = i;
			    System.out.println("Next 500 world #: " + UHCWorldCheckerTask.number500);
			    break;
		    }
	    }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("1k_worlds"); i++) {
            File file = new File(folder, "uhcworld_1k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number1K = i;
                System.out.println("Next 1k world #: " + UHCWorldCheckerTask.number1K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("2k_worlds"); i++) {
            File file = new File(folder, "uhcworld_2k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number2K = i;
                System.out.println("Next 2k world #: " + UHCWorldCheckerTask.number2K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("3k_worlds"); i++) {
            File file = new File(folder, "uhcworld_3k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number3K = i;
                System.out.println("Next 3k world #: " + UHCWorldCheckerTask.number3K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("4k_worlds"); i++) {
            File file = new File(folder, "uhcworld_4k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number4K = i;
                System.out.println("Next 4k world #: " + UHCWorldCheckerTask.number4K);
                break;
            }
        }
    }

    public void checkForCorruptWorlds(File folder, String size) {
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt(size + "_worlds"); i++) {
            File file = new File(folder, "uhcworld_" + size + "_" + i);
            if (file.exists()) {
                // Check if any lock exists, blow it away if it does
                File lock = new File(folder, "uhcworld_" + size + "_" + i + "/gen.lock");
                if (lock.exists()) {
                    lock.delete();
                }

                File nether = new File(folder, "uhcworld_" + size + "_" + i + "_nether");
                File end = new File(folder, "uhcworld_" + size + "_" + i + "_the_end");

                if (!nether.exists() || !end.exists()) {
                    try {
                        FileUtils.deleteDirectory(file);

                        if (nether.exists()) {
                            FileUtils.deleteDirectory(nether);
                        }

                        if (end.exists()) {
                            FileUtils.deleteDirectory(end);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void run() {
        // Don't let one start if one is generating already
        if (UHCWorldCheckerTask.isGenerating) {
            return;
        }

        // Check how many worlds we have right now
        UHCWorldCheckerTask.number100 = null;
        UHCWorldCheckerTask.number500 = null;
        UHCWorldCheckerTask.number1K = null;
        UHCWorldCheckerTask.number2K = null;
        UHCWorldCheckerTask.number3K = null;
        UHCWorldCheckerTask.number4K = null;
        File folder = new File("worlds/"); // Current plugin directory by default
	    for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("100_worlds"); i++) {
		    File file = new File(folder, "uhcworld_100_" + i);
		    if (!file.exists()) {
			    UHCWorldCheckerTask.number100 = i;
			    System.out.println("Next 100 world #: " + UHCWorldCheckerTask.number100);
			    break;
		    }
	    }
	    for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("500_worlds"); i++) {
		    File file = new File(folder, "uhcworld_500_" + i);
		    if (!file.exists()) {
			    UHCWorldCheckerTask.number500 = i;
			    System.out.println("Next 500 world #: " + UHCWorldCheckerTask.number500);
			    break;
		    }
	    }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("1k_worlds"); i++) {
            File file = new File(folder, "uhcworld_1k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number1K = i;
                System.out.println("Next 1k world #: " + UHCWorldCheckerTask.number1K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("2k_worlds"); i++) {
            File file = new File(folder, "uhcworld_2k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number2K = i;
                System.out.println("Next 2k world #: " + UHCWorldCheckerTask.number2K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("3k_worlds"); i++) {
            File file = new File(folder, "uhcworld_3k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number3K = i;
                System.out.println("Next 3k world #: " + UHCWorldCheckerTask.number3K);
                break;
            }
        }
        for (int i = 1; i <= UHCWorldGenerator.plugin.getConfig().getInt("4k_worlds"); i++) {
            File file = new File(folder, "uhcworld_4k_" + i);
            if (!file.exists()) {
                UHCWorldCheckerTask.number4K = i;
                System.out.println("Next 4k world #: " + UHCWorldCheckerTask.number4K);
                break;
            }
        }

        // Do time check after the check above because we want to finish generating the current world
        // Can we generate worlds at this time?
        int hour = DateTime.now().getHourOfDay();
        if (UHCWorldGenerator.plugin.getEndTime() >= UHCWorldGenerator.plugin.getStartTime()) {
            System.out.println(hour >= UHCWorldGenerator.plugin.getStartTime());
            System.out.println(hour < UHCWorldGenerator.plugin.getEndTime());
            if (!(hour >= UHCWorldGenerator.plugin.getStartTime() && hour < UHCWorldGenerator.plugin.getEndTime())) {
                Bukkit.getLogger().info("Not generating because not in generation time slot (Hour " + hour + ")");
                UHCWorldCheckerTask.lastAttemptTime = System.currentTimeMillis();
                return;
            }
        } else {
            if (!(hour >= UHCWorldGenerator.plugin.getStartTime() || hour < UHCWorldGenerator.plugin.getEndTime())) {
                Bukkit.getLogger().info("Not generating because not in generation time slot (Hour " + hour + ")");
                UHCWorldCheckerTask.lastAttemptTime = System.currentTimeMillis();
                return;
            }
        }

        // Try generating smallest maps first
	    if (UHCWorldCheckerTask.number100 != null) {
		    this.generateNewWorld(100, UHCWorldCheckerTask.number100);
	    } else if (UHCWorldCheckerTask.number500 != null) {
		    this.generateNewWorld(500, UHCWorldCheckerTask.number500);
        } else if (UHCWorldCheckerTask.number1K != null) {
            this.generateNewWorld(1000, UHCWorldCheckerTask.number1K);
        } else if (UHCWorldCheckerTask.number2K != null) {
            this.generateNewWorld(2000, UHCWorldCheckerTask.number2K);
        } else if (UHCWorldCheckerTask.number3K != null) {
            this.generateNewWorld(3000, UHCWorldCheckerTask.number3K);
        } else if (UHCWorldCheckerTask.number4K != null) {
            this.generateNewWorld(4000, UHCWorldCheckerTask.number4K);
        }
    }

    public void generateNewWorld(int radius, int newWorldNumber) {
        UHCWorldCheckerTask.RADIUS_OF_CENTER = radius;
        String newWorldName;
	    if (radius == 100) {
		    newWorldName = "uhcworld_100_" + newWorldNumber;
	    } else if (radius == 500) {
		    newWorldName = "uhcworld_500_" + newWorldNumber;
	    } else {
		    newWorldName = "uhcworld_" + (radius / 1000) + "k_" + newWorldNumber;
	    }

        // Just to avoid any weird bukkit race conditions
        UHCWorldCheckerTask.isGenerating = true;

        WorldCreator worldCreator = new WorldCreator(newWorldName);

        // Another fail safe
        try {
            UHCWorldCheckerTask.world = UHCWorldGenerator.plugin.getServer().createWorld(worldCreator);
        } catch (Exception e) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }

        int waterCount = 0;

        UHCWorldCheckerTask.lastAttemptTime = System.currentTimeMillis();
        Bukkit.getLogger().info("Loaded a new world.");
        boolean flag = false;
        int limitedCount = 0;
        for (int i = -UHCWorldCheckerTask.RADIUS_OF_CENTER; i <= UHCWorldCheckerTask.RADIUS_OF_CENTER; ++i) {
            boolean isInvalid = false;
            for (int j = -UHCWorldCheckerTask.RADIUS_OF_CENTER; j <= UHCWorldCheckerTask.RADIUS_OF_CENTER; j++) {
                Biome biome = UHCWorldCheckerTask.world.getBiome(i, j);

                BIOME_THRESHOLD threshold = isValidBiome(biome, i, j);
                if (threshold == BIOME_THRESHOLD.DISALLOWED) {
                    Bukkit.getLogger().info("Biome at " + i + " " + j + " is " + biome.name());
                    Bukkit.getLogger().info("Invalid biome");
                    isInvalid = true;
                    break;
                } else if (threshold == BIOME_THRESHOLD.LIMITED) {
                    if (++limitedCount >= LIMITED_COUNT) {
                        Bukkit.getLogger().info("Too much hills/forests/etc");
                        Bukkit.getLogger().info("Invalid biome");
                        isInvalid = true;
                        break;
                    }
                }

                boolean isCenter = i >= -100 && i <= 100 && j >= -100 && j <= 100;
                if (isCenter) {
                    Block block = UHCWorldCheckerTask.world.getHighestBlockAt(i, j).getLocation().add(0, -1, 0).getBlock();
                    if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
                        ++waterCount;
                    }
                }

                if (waterCount >= 1000) {
                    Bukkit.getLogger().info("Invalid center, too much water/lava.");
                    isInvalid = true;
                    break;
                }
            }

            if (isInvalid) {
                flag = true;
                Bukkit.getLogger().info("Invalid biome2");
                break;
            }
        }

        // Actually got this far...we have a valid world, generate the rest
        if (flag) {
            Bukkit.getLogger().info("Failed to find a good seed (" + UHCWorldCheckerTask.world.getSeed() + ").");
            Bukkit.getServer().unloadWorld(UHCWorldCheckerTask.world, false);
            deleteDirectory(new File("worlds/" + newWorldName));
            UHCWorldCheckerTask.isGenerating = false;
            UHCWorldCheckerTask.failedAttempts += 1;

            /*if (UHCWorldCheckerTask.failedAttempts >= 100) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }*/
            return;
        } else {
            Bukkit.getLogger().info("Found a good seed (" + UHCWorldCheckerTask.world.getSeed() + ").");
            this.cancel();
        }

        // Create Lock
        File lock = new File(folder, newWorldName + "/gen.lock");
        try {
            lock.createNewFile();
        } catch (IOException e) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            return;
        }

        UHCWorldCheckerTask.isGenerating = true;
        UHCWorldCheckerTask.newWorldName = newWorldName;

        // Start the worldborder stuff now
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + newWorldName + " set " + radius + " 0 0");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + newWorldName + " fill 1000");
        UHCWorldGenerator.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");

        /*new BukkitRunnable() {
            public void run() {
                Bukkit.getLogger().info("Size N " + ((CraftWorld) UHCWorldCheckerTask.world).getHandle().getN().size());
                Bukkit.getLogger().info("Size V " + ((CraftWorld) UHCWorldCheckerTask.world).getHandle().getV().size());
            }
        }.runTaskTimer(UHCWorldGenerator.plugin, 20, 20);*/
    }

    public static BIOME_THRESHOLD isValidBiome(Biome biome, int i, int j) {
        // flag = is this in 100x100
        boolean flag = i <= 100 && i >= -100 && j <= 100 && j >= -100;
        if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.DESERT_MOUNTAINS
                || biome == Biome.PLAINS || biome == Biome.SUNFLOWER_PLAINS
                || biome == Biome.SWAMPLAND || biome == Biome.SWAMPLAND_MOUNTAINS || biome == Biome.SMALL_MOUNTAINS
                || biome == Biome.SAVANNA || biome == Biome.SAVANNA_MOUNTAINS || biome == Biome.SAVANNA_PLATEAU || biome == Biome.SAVANNA_PLATEAU_MOUNTAINS
                || biome == Biome.RIVER
                || biome == Biome.FROZEN_RIVER || biome == Biome.ICE_PLAINS) {
            return BIOME_THRESHOLD.ALLOWED;
        } else if (flag && (biome == Biome.FOREST || biome == Biome.FOREST_HILLS || biome == Biome.BIRCH_FOREST || biome == Biome.BIRCH_FOREST_HILLS || biome == Biome.BIRCH_FOREST_HILLS_MOUNTAINS
                             || biome == Biome.BIRCH_FOREST_MOUNTAINS || biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS || biome == Biome.TAIGA_MOUNTAINS || biome == Biome.ICE_PLAINS_SPIKES
                             || biome == Biome.MEGA_SPRUCE_TAIGA || biome == Biome.MEGA_SPRUCE_TAIGA_HILLS || biome == Biome.MEGA_TAIGA || biome == Biome.MEGA_TAIGA_HILLS || biome == Biome.FLOWER_FOREST
                             || biome == Biome.COLD_BEACH || biome == Biome.COLD_TAIGA || biome == Biome.COLD_TAIGA_HILLS || biome == Biome.COLD_TAIGA_HILLS || biome == Biome.COLD_TAIGA_MOUNTAINS)) {
            return BIOME_THRESHOLD.LIMITED;
        } else if (flag && (biome == Biome.ROOFED_FOREST || biome == Biome.ROOFED_FOREST_MOUNTAINS
		            || biome == Biome.MESA || biome == Biome.MESA_PLATEAU || biome == Biome.MESA_BRYCE || biome == Biome.MESA_PLATEAU_FOREST || biome == Biome.MESA_PLATEAU_FOREST_MOUNTAINS || biome == Biome.MESA_PLATEAU_MOUNTAINS
                    || biome == Biome.EXTREME_HILLS || biome == Biome.EXTREME_HILLS_MOUNTAINS || biome == Biome.EXTREME_HILLS_PLUS || biome == Biome.EXTREME_HILLS_PLUS_MOUNTAINS
                    || biome == Biome.FROZEN_OCEAN || biome == Biome.ICE_MOUNTAINS)) {
            return BIOME_THRESHOLD.DISALLOWED;
        }

        // We are only picky at 0,0 otherwise stuff is allowed
        return flag ? BIOME_THRESHOLD.DISALLOWED : BIOME_THRESHOLD.ALLOWED;
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void addBedrockBorder(final String worldName, final int radius) {
        new BukkitRunnable() {

            private int counter = -radius - 1;
            private boolean phase1 = false;
            private boolean phase2 = false;
            private boolean phase3 = false;

            @Override
            public void run() {
                if (!phase1) {
                    int maxCounter = counter + 500;
                    int x = -radius - 1;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
	                    if (radius == 100) {
		                    block.setType(Material.GLASS);
	                    } else {
		                    block.setType(Material.BEDROCK);
	                    }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase1 = true;
                    }

                    return;
                }

                if (!phase2) {
                    int maxCounter = counter + 500;
                    int x = radius;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
	                    if (radius == 100) {
		                    block.setType(Material.GLASS);
	                    } else {
		                    block.setType(Material.BEDROCK);
	                    }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase2 = true;
                    }

                    return;
                }

                if (!phase3) {
                    int maxCounter = counter + 500;
                    int z = -radius - 1;
                    for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                        if (x == radius || x == -radius - 1) {
                            continue;
                        }

                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
	                    if (radius == 100) {
		                    block.setType(Material.GLASS);
	                    } else {
		                    block.setType(Material.BEDROCK);
	                    }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase3 = true;
                    }

                    return;
                }


                int maxCounter = counter + 500;
                int z = radius;
                for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                    if (x == radius || x == -radius - 1) {
                        continue;
                    }

                    Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
	                if (radius == 100) {
		                block.setType(Material.GLASS);
	                } else {
		                block.setType(Material.BEDROCK);
	                }
                }

                if (counter >= radius) {
                    // Fire off our custom event
                    BorderGenerationCompleteEvent event = new BorderGenerationCompleteEvent(worldName);
                    UHCWorldGenerator.plugin.getServer().getPluginManager().callEvent(event);

                    this.cancel();
                }
            }
        }.runTaskTimer(UHCWorldGenerator.plugin, 0, 5);
    }


}
