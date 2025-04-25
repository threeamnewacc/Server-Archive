package cc.patrone.practice.manager.impl;

import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.util.InventoryUtil;

import java.util.Arrays;

public class KitManager extends Manager {

    public KitManager(ManagerHandler managerHandler) {
        super(managerHandler);
        fetchKits();
    }

    private void fetchKits() {
        Arrays.stream(Kit.values()).forEach(k -> {
            try {
                if (this.managerHandler.getPlugin().getConfig().get("kit." + k.getName() + ".inventory") != null)
                    k.setInventory(InventoryUtil.fromBase64(this.managerHandler.getPlugin().getConfig().getString("kit." + k.getName() + ".inventory")));
                if (this.managerHandler.getPlugin().getConfig().get("kit." + k.getName() + ".armor") != null)
                    k.setArmor(InventoryUtil.itemStackArrayFromBase64(this.managerHandler.getPlugin().getConfig().getString("kit." + k.getName() + ".armor")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void save() {
        Arrays.stream(Kit.values()).forEach(k -> {
            try {
                if (k.getInventory() != null) this.managerHandler.getPlugin().getConfig().set("kit." + k.getName() + ".inventory", InventoryUtil.toBase64(k.getInventory()));
                if (k.getArmor() != null) this.managerHandler.getPlugin().getConfig().set("kit." + k.getName() + ".armor", InventoryUtil.itemStackArrayToBase64(k.getArmor()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        this.managerHandler.getPlugin().saveConfig();
    }
}
