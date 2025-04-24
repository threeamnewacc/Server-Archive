package me.devkevin.practice.location.manager;

import lombok.Data;
import me.devkevin.practice.Practice;
import me.devkevin.practice.location.CustomLocation;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Data
public class CustomLocationManager {

    private static Practice plugin = Practice.getInstance();

    // spawns related
    private CustomLocation spawn;
    private CustomLocation spawnMin;
    private CustomLocation spawnMax;

    private CustomLocation editor;

    // Sumo spawns related
    private CustomLocation sumoLocation;
    private CustomLocation sumoFirst;
    private CustomLocation sumoSecond;
    private CustomLocation sumoMin;
    private CustomLocation sumoMax;

    private CustomLocation oitcLocation;
    private List<CustomLocation> oitcSpawnpoints;
    private CustomLocation oitcMin;
    private CustomLocation oitcMax;

    private CustomLocation tnttagLocation;
    private CustomLocation tnttagGameLocation;
    private CustomLocation tnttagMin;
    private CustomLocation tnttagMax;

    private CustomLocation tntLocation;
    private CustomLocation tntMin;
    private CustomLocation tntMax;

    private CustomLocation lmsLocation;

    private CustomLocation npc1;
    private CustomLocation npc2;
    private CustomLocation npc3;

    private CustomLocation npc1Name;
    private CustomLocation npc2Name;
    private CustomLocation npc3Name;

    private CustomLocation holoLeaderboardsLocation, holoWinstreakLocation;

    private List<CustomLocation> lmsLocations = new ArrayList<>();

    public CustomLocationManager() {
        this.oitcSpawnpoints = new ArrayList<>();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration file = plugin.getMainConfig().getConfig();

        if (file.contains("spawn")) {
            spawn = CustomLocation.stringToLocation(file.getString("spawn"));
        }

        if (file.contains("spawnMax")) {
            spawnMax = CustomLocation.stringToLocation(file.getString("spawnMax"));
        }

        if (file.contains("spawnMin")) {
            spawnMin = CustomLocation.stringToLocation(file.getString("spawnMin"));
        }

        if (file.contains("npc1")) {
            npc1 = CustomLocation.stringToLocation(file.getString("npc1"));
        }

        if (file.contains("npc1Name"))
            npc1Name = CustomLocation.stringToLocation(file.getString("npc1Name"));

        if (file.contains("npc2Name"))
            npc2Name = CustomLocation.stringToLocation(file.getString("npc2Name"));

        if (file.contains("npc3Name"))
            npc3Name = CustomLocation.stringToLocation(file.getString("npc3Name"));

        if (file.contains("npc2")) {
            npc2 = CustomLocation.stringToLocation(file.getString("npc2"));
        }

        if (file.contains("npc3")) {
            npc3 = CustomLocation.stringToLocation(file.getString("npc3"));
        }

        if (file.contains("holoLeaderboardsLocation")) {
            this.holoLeaderboardsLocation = CustomLocation.stringToLocation(file.getString("holoLeaderboardsLocation"));
        }

        if (file.contains("winstreakHoloLocation")) {
            this.holoWinstreakLocation = CustomLocation.stringToLocation(file.getString("winstreakHoloLocation"));
        }

        if (file.contains("editor")) {
            editor = CustomLocation.stringToLocation(file.getString("editor"));
        }

        if (file.contains("sumoLocation")) {
            sumoLocation = CustomLocation.stringToLocation(file.getString("sumoLocation"));
            sumoMin = CustomLocation.stringToLocation(file.getString("sumoMin"));
            sumoMax = CustomLocation.stringToLocation(file.getString("sumoMax"));
            sumoFirst = CustomLocation.stringToLocation(file.getString("sumoFirst"));
            sumoSecond = CustomLocation.stringToLocation(file.getString("sumoSecond"));
        }

        if (file.contains("oitcLocation")) {
            oitcLocation = CustomLocation.stringToLocation(file.getString("oitcLocation"));
            oitcMin = CustomLocation.stringToLocation(file.getString("oitcMin"));
            oitcMax = CustomLocation.stringToLocation(file.getString("oitcMax"));

            for (String spawnpoint : file.getStringList("oitcSpawnpoints")) {
                oitcSpawnpoints.add(CustomLocation.stringToLocation(spawnpoint));
            }
        }

        if (file.contains("lmsLocation")) {
            this.lmsLocation = CustomLocation.stringToLocation(file.getString("lmsLocation"));


            file.getStringList("lmsSpawnpoints").forEach(point ->
                    lmsLocations.add(CustomLocation.stringToLocation(point)));
        }

        if (file.contains("tntrunlocation")) {
            tntLocation = CustomLocation.stringToLocation(file.getString("tntrunlocation"));
        }

        if (file.contains("tntrunMin")) {
            tntMin = CustomLocation.stringToLocation(file.getString("tntrunMin"));
        }

        if (file.contains("tntrunMax")) {
            tntMax = CustomLocation.stringToLocation(file.getString("tntrunMax"));
        }

        if (file.contains("taglocation")) {
            tnttagLocation =  CustomLocation.stringToLocation(file.getString("taglocation"));
        }
        if (file.contains("tagGameLocation")) {
            tnttagGameLocation =  CustomLocation.stringToLocation(file.getString("tagGameLocation"));
        }
        if (file.contains("tagmin")){
            tnttagMin =  CustomLocation.stringToLocation(file.getString("tagmin"));
        }
        if (file.contains("tagmax")){
            tnttagMax =  CustomLocation.stringToLocation(file.getString("tagmax"));
        }
    }

