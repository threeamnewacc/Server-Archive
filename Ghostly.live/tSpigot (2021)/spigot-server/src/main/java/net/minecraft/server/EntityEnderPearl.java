package net.minecraft.server;

import me.enzol.spigot.TrainingSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EntityEnderPearl extends EntityProjectile {

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.c = entityliving;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
    }


    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            BlockPosition position = movingobjectposition.a();
            Block block = this.world.getType(position).getBlock();
            if (block == Blocks.TRIPWIRE && ThroughString) {
                return;
            }
            if (block == Blocks.WEB && ThroughWeb) {
                return;
            }
            if (block == Blocks.VINE && ThroughVine) {
                return;
            }
            if ((block == Blocks.ACACIA_FENCE_GATE && ThroughGate) || (
                    block == Blocks.BIRCH_FENCE_GATE && ThroughGate) || (
                    block == Blocks.DARK_OAK_FENCE_GATE && ThroughGate) || (
                    block == Blocks.FENCE_GATE && ThroughGate) || (
                    block == Blocks.JUNGLE_FENCE_GATE && ThroughGate) || (
                    block == Blocks.SPRUCE_FENCE_GATE && ThroughGate)) {
                BlockIterator bi = null;
                try {
                    Vector l = new Vector(this.locX, this.locY, this.locZ);
                    Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                    Vector dir = (new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ())).normalize();
                    bi = new BlockIterator(this.world.getWorld(), l, dir, 0.0D, 1);
                }
                catch (IllegalStateException ignored) {}
                if (bi != null) {
                    boolean open = true;
                    while (bi.hasNext()) {
                        org.bukkit.block.Block b = bi.next();
                        if (b.getState().getData() instanceof Gate && !((Gate)b.getState().getData()).isOpen()) {
                            open = false;
                            break;
                        }
                    }
                    if (open) {
                        return;
                    }
                }
            }
        }
        EntityLiving entityliving = getShooter();
        if (movingobjectposition.entity != null) {
            if (movingobjectposition.entity == this.c) {
                return;
            }
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, entityliving), 0.0F);
        }
        if (this.inUnloadedChunk && this.world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        for (int i = 0; i < 32; i++) {
            this.world.addParticle(EnumParticle.PORTAL, this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian(), new int[0]);
        }
        if (!this.world.isClientSide) {
            if (entityliving instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)entityliving;
                if (entityplayer.playerConnection.a().g() && entityplayer.world == this.world && !entityplayer.isSleeping()) {
                    CraftPlayer player = entityplayer.getBukkitEntity();
                    Location location = getBukkitEntity().getLocation();
                    location.setX(location.getBlockX() + 0.5D);
                    location.setY(location.getBlockY() + 0.5D);
                    location.setZ(location.getBlockZ() + 0.5D);
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());
                    PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                    Bukkit.getPluginManager().callEvent(teleEvent);
                    if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                        if (this.random.nextFloat() < 0.05F) this.world.getGameRules().getBoolean("doMobSpawning");
                        entityplayer.playerConnection.teleport(teleEvent.getTo());
                        entityliving.fallDistance = 0.0F;
                        CraftEventFactory.entityDamage = this;
                        entityliving.damageEntity(DamageSource.FALL, 5.0F);
                        CraftEventFactory.entityDamage = null;
                    }

                }
            } else if (entityliving != null) {
                entityliving.enderTeleportTo(this.locX, this.locY, this.locZ);
                entityliving.fallDistance = 0.0F;
            }
            die();
        }
    }


    public void t_() {
        EntityLiving entityliving = getShooter();
        if (entityliving != null && entityliving instanceof EntityHuman && !entityliving.isAlive()) {
            die();
        } else {

            super.t_();
        }
    }


    public static boolean ThroughString = TrainingSpigot.INSTANCE.getConfig().isPearlThroughString();
    public static boolean ThroughGate = TrainingSpigot.INSTANCE.getConfig().isPearlThroughGate();
    public static boolean ThroughWeb = TrainingSpigot.INSTANCE.getConfig().isPearlThroughWeb();
    public static boolean ThroughVine = TrainingSpigot.INSTANCE.getConfig().isPearlThroughVine();
    private EntityLiving c;
}