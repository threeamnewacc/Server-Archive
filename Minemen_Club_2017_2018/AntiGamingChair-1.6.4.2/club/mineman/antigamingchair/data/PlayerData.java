// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.data;

import java.util.concurrent.ConcurrentHashMap;
import club.mineman.antigamingchair.check.impl.velocity.VelocityD;
import club.mineman.antigamingchair.check.impl.velocity.VelocityC;
import club.mineman.antigamingchair.check.impl.velocity.VelocityB;
import club.mineman.antigamingchair.check.impl.velocity.VelocityA;
import club.mineman.antigamingchair.check.impl.timer.TimerA;
import club.mineman.antigamingchair.check.impl.range.RangeA;
import club.mineman.antigamingchair.check.impl.killaura.KillAuraF;
import club.mineman.antigamingchair.check.impl.killaura.KillAuraE;
import club.mineman.antigamingchair.check.impl.range.KillAuraD;
import club.mineman.antigamingchair.check.impl.killaura.KillAuraC;
import club.mineman.antigamingchair.check.impl.killaura.KillAuraB;
import club.mineman.antigamingchair.check.impl.killaura.KillAuraA;
import club.mineman.antigamingchair.check.impl.inventory.InventoryB;
import club.mineman.antigamingchair.check.impl.inventory.InventoryA;
import club.mineman.antigamingchair.check.impl.fly.FlyC;
import club.mineman.antigamingchair.check.impl.fly.FlyB;
import club.mineman.antigamingchair.check.impl.fly.FlyA;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsF;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsE;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsD;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsC;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsB;
import club.mineman.antigamingchair.check.impl.badpackets.BadPacketsA;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerG;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerF;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerE;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerC;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerB;
import club.mineman.antigamingchair.check.impl.autoclicker.AutoClickerA;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistC;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistA;
import club.mineman.antigamingchair.check.impl.aimassist.AimAssistB;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.HashMap;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.client.ClientType;
import java.util.Set;
import club.mineman.antigamingchair.location.CustomLocation;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Constructor;
import java.util.Map;
import club.mineman.antigamingchair.check.ICheck;

public class PlayerData
{
    public static final Class<? extends ICheck>[] CHECKS;
    private static final Map<Class<? extends ICheck>, Constructor<? extends ICheck>> CONSTRUCTORS;
    private final Map<UUID, List<CustomLocation>> recentPlayerPackets;
    private final Map<ICheck, Set<Long>> checkViolationTimes;
    private final Map<Class<? extends ICheck>, ICheck> checkMap;
    private final Map<Integer, Long> keepAliveTimes;
    private final Map<ICheck, Double> checkVlMap;
    private final Set<UUID> playersWatching;
    private final Set<String> filteredPhrases;
    private final Set<CustomLocation> teleportLocations;
    private StringBuilder sniffedPacketBuilder;
    private CustomLocation lastMovePacket;
    private ClientType client;
    private boolean allowTeleport;
    private boolean inventoryOpen;
    private boolean sendingVape;
    private boolean attackedSinceVelocity;
    private boolean underBlock;
    private boolean sprinting;
    private boolean inLiquid;
    private boolean onGround;
    private boolean sniffing;
    private boolean onStairs;
    private boolean banWave;
    private boolean placing;
    private boolean banning;
    private boolean digging;
    private boolean inWeb;
    private boolean onIce;
    private double lastGroundY;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private long lastDelayedMovePacket;
    private long lastAttackPacket;
    private long lastTeleportTime;
    private long lastVelocity;
    private long ping;
    private int velocityH;
    private int velocityV;
    private int cps;
    
