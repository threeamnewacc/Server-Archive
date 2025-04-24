package me.devkevin.landcore.handler;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.location.CustomLocation;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.refinedev.spigot.api.handlers.impl.PacketHandler;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 17:51
 * PacketListener / me.devkevin.landcore.handler / LandCore
 */
public abstract class PacketListener implements PacketHandler {
    /*@Override
    public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        Player player = playerConnection.getPlayer();
        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if ("PacketPlayInFlying".equals(packet.getClass().getSimpleName())) {
            handleFlyPacket((PacketPlayInFlying) packet, coreProfile);
        }
    }

    @Override
    public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {
        Player player = playerConnection.getPlayer();
        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        switch (packet.getClass().getSimpleName()) {
            case "PacketPlayOutEntityTeleport": {
                this.handleTeleportPacket((PacketPlayOutEntityTeleport) packet, coreProfile, player);
                break;
            }
            case "PacketPlayOutEntityLook":
            case "PacketPlayOutRelEntityMove":
            case "PacketPlayOutRelEntityMoveLook":
            case "PacketPlayOutEntity": {
                this.handleEntityPacket((PacketPlayOutEntity) packet, coreProfile, player);
                break;
            }
        }
    }

    private void handleTeleportPacket(PacketPlayOutEntityTeleport packet, CoreProfile coreProfile, final Player player) {
        final Entity targetEntity = ((CraftPlayer) player).getHandle().getWorld().a(packet.getA());
        if (targetEntity instanceof EntityPlayer) {
            Player target = (Player) targetEntity.getBukkitEntity();
            double x = packet.getB() / 32.0;
            double y = packet.getC() / 32.0;
            double z = packet.getD() / 32.0;
            float yaw = packet.getE() * 360.0f / 256.0f;
            float pitch = packet.getF() * 360.0f / 256.0f;
            if (coreProfile.getMisplace() != 0.0) {
                CustomLocation lastLocation = coreProfile.getLastMovePacket();
                float entityYaw = this.getAngle(x, z, lastLocation);
                double addX = Math.cos(Math.toRadians(entityYaw + 90.0f)) * coreProfile.getMisplace();
                double addZ = Math.sin(Math.toRadians(entityYaw + 90.0f)) * coreProfile.getMisplace();
                x -= addX;
                z -= addZ;
                packet.setB(MathHelper.floor(x * 32.0));
                packet.setD(MathHelper.floor(z * 32.0));
            }
            coreProfile.addPlayerPacket(target.getUniqueId(), new CustomLocation(x, y, z, yaw, pitch));
        }
    }

    private void handleEntityPacket(final PacketPlayOutEntity packet, final CoreProfile coreProfile, final Player player) {
        final Entity targetEntity = ((CraftPlayer) player).getHandle().getWorld().a(packet.getA());
        if (targetEntity instanceof EntityPlayer) {
            final Player target = (Player) targetEntity.getBukkitEntity();
            final CustomLocation customLocation = coreProfile.getLastPlayerPacket(target.getUniqueId(), 1);
            if (customLocation != null) {
                final double x = packet.getB() / 32.0;
                final double y = packet.getC() / 32.0;
                final double z = packet.getD() / 32.0;
                float yaw = packet.getE() * 360.0f / 256.0f;
                float pitch = packet.getF() * 360.0f / 256.0f;
                if (!packet.isH()) {
                    yaw = customLocation.getYaw();
                    pitch = customLocation.getPitch();
                }
                coreProfile.addPlayerPacket(target.getUniqueId(), new CustomLocation(customLocation.getX() + x, customLocation.getY() + y, customLocation.getZ() + z, yaw, pitch));
            }
        }
    }

    private void handleFlyPacket(final PacketPlayInFlying packet, final CoreProfile coreProfile) {
        final CustomLocation customLocation = new CustomLocation(packet.a(), packet.b(), packet.c(), packet.d(), packet.e());
        final CustomLocation lastLocation = coreProfile.getLastMovePacket();
        if (lastLocation != null) {
            if (!packet.g()) {
                customLocation.setX(lastLocation.getX());
                customLocation.setY(lastLocation.getY());
                customLocation.setZ(lastLocation.getZ());
            }
            if (!packet.h()) {
                customLocation.setYaw(lastLocation.getYaw());
                customLocation.setPitch(lastLocation.getPitch());
            }
        }
        coreProfile.setLastMovePacket(customLocation);
    }

    private float getAngle(final double posX, final double posZ, final CustomLocation location) {
        final double x = posX - location.getX();
        final double z = posZ - location.getZ();
        float newYaw = (float) Math.toDegrees(-Math.atan(x / z));
        if (z < 0.0 && x < 0.0) {
            newYaw = (float) (90.0 + Math.toDegrees(Math.atan(z / x)));
        } else if (z < 0.0 && x > 0.0) {
            newYaw = (float) (-90.0 + Math.toDegrees(Math.atan(z / x)));
        }
        return newYaw;
    }*/
}
