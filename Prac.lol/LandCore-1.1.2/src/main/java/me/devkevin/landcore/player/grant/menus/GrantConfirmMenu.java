package me.devkevin.landcore.player.grant.menus;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.event.player.PlayerRankChangeEvent;
import me.devkevin.landcore.player.grant.Grant;
import me.devkevin.landcore.player.grant.procedure.GrantProcedure;
import me.devkevin.landcore.player.grant.procedure.GrantProcedureStage;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.ButtonSound;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.message.CC;


import me.devkevin.landcore.utils.time.TimeFormatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 13:10
 * GrantConfirmMenu / land.pvp.core.player.grant.menus / LandCore
 */
@RequiredArgsConstructor
public class GrantConfirmMenu extends Menu {

    private final GrantProcedure grantProcedure;

    @Override
    public String getTitle(Player player) {
        return CC.translate("&aAre you sure?");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        surroundButtons(true, buttons, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());

        int[] confirm = {10, 11, 12, 19, 21, 28, 29, 30};

        for (int i : confirm) {
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(13).build();
                }
            });
        }

        buttons.put(20, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(CC.GRAY + "Click here to confirm");
                lore.add(CC.GRAY + "this current grant.");
                return new ItemBuilder(Material.WOOL).durability(5).lore(CC.translate(lore)).name(CC.B + CC.GREEN + "Confirm").build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (grantProcedure != null && grantProcedure.getStage() == GrantProcedureStage.CONFIRMATION) {
                    Grant grant = new Grant(grantProcedure.getRank(), grantProcedure.getDuration(), System.currentTimeMillis(), grantProcedure.getAddedBy(), grantProcedure.getReason());

                    grantProcedure.getAddedTo().setGrant(grant);
                    grantProcedure.getAddedTo().getGrants().add(grant);
                    Bukkit.getPlayer(grantProcedure.getAddedTo().getName()).sendMessage(CC.translate("&aYour rank has been set to " + grantProcedure.getRank().getColor() + grantProcedure.getRank().getName() + "&a."));
                    player.sendMessage(CC.translate("&aYou have set &e" + grantProcedure.getAddedTo().getName() + " &arank to " + grantProcedure.getRank().getColor() + grantProcedure.getRank().getName() + "&a."));
                    TaskUtil.runAsync(() -> Bukkit.getPlayer(grantProcedure.getAddedTo().getName()).setPlayerListName(grantProcedure.getRank().getColor() + grantProcedure.getAddedTo().getName()));
                    GrantProcedure.getProcedures().remove(grantProcedure);

                    LandCore.getInstance().getServer().getPluginManager().callEvent(new PlayerRankChangeEvent(grantProcedure.getAddedTo().getPlayer(), grantProcedure.getAddedTo(), grant, grant.getDuration()));

                    player.closeInventory();
                    playSound(player, ButtonSound.SUCCESS);
                }
            }
        });

        buttons.put(24, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(CC.GRAY + "Click here to cancel");
                lore.add(CC.GRAY + "this current grant.");
                return new ItemBuilder(Material.WOOL).durability(14).lore(CC.translate(lore)).name(CC.B + CC.RED + "Cancel").build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (grantProcedure != null && grantProcedure.getStage() == GrantProcedureStage.CONFIRMATION) {
                    GrantProcedure.getProcedures().remove(grantProcedure);
                    player.sendMessage(CC.RED + "You have cancelled grant procedure.");
                    player.closeInventory();
                    playSound(player, ButtonSound.FAIL);
                }
            }
        });

        buttons.put(22, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.SECONDARY + "Player&7: " + CC.PRIMARY + grantProcedure.getAddedTo().getName());
                lore.add(CC.SECONDARY + "Rank&7: " + CC.PRIMARY + grantProcedure.getRank().getColor() + grantProcedure.getRank().getName());
                lore.add(CC.SECONDARY + "Reason&7: " + CC.PRIMARY + grantProcedure.getReason());
                lore.add(CC.SECONDARY + "Duration&7: " + CC.PRIMARY + (grantProcedure.getDuration() == -1L ? "Permanent" : TimeFormatUtils.getDetailedTime(grantProcedure.getDuration())));
                lore.add("");
                lore.add(CC.GRAY + "Click one of the items");
                lore.add(CC.GRAY + "to finish the procedure.");
                return new ItemBuilder(Material.PAPER).lore(CC.translate(lore)).name(CC.B + CC.GREEN + "Grant Info").build();

            }
        });

        int[] cancel = {14, 15, 16, 23, 25, 32, 33, 34};

        for (int i : cancel) {
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(14).build();
                }
            });
        }
        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }
}
