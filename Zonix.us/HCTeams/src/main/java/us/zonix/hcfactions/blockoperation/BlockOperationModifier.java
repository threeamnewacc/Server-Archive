package us.zonix.hcfactions.blockoperation;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.blockoperation.state.BlockOperationState;
import us.zonix.hcfactions.blockoperation.state.type.BlockOperationBrewingStandState;
import us.zonix.hcfactions.blockoperation.state.type.BlockOperationFurnaceState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.FactionsPlugin;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BlockOperationModifier {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static Set<BlockOperationModifier> modifiers = new HashSet<>();

    @Getter @Setter private final BlockOperationState state;

    public BlockOperationModifier(BlockOperationState state) {
        this.state = state;

        modifiers.add(this);
    }

    public static BlockOperationModifier getByBlock(Block block) {
        for (BlockOperationModifier modifier : modifiers) {
            if (modifier.getState() instanceof BlockOperationFurnaceState) {
                BlockOperationFurnaceState state = (BlockOperationFurnaceState) modifier.getState();
                if (state.getFurnace().getBlock().equals(block)) {
                    return modifier;
                }
            } else {
                BlockOperationBrewingStandState state = (BlockOperationBrewingStandState) modifier.getState();
                if (state.getBrewingStand().getBlock().equals(block)) {
                    return modifier;
                }
            }
        }
        return null;
    }

    public static Set<BlockOperationModifier> getModifiers() {
        return modifiers;
    }

    public static void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<BlockOperationModifier> iterator = modifiers.iterator();
                while (iterator.hasNext()) {
                    BlockOperationModifier modifier = iterator.next();

                    if (modifier.getState() instanceof BlockOperationFurnaceState) {
                        BlockOperationFurnaceState state = (BlockOperationFurnaceState) modifier.getState();
                        Furnace furnace = state.getFurnace();

                        if (furnace.getInventory().getItem(0) != null) {
                            if (furnace.getCookTime() > 0 || furnace.getBurnTime() > 0) {
                                furnace.setCookTime((short) (furnace.getCookTime() + state.getIncrease()));
                                furnace.setBurnTime((short) (furnace.getBurnTime() + state.getIncrease()));
                            }
                        } else {
                            if (furnace.getInventory().getViewers().isEmpty()) {
                                iterator.remove();
                            }

                            furnace.setCookTime((short) 0);
                            furnace.setBurnTime((short) 0);
                        }
                        continue;
                    }

                    if (modifier.getState() instanceof BlockOperationBrewingStandState) {
                        BlockOperationBrewingStandState state = (BlockOperationBrewingStandState) modifier.getState();
                        BrewingStand brewingStand = state.getBrewingStand();

                        if (brewingStand.getInventory().getViewers().isEmpty() && brewingStand.getInventory().getItem(3) == null) {
                            iterator.remove();
                        }

                        if (brewingStand.getBrewingTime() > 1) {
                            brewingStand.setBrewingTime(Math.max(1, brewingStand.getBrewingTime() - state.getIncrease()));
                        }

                    }

                }
            }
        }.runTaskTimerAsynchronously(main, 2L, 2L);
    }

}
