package net.badlion.tournament.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoloTeam extends DefaultTeam {

    public SoloTeam(UUID leader) {
        super(leader);
    }

    public SoloTeam(String name, UUID leader) {
        super(name, leader);
    }

    @Override
    public String getType() {
        return "solo";
    }

    @Override
    public SoloTeam clone() {
        return new SoloTeam(this.getName(), this.getLeader());
    }

    @Override
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(getLeader());
        if (player != null) {
            player.sendMessage(message);
        }
    }
}
