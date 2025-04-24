package me.devkevin.landcore.player.tags.menu;

import lombok.Getter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.player.tags.enums.CountriesPrefix;
import me.devkevin.landcore.player.tags.enums.SymbolPrefix;
import me.devkevin.landcore.player.tags.enums.TextSymbolPrefix;
import me.devkevin.landcore.utils.inventory.InventoryUI;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.item.ItemUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.devkevin.landcore.LandCoreAPI.PLACEHOLDER_ITEM;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:10
 * PrefixMenu / me.devkevin.landcore.player.tags.menu / LandCore
 */
@Getter
public class PrefixMenu {
    private final InventoryUI menu = new InventoryUI(CC.YELLOW + CC.B + "Select a Prefix", true, 3);

    private final InventoryUI countriesPrefixMenu = new InventoryUI(CC.YELLOW + CC.B + "Countries Prefix", true, 4);
    private final InventoryUI symbolPrefixMenu = new InventoryUI(CC.YELLOW + CC.B + "Symbols Prefix", true, 4);
    private final InventoryUI textPrefixMenu = new InventoryUI(CC.YELLOW + CC.B + "Text Prefix", true, 4);

    public InventoryUI getMenu(Player player) {
        for (int i = 0; i < 9 * 3; i++) {
            this.menu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }

        for (int i = 0; i < 9 * 4; i++) {
            this.countriesPrefixMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
            this.symbolPrefixMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
            this.textPrefixMenu.setItem(i, new InventoryUI.EmptyClickableItem(PLACEHOLDER_ITEM));
        }
        
        this.menu.setItem(11, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.GRASS)
                .name(CC.GREEN + CC.B + "Countries Prefix")
                .lore("")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(countriesPrefixMenu.getCurrentPage());
            }
        });

        this.menu.setItem(13, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.NAME_TAG)
                .name(CC.GREEN + CC.B + "Symbol Prefix")
                .lore("")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(symbolPrefixMenu.getCurrentPage());
            }
        });

        this.menu.setItem(15, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.PAPER)
                .name(CC.GREEN + CC.B + "Text Prefix")
                .lore("")
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(textPrefixMenu.getCurrentPage());
            }
        });


        this.countriesPrefixMenu.setItem(35, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.INK_SACK)
                .name(CC.RED + "Go back")
                .durability(1)
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(menu.getCurrentPage());
            }
        });

        this.symbolPrefixMenu.setItem(35, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.INK_SACK)
                .name(CC.RED + "Go back")
                .durability(1)
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(menu.getCurrentPage());
            }
        });

        this.textPrefixMenu.setItem(35, new InventoryUI.AbstractClickableItem(new ItemBuilder(Material.INK_SACK)
                .name(CC.RED + "Go back")
                .durability(1)
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(menu.getCurrentPage());
            }
        });


        int count = 0;
        for (CountriesPrefix prefix : CountriesPrefix.values()) {
            CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            String c = profile.getCustomColor() != null && !profile.getCustomColor().isEmpty() && profile.hasRank(Rank.BASIC) ? profile.getCustomColor() : profile.getGrant().getRank().getColor();

            countriesPrefixMenu.setItem(count, new InventoryUI.AbstractClickableItem(
                    new ItemBuilder(Material.NAME_TAG).name(prefix.getPrefix())
                            .lore(CC.GREEN + CC.B + "Shows up as:")
                            .lore(prefix.getPrefix() + " " + profile.getGrant().getRank().getRawFormat() + c + player.getName() + CC.WHITE + ": Hello!")
                            .lore("")
                            .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                    if (profile == null) {
                        player.closeInventory();
                        return;
                    }

                    if (/*!profile.isBoughtSymbols() &&*/ !profile.hasRank(prefix.getRank())) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot choose this prefix with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://store.prac.lol/ to choose prefix.");
                        player.sendMessage("");
                        return;
                    }

                    player.closeInventory();

                    profile.setCustomPrefix(prefix.getPrefix());
                    player.sendMessage(CC.GREEN + CC.B + "You have been selected " + CC.YELLOW + prefix.getPrefix());
                }
            });
            count++;
        }

        this.countriesPrefixMenu.setItem(26, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FIREBALL, CC.RED + "Clear Prefix")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (profile == null) {
                    player.closeInventory();
                    return;
                }

                player.closeInventory();

                profile.setCustomPrefix("");

                player.sendMessage(CC.GREEN + CC.B + "Your prefix has been updated to " + "Clear Prefix" + CC.GREEN + CC.B + ".");
            }
        });


        int count2 = 0;
        for (SymbolPrefix prefix : SymbolPrefix.values()) {
            CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
            String c = profile.getCustomColor() != null && !profile.getCustomColor().isEmpty() && profile.hasRank(Rank.BASIC) ? profile.getCustomColor() : profile.getGrant().getRank().getColor();
            String p = profile.getCustomPrefix().equals("") ? "" : profile.getCustomPrefix() + " ";

            symbolPrefixMenu.setItem(count2, new InventoryUI.AbstractClickableItem(
                    new ItemBuilder(Material.PAPER).name(prefix.getPrefix())
                            .lore(CC.GREEN + CC.B + "Shows up as:")
                            .lore(p + profile.getGrant().getRank().getRawFormat() + c + player.getName() + prefix.getPrefix() + CC.WHITE + ": Hello!")
                            .lore("")
                            .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                    if (profile == null) {
                        player.closeInventory();
                        return;
                    }

                    if (/*!profile.isBoughtSymbols() &&*/ !profile.hasRank(prefix.getRank())) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot choose this prefix with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://store.prac.lol/ to choose prefix.");
                        player.sendMessage("");
                        return;
                    }

                    player.closeInventory();

                    profile.setCustomSuffix(prefix.getPrefix());
                    player.sendMessage(CC.GREEN + CC.B + "You have been selected " + CC.YELLOW + prefix.getPrefix());
                }
            });
            count2++;
        }

        this.symbolPrefixMenu.setItem(26, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FIREBALL, CC.RED + "Clear Prefix")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (profile == null) {
                    player.closeInventory();
                    return;
                }

                player.closeInventory();

                profile.setCustomSuffix("");

                player.sendMessage(CC.GREEN + CC.B + "Your prefix has been updated to " + "Clear Prefix" + CC.GREEN + CC.B + ".");
            }
        });

        int count3 = 0;
        for (TextSymbolPrefix prefix : TextSymbolPrefix.values()) {
            CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());
            String c = profile.getCustomColor() != null && !profile.getCustomColor().isEmpty() && profile.hasRank(Rank.BASIC) ? profile.getCustomColor() : profile.getGrant().getRank().getColor();
            String p = profile.getCustomPrefix().equals("") ? "" : profile.getCustomPrefix() + " ";

            textPrefixMenu.setItem(count3, new InventoryUI.AbstractClickableItem(
                    new ItemBuilder(Material.PAPER).name(prefix.getPrefix())
                            .lore(CC.GREEN + CC.B + "Shows up as:")
                            .lore(p + profile.getGrant().getRank().getRawFormat() + c + player.getName() + prefix.getPrefix() + CC.WHITE + ": Hello!")
                            .lore("")
                            .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                    if (profile == null) {
                        player.closeInventory();
                        return;
                    }

                    if (/*!profile.isBoughtSymbols() &&*/ !profile.hasRank(prefix.getRank())) {
                        player.sendMessage("");
                        player.sendMessage(CC.RED + "You cannot choose this prefix with " + profile.getRank().getColor() + profile.getRank().getName() + CC.RED + " rank.");
                        player.sendMessage(CC.GRAY + "Purchase rank at https://store.prac.lol/ to choose prefix.");
                        player.sendMessage("");
                        return;
                    }

                    player.closeInventory();

                    profile.setCustomSuffix(prefix.getPrefix());
                    player.sendMessage(CC.GREEN + CC.B + "You have been selected " + CC.YELLOW + prefix.getPrefix());
                }
            });
            count3++;
        }

        this.textPrefixMenu.setItem(26, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FIREBALL, CC.RED + "Clear Prefix")) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (profile == null) {
                    player.closeInventory();
                    return;
                }

                player.closeInventory();

                profile.setCustomSuffix("");

                player.sendMessage(CC.GREEN + CC.B + "Your prefix has been updated to " + "Clear Prefix" + CC.GREEN + CC.B + ".");
            }
        });


        return menu;
    }
}
