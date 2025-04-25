package us.zonix.hcfactions.crowbar;

import us.zonix.hcfactions.FactionsPlugin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.zonix.hcfactions.FactionsPlugin;

import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
public class Crowbar {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static final Material MATERIAL = Material.getMaterial(main.getMainConfig().getString("CROWBAR.ITEM"));
    private static final String NAME = main.getLanguageConfig().getString("CROWBAR.NAME");
    private static final List<String> LORE = main.getLanguageConfig().getStringList("CROWBAR.LORE");

    @Getter private ItemStack itemStack;
    @Getter @Setter private int spawnerUses;
    @Getter @Setter private int portalUses;

    public Crowbar(ItemStack itemStack) {
        this.itemStack = itemStack;

        setup();
    }

    public Crowbar update() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(NAME);

        List<String> lore = new ArrayList<>();

        for (String sub : LORE) {
            lore.add(sub.replace("%SPAWNER_USES%", spawnerUses + "").replace("%PORTAL_USES%", portalUses + ""));
        }

        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return this;
    }

    private void setup() {
        int spawnerIndex = 0;
        int portalIndex = 0;

        for (int i = 0; i < LORE.size(); i++) {
            String sub = LORE.get(i);
            if (sub.contains("%SPAWNER_USES%")) {
                spawnerIndex = i;
            }
            if (sub.contains("%PORTAL_USES%")) {
                portalIndex = i;
            }
        }

        int spawner;
        int portal;

        try {spawner = Integer.parseInt(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(spawnerIndex)).replaceAll("[^0-9]", "").replace(" ", ""));} catch (Exception ignored) { ignored.printStackTrace(); spawner = 0; }
        try {portal = Integer.parseInt(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(portalIndex)).replaceAll("[^0-9]", "").replace(" ", ""));} catch (Exception ignored) { ignored.printStackTrace(); portal = 0; }

        spawnerUses = spawner;
        portalUses = portal;
    }

    public static Crowbar getNewCrowbar() {
        ItemStack toReturn = new ItemStack(MATERIAL);

        ItemMeta meta = toReturn.getItemMeta();
        meta.setDisplayName(NAME);

        List<String> lore = new ArrayList<>();

        for (String sub : LORE) {
            lore.add(sub.replace("%SPAWNER_USES%", 1 + "").replace("%PORTAL_USES%", 8 + ""));
        }

        meta.setLore(lore);

        toReturn.setItemMeta(meta);

        return new Crowbar(toReturn);
    }

    public static Crowbar getByItemStack(ItemStack itemStack) {
        return itemStack.getType() == MATERIAL && NAME.equals(itemStack.getItemMeta().getDisplayName()) && itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().size() == LORE.size() ? new Crowbar(itemStack) : null;
    }

}
