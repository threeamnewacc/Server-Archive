package cc.patrone.practice.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class CustomKit {

    private Inventory inventory;
    private ItemStack[] armor;

}
