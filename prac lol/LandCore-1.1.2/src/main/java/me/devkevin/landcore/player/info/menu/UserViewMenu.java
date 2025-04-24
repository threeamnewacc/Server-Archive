package me.devkevin.landcore.player.info.menu;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.ButtonSound;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.menu.buttons.CloseButton;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
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
 * 20/01/2023 @ 19:54
 * UserViewMenu / land.pvp.core.player.info.menu / LandCore
 */
@RequiredArgsConstructor
public class UserViewMenu extends Menu {

    {
        setAutoUpdate(true);
    }

    private final CoreProfile data;

    @Override
    public String getTitle(Player player) {
        return CC.PRIMARY + "User Menu -> " + data.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(26, new CloseButton());

        surroundButtons(true, buttons, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(7).build());

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.NETHER_STAR);

                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.SECONDARY + "Player&7: " + CC.PRIMARY + data.getName());
                lore.add("");

                return item.name(CC.PRIMARY + "Information").lore(CC.translate(lore)).build();
            }
        });

        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.QUARTZ);

                List<String> lore = new ArrayList<>();

                lore.add("");
                lore.add(CC.SECONDARY + "Current rank is " + data.getRank().getColor() + data.getRank().getName());
                lore.add("");
                lore.add(CC.PRIMARY + "Information&7: ");
                lore.add(CC.SECONDARY + " Added by&7: " + CC.PRIMARY + data.getGrant().getAddedBy());
                lore.add(CC.SECONDARY + " Reason&7: " + CC.PRIMARY + data.getGrant().getReason());
                lore.add("");
                lore.add(CC.GRAY + "Click here to view more info.");

                return item.name(CC.PRIMARY + "Rank Info").lore(CC.translate(lore)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.performCommand("grants " + data.getName());
                playSound(player, ButtonSound.CLICK);
            }
        });

        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.WOOL).durability(14);

                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(CC.SECONDARY + "Active&7: " + CC.GREEN + "No");
                lore.add(CC.SECONDARY + "Number of punishments&7: " + CC.PRIMARY + "5");
                lore.add(CC.SECONDARY + "Last punishment&7: " + CC.PRIMARY + "Permanent Mute");
                lore.add("");
                lore.add(CC.GRAY + "Click here to view more info.");

                return item.name(CC.PRIMARY + "Punishment Info").lore(CC.translate(lore)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                //todo
            }
        });

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.BLAZE_POWDER);

                List<String> lore = new ArrayList<>();

                lore.add("");
                lore.add(CC.SECONDARY + "Prefix&7: " + CC.PRIMARY + data.getGrant().getRank().getRawFormat());
                lore.add(CC.SECONDARY + "Color&7: " + data.getGrant().getRank().getColor() + data.getGrant().getRank().getName());
                lore.add(CC.SECONDARY + "Booster&7: " + CC.PRIMARY + "None");
                lore.add("");
                lore.add(CC.GRAY + "Click here to view more info.");

                return item.name(CC.PRIMARY + "Cosmetics Info").lore(CC.translate(lore)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            }
        });

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.EMERALD);

                List<String> lore = new ArrayList<>();

                lore.add("");
                lore.add(CC.SECONDARY + "Balance&7: " + CC.PRIMARY + " 0");
                lore.add("");
                lore.add(CC.GRAY + "Click here to view more info.");

                return item.name(CC.PRIMARY + "Gems Info").lore(CC.translate(lore)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            }
        });


        return buttons;
    }


    @Override
    public int getSize() {
        return 9 * 3;
    }
}
