package cc.patrone.practice.managers;

import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.editor.EditorData;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.kit.PlayerKit;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.utils.PlayerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class EditorManager {
	private final PracticePlugin plugin;

	public void startEditing(Player player, PracticeProfile profile, Kit kit) {
		profile.setPlayerState(PlayerState.EDITING);
		profile.setEditorData(new EditorData(kit, new PlayerKit(kit.getName())));

		zone.potion.utils.player.PlayerUtil.clearPlayer(player);

		kit.apply(player, false);

		player.teleport(plugin.getLocationManager().getKitEditor());
		player.sendMessage(CC.PRIMARY + "Now editing kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + ".");
	}

	public void stopEditing(Player player, PracticeProfile profile) {
		profile.getEditorData().unregisterMenu(plugin);
		profile.setEditorData(null);

		zone.potion.utils.player.PlayerUtil.clearPlayer(player);
		PlayerUtil.toggleFlyFor(player);

		plugin.getPlayerManager().resetPlayerMinimally(player, profile, true);

		player.sendMessage(CC.GREEN + "Finished editing kit.");
	}
}
