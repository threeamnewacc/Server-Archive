package us.zonix.hcfactions.event.koth.procedure;

import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.factions.claims.ClaimPillar;

import java.util.Arrays;

@Accessors(chain = true, fluent = true)
public class KothCreateProcedure {

    @Getter @Setter private KothCreateProcedureStage stage;
    @Getter @Setter private String name;
    @Getter @Setter private ClaimPillar[] pillars;
    @Getter @Setter private int clicks;
    @Getter @Setter private int height;

    public KothCreateProcedure() {
        pillars = new ClaimPillar[2];
    }

    public static ItemStack getWand() {
        return new ItemBuilder(Material.DIAMOND_HOE).name(ChatColor.GREEN + "KoTH Zone Selection").lore(Arrays.asList(
                "&aLeft click the ground&7 to set the &afirst&7 point.",
                "&aRight click the ground&7 to set the &asecond&7 point.",
                "&aSneak and left click the air&7 to confirm zone once both points set.",
                "&aRight click the air twice&7 to clear your selection."
        )).build();
    }

    public static Inventory getConfirmationInventory(KothCreateProcedure procedure) {
        Inventory toReturn = Bukkit.createInventory(null, 18, ChatColor.RED + "Confirm KoTH?");


        toReturn.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Cancel").build());
        toReturn.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Cancel").build());
        toReturn.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Procedure Information").lore(Arrays.asList(
                ChatColor.YELLOW + "Name: " + ChatColor.RED + procedure.name(),
                ChatColor.YELLOW + "Height: " + ChatColor.RED + procedure.height(),
                ChatColor.YELLOW + "Corner 1: &cWorld: " + procedure.pillars[0].getOriginalLocation().getWorld().getName().replace("_", " ") + ", &cX: " + procedure.pillars[0].getOriginalLocation().getBlockX() + ", Y: " + procedure.pillars[0].getOriginalLocation().getBlockY() + ", Z: " + procedure.pillars[0].getOriginalLocation().getBlockZ(),
                ChatColor.YELLOW + "Corner 2: &cWorld: " + procedure.pillars[1].getOriginalLocation().getWorld().getName().replace("_", " ") + ", &cX: " + procedure.pillars[1].getOriginalLocation().getBlockX() + ", Y: " + procedure.pillars[1].getOriginalLocation().getBlockY() + ", Z: " + procedure.pillars[1].getOriginalLocation().getBlockZ()
        )).build());

        for (int i = 0; i < 9; i++) {
            toReturn.setItem(9 + i, new ItemBuilder(Material.WOOL).durability(5).name(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm KoTH").build());
        }

        return toReturn;
    }

}
