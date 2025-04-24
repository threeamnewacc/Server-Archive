package me.devkevin.landcore.player.rank.menu;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.menus.QuickGrantMenu;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.Clickable;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.buttons.CloseButton;
import me.devkevin.landcore.utils.menu.pagination.PageButton;
import me.devkevin.landcore.utils.menu.pagination.PaginatedMenu;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 12:20
 * RankListMenu / land.pvp.core.player.rank.menu / LandCore
 */
@RequiredArgsConstructor
public class RankListMenu extends PaginatedMenu {

    private final CoreProfile coreProfile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.PRIMARY + "Rank List";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));
        buttons.put(35, new CloseButton());

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.SECONDARY + "Available Ranks&7: " + CC.PRIMARY + Rank.values().length);
                lore.add(CC.SECONDARY + "Staff Ranks&7: " + CC.PRIMARY + "6");
                lore.add(CC.SECONDARY + "Media Ranks&7: " + CC.PRIMARY + "3");
                lore.add(CC.SECONDARY + "Donator Ranks&7: " + CC.PRIMARY + "5");
                lore.add(CC.SECONDARY + "Special Ranks&7: " + CC.PRIMARY + "3");
                return new ItemBuilder(Material.NETHER_STAR).lore(CC.translate(lore)).name(CC.PRIMARY + "Information").build();
            }
        });

        buttons.put(27, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.GRAY + "Click here to change");
                lore.add(CC.GRAY + "make a grant.");
                return new ItemBuilder(Material.PAPER).lore(CC.translate(lore)).name(CC.PRIMARY + "Make Grant").build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new QuickGrantMenu().openMenu(player);
            }
        });

        surroundButtons(false, buttons, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Stream.of(Rank.values()).sorted(Comparator.comparingInt(Rank::ordinal).reversed()).forEach(rank -> {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemBuilder item = new ItemBuilder(Material.WOOL).durability(StringUtil.convertChatColorToWoolData(ChatColor.getByChar(rank.getColor().replace(String.valueOf('§'), "").replace("&", "").replace("o", ""))));

                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(CC.SECONDARY + "Color&7: " + rank.getColor() + "Color");
                    lore.add(CC.SECONDARY + "Prefix&7: " + rank.getRawFormat());
                    lore.add("");
                    lore.add(CC.PRIMARY + "Permissions&7: ");
                    lore.add(CC.GRAY + " " + CC.CIRCLE + " " + CC.SECONDARY  + "landcore." + rank.getName().toLowerCase());
                    lore.add("");
                    lore.add(CC.GRAY + "Click here to see");
                    lore.add(CC.GRAY + "chat preview of this rank.");
                    return item.name(CC.translate(rank.getColor() + rank.getName())).lore(CC.translate(lore)).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    Clickable clickable = new Clickable();
                    player.closeInventory();
                    player.sendMessage(CC.GRAY + CC.SEPARATOR);
                    player.sendMessage(CC.PRIMARY + "Rank Preview" + CC.GRAY +":");
                    player.sendMessage(CC.translate(CC.SECONDARY + " With Tag&7: " + "&7[&6&lG&e&lG&7] " + rank.getRawFormat() + rank.getColor() + rank.getName() + CC.GRAY + ": " + CC.WHITE + "Hello there!"));
                    player.sendMessage(CC.translate(CC.SECONDARY + " Without Tag&7: " + rank.getRawFormat() + rank.getColor() + rank.getName() + CC.GRAY + ": " + CC.WHITE + "Hello there!"));
                    player.sendMessage("");
                    player.sendMessage(CC.SECONDARY + "Showed preview for " + rank.getColor() + rank.getName() + CC.SECONDARY + " rank.");

                    clickable.add("&a&lClick to reopen!", "&aClick to open rank list menu!", "/ranklist");
                    clickable.sendToPlayer(player);
                    player.sendMessage(CC.GRAY + CC.SEPARATOR);
                }
            });
        });

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 4;
    }
}
