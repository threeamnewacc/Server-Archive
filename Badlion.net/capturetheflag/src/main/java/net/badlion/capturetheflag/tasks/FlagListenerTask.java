package net.badlion.capturetheflag.tasks;

import net.badlion.capturetheflag.CTF;
import net.badlion.capturetheflag.CTFGame;
import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.CTFTeam;
import net.badlion.capturetheflag.manager.FlagManager;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagListenerTask extends BukkitRunnable {

    public void run() {
        if (CTF.getInstance().getCTFGame().getGameState().ordinal() < MPGGame.GameState.GAME.ordinal()) {
            return;
        }
        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
            CTFPlayer ctfPlayer = (CTFPlayer) mpgPlayer;
            for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
                CTFTeam ctfTeam = (CTFTeam) mpgTeam;
                if (mpgPlayer.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                    if (ctfTeam == ctfPlayer.getTeam() && ctfTeam.isInBaseRegion(ctfPlayer.getPlayer())) {
                        if (ctfPlayer.isCarryingFlag() && CTF.getInstance().getCTFGame().getCTFGamemode() == CTFGame.CTFGamemode.CAPTURE) {
                            FlagManager.deliverFlag(ctfPlayer, ctfPlayer.getFlagTeam());
                            ctfPlayer.setCarryingFlag(null);
                            return;
                        }
                    } else {
                        if (ctfPlayer.getTeam() != ctfTeam && !ctfPlayer.isCarryingFlag() && ctfTeam.isInCaptureRegion(ctfPlayer.getPlayer())) {
                            ctfPlayer.setCarryingFlag(ctfTeam);
                            FlagManager.pickUpFlag(ctfPlayer, ctfTeam);
                        }
                    }
                }
            }
        }
    }

}
