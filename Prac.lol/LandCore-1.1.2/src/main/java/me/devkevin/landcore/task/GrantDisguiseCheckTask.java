package me.devkevin.landcore.task;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.nick.api.NickAPI;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/02/2023 @ 12:12
 * GrantDisguiseCheckTask / me.devkevin.landcore.task / LandCore
 */
public class GrantDisguiseCheckTask extends BukkitRunnable {

    public GrantDisguiseCheckTask() {
        this.runTaskTimerAsynchronously(LandCore.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (NickAPI.isNicked(online)) {
                InternalNametag.reloadPlayer(online);
                InternalNametag.reloadOthersFor(online);

                NickAPI.refreshPlayerSync(online);
            }
        }
    }
}
