package us.zonix.hcfactions.factions.claims;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.Faction;
import lombok.Getter;
import org.bukkit.*;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.Profile;

import java.util.*;

@Getter
public class Claim {

    private static Set<Claim> claims = new HashSet<>();
    private static Map<String, HashSet<Claim>> chunkClaimMap = new HashMap<>();

    private Faction faction;
    private String worldName;
    private int[] coordinates;
    private Location[] corners;
    private ArrayList<Location> border;

    public Claim(Faction faction, int[] coordinates, String worldName) {
        this.faction = faction;
        this.coordinates = new int[]{Math.min(coordinates[0], coordinates[1]), Math.min(coordinates[2], coordinates[3]), Math.max(coordinates[0], coordinates[1]), Math.max(coordinates[2], coordinates[3])};
        this.worldName = worldName;

        Location firstCorner = new Location(Bukkit.getWorld(worldName), getFirstX(), 0, getFirstZ());
        Location secondCorner = new Location(Bukkit.getWorld(worldName), getFirstX(), 0, getSecondZ());
        Location thirdCorner = new Location(Bukkit.getWorld(worldName), getSecondX(), 0, getFirstZ());
        Location fourthCorner = new Location(Bukkit.getWorld(worldName), getSecondX(), 0, getSecondZ());
        corners = new Location[]{firstCorner, secondCorner, thirdCorner, fourthCorner};

        index();
        setupBorder();

        faction.getClaims().add(this);
        claims.add(this);
    }

    private void index() {
        for (int x = getFirstX(); x < getSecondX() + 1; x++) {
            for (int z = getFirstZ(); z < getSecondZ() + 1; z++) {
                int toSetX = x >> 4;
                int toSetZ = z >> 4;
                if (!(chunkClaimMap.containsKey(toSetX + ":" + toSetZ))) {
                    chunkClaimMap.put(toSetX + ":" + toSetZ, new HashSet<Claim>());
                }
                chunkClaimMap.get(toSetX + ":" + toSetZ).add(Claim.this);
            }
        }
    }


    public boolean isGreaterThan(Claim claim) {
        if (FactionsPlugin.getInstance().getMainConfig().getStringList("FACTION_GENERAL.BYPASS_INSIDE_CHECK").contains(faction.getName())) {
            return false;
        }

        if (FactionsPlugin.getInstance().getMainConfig().getStringList("FACTION_GENERAL.BYPASS_INSIDE_CHECK").contains(claim.getFaction().getName())) {
            return true;
        }

        int distance1 = (int) claim.getCorners()[0].distance(claim.getCorners()[3]);
        int distance2 = (int) getCorners()[0].distance(getCorners()[3]);
        return distance2 > distance1;
    }

    public int getWidth() {
        return  (int) corners[2].distance(corners[0]) + 1;
    }

    public int getLength() {
        return (int) corners[2].distance(corners[1]) + 1;
    }

    public static ArrayList<Material> getMapBlocks() {
        ArrayList<Material> toReturn = new ArrayList<>();

        for (Material material : Material.values()) {
            if (material.isBlock() && material.isOccluding() && material.isSolid() && !material.name().contains("STAIRS") && !material.name().contains("SLAB") && !material.name().contains("MUSHROOM")) {
                toReturn.add(material);
            }
        }

        return toReturn;
    }

    public boolean overlaps(double x1, double z1, double x2, double z2) {
        double[] dim = new double[2];

        dim[0] = x1;
        dim[1] = x2;
        Arrays.sort(dim);

        if (getFirstX() > dim[1] || getSecondX() < dim[0]) {
            return false;
        }

        dim[0] = z1;
        dim[1] = z2;
        Arrays.sort(dim);

        if (getFirstZ() > dim[1] || getSecondZ() < dim[0]) {
            return false;
        }

        return true;
    }

