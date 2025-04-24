package me.devkevin.landcore.player.grant.menus;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.Grant;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.buttons.CloseButton;
import me.devkevin.landcore.utils.menu.pagination.PageButton;
import me.devkevin.landcore.utils.menu.pagination.PaginatedMenu;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 13:00
 * GrantViewMenu / land.pvp.core.player.grant.menus / LandCore
 */
@RequiredArgsConstructor
public class GrantViewMenu extends PaginatedMenu {

    private final CoreProfile data;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.PRIMARY + "Viewing grants..";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));
        buttons.put(26, new CloseButton());

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.SECONDARY + "No additional informations.");
                lore.add("");
                return new ItemBuilder(Material.NETHER_STAR).lore(CC.translate(lore)).name(CC.PRIMARY + "Information").build();
            }
        });


        surroundButtons(false, buttons, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        data.getGrants().stream().filter(grant -> grant.getRank() != Rank.MEMBER).sorted(Comparator.comparingLong(Grant::getAddedAt).reversed()).forEach(grant -> {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {

                    List<String> lore = new ArrayList<>();
                    ItemBuilder item = (data.getRank().getName() == grant.getRank().getName() ? new ItemBuilder(Material.WOOL).durability(5) : new ItemBuilder(Material.WOOL).durability(14));
                    if (data.getRank().getName() == grant.getRank().getName()) {
                        Player grantplayer = Bukkit.getPlayer(grant.getAddedBy());
                        lore.add("");
                        lore.add(CC.SECONDARY + "Rank&7: " + grant.getRank().getColor() + grant.getRank().getName());
                        lore.add(CC.SECONDARY + "Expires in&7: " + CC.PRIMARY + grant.getNiceDuration());
                        lore.add(CC.SECONDARY + "Added by&7: " + CC.PRIMARY + grant.getAddedBy());
                        lore.add(CC.SECONDARY + "Added at&7: " + CC.PRIMARY + new Date(grant.getAddedAt()));
                        lore.add(CC.SECONDARY + "Reason&7: " + CC.PRIMARY + grant.getReason());
                        lore.add("");
                        lore.add(CC.GREEN + "This grant is active!");
                    } else {
                        lore.add("");
                        lore.add(CC.SECONDARY + "Rank&7: " + grant.getRank().getColor() + grant.getRank().getName());
                        lore.add(CC.SECONDARY + "Expires in&7: " + CC.RED + "Expired");
                        lore.add(CC.SECONDARY + "Added by&7: " + CC.PRIMARY + grant.getAddedBy());
                        lore.add(CC.SECONDARY + "Added at&7: " + CC.PRIMARY + new Date(grant.getAddedAt()));
                        lore.add(CC.SECONDARY + "Reason&7: " + CC.PRIMARY + grant.getReason());
                        lore.add("");
                        lore.add(CC.SECONDARY + "Removed by&7: " + CC.BD_RED + "Console");
                        lore.add(CC.SECONDARY + "");
                        lore.add(CC.RED + "This grant is removed!");

                    }
                    return item.name(CC.PRIMARY + "Grant").lore(CC.translate(lore)).build();
                }
            });
        });

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }
}
