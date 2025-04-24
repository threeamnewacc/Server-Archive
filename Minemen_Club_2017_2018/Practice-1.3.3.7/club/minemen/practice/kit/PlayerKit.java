// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.kit;

import java.beans.ConstructorProperties;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerKit
{
    private final String name;
    private final int index;
    private ItemStack[] contents;
    private String displayName;
    
    public void applyToPlayer(final Player player) {
        for (final ItemStack itemStack : this.contents) {
            if (itemStack != null && itemStack.getAmount() <= 0) {
                itemStack.setAmount(1);
            }
        }
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(Practice.getInstance().getKitManager().getKit(this.name).getArmor());
        player.updateInventory();
        player.sendMessage(CC.PRIMARY + "Giving you " + CC.SECONDARY + this.displayName + CC.PRIMARY + ".");
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public ItemStack[] getContents() {
        return this.contents;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    @ConstructorProperties({ "name", "index", "contents", "displayName" })
    public PlayerKit(final String name, final int index, final ItemStack[] contents, final String displayName) {
        this.name = name;
        this.index = index;
        this.contents = contents;
        this.displayName = displayName;
    }
}
