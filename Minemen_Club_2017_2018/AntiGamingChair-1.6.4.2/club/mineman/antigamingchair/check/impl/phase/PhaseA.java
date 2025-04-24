// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check.impl.phase;

import java.util.Arrays;
import org.bukkit.Location;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.paper.event.PlayerUpdatePositionEvent;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.location.CustomLocation;
import org.bukkit.Material;
import java.util.List;
import club.mineman.antigamingchair.check.checks.PositionCheck;

public class PhaseA extends PositionCheck
{
    private static final List<Material> PHASE_BLOCKS;
    private static final boolean TELEPORT_ON_FAIL = false;
    private CustomLocation lastNotInBlockLocation;
    private boolean inBlock;
    private int blocksPhased;
    
    public PhaseA(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
        this.inBlock = false;
        this.blocksPhased = 0;
    }
    
    @Override
    public void handleCheck(final Player player, final PlayerUpdatePositionEvent event) {
        double vl = this.playerData.getCheckVl(this);
        final boolean inBlock = this.inBlock;
        final Location to = event.getTo();
        try {
            if (PhaseA.PHASE_BLOCKS.contains(to.getBlock().getType())) {
                this.inBlock = false;
                return;
            }
            if (to.getBlock().getType().name().contains("FENCE") || to.getBlock().getType().name().contains("DOOR") || !to.getBlock().getType().isSolid()) {
                this.inBlock = false;
                return;
            }
            if (this.playerData.getLastTeleportTime() + 1000L > System.currentTimeMillis()) {
                this.inBlock = false;
                return;
            }
            this.inBlock = true;
            final Location from = event.getFrom();
            if (inBlock && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
                vl += 1.0 + ++this.blocksPhased / 10.0;
                if (vl > 5.0) {
                    this.plugin.getAlertsManager().forceAlert("failed Phase Check A (Development). " + this.blocksPhased + ". VL " + vl + ".", player);
                }
            }
        }
        finally {
            if (inBlock && !this.inBlock) {
                this.lastNotInBlockLocation = CustomLocation.fromBukkitLocation(to);
                this.blocksPhased = 0;
                vl -= 0.45;
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
    
    static {
        PHASE_BLOCKS = Arrays.asList(Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER, Material.WATER_LILY, Material.LADDER, Material.AIR, Material.ANVIL, Material.RAILS, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL, Material.TORCH, Material.BED, Material.BED_BLOCK, Material.BREWING_STAND, Material.BREWING_STAND_ITEM);
    }
}
