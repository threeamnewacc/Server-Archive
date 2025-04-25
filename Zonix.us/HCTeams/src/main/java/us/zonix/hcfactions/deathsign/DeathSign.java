package us.zonix.hcfactions.deathsign;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.ItemBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeathSign {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
    private static final List<String> SIGN_TEXT = main.getLanguageConfig().getStringList("DEATH_SIGN.SIGN");
    private static final String ITEMSTACK_NAME = main.getLanguageConfig().getString("DEATH_SIGN.ITEM_STACK.NAME");
    private static final List<String> ITEMSTACK_LORE = main.getLanguageConfig().getStringList("DEATH_SIGN.ITEM_STACK.LORE");

    @Getter private final String killer;
    @Getter private final String killed;
    @Getter private final String time;

    public DeathSign(String killer, String killed, String time) {
        this.killer = killer;
        this.killed = killed;
        this.time = time;
    }

    public DeathSign(String killer, String killed) {
        this(killer, killed, format.format(new Date()));
    }

    public Sign toSign(Block block) {

        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                sign.setLine(i, SIGN_TEXT.get(i).replace("%KILLER%", killer).replace("%KILLED%", killed).replace("%TIME%", time));
            }

            sign.update();

            return sign;
        }
        return null;
    }

    public ItemStack toItemStack() {
        List<String> lore = new ArrayList<>();

        for (String line : ITEMSTACK_LORE) {
            lore.add(line.replace("%KILLER%", killer).replace("%KILLED%", killed).replace("%TIME%", time));
        }

        return new ItemBuilder(Material.SIGN).name(ITEMSTACK_NAME).lore(lore).build();
    }

    public static DeathSign getByItemStack(ItemStack itemStack) {
        if (itemStack.getType() == Material.SIGN && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ITEMSTACK_NAME)) {
            List<String> lore = itemStack.getItemMeta().getLore();

            if (lore.size() == ITEMSTACK_LORE.size() && lore.get(1).equals(ITEMSTACK_LORE.get(1))) {

                String killer = ChatColor.stripColor(lore.get(2));
                String killed = ChatColor.stripColor(lore.get(0));
                String time = ChatColor.stripColor(lore.get(3));

                return new DeathSign(killer, killed, time);
            }

        }

        return null;
    }

    public static DeathSign getBySign(Sign sign) {
         String[] lines = sign.getLines();

        if (lines[1].equals(SIGN_TEXT.get(1))) {

            String killer = ChatColor.stripColor(lines[2]);
            String killed = ChatColor.stripColor(lines[0]);
            String time = ChatColor.stripColor(lines[3]);

            return new DeathSign(killer, killed, time);
        }


        return null;
    }

}
