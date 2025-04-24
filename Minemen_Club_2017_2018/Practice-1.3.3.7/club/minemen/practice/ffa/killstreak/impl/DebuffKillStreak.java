// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.ffa.killstreak.impl;

import java.util.Arrays;
import java.util.List;
import club.minemen.practice.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import club.minemen.practice.ffa.killstreak.KillStreak;

public class DebuffKillStreak implements KillStreak
{
    private static final ItemStack SLOWNESS;
    private static final ItemStack POISON;
    
    @Override
    public void giveKillStreak(final Player player) {
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.SLOWNESS.clone());
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.POISON.clone());
    }
    
    @Override
    public List<Integer> getStreaks() {
        return Arrays.asList(7, 25);
    }
    
    static {
        SLOWNESS = new ItemStack(Material.POTION, 1, (short)16394);
        POISON = new ItemStack(Material.POTION, 1, (short)16388);
    }
}
