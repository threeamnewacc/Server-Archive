package us.zonix.hcfactions.blockoperation;

import us.zonix.hcfactions.blockoperation.state.type.BlockOperationBrewingStandState;
import us.zonix.hcfactions.blockoperation.state.type.BlockOperationFurnaceState;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Iterator;

public class BlockOperationModifierListeners implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Furnace || block.getState() instanceof BrewingStand) {
                BlockOperationModifier modifier = BlockOperationModifier.getByBlock(event.getClickedBlock());
                if (modifier == null) {
                    if (block.getState() instanceof Furnace) {
                        new BlockOperationModifier(new BlockOperationFurnaceState((Furnace) block.getState()));
                    } else {
                        new BlockOperationModifier(new BlockOperationBrewingStandState((BrewingStand) block.getState()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        Iterator<BlockOperationModifier> iterator = BlockOperationModifier.getModifiers().iterator();
        while (iterator.hasNext()) {
            BlockOperationModifier modifier = iterator.next();
            if (modifier.getState().getLocation().getChunk().equals(event.getChunk())) {
                iterator.remove();
            }
        }
    }

}
