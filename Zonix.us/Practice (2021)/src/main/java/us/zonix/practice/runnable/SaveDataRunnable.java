package us.zonix.practice.runnable;

import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;

public class SaveDataRunnable implements Runnable {
    private final Practice plugin = Practice.getInstance();

    @Override
    public void run() {
        for (PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
            this.plugin.getPlayerManager().saveData(playerData);
        }
    }
}
