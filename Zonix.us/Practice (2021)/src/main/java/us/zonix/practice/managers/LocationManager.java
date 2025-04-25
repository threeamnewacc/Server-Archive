package us.zonix.practice.managers;

import java.util.ArrayList;
import java.util.List;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.file.ConfigFile;

public class LocationManager {
    private final Practice plugin = Practice.getInstance();
    private ConfigFile spawnsFile;
    private CustomLocation spawnLocation;
    private CustomLocation spawnMin;
    private CustomLocation spawnMax;
    private CustomLocation editorLocation;
    private CustomLocation editorMin;
    private CustomLocation editorMax;
    private CustomLocation ffaLocation;
    private CustomLocation ffaMax;
    private CustomLocation ffaMin;
    private CustomLocation sumoLocation;
    private CustomLocation sumoFirst;
    private CustomLocation sumoSecond;
    private CustomLocation sumoMin;
    private CustomLocation sumoMax;
    private CustomLocation oitcLocation;
    private List<CustomLocation> oitcSpawnpoints = new ArrayList<>();
    private CustomLocation oitcMin;
    private CustomLocation oitcMax;
    private CustomLocation parkourLocation;
    private CustomLocation parkourGameLocation;
    private CustomLocation parkourMin;
    private CustomLocation parkourMax;
    private CustomLocation redroverLocation;
    private CustomLocation redroverFirst;
    private CustomLocation redroverSecond;
    private CustomLocation redroverMin;
    private CustomLocation redroverMax;
    private CustomLocation waterDropLocation;
    private CustomLocation waterDropJump;
    private CustomLocation waterDropMin;
    private CustomLocation waterDropMax;
    private CustomLocation waterDropFirst;
    private CustomLocation waterDropSecond;
    private CustomLocation woolLocation;
    private CustomLocation woolCenter;
    private CustomLocation woolMin;
    private CustomLocation woolMax;
    private CustomLocation lightsLocation;
    private CustomLocation lightsStart;
    private CustomLocation lightsMin;
    private CustomLocation lightsMax;
    private CustomLocation tntTagSpawn;
    private CustomLocation tntTagLocation;

    public LocationManager() {
        this.spawnsFile = new ConfigFile(this.plugin, "locations");
        this.loadConfig();
    }

