package com.probablycoding.bukkit.playersimulator;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.PlayerList;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerSimulator
		extends JavaPlugin
		implements Listener
{
	private TPSCheck tpsCheck = new TPSCheck();
	private boolean toggle = false;

	public void onEnable()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, this.tpsCheck, 20L, 20L);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable()
	{
		getServer().getScheduler().cancelTasks(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if (command.getName().equalsIgnoreCase("botlocations"))
		{
			PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
			for (EntityPlayer entityplayer : (CopyOnWriteArrayList<EntityPlayer>)playerList.players) {
				if (entityplayer.getName().startsWith(ChatColor.BLUE + "Bot"))
				{
					System.out.println(entityplayer.locX + "," + entityplayer.locY + "," + entityplayer.locZ);
				}
			}
		}
		if (command.getName().equalsIgnoreCase("spawnbots"))
		{
			int range = 2000;
			int num = 1;
			if (args.length > 0) {
				num = Math.max(Integer.parseInt(args[0]), 1);
			}
			if (args.length > 1) {
				range = Math.max(Integer.parseInt(args[1]), 1);
			}
			for (int i = 0; i < num; i++)
			{
				Random random = new Random();
				String name = ChatColor.BLUE + "Farmer";//"Bot" + random.nextInt(1000) + i;
				WorldServer world = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle();
				PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
				UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
				GameProfile gameProfile = new GameProfile(uuid, name);
				gameProfile.getProperties().put("textures", new Property("textures", EntityBot.BLANK_TEXTURE_VALUE, EntityBot.BLANK_TEXTURE_SIG));


				EntityPlayer entityplayer = new EntityBot(playerList.getServer(), world, gameProfile, new PlayerInteractManager(world));
				new DummyPlayerConnection(playerList.getServer(), new DummyNetworkManager(), entityplayer);

				entityplayer.spawnIn(world);
				entityplayer.playerInteractManager.a((WorldServer)entityplayer.world);
				entityplayer.playerInteractManager.b(world.getWorldData().getGameType());

				if (sender instanceof Player) {
					entityplayer.setPosition(((Player)sender).getLocation().getX(), ((Player)sender).getLocation().getY(), ((Player)sender).getLocation().getZ());
				} else {
					entityplayer.setPosition(random.nextInt(range * 2) - range, 100.0D, random.nextInt(range * 2) - range);

				}

				try {
					PlayerLoginEvent event = new PlayerLoginEvent(entityplayer.getBukkitEntity(), "mc.badlion.net", InetAddress.getByName("127.0.0.1"), InetAddress.getByName("127.0.0.1"));
					Bukkit.getPluginManager().callEvent(event);
					PlayerJoinEvent event1 = new PlayerJoinEvent(entityplayer.getBukkitEntity(), "YOLO");
					Bukkit.getPluginManager().callEvent(event1);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ArrayList<Player> foo = new ArrayList<>();

				playerList.players.add(entityplayer);
				world.addEntity(entityplayer);
				playerList.a(entityplayer, null);

				sender.sendMessage("Added player " + entityplayer.getName() + ChatColor.RESET + " at " + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ".");
			}
			return true;
		}
		if (command.getName().equalsIgnoreCase("killbots"))
		{
			PlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
			for (EntityPlayer entityplayer : (CopyOnWriteArrayList<EntityPlayer>)playerList.players) {
				if (entityplayer.getName().startsWith(ChatColor.BLUE + "Bot"))
				{
					entityplayer.playerConnection.disconnect("");
					sender.sendMessage("Disconnected " + entityplayer.getName());
				}
			}
		}
		if (command.getName().equalsIgnoreCase("debug"))
		{
			this.toggle = (!this.toggle);
			float tps = 0.0F;
			for (Long l : this.tpsCheck.history) {
				if (l != null) {
					tps += (float)(20L / (l.longValue() / 1000L));
				}
			}
			tps /= this.tpsCheck.history.size();

			sender.sendMessage("TPS: " + tps + " Loaded chunks: " + ((World)Bukkit.getWorlds().get(0)).getLoadedChunks().length + " Entities: " + ((World)Bukkit.getWorlds().get(0)).getEntities().size());
		}
		return false;
	}
}