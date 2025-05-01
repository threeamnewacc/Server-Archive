package net.badlion.capturetheflag;


import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CTFWorld extends MPGWorld {

	private CTFGame.CTFGamemode ctfGamemode;

    private HashMap<CTFTeam, Location> flagLocations = new HashMap<>();
    private HashMap<CTFTeam, Location[]> captureRegions = new HashMap<>();
	private HashMap<CTFTeam, Integer> flagPositions = new HashMap<>();

    public CTFWorld(GWorld world){
        super(world);
    }

    @Override
    public void load() {
        super.load();

		// TODO: ??? NOT SURE IF WE STILL WANT SOME OF THESE VALUES IN THE CONFIG


	    // Get gamemode type

		try {
			String gamemode = this.gWorld.getYml().getString("game_mode").toUpperCase();
			this.ctfGamemode = CTFGame.CTFGamemode.valueOf(gamemode);
		} catch (IllegalArgumentException e)  {
			throw new RuntimeException("Error loading CTFWorld. Invalid gamemode specified for CTFWorld " + this.gWorld.getInternalName());
		}

	    // Get team colors
	    for (String color : Arrays.asList(ChatColor.RED.name(), ChatColor.BLUE.name(), ChatColor.GREEN.name(), ChatColor.YELLOW.name())) {
		    // Initialize our CTFTeams here, spawn locations for each team should be loaded
		    CTFTeam team = new CTFTeam(ChatColor.valueOf(color.toUpperCase()));
		    MPGTeamManager.storeTeam(team);

		    this.flagLocations.put(team, Gberry.parseLocation(this.gWorld.getYml().getString(color + "_flag_location")));
			this.flagPositions.put(team, this.gWorld.getYml().getInt(color + "_flag_position"));

			team.setFlagState(CTFTeam.FlagState.BASE);
			team.setFlagLocation(flagLocations.get(team));
			team.placeFlag();

		    // Get the capture region locations for this team
		    List<String> captureRegionStrings = this.gWorld.getYml().getStringList(color + "_capture_region");

		    // Validate correct number of regions
		    if (captureRegionStrings.size() != 2) {
			    throw new RuntimeException("Too many regions specified for " + color + "_capture_region");
		    }

		    // Parse all regions
		    Location[] captureRegion = new Location[2];
		    for (int i = 0; i < captureRegionStrings.size(); i++) {
			    captureRegion[i] = Gberry.parseLocation(captureRegionStrings.get(i));
		    }

		    this.captureRegions.put(team, captureRegion);
	    }
    }

    public CTFGame.CTFGamemode getCTFGamemode() {
        return ctfGamemode;
    }


	public Location getFlagLocation(CTFTeam team) {
		return this.flagLocations.get(team);
	}

	public Collection<Location> getAllFlagLocations() {
		return this.flagLocations.values();
	}

	public Location[] getCaptureRegion(CTFTeam ctfTeam) {
		return this.captureRegions.get(ctfTeam);
	}

	public int getFlagPosition(CTFTeam team) {
		return this.flagPositions.get(team);
	}

}
