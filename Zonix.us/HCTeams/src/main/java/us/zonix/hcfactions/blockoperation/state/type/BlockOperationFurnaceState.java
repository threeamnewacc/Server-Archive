package us.zonix.hcfactions.blockoperation.state.type;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.blockoperation.state.BlockOperationState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Furnace;
import us.zonix.hcfactions.FactionsPlugin;

public class BlockOperationFurnaceState implements BlockOperationState {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static final String NAME = "BlockOperationFurnaceState";

    @Getter @Setter private final Furnace furnace;

    public BlockOperationFurnaceState(Furnace furnace) {
        this.furnace = furnace;
    }

    @Override
    public Location getLocation() {
        return furnace.getLocation();
    }

    @Override
    public int getIncrease() {
        return main.getMainConfig().getInt("BLOCK_MODIFIER.FURNACE_COOK_INCREASE");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