    private void loadConfig() {
        if (this.spawnsFile.getConfiguration().contains("SPAWN.LOCATION")) {
            this.spawnLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SPAWN.LOCATION"));
            this.spawnMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SPAWN.MIN"));
            this.spawnMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SPAWN.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("EDITOR.LOCATION")) {
            this.editorLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("EDITOR.LOCATION"));
            this.editorMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("EDITOR.MIN"));
            this.editorMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("EDITOR.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("FFA.LOCATION")) {
            this.ffaLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("FFA.LOCATION"));
            this.ffaMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("FFA.MIN"));
            this.ffaMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("FFA.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("SUMO.LOCATION")) {
            this.sumoLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SUMO.LOCATION"));
            this.sumoMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SUMO.MIN"));
            this.sumoMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SUMO.MAX"));
            this.sumoFirst = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SUMO.FIRST"));
            this.sumoSecond = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("SUMO.SECOND"));
        }

        if (this.spawnsFile.getConfiguration().contains("OITC.LOCATION")) {
            this.oitcLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("OITC.LOCATION"));
            this.oitcMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("OITC.MIN"));
            this.oitcMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("OITC.MAX"));

            for (String spawnpoint : this.spawnsFile.getConfiguration().getStringList("OITC.SPAWN_POINTS")) {
                this.oitcSpawnpoints.add(CustomLocation.stringToLocation(spawnpoint));
            }
        }

        if (this.spawnsFile.getConfiguration().contains("REDROVER.LOCATION")) {
            this.redroverLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("REDROVER.LOCATION"));
            this.redroverMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("REDROVER.MIN"));
            this.redroverMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("REDROVER.MAX"));
            this.redroverFirst = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("REDROVER.FIRST"));
            this.redroverSecond = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("REDROVER.SECOND"));
        }

        if (this.spawnsFile.getConfiguration().contains("PARKOUR.LOCATION")) {
            this.parkourLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("PARKOUR.LOCATION"));
            this.parkourGameLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("PARKOUR.GAME_LOCATION"));
            this.parkourMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("PARKOUR.MIN"));
            this.parkourMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("PARKOUR.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("WATERDROP.LOCATION")) {
            this.waterDropLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.LOCATION"));
            this.waterDropJump = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.JUMP_LOCATION"));
            this.waterDropMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.MIN"));
            this.waterDropMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.MAX"));
            this.waterDropFirst = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.FIRST"));
            this.waterDropSecond = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WATERDROP.SECOND"));
        }

        if (this.spawnsFile.getConfiguration().contains("WOOL.LOCATION")) {
            this.woolLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WOOL.LOCATION"));
            this.woolCenter = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WOOL.CENTER"));
            this.woolMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WOOL.MIN"));
            this.woolMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("WOOL.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("LIGHTS.LOCATION")) {
            this.lightsLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("LIGHTS.LOCATION"));
            this.lightsStart = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("LIGHTS.START"));
            this.lightsMin = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("LIGHTS.MIN"));
            this.lightsMax = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("LIGHTS.MAX"));
        }

        if (this.spawnsFile.getConfiguration().contains("TNT_TAG.LOCATION")) {
            this.tntTagLocation = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("TNT_TAG.LOCATION"));
            this.tntTagSpawn = CustomLocation.stringToLocation(this.spawnsFile.getConfiguration().getString("TNT_TAG.SPAWN"));
        }
    }

    public ConfigFile getConfig() {
        return this.spawnsFile;
    }

    public void saveLocationsFile() {
        this.spawnsFile.save();
    }

    public List<String> fromLocations(List<CustomLocation> locations) {
        List<String> toReturn = new ArrayList<>();

        for (CustomLocation location : locations) {
            toReturn.add(CustomLocation.locationToString(location));
        }

        return toReturn;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public ConfigFile getSpawnsFile() {
        return this.spawnsFile;
    }

    public CustomLocation getSpawnLocation() {
        return this.spawnLocation;
    }

    public CustomLocation getSpawnMin() {
        return this.spawnMin;
    }

    public CustomLocation getSpawnMax() {
        return this.spawnMax;
    }

    public CustomLocation getEditorLocation() {
        return this.editorLocation;
    }

    public CustomLocation getEditorMin() {
        return this.editorMin;
    }

    public CustomLocation getEditorMax() {
        return this.editorMax;
    }

    public CustomLocation getFfaLocation() {
        return this.ffaLocation;
    }

    public CustomLocation getFfaMax() {
        return this.ffaMax;
    }

    public CustomLocation getFfaMin() {
        return this.ffaMin;
    }

    public CustomLocation getSumoLocation() {
        return this.sumoLocation;
    }

    public CustomLocation getSumoFirst() {
        return this.sumoFirst;
    }

    public CustomLocation getSumoSecond() {
        return this.sumoSecond;
    }

    public CustomLocation getSumoMin() {
        return this.sumoMin;
    }

    public CustomLocation getSumoMax() {
        return this.sumoMax;
    }

    public CustomLocation getOitcLocation() {
        return this.oitcLocation;
    }

    public List<CustomLocation> getOitcSpawnpoints() {
        return this.oitcSpawnpoints;
    }

    public CustomLocation getOitcMin() {
        return this.oitcMin;
    }

    public CustomLocation getOitcMax() {
        return this.oitcMax;
    }

    public CustomLocation getParkourLocation() {
        return this.parkourLocation;
    }

    public CustomLocation getParkourGameLocation() {
        return this.parkourGameLocation;
    }

    public CustomLocation getParkourMin() {
        return this.parkourMin;
    }

    public CustomLocation getParkourMax() {
        return this.parkourMax;
    }

    public CustomLocation getRedroverLocation() {
        return this.redroverLocation;
    }

    public CustomLocation getRedroverFirst() {
        return this.redroverFirst;
    }

    public CustomLocation getRedroverSecond() {
        return this.redroverSecond;
    }

    public CustomLocation getRedroverMin() {
        return this.redroverMin;
    }

    public CustomLocation getRedroverMax() {
        return this.redroverMax;
    }

    public CustomLocation getWaterDropLocation() {
        return this.waterDropLocation;
    }

    public CustomLocation getWaterDropJump() {
        return this.waterDropJump;
    }

    public CustomLocation getWaterDropMin() {
        return this.waterDropMin;
    }

    public CustomLocation getWaterDropMax() {
        return this.waterDropMax;
    }

    public CustomLocation getWaterDropFirst() {
        return this.waterDropFirst;
    }

    public CustomLocation getWaterDropSecond() {
        return this.waterDropSecond;
    }

    public CustomLocation getWoolLocation() {
        return this.woolLocation;
    }

    public CustomLocation getWoolCenter() {
        return this.woolCenter;
    }

    public CustomLocation getWoolMin() {
        return this.woolMin;
    }

    public CustomLocation getWoolMax() {
        return this.woolMax;
    }

    public CustomLocation getLightsLocation() {
        return this.lightsLocation;
    }

    public CustomLocation getLightsStart() {
        return this.lightsStart;
    }

    public CustomLocation getLightsMin() {
        return this.lightsMin;
    }

    public CustomLocation getLightsMax() {
        return this.lightsMax;
    }

    public CustomLocation getTntTagSpawn() {
        return this.tntTagSpawn;
    }

    public CustomLocation getTntTagLocation() {
        return this.tntTagLocation;
    }

    public void setSpawnsFile(ConfigFile spawnsFile) {
        this.spawnsFile = spawnsFile;
    }

    public void setSpawnLocation(CustomLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setSpawnMin(CustomLocation spawnMin) {
        this.spawnMin = spawnMin;
    }

    public void setSpawnMax(CustomLocation spawnMax) {
        this.spawnMax = spawnMax;
    }

    public void setEditorLocation(CustomLocation editorLocation) {
        this.editorLocation = editorLocation;
    }

    public void setEditorMin(CustomLocation editorMin) {
        this.editorMin = editorMin;
    }

    public void setEditorMax(CustomLocation editorMax) {
        this.editorMax = editorMax;
    }

    public void setFfaLocation(CustomLocation ffaLocation) {
        this.ffaLocation = ffaLocation;
    }

    public void setFfaMax(CustomLocation ffaMax) {
        this.ffaMax = ffaMax;
    }

    public void setFfaMin(CustomLocation ffaMin) {
        this.ffaMin = ffaMin;
    }

    public void setSumoLocation(CustomLocation sumoLocation) {
        this.sumoLocation = sumoLocation;
    }

    public void setSumoFirst(CustomLocation sumoFirst) {
        this.sumoFirst = sumoFirst;
    }

    public void setSumoSecond(CustomLocation sumoSecond) {
        this.sumoSecond = sumoSecond;
    }

    public void setSumoMin(CustomLocation sumoMin) {
        this.sumoMin = sumoMin;
    }

    public void setSumoMax(CustomLocation sumoMax) {
        this.sumoMax = sumoMax;
    }

    public void setOitcLocation(CustomLocation oitcLocation) {
        this.oitcLocation = oitcLocation;
    }

    public void setOitcSpawnpoints(List<CustomLocation> oitcSpawnpoints) {
        this.oitcSpawnpoints = oitcSpawnpoints;
    }

    public void setOitcMin(CustomLocation oitcMin) {
        this.oitcMin = oitcMin;
    }

    public void setOitcMax(CustomLocation oitcMax) {
        this.oitcMax = oitcMax;
    }

    public void setParkourLocation(CustomLocation parkourLocation) {
        this.parkourLocation = parkourLocation;
    }

    public void setParkourGameLocation(CustomLocation parkourGameLocation) {
        this.parkourGameLocation = parkourGameLocation;
    }

    public void setParkourMin(CustomLocation parkourMin) {
        this.parkourMin = parkourMin;
    }

    public void setParkourMax(CustomLocation parkourMax) {
        this.parkourMax = parkourMax;
    }

    public void setRedroverLocation(CustomLocation redroverLocation) {
        this.redroverLocation = redroverLocation;
    }

    public void setRedroverFirst(CustomLocation redroverFirst) {
        this.redroverFirst = redroverFirst;
    }

    public void setRedroverSecond(CustomLocation redroverSecond) {
        this.redroverSecond = redroverSecond;
    }

    public void setRedroverMin(CustomLocation redroverMin) {
        this.redroverMin = redroverMin;
    }

    public void setRedroverMax(CustomLocation redroverMax) {
        this.redroverMax = redroverMax;
    }

    public void setWaterDropLocation(CustomLocation waterDropLocation) {
        this.waterDropLocation = waterDropLocation;
    }

    public void setWaterDropJump(CustomLocation waterDropJump) {
        this.waterDropJump = waterDropJump;
    }

    public void setWaterDropMin(CustomLocation waterDropMin) {
        this.waterDropMin = waterDropMin;
    }

    public void setWaterDropMax(CustomLocation waterDropMax) {
        this.waterDropMax = waterDropMax;
    }

    public void setWaterDropFirst(CustomLocation waterDropFirst) {
        this.waterDropFirst = waterDropFirst;
    }

    public void setWaterDropSecond(CustomLocation waterDropSecond) {
        this.waterDropSecond = waterDropSecond;
    }

    public void setWoolLocation(CustomLocation woolLocation) {
        this.woolLocation = woolLocation;
    }

    public void setWoolCenter(CustomLocation woolCenter) {
        this.woolCenter = woolCenter;
    }

    public void setWoolMin(CustomLocation woolMin) {
        this.woolMin = woolMin;
    }

    public void setWoolMax(CustomLocation woolMax) {
        this.woolMax = woolMax;
    }

    public void setLightsLocation(CustomLocation lightsLocation) {
        this.lightsLocation = lightsLocation;
    }

    public void setLightsStart(CustomLocation lightsStart) {
        this.lightsStart = lightsStart;
    }

    public void setLightsMin(CustomLocation lightsMin) {
        this.lightsMin = lightsMin;
    }

    public void setLightsMax(CustomLocation lightsMax) {
        this.lightsMax = lightsMax;
    }

    public void setTntTagSpawn(CustomLocation tntTagSpawn) {
        this.tntTagSpawn = tntTagSpawn;
    }

    public void setTntTagLocation(CustomLocation tntTagLocation) {
        this.tntTagLocation = tntTagLocation;
    }
}
