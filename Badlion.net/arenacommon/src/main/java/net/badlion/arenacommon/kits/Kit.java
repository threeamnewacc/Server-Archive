package net.badlion.arenacommon.kits;

import net.badlion.arenacommon.helper.ItemStackHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Kit {

    private String name;
    private int kitId;
    protected ItemStack[] inventoryItems = new ItemStack[36];
    protected ItemStack[] armorItems = new ItemStack[9];

    public Kit(String name) {
        this.name = name;
    }

    public int getId(){
        return this.kitId;
    }
    public void setId(int kitId){
        this.kitId = kitId;
    }

    public ItemStack[] getInventoryItems(){
        return this.inventoryItems;
    }
    public ItemStack[] getArmorItems(){
        return this.armorItems;
    }

    public void loadKit(Player player) {
        player.getInventory().setContents(this.inventoryItems);
        player.getInventory().setArmorContents(this.armorItems);
        player.updateInventory();
    }

    public void handleKill(Player player) {
        // Do any special effects such as extra coins
    }
    public static Kit fromPlayer(Player player, String name, UUID styleID) {
        Kit kit = new Kit(name);
        kit.inventoryItems = ItemStackHelper.clone(player.getInventory().getContents());
        kit.armorItems = ItemStackHelper.clone(player.getInventory().getArmorContents());
        return kit;
    }

    public static Kit fromPlayer(Player player, int kitID, String name, UUID styleID) {
        Kit kit = new Kit(name);
        kit.kitId = kitID;
        kit.inventoryItems = ItemStackHelper.clone(player.getInventory().getContents());
        kit.armorItems = ItemStackHelper.clone(player.getInventory().getArmorContents());
        return kit;
    }

    public void giveToPlayer(Player player) {
        player.getInventory().setContents(ItemStackHelper.clone(inventoryItems));
        player.getInventory().setArmorContents(ItemStackHelper.clone(armorItems));
    }

}
