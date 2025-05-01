package net.badlion.skywarslobby.kits;

import net.badlion.skywarslobby.managers.KitCreationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SkyWarsKit {

    private UUID uuid;

    private KitType kitType;
	private Integer kitNumber;

    protected ItemStack previewItem;

	protected ItemStack[] armorContents;
    protected ItemStack[] inventoryContents;

	private int totalWeight = 0;
	private int totalCreatingWeight = 0;

    public SkyWarsKit(UUID uuid, KitType kitType, Integer kitNumber, ItemStack previewItem,
                      ItemStack[] armorContents, ItemStack[] inventoryContents) {
        this.uuid = uuid;

	    this.kitType = kitType;

        this.kitNumber = kitNumber;

        this.previewItem = previewItem;

        this.armorContents = armorContents;
	    this.inventoryContents = inventoryContents;

	    // Calculate the total weight of all their unlockedItems
	    if (armorContents != null) {
		    for (ItemStack item : armorContents) {
			    if (item != null && item.getType() != Material.AIR) {
				    this.totalWeight += KitCreationManager.getItemWeight(item);
			    }
		    }
	    }

	    if (inventoryContents != null) {
		    for (ItemStack item : inventoryContents) {
			    if (item != null && item.getType() != Material.AIR) {
				    this.totalWeight += KitCreationManager.getItemWeight(item);
			    }
		    }
	    }

	    this.totalCreatingWeight = this.totalWeight;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SkyWarsKit) {
			return (((SkyWarsKit) obj).getKitNumber().equals(this.kitNumber)
					&& ((SkyWarsKit) obj).getKitType() == this.kitType);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.uuid.hashCode();
		hash = hash * 31 + this.kitNumber.hashCode();
		hash = hash * 31 + this.kitType.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return this.kitType + "-" + this.uuid + "-" + this.kitNumber;
	}

    public UUID getUUID() {
        return uuid;
    }

    public Integer getKitNumber() {
        return kitNumber;
    }

    public KitType getKitType() {
        return kitType;
    }

    public ItemStack getPreviewItem() {
        return previewItem;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

	public void setArmorContents(ItemStack[] armorContents) {
		this.armorContents = armorContents;
	}

	public ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

	public void setInventoryContents(ItemStack[] inventoryContents) {
		this.inventoryContents = inventoryContents;
	}

	public void addWeight(ItemStack item) {
		this.totalWeight += KitCreationManager.getItemWeight(item);
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	public int getTotalCreatingWeight() {
		return totalCreatingWeight;
	}

	public void resetTotalCreatingWeight() {
		this.totalCreatingWeight = 0;
	}

}