package cc.patrone.practice.kit;

import cc.patrone.practice.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum Kit {

    NODEBUFF("NoDebuff", new ItemBuilder(Material.POTION, 1,  (byte) 0).setDurability((short) 16421).toItemStack(), true, false, 0);

    private final String name;
    private final ItemStack display;
    private final boolean editable;
    private final boolean sumo;
    private final int id;
    private Inventory inventory;
    private ItemStack[] armor;
    private Inventory editInventory;
    private List<UUID> unrankedQueue = new ArrayList<>();
    private List<UUID> rankedQueue = new ArrayList<>();
    private List<UUID> unrankedMatch = new ArrayList<>();
    private List<UUID> rankedMatch = new ArrayList<>();

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setEditInventory(Inventory editInventory) {
        this.editInventory = editInventory;
    }

    public static Kit getKit(int id) {
        return Arrays.stream(values()).filter(k -> k.getId() == id).findFirst().orElse(null);
    }

    public static Kit getKit(String name) {
        return Arrays.stream(values()).filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static int getFighting() {
        int fighting = 0;
        for (Kit kit : Kit.values()) {
            fighting += kit.getUnrankedMatch().size() + kit.getRankedMatch().size();
        }
        return fighting;
    }
}