    public PlayerData(final AntiGamingChair plugin) {
        this.recentPlayerPackets = new HashMap<UUID, List<CustomLocation>>();
        this.checkViolationTimes = new HashMap<ICheck, Set<Long>>();
        this.checkMap = new HashMap<Class<? extends ICheck>, ICheck>();
        this.keepAliveTimes = new HashMap<Integer, Long>();
        this.checkVlMap = new HashMap<ICheck, Double>();
        this.playersWatching = new HashSet<UUID>();
        this.filteredPhrases = new HashSet<String>();
        this.teleportLocations = new HashSet<CustomLocation>();
        this.sniffedPacketBuilder = new StringBuilder();
        this.client = ClientType.VANILLA;
        for (final Class<? extends ICheck> check : PlayerData.CONSTRUCTORS.keySet()) {
            final Constructor<? extends ICheck> constructor = PlayerData.CONSTRUCTORS.get(check);
            try {
                this.checkMap.put(check, (ICheck)constructor.newInstance(plugin, this));
            }
            catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    public <T extends ICheck> T getCheck(final Class<T> clazz) {
        return (T)this.checkMap.get(clazz);
    }
    
    public CustomLocation getLastPlayerPacket(final UUID playerUUID, final int index) {
        if (this.recentPlayerPackets.containsKey(playerUUID) && this.recentPlayerPackets.get(playerUUID).size() > index) {
            return this.recentPlayerPackets.get(playerUUID).get(this.recentPlayerPackets.get(playerUUID).size() - index);
        }
        return null;
    }
    
    public void addPlayerPacket(final UUID playerUUID, final CustomLocation customLocation) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations == null) {
            customLocations = new ArrayList<CustomLocation>();
        }
        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }
        customLocations.add(customLocation);
        this.recentPlayerPackets.put(playerUUID, customLocations);
    }
    
    public void addTeleportLocation(final CustomLocation teleportLocation) {
        this.teleportLocations.add(teleportLocation);
    }
    
    public boolean allowTeleport(final CustomLocation teleportLocation) {
        for (final CustomLocation customLocation : this.teleportLocations) {
            final double delta = Math.pow(teleportLocation.getX() - customLocation.getX(), 2.0) + Math.pow(teleportLocation.getY() - customLocation.getY(), 2.0) + Math.pow(teleportLocation.getZ() - customLocation.getZ(), 2.0);
            if (delta == 0.0) {
                this.teleportLocations.remove(customLocation);
                return true;
            }
        }
        return false;
    }
    
    public double getCheckVl(final ICheck check) {
        if (!this.checkVlMap.containsKey(check)) {
            this.checkVlMap.put(check, 0.0);
        }
        return this.checkVlMap.get(check);
    }
    
    public void setCheckVl(double vl, final ICheck check) {
        if (vl < 0.0) {
            vl = 0.0;
        }
        this.checkVlMap.put(check, vl);
    }
    
    public boolean isPlayerWatching(final Player player) {
        return this.playersWatching.contains(player.getUniqueId());
    }
    
    public void togglePlayerWatching(final Player player) {
        if (!this.playersWatching.remove(player.getUniqueId())) {
            this.playersWatching.add(player.getUniqueId());
        }
    }
    
    public boolean isKeywordFiltered(final String keyword) {
        return this.filteredPhrases.contains(keyword);
    }
    
    public void toggleKeywordFilter(final String keyword) {
        if (!this.filteredPhrases.remove(keyword)) {
            this.filteredPhrases.add(keyword);
        }
    }
    
    public boolean keepAliveExists(final int id) {
        return this.keepAliveTimes.containsKey(id);
    }
    
    public long getKeepAliveTime(final int id) {
        final long time = this.keepAliveTimes.get(id);
        this.keepAliveTimes.remove(id);
        return time;
    }
    
    public void addKeepAliveTime(final int id) {
        this.keepAliveTimes.put(id, System.currentTimeMillis());
    }
    
    public int getViolations(final ICheck check, final Long time) {
        final Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps != null) {
            int violations = 0;
            for (final long timestamp : timestamps) {
                if (System.currentTimeMillis() - timestamp <= time) {
                    ++violations;
                }
            }
            return violations;
        }
        return 0;
    }
    
    public void addViolation(final ICheck check) {
        Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps == null) {
            timestamps = new HashSet<Long>();
        }
        timestamps.add(System.currentTimeMillis());
        this.checkViolationTimes.put(check, timestamps);
    }
    
    public Map<UUID, List<CustomLocation>> getRecentPlayerPackets() {
        return this.recentPlayerPackets;
    }
    
    public Map<ICheck, Set<Long>> getCheckViolationTimes() {
        return this.checkViolationTimes;
    }
    
    public Map<Class<? extends ICheck>, ICheck> getCheckMap() {
        return this.checkMap;
    }
    
    public Map<Integer, Long> getKeepAliveTimes() {
        return this.keepAliveTimes;
    }
    
    public Map<ICheck, Double> getCheckVlMap() {
        return this.checkVlMap;
    }
    
    public Set<UUID> getPlayersWatching() {
        return this.playersWatching;
    }
    
    public Set<String> getFilteredPhrases() {
        return this.filteredPhrases;
    }
    
    public Set<CustomLocation> getTeleportLocations() {
        return this.teleportLocations;
    }
    
    public StringBuilder getSniffedPacketBuilder() {
        return this.sniffedPacketBuilder;
    }
    
    public CustomLocation getLastMovePacket() {
        return this.lastMovePacket;
    }
    
    public ClientType getClient() {
        return this.client;
    }
    
    public boolean isAllowTeleport() {
        return this.allowTeleport;
    }
    
    public boolean isInventoryOpen() {
        return this.inventoryOpen;
    }
    
    public boolean isSendingVape() {
        return this.sendingVape;
    }
    
    public boolean isAttackedSinceVelocity() {
        return this.attackedSinceVelocity;
    }
    
    public boolean isUnderBlock() {
        return this.underBlock;
    }
    
    public boolean isSprinting() {
        return this.sprinting;
    }
    
    public boolean isInLiquid() {
        return this.inLiquid;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public boolean isSniffing() {
        return this.sniffing;
    }
    
    public boolean isOnStairs() {
        return this.onStairs;
    }
    
    public boolean isBanWave() {
        return this.banWave;
    }
    
    public boolean isPlacing() {
        return this.placing;
    }
    
    public boolean isBanning() {
        return this.banning;
    }
    
    public boolean isDigging() {
        return this.digging;
    }
    
    public boolean isInWeb() {
        return this.inWeb;
    }
    
    public boolean isOnIce() {
        return this.onIce;
    }
    
    public double getLastGroundY() {
        return this.lastGroundY;
    }
    
    public double getVelocityX() {
        return this.velocityX;
    }
    
    public double getVelocityY() {
        return this.velocityY;
    }
    
    public double getVelocityZ() {
        return this.velocityZ;
    }
    
    public long getLastDelayedMovePacket() {
        return this.lastDelayedMovePacket;
    }
    
    public long getLastAttackPacket() {
        return this.lastAttackPacket;
    }
    
    public long getLastTeleportTime() {
        return this.lastTeleportTime;
    }
    
    public long getLastVelocity() {
        return this.lastVelocity;
    }
    
    public long getPing() {
        return this.ping;
    }
    
    public int getVelocityH() {
        return this.velocityH;
    }
    
    public int getVelocityV() {
        return this.velocityV;
    }
    
    public int getCps() {
        return this.cps;
    }
    
    public void setLastMovePacket(final CustomLocation lastMovePacket) {
        this.lastMovePacket = lastMovePacket;
    }
    
    public void setClient(final ClientType client) {
        this.client = client;
    }
    
    public void setAllowTeleport(final boolean allowTeleport) {
        this.allowTeleport = allowTeleport;
    }
    
    public void setInventoryOpen(final boolean inventoryOpen) {
        this.inventoryOpen = inventoryOpen;
    }
    
    public void setSendingVape(final boolean sendingVape) {
        this.sendingVape = sendingVape;
    }
    
    public void setAttackedSinceVelocity(final boolean attackedSinceVelocity) {
        this.attackedSinceVelocity = attackedSinceVelocity;
    }
    
    public void setUnderBlock(final boolean underBlock) {
        this.underBlock = underBlock;
    }
    
    public void setSprinting(final boolean sprinting) {
        this.sprinting = sprinting;
    }
    
    public void setInLiquid(final boolean inLiquid) {
        this.inLiquid = inLiquid;
    }
    
    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }
    
    public void setOnStairs(final boolean onStairs) {
        this.onStairs = onStairs;
    }
    
    public void setBanWave(final boolean banWave) {
        this.banWave = banWave;
    }
    
    public void setPlacing(final boolean placing) {
        this.placing = placing;
    }
    
    public void setBanning(final boolean banning) {
        this.banning = banning;
    }
    
    public void setDigging(final boolean digging) {
        this.digging = digging;
    }
    
    public void setInWeb(final boolean inWeb) {
        this.inWeb = inWeb;
    }
    
    public void setOnIce(final boolean onIce) {
        this.onIce = onIce;
    }
    
    public void setLastGroundY(final double lastGroundY) {
        this.lastGroundY = lastGroundY;
    }
    
    public void setVelocityX(final double velocityX) {
        this.velocityX = velocityX;
    }
    
    public void setVelocityY(final double velocityY) {
        this.velocityY = velocityY;
    }
    
    public void setVelocityZ(final double velocityZ) {
        this.velocityZ = velocityZ;
    }
    
    public void setLastDelayedMovePacket(final long lastDelayedMovePacket) {
        this.lastDelayedMovePacket = lastDelayedMovePacket;
    }
    
    public void setLastAttackPacket(final long lastAttackPacket) {
        this.lastAttackPacket = lastAttackPacket;
    }
    
    public void setLastTeleportTime(final long lastTeleportTime) {
        this.lastTeleportTime = lastTeleportTime;
    }
    
    public void setLastVelocity(final long lastVelocity) {
        this.lastVelocity = lastVelocity;
    }
    
    public void setPing(final long ping) {
        this.ping = ping;
    }
    
    public void setVelocityV(final int velocityV) {
        this.velocityV = velocityV;
    }
    
    public void setCps(final int cps) {
        this.cps = cps;
    }
    
    static {
        CHECKS = new Class[] { AimAssistB.class, AimAssistA.class, AimAssistC.class, AutoClickerA.class, AutoClickerB.class, AutoClickerC.class, AutoClickerE.class, AutoClickerE.class, AutoClickerF.class, AutoClickerG.class, BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class, BadPacketsE.class, BadPacketsF.class, FlyA.class, FlyB.class, FlyC.class, InventoryA.class, InventoryB.class, KillAuraA.class, KillAuraB.class, KillAuraC.class, KillAuraD.class, KillAuraE.class, KillAuraF.class, RangeA.class, TimerA.class, VelocityA.class, VelocityB.class, VelocityC.class, VelocityD.class };
        CONSTRUCTORS = new ConcurrentHashMap<Class<? extends ICheck>, Constructor<? extends ICheck>>();
        for (final Class<? extends ICheck> check : PlayerData.CHECKS) {
            try {
                PlayerData.CONSTRUCTORS.put(check, check.getConstructor(AntiGamingChair.class, PlayerData.class));
            }
            catch (final NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
