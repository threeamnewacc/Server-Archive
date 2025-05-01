package net.badlion.ffa;

import net.badlion.mpg.MPGGame;

public class FFAGame extends MPGGame {

    public FFAGame(FFAWorld ffaWorld) {
	    super(FFA.FFA_GAMEMODE, ffaWorld);

	    this.region.setAllowPlacedBlocks(false);
	    this.region.setAllowBrokenBlocks(false);
    }

	@Override
	public void preGame() {

	}

	@Override
	public void startGame() {
	}

	@Override
	public void preDeathMatch() {

	}

	@Override
	public void deathMatchCountdown() {

	}

	@Override
	public void deathMatch() {

	}

	@Override
	public boolean checkForEndGame() {
		return false;
	}

	@Override
    public FFAWorld getWorld() {
        return (FFAWorld) this.world;
    }

}
