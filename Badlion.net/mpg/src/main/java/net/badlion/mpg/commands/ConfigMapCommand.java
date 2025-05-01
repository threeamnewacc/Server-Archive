package net.badlion.mpg.commands;

import com.google.common.base.Joiner;
import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.gguard.UnsafeLocation;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.MapChestFinderTask;
import net.badlion.worldrotator.GWorld;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMapCommand implements CommandExecutor {

    private static Map<String, World> loadedWorlds = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
	        for (MPGPlayer.PlayerState playerState : MPGPlayer.PlayerState.values()) {
		        System.out.println("=====" + playerState + "======");
		        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(playerState)) {
			        System.out.println(mpgPlayer.getUsername() + " (" + mpgPlayer.getState() + ")");
		        }
	        }
	        return true;
        }

        Player player = (Player) sender;

	    Location playerLocation = Gberry.getCenterOfBlock(player.getLocation());

        String locString = Gberry.getLocationString(playerLocation);

        switch (args[0]) {
	        case "lel":
		        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
			        if (!mpgPlayer.getUsername().equals("SmellyPenguin")) {
				        mpgPlayer.getPlayer().damage(20D);
			        }
		        }
		        break;
	        case "naughtystaff":
		        final List<String> groups = new ArrayList<>();

		        groups.add("admin");
		        groups.add("builder");
		        groups.add("developer");
		        groups.add("tournament");
		        groups.add("jrdev");
		        groups.add("arenamngr");
		        groups.add("sgmngr");
		        groups.add("uhcmngr");
		        groups.add("buildmngr");
		        groups.add("builderplus");
		        groups.add("senioruhc");
		        groups.add("sgseniormod");
		        groups.add("seniormod");
		        groups.add("uhc");
		        groups.add("mod");
		        groups.add("sgmod");
		        groups.add("uhct");
		        groups.add("sgtrial");
		        groups.add("trial");
		        groups.add("tournmngr");
		        groups.add("gcheat");
		        groups.add("translator");
		        groups.add("kohi");
		        groups.add("kohitrial");
		        groups.add("kohisenior");
		        groups.add("kohimngr");
		        groups.add("fmod");
		        groups.add("fsenior");
		        groups.add("fmngr");

		        BukkitUtil.runTaskAsync(new Runnable() {
			        @Override
			        public void run() {
				        String query = "SELECT * FROM potion_gperms_users WHERE \"group\" = ?;";
				        String query2 = "SELECT * FROM smelly_chat_settings WHERE uuid = ?;";

				        Connection connection = null;
				        PreparedStatement ps = null;
				        ResultSet rs = null;
				        ResultSet rs2 = null;

				        try {
					        connection = Gberry.getConnection();

					        for (String group : groups) {
						        ps = connection.prepareStatement(query);
						        ps.setString(1, group);

						        rs = Gberry.executeQuery(connection, ps);

						        while (rs.next()) {
							        String uuid = rs.getString("uuid");

							        ps = connection.prepareStatement(query2);
							        ps.setString(1, uuid);

							        rs2 = Gberry.executeQuery(connection, ps);

							        if (rs2.next()) {
								        try {
									        for (String str : CompressionUtil.decompress(rs2.getBytes("settings")).split(",")) {
										        if (!str.isEmpty()) {
											        try {
												        String[] array2 = str.split(":");
												        if (array2[0].equals("PRIVATE_MESSAGES")) {
													        //System.out.println(Gberry.getUsernameFromUUID(uuid) + " " + Boolean.valueOf(array2[1]));

													        if (!Boolean.valueOf(array2[1])) {
														        System.out.println(Gberry.getUsernameFromUUID(uuid));
														        break;
													        }
												        }
											        } catch (IllegalArgumentException e) {
												        e.printStackTrace();
											        }
										        }
									        }
								        } catch (Exception e) {
									        e.printStackTrace();
								        }
							        }
						        }
					        }
				        } catch (SQLException e) {
					        e.printStackTrace();
				        } finally {
					        Gberry.closeComponents(rs, ps, connection);
					        if (rs2 != null) {
						        try {
							        rs2.close();
						        } catch (SQLException e) {
							        e.printStackTrace();
						        }
					        }
				        }
			        }
		        });
		        break;
	        case "loadchunks":
		        World world = player.getWorld();

		        int x = player.getLocation().getChunk().getX();
		        int z = player.getLocation().getChunk().getZ();

		        for (int i = -50; i < 50; i++) {
			        for (int j = -50; j < 50; j++) {
				        world.loadChunk(x + i, z + j);
			        }
		        }

		        player.sendMessage(ChatColor.GREEN + "A 50x50 region of chunks around you has been loaded");
		        break;
	        case "serverstate":
		        player.sendMessage(MPG.getInstance().getServerState() + "");
		        break;
	        case "gamestate":
		        player.sendMessage(MPG.getInstance().getMPGGame().getGameState() + "");
		        break;
	        case "state":
		        player.sendMessage(MPGPlayerManager.getMPGPlayer(player).getState() + "");
		        break;
	        case "test":
		        player.getWorld().spawnDeadLayingEntity(player, player.getLocation(), 5, true);

		        player.sendMessage(player.getLocation().toString());
		        break;

		        /*final Location location = player.getLocation().add(-0.45, 0.33, 0);

		        // Set bed at y = 0
		        Location bed = location.clone();
		        bed.setY(0);
		        bed.getBlock().setType(Material.BED_BLOCK);

		        Object playerProfile = TinyProtocolReferences.getPlayerProfile.invoke(player);

		        Object properties = TinyProtocolReferences.gameProfilePropertyMap.get(playerProfile);
		        Collection<Object> propertyCollection = (Collection<Object>) TinyProtocolReferences.propertyMapGet.invoke(properties, "textures");

		        Object property = propertyCollection.iterator().next();

		        final String texture = TinyProtocolReferences.propertyValue.get(property);
		        final String signature = TinyProtocolReferences.propertySignature.get(property);
		        System.out.println(texture);
		        System.out.println(signature);

		        int entityId = Entity.entityCount++;

		        Object[] metadata = new Object[32];
		        metadata[6] = 1.0f; // health

		        GameProfile profile = new GameProfile(UUID.randomUUID(), "§8§f§4§2§5§3§c§8");
		        Property profileProperty = new Property("textures", MPGGame.SKELETON_TEXTURE, MPGGame.SKELETON_SIGNATURE);
		        //Property profileProperty = new Property("textures", texture, signature);
		        profile.getProperties().put("textures", profileProperty);

		        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo();
		        addPlayer.action = PacketPlayOutPlayerInfo.ADD_PLAYER;
		        addPlayer.username = profile.getName();
		        addPlayer.player = profile;
		        addPlayer.gamemode = 1;

		        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn();
		        spawnPacket.a = entityId;
		        spawnPacket.b = profile;
		        spawnPacket.c = location.getBlockX();
		        spawnPacket.d = location.getBlockY();
		        spawnPacket.e = location.getBlockZ();
		        spawnPacket.metadata = metadata;

		        final PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport();
		        teleportPacket.a = entityId;
		        teleportPacket.b = MathHelper.floor(location.getX() * 32D);
		        teleportPacket.c = MathHelper.floor(location.getY() * 32D);
		        teleportPacket.d = MathHelper.floor(location.getZ() * 32D);
		        teleportPacket.e = (byte) ((int) (0 * 256.0F / 360.0F));
		        teleportPacket.f = (byte) ((int) (0 * 256.0F / 360.0F));
		        teleportPacket.doBlockHeightCorrection = false;

		        // TODO: LINE=======================================

		        int batEntityId = Entity.entityCount++;

		        PacketPlayOutSpawnEntityLiving batSpawnPacket = new PacketPlayOutSpawnEntityLiving();
		        batSpawnPacket.a = batEntityId;
		        batSpawnPacket.b = 65; // bat entity
		        batSpawnPacket.c = MathHelper.floor(location.getX() * 32); // x
		        batSpawnPacket.d = MathHelper.floor(location.getY() * 32); // y
		        batSpawnPacket.e = MathHelper.floor(location.getZ() * 32); // z
		        batSpawnPacket.metadata = new Object[32];
		        batSpawnPacket.metadata[0] = (byte) 0x20; // invisible
		        batSpawnPacket.uuid = UUID.randomUUID();
		        batSpawnPacket.clss = EntityBat.class;

		        // 1.7-1.8 uses attach
		        PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity();
		        attachPacket.a = 0;
		        attachPacket.b = batEntityId;
		        attachPacket.c = entityId;

		        // 1.9+ uses mount
		        net.badlion.gspigot.protocol107.PacketPlayOutMount mountPacket = new net.badlion.gspigot.protocol107.PacketPlayOutMount();
		        mountPacket.entityId = entityId;
		        mountPacket.passengerId = batEntityId;

		        final PacketPlayOutBed bedPacket = new PacketPlayOutBed();

		        bedPacket.a = entityId;
		        bedPacket.b = location.getBlockX();
		        bedPacket.c = 0;
		        bedPacket.d = location.getBlockZ();

		        final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityId, batEntityId);

		        for (Player pl : Bukkit.getOnlinePlayers()) {
			        if (pl.getVersion() >= 47) {
				        Gberry.protocol.sendPacket(pl, addPlayer);
			        }

			        Gberry.protocol.sendPacket(pl, spawnPacket);

			        Gberry.protocol.sendPacket(pl, batSpawnPacket);

			        if (pl.getVersion() < 107) {
				        Gberry.protocol.sendPacket(pl, attachPacket);
			        } else {
				        Gberry.protocol.sendPacket(pl, mountPacket);
			        }
		        }

		        Bukkit.newHologram(location.clone().add(0, 0.3, 0), ChatColor.RED + player.getDisguisedName());

		        BukkitUtil.runTaskLater(new Runnable() {
			        @Override
			        public void run() {
				        for (Player pl : Bukkit.getOnlinePlayers()) {
					        Gberry.protocol.sendPacket(pl, bedPacket);

					        Gberry.protocol.sendPacket(pl, teleportPacket);
				        }
			        }
		        }, 2L);

		        BukkitUtil.runTaskLater(new Runnable() {
			        @Override
			        public void run() {
				        int entityId = Entity.entityCount++;

				        Object[] metadata = new Object[32];
				        metadata[6] = 1.0f; // health

				        GameProfile profile = new GameProfile(UUID.randomUUID(), "§8§f§4§2§5§3§c§8");
				        Property profileProperty = new Property("textures", texture, signature);
				        profile.getProperties().put("textures", profileProperty);

				        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo();
				        addPlayer.action = PacketPlayOutPlayerInfo.ADD_PLAYER;
				        addPlayer.username = profile.getName();
				        addPlayer.player = profile;
				        addPlayer.gamemode = 1;

				        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn();
				        spawnPacket.a = entityId;
				        spawnPacket.b = profile;
				        spawnPacket.c = location.getBlockX();
				        spawnPacket.d = location.getBlockY();
				        spawnPacket.e = location.getBlockZ();
				        spawnPacket.metadata = metadata;

				        final PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport();
				        teleportPacket.a = entityId;
				        teleportPacket.b = MathHelper.floor(location.getX() * 32D);
				        teleportPacket.c = MathHelper.floor(location.getY() * 32D);
				        teleportPacket.d = MathHelper.floor(location.getZ() * 32D);
				        teleportPacket.e = (byte) ((int) (0 * 256.0F / 360.0F));
				        teleportPacket.f = (byte) ((int) (0 * 256.0F / 360.0F));
				        teleportPacket.doBlockHeightCorrection = false;

				        // TODO: LINE=======================================

				        int batEntityId = Entity.entityCount++;

				        PacketPlayOutSpawnEntityLiving batSpawnPacket = new PacketPlayOutSpawnEntityLiving();
				        batSpawnPacket.a = batEntityId;
				        batSpawnPacket.b = 65; // bat entity
				        batSpawnPacket.c = MathHelper.floor(location.getX() * 32); // x
				        batSpawnPacket.d = MathHelper.floor(location.getY() * 32); // y
				        batSpawnPacket.e = MathHelper.floor(location.getZ() * 32); // z
				        batSpawnPacket.metadata = new Object[32];
				        batSpawnPacket.metadata[0] = (byte) 0x20; // invisible
				        batSpawnPacket.uuid = UUID.randomUUID();
				        batSpawnPacket.clss = EntityBat.class;

				        // 1.7-1.8 uses attach
				        PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity();
				        attachPacket.a = 0;
				        attachPacket.b = batEntityId;
				        attachPacket.c = entityId;

				        // 1.9+ uses mount
				        net.badlion.gspigot.protocol107.PacketPlayOutMount mountPacket = new net.badlion.gspigot.protocol107.PacketPlayOutMount();
				        mountPacket.entityId = entityId;
				        mountPacket.passengerId = batEntityId;

				        final PacketPlayOutBed bedPacket = new PacketPlayOutBed();

				        bedPacket.a = entityId;
				        bedPacket.b = location.getBlockX();
				        bedPacket.c = 0;
				        bedPacket.d = location.getBlockZ();

				        for (Player pl : Bukkit.getOnlinePlayers()) {
					        Gberry.protocol.sendPacket(pl, destroyPacket);

					        if (pl.getVersion() >= 47) {
						        Gberry.protocol.sendPacket(pl, addPlayer);
					        }

					        Gberry.protocol.sendPacket(pl, spawnPacket);

					        Gberry.protocol.sendPacket(pl, batSpawnPacket);

					        if (pl.getVersion() < 107) {
						        Gberry.protocol.sendPacket(pl, attachPacket);
					        } else {
						        Gberry.protocol.sendPacket(pl, mountPacket);
					        }
				        }

				        BukkitUtil.runTaskLater(new Runnable() {
					        @Override
					        public void run() {
						        for (Player pl : Bukkit.getOnlinePlayers()) {
							        Gberry.protocol.sendPacket(pl, bedPacket);

							        Gberry.protocol.sendPacket(pl, teleportPacket);
						        }
					        }
				        }, 2L);
			        }
		        }, 200L);
		        break;
	        case "test2":
		        player = Bukkit.getPlayerExact("LaFerrari");

		        playerProfile = TinyProtocolReferences.getPlayerProfile.invoke(player);

		        properties = TinyProtocolReferences.gameProfilePropertyMap.get(playerProfile);
		        propertyCollection = (Collection<Object>) TinyProtocolReferences.propertyMapGet.invoke(properties, "textures");

		        property = propertyCollection.iterator().next();

		        System.out.println(TinyProtocolReferences.propertyValue.get(property));
		        System.out.println(TinyProtocolReferences.propertySignature.get(property));
		        break;                                                             */
	        case "loadworld":
                GWorld loadedWorld = WorldRotator.getInstance().getGWorld(args[1]);
                if (loadedWorld != null) {
                    if (Bukkit.getWorld(loadedWorld.getNiceWorldName()) != null) {
                        ConfigMapCommand.loadedWorlds.put(loadedWorld.getInternalName(), Bukkit.getWorld(loadedWorld.getNiceWorldName()));
	                    sender.sendMessage(ChatColor.GREEN + args[1] + " has been loaded");
                        return true;
                    }

                    WorldCreator wc = new WorldCreator(loadedWorld.getInternalName());
                    wc.generator(new CleanroomChunkGenerator("."));
                    world = MPG.getInstance().getServer().createWorld(wc);
                    world.setAnimalSpawnLimit(0);
                    world.setMonsterSpawnLimit(0);
                    world.setTime(6000);
                    world.setGameRuleValue("doDaylightCycle", "false");
                    world.setGameRuleValue("doMobSpawning", "false");
                    world.setGameRuleValue("doTileDrops", "false");
                    world.getEntities().clear();
                    loadedWorld.setLoaded(true);
                    ConfigMapCommand.loadedWorlds.put(loadedWorld.getInternalName(), world);
                    loadedWorld.setBukkitWorld(world);

                    // Protect world
                    ProtectedRegion region = new ProtectedRegion(world.getName(), new UnsafeLocation(world.getName(), -10000, 0, -10000), new UnsafeLocation(world.getName(), 10000, 256, 10000));
                    region.setAllowBrokenBlocks(false);
                    region.setAllowCreatureSpawn(false);
                    region.setAllowCreeperBlockDamage(false);
                    region.setAllowFire(false);
                    region.setAllowFireIgniteByPlayer(false);
                    region.setAllowIceMelt(false);
                    region.setAllowPistonUsage(false);
                    region.setAllowPlacedBlocks(false);
                    region.setAllowTNTBlockDamage(false);
                    region.setChangeMobDamageToPlayer(false);
                    region.setAllowBlockMovement(false);
                    region.setAllowedBucketPlacements(false);
                    region.setDamageMultiplier(2.0);
                    region.setAllowEnderPearls(false);
                    region.setAllowEndermanMoveBlocks(false);
                    region.setAllowPlantGrowth(false);
                    region.setAllowPlantSpread(false);
                    region.setAllowFireSpread(false);
                    region.setAllowHangingItems(false);
                    region.setAllowItemInteraction(false);
                    region.setAllowChestInteraction(false);
                    region.setAllowBlockChangesByEntities(false);
                    region.setAllowLeafDecay(false);
                    region.setOverrideChestUsage(false);
                    region.setHealPlayers(false);
                    region.setFeedPlayers(false);
                    region.setAllowPotionEffects(false);
	                GGuard.getInstance().addProtectedRegion(region);

                    sender.sendMessage(ChatColor.GREEN + args[1] + " has been loaded");
                } else {
                    sender.sendMessage("That is not a valid world name!");
                }
                break;
            case "author":
            case "authors":
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("author", Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(ChatColor.GREEN + "Set Author");
                break;
            case "tp":
                if (ConfigMapCommand.loadedWorlds.containsKey(args[1])) {
                    player.teleport(new Location(ConfigMapCommand.loadedWorlds.get(args[1]), 0, 100, 0));
                    player.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.DARK_GREEN + args[1]);
                } else {
                    sender.sendMessage("That is not a valid world name!");
                }
                break;
            case "findchests":
                World currentWorld = player.getWorld();
                Chunk[] loadedChunks = currentWorld.getLoadedChunks(); // NOTE: We are assuming the map's chunks have been loaded correctly, so don't let BurntCactus do this set-up
                MapChestFinderTask mapChestFinderTask = new MapChestFinderTask(loadedChunks, WorldRotator.getInstance().getGWorld(currentWorld.getName()));
                mapChestFinderTask.runTask(MPG.getInstance());
                sender.sendMessage(ChatColor.GREEN + "Chests are being looked for...");
                break;
	        case "clearspawns":
		        List<String> spawnLocations = WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().getStringList("spawn_locations");
		        spawnLocations.clear();
		        WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("spawn_locations", spawnLocations);
		        sender.sendMessage(ChatColor.GREEN + "Spawn Locations cleared.");
		        break;
            case "addspawn":
	            spawnLocations = WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().getStringList("spawn_locations");
                spawnLocations.add(Gberry.getLocationString(playerLocation.clone().add(0, 1, 0)));
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("spawn_locations", spawnLocations);
                sender.sendMessage(ChatColor.GREEN + "Spawn Location saved.");
                break;
	        case "cleardeathspawns":
		        List<String> deathMatchLocations = WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().getStringList("deathmatch_locations");
		        deathMatchLocations.clear();
		        WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_locations", deathMatchLocations);
		        sender.sendMessage(ChatColor.GREEN + "DeathMatch Spawn Locations cleared.");
		        break;
            case "adddeathspawn":
                deathMatchLocations = WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().getStringList("deathmatch_locations");
                deathMatchLocations.add(Gberry.getLocationString(playerLocation.clone().add(0, 1, 0)));
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_locations", deathMatchLocations);
                sender.sendMessage(ChatColor.GREEN + "DeathMatch Spawn Location saved.");
                break;
            case "saveconfig":
                try {
                    WorldRotator.getInstance().getGWorld(player.getWorld().getName()).save();
                    sender.sendMessage(ChatColor.GREEN + "Config file has been saved.");
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.DARK_RED + "SOMETHING TERRIBLE HAS HAPPENED: FILE SAVE HAS FAILED");
                }
                break;
	        case "addsupplydrop":
		        List<String> supplyDropLocations = WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().getStringList("supply_drop_locations");
		        supplyDropLocations.add(locString);
		        WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("supply_drop_locations", supplyDropLocations);
		        sender.sendMessage(ChatColor.GREEN + "Supply Drop Location saved.");
		        break;
	        case "spectator_location":
		        WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("spectator_location", locString);
		        sender.sendMessage(ChatColor.GREEN + "Set Spectator Location");
		        break;
            case "spectator_deathmatch_location":
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_spectator_location", locString);
                sender.sendMessage(ChatColor.GREEN + "Set Spectator Deathmatch Location");
                break;
            case "deathmatch_arena":
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_arena", Boolean.parseBoolean(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch arena");
                break;
            case "deathmatch_radius":
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_radius", Integer.parseInt(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch radius");
                break;
            case "deathmatch_center":
                WorldRotator.getInstance().getGWorld(player.getWorld().getName()).getYml().set("deathmatch_center", locString);
                sender.sendMessage(ChatColor.GREEN + "Set Deathmatch center");
                break;
            default:
                sender.sendMessage("??? TRASH");
	            break;
        }

        return true;
    }

}
