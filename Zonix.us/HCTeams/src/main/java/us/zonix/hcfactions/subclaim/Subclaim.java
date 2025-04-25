package us.zonix.hcfactions.subclaim;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import lombok.Getter;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.profile.Profile;

public class Subclaim {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    public static final BlockFace[] FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH};
    private static final String SIGN_TEXT = main.getLanguageConfig().getString("SUBCLAIM.SIGN_TEXT");

    @Getter private final Block block;
    @Getter private final String[] signText;

    public Subclaim(Block block, String[] signText) {
        this.block = block;
        this.signText = signText;
    }

    public boolean isAllowed(Player player) {
        Profile profile = Profile.getByPlayer(player);

        if (profile != null) {
            Claim claim = Claim.getProminentClaimAt(block.getLocation());

            if (claim != null && claim.getFaction() instanceof PlayerFaction) {
                PlayerFaction faction = (PlayerFaction) claim.getFaction();

                if (faction.isRaidable()) {
                    return true;
                }

                if (!faction.getOfficers().contains(player.getUniqueId()) && !faction.getLeader().equals(player.getUniqueId())) {

                    for (String allowed : signText) {
                        if (!allowed.isEmpty() && player.getName().toLowerCase().contains(allowed.toLowerCase())) {
                            return true;
                        }
                    }

                    return false;
                }

            }
        }

        return true;
    }

    public static Subclaim getByBlock(Block block) {
        return getByBlock(block, true);
    }

    private static Subclaim getByBlock(Block block, boolean checkDouble) {
        if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();

            if (checkDouble) {
                if (holder.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) holder.getInventory().getHolder();

                    for (InventoryHolder side : new InventoryHolder[]{doubleChest.getLeftSide(), doubleChest.getRightSide()}) {
                        Chest chest = (Chest) side;
                        if (chest.getLocation().equals(block.getLocation())) {
                            Subclaim subclaim = getByBlock(((Chest)(doubleChest.getLeftSide().equals(side) ? doubleChest.getRightSide() : doubleChest.getLeftSide())).getBlock(), false);
                            if (subclaim != null) {
                                return subclaim;
                            }
                        }
                    }
                }
            }

            for (BlockFace face : FACES) {
                if (block.getRelative(face).getState() instanceof Sign) {
                    Sign sign = (Sign) block.getRelative(face).getState();
                    org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();

                    if (sign.getLine(0).equalsIgnoreCase(SIGN_TEXT) && sign.getBlock().getRelative(signData.getAttachedFace()).equals(block)){
                        return new Subclaim(block, sign.getLines());
                    }
                }
            }

        }

        return null;
    }

}
