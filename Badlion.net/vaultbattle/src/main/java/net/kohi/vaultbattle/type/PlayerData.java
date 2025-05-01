package net.kohi.vaultbattle.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private Team team;

    private boolean spectating = false;
    private boolean pickingTeam = false;

    private List<UUID> allowedPlayers = new ArrayList<>();

    public Team getTeam() {
        return this.team;
    }

    public boolean isSpectating() {
        return this.spectating;
    }

    public boolean isPickingTeam() {
        return this.pickingTeam;
    }

    public List<UUID> getAllowedPlayers() {
        return this.allowedPlayers;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setSpectating(boolean spectating) {
        this.spectating = spectating;
    }

    public void setPickingTeam(boolean pickingTeam) {
        this.pickingTeam = pickingTeam;
    }

    public void setAllowedPlayers(List<UUID> allowedPlayers) {
        this.allowedPlayers = allowedPlayers;
    }
}
