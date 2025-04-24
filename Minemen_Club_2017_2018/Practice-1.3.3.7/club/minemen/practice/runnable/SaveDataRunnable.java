// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.util.Iterator;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.Practice;

public class SaveDataRunnable implements Runnable
{
    private final Practice plugin;
    
    @Override
    public void run() {
        for (final PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
            this.plugin.getPlayerManager().saveData(playerData);
        }
    }
    
    public SaveDataRunnable() {
        this.plugin = Practice.getInstance();
    }
}
