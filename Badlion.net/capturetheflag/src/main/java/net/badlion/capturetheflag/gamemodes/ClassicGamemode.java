package net.badlion.capturetheflag.gamemodes;

import net.badlion.capturetheflag.CTF;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.bukkitevents.KitLoadEvent;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
// TODO: IMPLEMENT LISTENER WHEREVER YOU SET THE ACTUAL GAMEMODE
public class ClassicGamemode extends Gamemode implements Listener {

	private MPGKit defaultKit;

	public ClassicGamemode() {
		ItemStack[] armorContents = new ItemStack[4];
		ItemStack[] inventoryContents = new ItemStack[36];

		armorContents[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
		armorContents[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		armorContents[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		armorContents[3] = new ItemStack(Material.LEATHER_HELMET, 1);

		inventoryContents[0] = new ItemStack(Material.WOOD_SWORD);
		inventoryContents[1] = new ItemStack(Material.BOW);
        inventoryContents[2] = new ItemStack(Material.COOKED_BEEF, 64);
        inventoryContents[3] = new ItemStack(Material.GOLDEN_APPLE);
		inventoryContents[8] = new ItemStack(Material.ARROW, 16);

		// Create default CTF kit
		this.defaultKit = new MPGKit(null, "defaultctf", 0, "defaultctf", "defaultctf", null, inventoryContents, armorContents);
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {

        ChatColor color = MPGPlayerManager.getMPGPlayer(event.getPlayer()).getTeam().getColor();

        for (ItemStack itemStack : event.getPlayer().getInventory().getArmorContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            if (itemStack.getType() == Material.LEATHER_HELMET || itemStack.getType() == Material.LEATHER_CHESTPLATE
                    || itemStack.getType() == Material.LEATHER_LEGGINGS || itemStack.getType() == Material.LEATHER_BOOTS) {
                LeatherArmorMeta itemMeta = ((LeatherArmorMeta) itemStack.getItemMeta());
                itemMeta.setColor(Gberry.getColorFromChatColor(color));
                itemStack.setItemMeta(itemMeta);
            }
        }

		ItemStackUtil.addUnbreakingToWeapons(event.getPlayer());
		ItemStackUtil.addUnbreakingToArmor(event.getPlayer());
	}

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (CTF.getInstance().getCTFGame().getGameState() == MPGGame.GameState.GAME) {
            if (event.getWhoClicked() instanceof Player && event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public ItemStack getTierItem(int tier) {
        return null;
    }

    @Override
    public List<ItemStack> getCommonTierItems(int tier) {
        return null;
    }

    @Override
    public int getNumOfTierRandom(int tier) {
        return 0;
    }

    @Override
    public int getNumOfTierGuaranteed(int tier) {
        return 0;
    }

    @Override
    public void handleDeath(LivingEntity died) {
        // TODO: ???
    }

    @Override
    public String getName() {
        return "Classic";
    }

    @Override
    public MPGKit getDefaultKit() {
        return this.defaultKit;
    }

}
