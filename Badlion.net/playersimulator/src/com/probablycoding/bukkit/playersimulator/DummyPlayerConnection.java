package com.probablycoding.bukkit.playersimulator;

import java.util.List;
import net.minecraft.server.v1_7_R4.DedicatedPlayerList;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumProtocol;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInAbilities;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInChat;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInCloseWindow;
import net.minecraft.server.v1_7_R4.PacketPlayInCustomPayload;
import net.minecraft.server.v1_7_R4.PacketPlayInEnchantItem;
import net.minecraft.server.v1_7_R4.PacketPlayInEntityAction;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInKeepAlive;
import net.minecraft.server.v1_7_R4.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInSettings;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R4.PacketPlayInTabComplete;
import net.minecraft.server.v1_7_R4.PacketPlayInTransaction;
import net.minecraft.server.v1_7_R4.PacketPlayInUpdateSign;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R4.PacketPlayInWindowClick;
import net.minecraft.server.v1_7_R4.PlayerChunkMap;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

public class DummyPlayerConnection
		extends PlayerConnection
{
	private boolean disconnected = false;

	public DummyPlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer)
	{
		super(minecraftserver, networkmanager, entityplayer);
	}

	public CraftPlayer getPlayer()
	{
		return this.player == null ? null : this.player.getBukkitEntity();
	}

	public void a() {}

	public NetworkManager b()
	{
		return this.networkManager;
	}

	public void disconnect(String s)
	{
		WorldServer worldserver = this.player.r();

		worldserver.kill(this.player);
		worldserver.getPlayerChunkMap().removePlayer(this.player);
		((CraftServer)Bukkit.getServer()).getHandle().players.remove(this.player);
		this.disconnected = true;
	}

	public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {}

	public void a(PacketPlayInFlying packetplayinflying) {}

	public void a(double d0, double d1, double d2, float f, float f1) {}

	public void teleport(Location dest) {}

	public void a(PacketPlayInBlockDig packetplayinblockdig) {}

	public void a(PacketPlayInBlockPlace packetplayinblockplace) {}

	public void a(IChatBaseComponent ichatbasecomponent) {}

	public void sendPacket(Packet packet) {}

	public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {}

	public void a(PacketPlayInChat packetplayinchat) {}

	public void chat(String s, boolean async) {}

	public void a(PacketPlayInArmAnimation packetplayinarmanimation) {}

	public void a(PacketPlayInEntityAction packetplayinentityaction) {}

	public void a(PacketPlayInUseEntity packetplayinuseentity) {}

	public void a(PacketPlayInClientCommand packetplayinclientcommand) {}

	public void a(PacketPlayInCloseWindow packetplayinclosewindow) {}

	public void a(PacketPlayInWindowClick packetplayinwindowclick) {}

	public void a(PacketPlayInEnchantItem packetplayinenchantitem) {}

	public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {}

	public void a(PacketPlayInTransaction packetplayintransaction) {}

	public void a(PacketPlayInUpdateSign packetplayinupdatesign) {}

	public void a(PacketPlayInKeepAlive packetplayinkeepalive) {}

	public void a(PacketPlayInAbilities packetplayinabilities) {}

	public void a(PacketPlayInTabComplete packetplayintabcomplete) {}

	public void a(PacketPlayInSettings packetplayinsettings) {}

	public void a(PacketPlayInCustomPayload packetplayincustompayload) {}

	public void a(EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {}
}