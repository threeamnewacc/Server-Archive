package us.zonix.practice.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.kit.PlayerKit;
import us.zonix.practice.util.PlayerUtil;

public class EditorManager {
    private final Practice plugin = Practice.getInstance();
    private final Map<UUID, String> editing = new HashMap<>();
    private final Map<UUID, PlayerKit> renaming = new HashMap<>();

    public void addEditor(Player player, Kit kit) {
        this.editing.put(player.getUniqueId(), kit.getName());
        this.plugin.getInventoryManager().addEditingKitInventory(player, kit);
        PlayerUtil.clearPlayer(player);
        player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
        player.getInventory().setContents(kit.getContents());
        player.sendMessage(ChatColor.GREEN + "You are editing kit " + ChatColor.YELLOW + kit.getName() + ChatColor.GREEN + ".");
    }

    public void removeEditor(UUID editor) {
        this.renaming.remove(editor);
        this.editing.remove(editor);
        this.plugin.getInventoryManager().removeEditingKitInventory(editor);
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
