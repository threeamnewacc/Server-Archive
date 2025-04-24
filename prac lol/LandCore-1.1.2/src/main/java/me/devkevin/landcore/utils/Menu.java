package me.devkevin.landcore.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/01/2023 @ 14:15
 * Menu / me.devkevin.landcore.utils / LandCore
 */
public abstract class Menu {

    @Getter
    private static Map<UUID, Menu> menus = new HashMap<>();

    private UUID uuid;
    private int rows;

    @Getter
    @Setter
    private boolean updateOnClick = true;

    @Getter
    private Inventory inventory;

    public Menu(Player player, String title, int rows, boolean update) {
        this.uuid = player.getUniqueId();
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows * 9, title);

        if(update) {
            this.updateInventory(player);
        }

        TaskUtil.run(() -> menus.put(player.getUniqueId(), this));
    }

    public abstract void updateInventory(Player player);

    public abstract void onClickItem(Player player, ItemStack stack, boolean isRightClicked);

    public void onClose() {}

    public void fill(ItemStack itemStack) {
        IntStream.range(0, this.inventory.getSize()).filter(i -> this.inventory.getItem(i) == null).forEach(i -> this.inventory.setItem(i, itemStack));
    }

    public void surround(ItemStack itemStack) {
        IntStream.range(0, this.inventory.getSize()).filter(i -> this.inventory.getItem(i) == null).forEach(i -> {
            if (i < 9 || i > this.inventory.getSize() - 10 || i % 9 == 0 || (i + 1) % 9 == 0) {
                this.inventory.setItem(i, itemStack);
            }
        });
    }

    public void playSound(boolean confirm) {
        this.getPlayer().playSound(this.getPlayer().getLocation(), confirm ? Sound.NOTE_PIANO : Sound.GLASS, 20F, confirm ? 15F : 5F);
    }

    public void openInventory() {
        this.getPlayer().openInventory(this.inventory);
    }

    public void set(int slot, ItemStack material) {
        this.inventory.setItem(slot, material);
    }

    public void add(ItemStack material) {
        this.inventory.addItem(material);
    }

    public void close() {
        this.getPlayer().closeInventory();
    }

    public void destroy() {
        menus.remove(this.uuid);
    }

    public static Menu getByUuid(UUID uuid) {
        return menus.get(uuid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public int getSize() {
        return rows * 9;
    }
}
