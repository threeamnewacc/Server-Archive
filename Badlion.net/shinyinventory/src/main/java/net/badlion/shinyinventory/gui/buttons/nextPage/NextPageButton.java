package net.badlion.shinyinventory.gui.buttons.nextPage;

import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.InterfaceManager;
import net.badlion.shinyinventory.gui.buttons.Button;
import net.badlion.shinyinventory.gui.interfaces.ChestOptionsPageInterface;
import net.badlion.shinyinventory.gui.interfaces.SinglePageInterface;
import net.badlion.shinyinventory.utils.Constants;
import net.badlion.shinyinventory.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ShinyDialga45 on 4/10/2015.
 */
public class NextPageButton extends Button {

    private SinglePageInterface page;

    public NextPageButton(int slot) {
        super(null, slot);
    }

    public NextPageButton(SinglePageInterface gui, int slot) {
        super(null, slot);
        this.page = gui;
    }

    public SinglePageInterface getNextPage(SinglePageInterface chestInterface) {
        try {
            if (chestInterface instanceof ChestOptionsPageInterface) {
                ChestOptionsPageInterface nextPage = new ChestOptionsPageInterface(this.page.rawButtons, this.page.getSize(), this.page.rawTitle, this.page, this.page.page + 1);
                nextPage.update();
                return nextPage != null ? nextPage : chestInterface;
            }
            SinglePageInterface nextPage = new SinglePageInterface(this.page.rawButtons, this.page.getSize(), this.page.rawTitle, this.page, this.page.page + 1);
            nextPage.update();
            return nextPage != null ? nextPage : chestInterface;
        } catch (Exception e) {
            return chestInterface;
        }
    }

    @Override
    public ItemCreator getIcon() {
        return new ItemCreator(Material.ARROW)
                .setName(Constants.PREFIX + "Next");
    }

    @Override
    public void function(Player player) {
        Interface currentInterface = InterfaceManager.getInterface(player.getOpenInventory());
        //Interface nextPage = getNextPage((SinglePageInterface) currentInterface);
        ((SinglePageInterface)currentInterface).openNextPage();
    }

}
