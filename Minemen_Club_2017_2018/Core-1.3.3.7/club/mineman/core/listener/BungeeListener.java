// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.listener;

import java.beans.ConstructorProperties;
import com.google.common.io.ByteArrayDataInput;
import org.bukkit.event.Event;
import club.mineman.core.event.bungee.BungeeReceivedEvent;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import club.mineman.core.CorePlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener
{
    private final CorePlugin plugin;
    
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        try {
            final ByteArrayDataInput in = ByteStreams.newDataInput(message);
            final String subChannel = in.readUTF();
            final short len = in.readShort();
            final byte[] messageBytes = new byte[len];
            in.readFully(messageBytes);
            final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(messageBytes));
            final String data = dis.readUTF();
            final Long systemTime = Long.parseLong(data.split(":")[0]);
            final BungeeReceivedEvent event = new BungeeReceivedEvent(player, subChannel, data.replace(systemTime + ":", ""), message, systemTime > System.currentTimeMillis());
            this.plugin.getServer().getPluginManager().callEvent((Event)event);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public BungeeListener(final CorePlugin plugin) {
        this.plugin = plugin;
    }
}