    public boolean isNearby(Location l, int buffer) {
        if (Bukkit.getWorld(worldName) == new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).getWorld()) {
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(0, 0, buffer))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(buffer, 0, 0))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(0, 0, -buffer))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(-buffer, 0, 0))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(-buffer, 0, buffer))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(buffer, 0, -buffer))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(-buffer, 0, -buffer))) {
                return true;
            }
            if (isInside(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()).add(buffer, 0, buffer))) {
                return true;
            }
        }
        return false;
    }

    private void setupBorder() {
        ArrayList<Location> toReturn = new ArrayList<>();
        World world = Bukkit.getWorld(worldName);

        for (int i = Math.min(corners[0].getBlockZ(), corners[1].getBlockZ()); i < Math.max(corners[0].getBlockZ(), corners[1].getBlockZ()); i++) {
            toReturn.add(new Location(world, corners[0].getBlockX(), 0, i));
        }
        for (int i = Math.min(corners[2].getBlockZ(), corners[3].getBlockZ()); i < Math.max(corners[2].getBlockZ(), corners[3].getBlockZ()); i++) {
            toReturn.add(new Location(world, corners[3].getBlockX(), 0, i));
        }
        for (int i = Math.min(corners[1].getBlockX(), corners[3].getBlockX()); i < Math.max(corners[1].getBlockX(), corners[3].getBlockX()) + 1; i++) {
            toReturn.add(new Location(world, i, 0, corners[1].getBlockZ()));
        }
        for (int i = Math.min(corners[0].getBlockX(), corners[2].getBlockX()); i < Math.max(corners[0].getBlockX(), corners[2].getBlockX()); i++) {
            toReturn.add(new Location(world, i, 0, corners[0].getBlockZ()));
        }

        border = toReturn;
    }

    public void remove() {
        claims.remove(this);

        for (String key : Claim.getChunkClaimMap().keySet()) {
            HashSet<Claim> keySet = chunkClaimMap.get(key);
            if (keySet.contains(this)) {
                keySet.remove(this);
            }
        }

        for (String key : new ArrayList<>(Claim.getChunkClaimMap().keySet())) {
            if (Claim.getChunkClaimMap().get(key).isEmpty()) {
                chunkClaimMap.remove(key);
            }
        }

        for (Profile profile : Profile.getProfiles()) {
            if (!(profile.getMapPillars().isEmpty())) {
                for (ClaimPillar claimPillar : new HashSet<>(profile.getMapPillars())) {
                    if (isInside(claimPillar.getLocation())) {
                        claimPillar.remove();
                        profile.getMapPillars().remove(claimPillar);
                    }
                }

                if (profile.getMapPillars().isEmpty()) {
                    profile.setViewingMap(false);
                }
            }
        }

        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            Location cornerThree = new Location(Bukkit.getWorld(worldName), getFirstX(), 0, getSecondZ());
            int width = (int) cornerThree.distance(corners[0]) + 1;
            int length = (int) cornerThree.distance(corners[3]) + 1;
            int value = (int) (width * length * FactionsPlugin.getInstance().getMainConfig().getDouble("FACTION_CLAIMING.PRICE_MULTIPLIER"));
            playerFaction.setBalance(playerFaction.getBalance() + value);
        }

        faction.getClaims().remove(this);
    }

    public int getFirstX() {
        return coordinates[0];
    }

    public int getSecondX() {
        return coordinates[2];
    }

    public int getFirstZ() {
        return coordinates[1];
    }

    public int getSecondZ() {
        return coordinates[3];
    }

    public boolean isInside(Location location) {
        return (location.getWorld().getName().equalsIgnoreCase(worldName) && (location.getBlockX() >= getFirstX() && location.getBlockX() <= getSecondX()) && (location.getBlockZ() >= getFirstZ() && location.getBlockZ() <= getSecondZ()));
    }

    public static Claim getProminentClaimAt(Location location) {
        List<Claim> possibleClaims = getClaimsAt(location);

        if (possibleClaims != null) {
            Claim currentClaim = null;

            for (Claim claim : possibleClaims) {
                if (claim.isInside(location)) {
                    if (currentClaim != null) {
                        if (currentClaim.isGreaterThan(claim)) {
                            currentClaim = claim;
                        }
                    }
                    else {
                        currentClaim = claim;
                    }
                }
            }

            return currentClaim;
        }

        return null;
    }

    public static ArrayList<Claim> getClaimsAt(Location location) {
        if (chunkClaimMap.containsKey((location.getBlockX() >> 4) + ":" + (location.getBlockZ() >> 4))) {
            return new ArrayList<>(chunkClaimMap.get((location.getBlockX() >> 4) + ":" + (location.getBlockZ() >> 4)));
        }
        return null;
    }

    public static Set<Claim> getNearbyClaimsAt(Location location, int radius) {
        Set<Claim> toReturn = new HashSet<>();
        int[] pos = new int[]{location.getBlockX(), location.getBlockZ()};
        Location currentLocation = new Location(location.getWorld(), pos[0], 0, pos[1]);

        for (int x = pos[0] - radius; x < pos[0] + radius; x++) {
            for (int z = pos[1] - radius; z < pos[1] + radius; z++) {
                currentLocation.setX(x);
                currentLocation.setZ(z);
                Claim claim = getProminentClaimAt(currentLocation);
                if (claim != null && !toReturn.contains(claim)) {
                    toReturn.add(claim);
                }
            }
        }

        return toReturn;
    }

    private static Map<String, HashSet<Claim>> getChunkClaimMap() {
        return chunkClaimMap;
    }

    public static Set<Claim> getClaims() {
        return claims;
    }

}
