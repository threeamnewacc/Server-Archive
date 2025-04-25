package us.zonix.hcfactions.profile.options;

import us.zonix.core.CorePlugin;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.options.item.ProfileOptionsItem;
import us.zonix.hcfactions.profile.options.item.ProfileOptionsItemState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.options.item.ProfileOptionsItemState;

import java.util.Arrays;

public class ProfileOptionsListeners implements Listener {

    @EventHandler
    public void onInventoryInteractEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);
        Inventory inventory = event.getInventory();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            Inventory options = profile.getOptions().getInventory();
            if (inventory.getTitle().equals(options.getTitle()) && Arrays.equals(inventory.getContents(), options.getContents())) {
                event.setCancelled(true);
                ProfileOptionsItem item = ProfileOptionsItem.fromItem(itemStack);

                if (item != null) {

                    if (item == ProfileOptionsItem.FOUND_DIAMOND_MESSAGES) {
                        profile.getOptions().setViewFoundDiamondMessages(!profile.getOptions().isViewFoundDiamondMessages());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isViewFoundDiamondMessages() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    } else if (item == ProfileOptionsItem.DEATH_MESSAGES) {
                        profile.getOptions().setViewDeathMessages(!profile.getOptions().isViewDeathMessages());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isViewDeathMessages() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    } else if (item == ProfileOptionsItem.PUBLIC_MESSAGES) {
                        profile.getOptions().setReceivePublicMessages(!profile.getOptions().isReceivePublicMessages());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isReceivePublicMessages() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    }

                }


            }

        }

    }

}
