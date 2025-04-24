// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.plugin.Plugin;
import club.mineman.core.CorePlugin;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

public final class BungeeUtil
{
    private BungeeUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static void sendMessage(final Player source, final String target, final String message) {
        Validate.notNull((Object)source, target, new Object[] { message, "Input values cannot be null!" });
        try {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(target);
            out.writeUTF(message);
            source.sendPluginMessage((Plugin)CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void kickPlayer(final Player source, final String target, final String reason) {
        Validate.notNull((Object)source, target, new Object[] { reason, "Input values cannot be null!" });
        try {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(target);
            out.writeUTF(reason);
            source.sendPluginMessage((Plugin)CorePlugin.getInstance(), "BungeeCord", out.toByteArray());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
