package net.badlion.uhc.util;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GoldenHeadDroppedEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GoldenHeadUtils {

    public static void makeHeadStakeForPlayer(LivingEntity entity) {
	    Location loc = entity.getEyeLocation();

	    // Mini UHC check
	    if (BadlionUHC.getInstance().isMiniUHC()) {
		    loc.getWorld().dropItemNaturally(loc, ItemStackUtil.createGoldenHead());
		    return;
	    }

	    Block block1 = loc.getBlock();
	    Block block2 = GoldenHeadUtils.getClosestGround(block1.getRelative(BlockFace.DOWN, 2));
	    if (block2 != null) {
		    /*Block block3 = block2.getRelative(BlockFace.UP, 2);
		    if ((block3 == null) || (!block3.isEmpty())) {
			    return;
		    }*/

			// Call event if we want to
			GoldenHeadDroppedEvent event = new GoldenHeadDroppedEvent(entity, block1.getLocation());
			BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				return;
			}

		    entity.teleport(block1.getLocation());
		    GoldenHeadUtils.setBlockAsHead(entity, block1);

		    Block block3 = block2.getRelative(BlockFace.UP);
		    if (block3 != null && block3.isEmpty()) {
			    block3.setType(Material.NETHER_FENCE);
		    }
	    }
    }

    private static void setBlockAsHead(LivingEntity entity, Block block) {
        block.setType(Material.SKULL);
        block.setData((byte) 1);
        Skull skull = (Skull) block.getState();
        skull.setSkullType(SkullType.PLAYER);
	    if (entity instanceof Player) {
		    skull.setOwner(((Player) entity).getDisguisedName());
	    } else {
		    skull.setOwner(entity.getCustomName());
	    }
        skull.update();
    }

	public static void dropBlockNaturally(String name, Block block) {
		block.setType(Material.SKULL);
		block.setData((byte) 1);
		Skull skull = (Skull) block.getState();
		skull.setSkullType(SkullType.PLAYER);
		skull.setOwner(name);
		skull.update();
		block.breakNaturally();
	}

    private static Block getClosestGround(Block block) {
        if (block == null) {
            return null;
        }
        if (!block.isEmpty()) {
            return block;
        }
        return GoldenHeadUtils.getClosestGround(block.getRelative(BlockFace.DOWN));
    }

}
