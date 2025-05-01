package net.badlion.uhc.util;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageHelperUtil {

    public static void messagePlayerIfOnline(UUID uuid, String msg) {
        Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(msg);
        }
    }
}
