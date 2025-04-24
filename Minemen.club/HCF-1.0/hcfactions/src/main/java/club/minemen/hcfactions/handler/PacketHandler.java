package club.minemen.hcfactions.handler;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FPlayers;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @since 12/3/2017
 */
@RequiredArgsConstructor
public class PacketHandler implements club.minemen.spigot.handler.PacketHandler {

	private static Field UUID_FIELD;

	static {
		try {
			UUID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
			UUID_FIELD.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final HCFactions plugin;

	@Override
	public void handleReceivedPacket(PlayerConnection playerConnection, Packet packet) {

	}

	@Override
	public void handleSentPacket(PlayerConnection playerConnection, Packet packet) {
		if (packet instanceof PacketPlayOutNamedEntitySpawn) {

			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Player other = plugin.getServer().getPlayer((UUID) UUID_FIELD.get(packet));
						if (other != null) {
							FPlayers.getInstance().get(playerConnection.getPlayer()).updatePlayerTeam(other);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(HCFactions.getInstance(), 0L);
		}
	}
}
