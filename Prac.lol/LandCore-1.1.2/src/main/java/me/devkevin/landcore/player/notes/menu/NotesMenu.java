package me.devkevin.landcore.player.notes.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.notes.Note;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.pagination.PaginatedMenu;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.time.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:51
 * NotesMenu / me.devkevin.landcore.player.notes.menu / LandCore
 */
@AllArgsConstructor
public class NotesMenu extends PaginatedMenu {
    private CoreProfile coreProfile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.PRIMARY + "Notes of " + coreProfile.getGrant().getRank().getColor() + coreProfile.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Note note : coreProfile.getNotes()) {
            buttons.put(buttons.size(), new NoteButton(note));
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class NoteButton extends Button {
        private Note note;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name(CC.translate("&eNote ID&7:&f " + note.getId()))
                    .lore(CC.translate("&eCreate by&7:&f " + note.getCreateBy()))
                    .lore(CC.translate("&eNote&7:&f " + note.getNote()))
                    .lore(CC.translate("&eCreate at&7:&f " + TimeUtil.dateToString(new Date(note.getCreateAt()), "&7")))
                    .build();
        }
    }
}
