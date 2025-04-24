package me.devkevin.landcore.player.grant.menus;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.procedure.GrantProcedure;
import me.devkevin.landcore.player.grant.procedure.GrantProcedureStage;
import me.devkevin.landcore.player.rank.Rank;
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
 * 20/01/2023 @ 13:02
 * GrantSelectMenu / land.pvp.core.player.grant.menus / LandCore
 */
@RequiredArgsConstructor
public class GrantSelectMenu extends PaginatedMenu {

    private final CoreProfile data;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.PRIMARY + "Please select a rank";
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
                lore.add("");
                lore.add(CC.GRAY + "Click one of the ranks");
                lore.add(CC.GRAY + "to start a grant procedure.");
                return new ItemBuilder(Material.NETHER_STAR).lore(CC.translate(lore)).name(CC.PRIMARY + "Information").build();
            }
        });

        buttons.put(27, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.GRAY + "Click here to change");
                lore.add(CC.GRAY + "grant player.");
                return new ItemBuilder(Material.PAPER).lore(CC.translate(lore)).name(CC.PRIMARY + "Change Player").build();
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
                    lore.add(CC.PRIMARY + "Permissions&7: ");
                    lore.add(CC.GRAY + " " + CC.CIRCLE + " " + CC.SECONDARY  + "landcore." + rank.getName().toLowerCase());
                    lore.add("");
                    lore.add(CC.SECONDARY + "Default duration&7: " + CC.PRIMARY + "Permanent");
                    lore.add("");
                    lore.add(CC.SECONDARY + "Right-Click to continue.");
                    lore.add(CC.SECONDARY + "Middle-Click to cancel procedure.");
                    lore.add("");
                    lore.add(CC.GRAY + "Click here to grant " + rank.getColor() + rank.getName() + CC.GRAY + " rank.");
                    return item.name(CC.translate(rank.getColor() + rank.getName())).lore(CC.translate(lore)).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if(clickType == ClickType.MIDDLE) {
                        player.closeInventory();
                    } else {
                        player.sendMessage(CC.translate("&aYou have selected " + rank.getColor() + rank.getName() + " rank."));
                        GrantProcedure grantProcedure = new GrantProcedure(rank, data, player.getName(), GrantProcedureStage.DURATION);
                        grantProcedure.setRank(rank);
                        new GrantDurationMenu(grantProcedure, data).openMenu(player);
                    }
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
