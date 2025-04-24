package me.devkevin.practice.util;

/**
 * Copyright 17/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */

import club.inverted.chatcolor.CC;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.devkevin.practice.Practice;
import net.minecraft.server.v1_8_R3.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NPCUtil {

    public static Practice plugin = Practice.getInstance();

    private static final Map<UUID, List<Entity>> entityMap = new ConcurrentHashMap<>();
    protected static final Map<UUID, String> signatureMap = new ConcurrentHashMap<>();
    protected static final Map<UUID, String> valueMap = new ConcurrentHashMap<>();

    public static void create(Player player, String value, String signature, Location location) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        UUID uuid = UUID.randomUUID();
        String name = UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(uuid, CC.translate("&6" + player.getName())), new PlayerInteractManager(world));

        /*for (Leaderboard leaderboard : plugin.getLeaderboardManager().getLeaderboards()) {

            Leaderboard player1Name = plugin.getLeaderboardManager().getSortedKitLeaderboards(leaderboard.getKit()).get(0);
            Leaderboard player2Name = plugin.getLeaderboardManager().getSortedKitLeaderboards(leaderboard.getKit()).get(1);
            Leaderboard player3Name = plugin.getLeaderboardManager().getSortedKitLeaderboards(leaderboard.getKit()).get(2);

            npc.setCustomName(CoreAPI.INSTANCE.getPlayerData(player1Name.getUuid()).getHighestRank().getColor() + player1Name.getPlayerName() + CC.translate("&7[&a" + player1Name.getPlayerElo() + "&7]"));
            npc.setCustomName(CoreAPI.INSTANCE.getPlayerData(player2Name.getUuid()).getHighestRank().getColor() + player2Name.getPlayerName() + CC.translate("&7[&a" + player2Name.getPlayerElo() + "&7]"));
            npc.setCustomName(CoreAPI.INSTANCE.getPlayerData(player3Name.getUuid()).getHighestRank().getColor() + player3Name.getPlayerName() + CC.translate("&7[&a" + player3Name.getPlayerElo() + "&7]"));

            npc.setCustomNameVisible(true);
        }*/

        npc.setLocation(location.getX(), location.getY(), location.getZ(), (float) location.getYaw(), (float) location.getPitch());
        npc.getProfile().getProperties().removeAll("textures");

        DataWatcher watcher = npc.getDataWatcher();
        watcher.watch(10, (byte) 127);

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(npc.getId(), watcher, true);

        npc.getProfile().getProperties().put("textures", new Property("textures", value, signature));

        PacketPlayOutEntityHeadRotation rotation =
                new PacketPlayOutEntityHeadRotation(npc, (byte) ((npc.yaw * 256.0F) / 360.0F));

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(rotation);
        connection.sendPacket(metadata);

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard)player.getScoreboard()).getHandle(), name);
        ChatMessage chatMessage1 = new ChatMessage("");
        ChatMessage chatMessage2 = new ChatMessage("");
        team.setPrefix(CC.translate(chatMessage1.getText()));
        team.setSuffix(CC.translate(chatMessage2.getText()));
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));

        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(npc.getName());
        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));

        EntityBat bat = new EntityBat(((CraftWorld) player.getPlayer().getWorld()).getHandle());

        bat.setCustomName(CC.GOLD + player.getName());
        bat.setCustomNameVisible(true);
        bat.setInvisible(true);

        bat.setPosition(location.getX(), location.getY(), location.getZ());
        bat.persistent = true;

        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(bat));

        bat.mount(npc);

        connection.sendPacket(new PacketPlayOutAttachEntity(0, bat, npc));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Practice.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

                    Location location = npc.getBukkitEntity().getLocation();
                    location = location.setDirection(player.getLocation().subtract(location).toVector());

                    float yaw = location.getYaw();
                    float pitch = location.getPitch();

                    connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((yaw % 360.) * 256 / 360)));
                }
            }
        }, 1L, 1L);

        TaskUtil.runLater(() -> {
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
        }, 40L);

        List<Entity> entities = entityMap.getOrDefault(player.getUniqueId(), new ArrayList<>());

        entities.add(bat);
        entities.add(npc);

        entityMap.put(player.getUniqueId(), entities);
    }

    public static void createOffline(String playerName, String textureValue, String textureSignature, Location location) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), playerName);
        profile.getProperties().put("textures", new Property("textures", textureValue, textureSignature));

        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer npc = new EntityPlayer(MinecraftServer.getServer(), world, profile, new PlayerInteractManager(world));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(npc);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(addPlayerPacket);
            connection.sendPacket(spawnPacket);
        }
    }

    public static void delete(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        List<Entity> map = entityMap.getOrDefault(player.getUniqueId(), new ArrayList<>());

        for (Entity entity : map) {
            PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entity.getId());

            connection.sendPacket(packetPlayOutEntityDestroy);
        }

        entityMap.put(player.getUniqueId(), new ArrayList<>());
    }

    public static String[] getSkinFromMojang(UUID name) {
        if (signatureMap.containsKey(name) && valueMap.containsKey(name)) {

            return new String[]{valueMap.get(name), signatureMap.get(name)};
        }

        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + name.toString().replace("-", "") + "?unsigned=false");

            CloseableHttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                JsonObject object = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);

                String value = object.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
                String signature = object.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("signature").getAsString();

                return new String[]{value, signature};
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}

