package net.badlion.smellychat.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.commands.ReportCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BungeeCordListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

		try {
			String subChannel = in.readUTF();
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			if (subChannel.equals("SmellyChat")) {
				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String msg = msgin.readUTF();
				String globalMessage = msgin.readUTF();

				if (msg.startsWith("Report")) {
					// Don't show lobby reports for now
					if (Gberry.serverName.toLowerCase().contains("bllobby")) {
						return;
					}

					String reportMessagePermission = msg.substring(6);
					for (Player pl : SmellyChat.getInstance().getMods()) {
						if (pl.hasPermission(reportMessagePermission) || ReportCommand.ACCEPTING_ALL_REPORTS.contains(pl.getUniqueId())) {
							if (!ReportCommand.REPORTS_DISABLED.contains(pl.getUniqueId())) {
								pl.sendMessage(globalMessage);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// There was an issue in creating the subchannel string
			e.printStackTrace();
		}
	}

}
