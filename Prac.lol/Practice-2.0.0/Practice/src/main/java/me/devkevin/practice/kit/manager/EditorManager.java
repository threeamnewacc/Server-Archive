package me.devkevin.practice.kit.manager;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 17/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class EditorManager {

    private final Practice plugin = Practice.getInstance();
    private final Map<UUID, String> editing = new HashMap<>();
    private final Map<UUID, PlayerKit> renaming = new HashMap<>();

    public void addEditor(Player player, Kit kit) {
        this.editing.put(player.getUniqueId(), kit.getName());
        this.plugin.getKitEditorMenu().addEditingKitInventory(player, kit);

        PlayerUtil.reset(player);
        player.teleport(this.plugin.getCustomLocationManager().getEditor().toBukkitLocation());

        player.getInventory().setArmorContents(kit.getArmor());
        player.getInventory().setContents(kit.getContents());
        player.sendMessage(CC.YELLOW + "You are editing kit " + CC.GOLD + kit.getName() + CC.YELLOW + ". Armor will be applied automatically in the kit.");
    }

    public void removeEditor(UUID editor) {
        this.renaming.remove(editor);
        this.editing.remove(editor);
        this.plugin.getKitEditorMenu().removeEditingKitInventory(editor);
    }

    public String getEditingKit(UUID editor) {
        return this.editing.get(editor);
    }

    public void addRenamingKit(UUID uuid, PlayerKit playerKit) {
        this.renaming.put(uuid, playerKit);
    }

    public void removeRenamingKit(UUID uuid) {
        this.renaming.remove(uuid);
    }

    public PlayerKit getRenamingKit(UUID uuid) {
        return this.renaming.get(uuid);
    }

}
