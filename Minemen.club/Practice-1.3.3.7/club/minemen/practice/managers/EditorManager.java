// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.util.PlayerUtil;
import club.minemen.practice.kit.Kit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import club.minemen.practice.kit.PlayerKit;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.Practice;

public class EditorManager
{
    private final Practice plugin;
    private final Map<UUID, String> editing;
    private final Map<UUID, PlayerKit> renaming;
    
    public EditorManager() {
        this.plugin = Practice.getInstance();
        this.editing = new HashMap<UUID, String>();
        this.renaming = new HashMap<UUID, PlayerKit>();
    }
    
    public void addEditor(final Player player, final Kit kit) {
        this.editing.put(player.getUniqueId(), kit.getName());
        this.plugin.getInventoryManager().addEditingKitInventory(player, kit);
        PlayerUtil.clearPlayer(player);
        player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
        player.getInventory().setContents(kit.getContents());
        player.sendMessage(CC.PRIMARY + "Now editing kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ". Armor will be applied automatically in the kit.");
    }
    
    public void removeEditor(final UUID editor) {
        this.renaming.remove(editor);
        this.editing.remove(editor);
        this.plugin.getInventoryManager().removeEditingKitInventory(editor);
    }
    
    public String getEditingKit(final UUID editor) {
        return this.editing.get(editor);
    }
    
    public void addRenamingKit(final UUID uuid, final PlayerKit playerKit) {
        this.renaming.put(uuid, playerKit);
    }
    
    public void removeRenamingKit(final UUID uuid) {
        this.renaming.remove(uuid);
    }
    
    public PlayerKit getRenamingKit(final UUID uuid) {
        return this.renaming.get(uuid);
    }
}
