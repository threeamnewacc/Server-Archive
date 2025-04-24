package me.devkevin.landcore.player.color.menu;

import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.WoolUtil;
import me.devkevin.landcore.utils.inventory.InventoryUI;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:31
 * ColorMenu / me.devkevin.landcore.player.color.menu / LandCore
 */
@Getter
public class ColorMenu {
    private final InventoryUI menu = new InventoryUI(CC.GOLD + "Color Editor", 4);

    public String[] COLORS = {
            "Purple", "dark_aqua", "light_gray",
            "Gray", "light_purple", "Green",
            "Aqua", "Gold", "Red", "Yellow",
            "dark_green", "White", "Black",
            "dark_red",
    };

    private String getColor(String colorName) {
        switch (colorName) {
            case "Purple": return CC.PURPLE;
            case "dark_aqua": return CC.D_AQUA;
            case "light_gray": return CC.GRAY;
            case "Gray": return CC.D_GRAY;
            case "light_purple": return CC.PINK;
            case "Green": return CC.GREEN;
            case "Aqua": return CC.AQUA;
            case "Gold": return CC.GOLD;
            case "Red": return CC.RED;
            case "Yellow": return CC.YELLOW;
            case "dark_green": return CC.DARK_GREEN;
            case "White" : return CC.WHITE;
            case "Black" : return CC.BLACK;
            case "dark_red" : return CC.D_RED;
            case "Remove Color": return null;
            default: return "default";
        }
    }

    public InventoryUI getMenu(Player player) {
        CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
        String c = profile.getCustomColor() != null && !profile.getCustomColor().isEmpty() && profile.hasRank(Rank.BASIC) ? profile.getCustomColor() : profile.getGrant().getRank().getColor();

        this.menu.setItem(4, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.REDSTONE_BLOCK)
                        .name(profile.getGrant().getRank().getRawFormat() + c + player.getName() + CC.R + ": Hello!")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "Your chat messages will",
                                CC.GRAY + CC.B + "be displayed like the example",
                                CC.GRAY + CC.B + "above.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();

            }
        });

        this.menu.setItem(35, new InventoryUI.AbstractClickableItem(
                new ItemBuilder(Material.GLASS)
                        .name(CC.RED + CC.B + "Remove color")
                        .lore(Arrays.asList(
                                "",
                                CC.GRAY + CC.B + "Reverts your chat color back",
                                CC.GRAY + CC.B + "to the original color created",
                                CC.GRAY + CC.B + "by your rank.",
                                ""
                        ))
                        .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player)event.getWhoClicked();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
                player.closeInventory();

                if (profile == null) {
                    player.sendMessage(CC.RED + "Please wait for your data to load.");
                    return;
                }

                String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                String customColor = getColor(name);

                String color = customColor != null ? customColor.replace("&", "§") : "";

                if (color.equals(profile.getCustomColor())) {
                    player.sendMessage(CC.RED + "You already have " + getColor(name) + name + CC.RED + " selected.");
                    return;
                }

                if (!profile.hasRank(Rank.BASIC)) {
                    player.sendMessage("");
                    player.sendMessage(CC.RED + "You cannot change your color name with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                    player.sendMessage(CC.GRAY + "Purchase rank at https://store.prac.lol/ to change your color name.");
                    player.sendMessage("");
                    return;
                }

                player.performCommand("setcolor " + "default");
                profile.save(true);
            }
        });

        int[] i = {9};

        Stream.of(COLORS).forEach(colorName -> {
            String color = getColor(colorName);

            menu.setItem(i[0], new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.WOOL)
                    .name(color + colorName)
                    .durability(WoolUtil.convertCCToWoolData(color)).lore(Arrays.asList(
                            CC.GRAY + CC.B + "Change your color name",
                            CC.GRAY + CC.B + "to " + colorName + CC.GRAY + CC.I + "."
                    ))
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player)event.getWhoClicked();
                    CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
                    player.closeInventory();

                    if (profile == null) {
                        player.sendMessage(CC.RED + "Please wait for your data to load.");
                        return;
                    }

                    String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                    String customColor = getColor(name);

                    String color = customColor != null ? customColor.replace("&", "§") : "";

                    if (color.equals(profile.getCustomColor())) {
                        player.sendMessage(CC.RED + "You already have " + getColor(name) + name + CC.RED + " selected.");
                        return;
                    }

                    if (!profile.hasRank(Rank.BASIC)) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot change your color name with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://store.prac.lol/ to change your color name.");
                        player.sendMessage("");
                        return;
                    }

                    player.performCommand("setcolor " + name);
                    profile.save(true);
                }
            });
            i[0]++;
        });

        return menu;
    }

}
