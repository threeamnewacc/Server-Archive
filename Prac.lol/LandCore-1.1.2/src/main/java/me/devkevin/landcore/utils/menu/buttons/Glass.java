package me.devkevin.landcore.utils.menu.buttons;

import lombok.AllArgsConstructor;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class Glass extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder item =  new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(CC.translate(" "));
        return item.build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        //Button.playNeutral(player);
    }

}
