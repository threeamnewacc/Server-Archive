package us.zonix.core.server;

import lombok.Getter;
import lombok.Setter;
import us.zonix.core.util.CC;

@Getter
@Setter
public class ServerData {

	private String serverName;
	private ServerType serverType;
	private ServerState serverState;
	private int onlinePlayers;
	private int maxPlayers;
	private long lastUpdate;
	private boolean whitelisted;

	public String getStatus() {
		if (this.serverType == ServerType.SURVIVAL_GAMES) {
			if (this.serverState == ServerState.SETUP) {
				return CC.RED + "Setup";
			} else if (this.serverState == ServerState.INACTIVE) {
				return CC.YELLOW + "Lobby";
			} else {
				return CC.GREEN + "Playing";
			}
		} else {
			return this.whitelisted ? CC.YELLOW + "Whitelisted" : CC.GREEN + "Online";
		}
	}

}
