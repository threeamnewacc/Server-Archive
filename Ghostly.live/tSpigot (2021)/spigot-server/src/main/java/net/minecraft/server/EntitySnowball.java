package net.minecraft.server;

public class EntitySnowball extends EntityProjectile {

    public EntitySnowball(World world) {
        super(world);
    }

    public EntitySnowball(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    protected void a(MovingObjectPosition movingobjectposition) {
    	// duplicate from EntityEgg or EntityEnderPearl
    	if (movingobjectposition.a() != null) {
    		
            Block block = this.world.getType(movingobjectposition.a()).getBlock();
            if (block == Blocks.FENCE_GATE) {
                BlockFenceGate fenceGate = (BlockFenceGate)block;
                
                if (fenceGate.b(this.world, movingobjectposition.a())) {
                    return;
                }
            }
        }
    	
        if (movingobjectposition.entity != null) {
            byte b0 = 0;

            if (movingobjectposition.entity instanceof EntityBlaze) {
                b0 = 3;
            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), (float) b0);
        }

        for (int i = 0; i < 8; ++i) {
            this.world.addParticle(EnumParticle.SNOWBALL, this.locX, this.locY, this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        if (!this.world.isClientSide) {
            this.die();
        }

    }
}
