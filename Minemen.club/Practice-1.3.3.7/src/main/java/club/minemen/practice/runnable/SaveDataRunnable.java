package club.minemen.practice.runnable;

import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SaveDataRunnable implements Runnable {

	private final Practice plugin = Practice.getInstance();

	@Override
	public void run() {
		for (PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
			this.plugin.getPlayerManager().saveData(playerData);
		}
	}

}
