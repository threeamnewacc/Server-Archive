package net.badlion.mpg.kits;

import net.badlion.mpg.bukkitevents.KitLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MPGKit {

    private UUID owner;

	protected String kitName;
    protected Integer kitNumber;

	private String type;
    private String gameMode;

    protected ItemStack previewItem;
    protected ItemStack[] inventoryContents;
    protected ItemStack[] armorContents;

	private List<PotionEffect> potionEffects = new ArrayList<>();

    public MPGKit(UUID owner, String kitName, Integer kitNumber, String type, String gameMode, ItemStack previewItem, ItemStack[] inventoryContents, ItemStack[] armorContents) {
        this.owner = owner;

	    this.kitName = kitName;
        this.kitNumber = kitNumber;

        this.type = type;
	    this.gameMode = gameMode;

        this.previewItem = previewItem;
        this.inventoryContents = inventoryContents;
        this.armorContents = armorContents;
    }

	public void load(Player player, boolean verbose) {
		player.getInventory().setContents(this.inventoryContents);
		player.getInventory().setArmorContents(this.armorContents);
		player.updateInventory();

		if (verbose) {
			player.sendMessage(ChatColor.GREEN + "Kit " + this.kitName + " loaded");
		}

		Bukkit.getServer().getPluginManager().callEvent(new KitLoadEvent(player, this));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MPGKit && ((MPGKit) obj).getOwner().equals(this.owner)
				&& ((MPGKit) obj).getKitNumber().equals(this.kitNumber) && ((MPGKit) obj).getGameMode().equals(this.gameMode)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.owner.hashCode();
		hash = hash * 31 + this.kitNumber.hashCode();
		hash = hash * 31 + this.gameMode.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return this.gameMode + "-" + this.owner + "-" + this.kitName + "-" + this.kitNumber;
	}

    public UUID getOwner() {
        return owner;
    }

	public String getName() {
		return kitName;
	}

    public Integer getKitNumber() {
        return kitNumber;
    }

	public String getType() {
		return type;
	}

    public String getGameMode() {
        return gameMode;
    }

    public ItemStack getPreviewItem() {
        return previewItem;
    }

    public ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

	public List<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

    public static String getKey(UUID uuid, String gameMode, int kitNumber) {
        return gameMode + "-" + uuid.toString() + "-" + kitNumber;
    }

}