    public void saveConfig() {
        FileConfiguration file = plugin.getMainConfig().getConfig();

        // Main spawn location
        if (spawn != null)
            file.set("spawn", CustomLocation.locationToString(spawn));
        if (spawnMin != null)
            file.set("spawnMin", CustomLocation.locationToString(spawnMin));
        if (spawnMax != null)
            file.set("spawnMax", CustomLocation.locationToString(spawnMax));

        if (npc1 != null)
            file.set("npc1", CustomLocation.locationToString(npc1));

        if (npc2 != null)
            file.set("npc2", CustomLocation.locationToString(npc2));

        if (npc3 != null)
            file.set("npc3", CustomLocation.locationToString(npc3));

        if (npc1Name != null)
            file.set("npc1Name", CustomLocation.locationToString(npc1Name));

        if (npc2Name != null)
            file.set("npc2Name", CustomLocation.locationToString(npc2Name));

        if (npc3Name != null)
            file.set("npc3Name", CustomLocation.locationToString(npc3Name));

        if (holoLeaderboardsLocation != null)
            file.set("holoLeaderboardsLocation", CustomLocation.locationToString(holoLeaderboardsLocation));

        if (holoWinstreakLocation != null)
            file.set("winstreakHoloLocation", CustomLocation.locationToString(holoWinstreakLocation));

        // Editor spawn location
        if (editor != null)
            file.set("editor", CustomLocation.locationToString(editor));

        if (sumoLocation != null)
            file.set("sumoLocation", CustomLocation.locationToString(sumoLocation));
        if (sumoMin != null)
            file.set("sumoMin", CustomLocation.locationToString(sumoMin));
        if (sumoMax != null)
            file.set("sumoMax", CustomLocation.locationToString(sumoMax));
        if (sumoFirst != null)
            file.set("sumoFirst", CustomLocation.locationToString(sumoFirst));
        if (sumoSecond != null)
            file.set("sumoSecond", CustomLocation.locationToString(sumoSecond));

        if (oitcLocation != null)
            file.set("oitcLocation", CustomLocation.locationToString(oitcLocation));
        if (oitcMin != null)
            file.set("oitcMin", CustomLocation.locationToString(oitcMin));
        if (oitcMax != null)
            file.set("oitcMax", CustomLocation.locationToString(oitcMax));
        if (oitcSpawnpoints != null)
            file.set("oitcSpawnpoints", this.fromLocations(oitcSpawnpoints));

        if (tnttagLocation != null)
            file.set("taglocation", CustomLocation.locationToString(tnttagLocation));
        if (tnttagGameLocation != null)
            file.set("tagGameLocation", CustomLocation.locationToString(tnttagGameLocation));
        if (tnttagMax != null)
            file.set("tagmax", CustomLocation.locationToString(tnttagMax));
        if (tnttagMin != null)
            file.set("tagmin", CustomLocation.locationToString(tnttagMin));

        if (tntLocation != null)
            file.set("tntrunlocation", CustomLocation.locationToString(tntLocation));
        if (tntMin != null)
            file.set("tntrunMin", CustomLocation.locationToString(tntMin));
        if (tntMax != null)
            file.set("tntrunMax", CustomLocation.locationToString(tntMax));

        if(lmsLocation != null)
            file.set("lmsLocation", CustomLocation.locationToString(this.lmsLocation));
        if(lmsLocations.size() > 0)
            file.set("lmsSpawnpoints", fromLocations(lmsLocations));

        // save the config
        plugin.getMainConfig().save();
    }

    private List<String> fromLocations(List<CustomLocation> locations) {

        List<String> toReturn = new ArrayList<>();
        for(CustomLocation location : locations) {
            toReturn.add(CustomLocation.locationToString(location));
        }

        return toReturn;
    }
}
