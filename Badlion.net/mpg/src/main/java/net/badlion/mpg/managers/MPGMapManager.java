package net.badlion.mpg.managers;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.bukkitevents.MapManagerInitializeEvent;
import net.badlion.mpg.commands.VoteCommand;

import java.util.*;

public class MPGMapManager {

    private static List<MPGWorld> worlds = new ArrayList<>();

    public static void initialize() {
        MapManagerInitializeEvent event = new MapManagerInitializeEvent();
        MPG.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.getWorlds().size() == 0) {
            throw new RuntimeException("No maps configured even though they are being used");
        }

        MPGMapManager.worlds.addAll(event.getWorlds());

	    // Add all the worlds to the map voting pool
	    if (MPG.USES_VOTING && MPG.getInstance().getConfigOption(MPG.ConfigFlag.VOTE_TYPE) == VoteCommand.VoteType.MAP) {
		    for (MPGWorld world : MPGMapManager.worlds) {
			    MPG.VOTE_OBJECTS.add(world);
		    }
	    }
    }

    public static MPGWorld getMPGWorldFromName(String worldName) {
        for (MPGWorld mpgWorld : MPGMapManager.worlds) {
            if (mpgWorld.getGWorld().getInternalName().equalsIgnoreCase(worldName)) {
                return mpgWorld;
            }
        }

        return null;
    }

    public static MPGWorld getRandomWorld() {
        Collections.shuffle(MPGMapManager.worlds);

        return MPGMapManager.worlds.get(0);
    }

}
