package us.zonix.hcfactions.factions.claims;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.LocationSerialization;

public class ClaimListeners implements Listener {

    private FactionsPlugin main = FactionsPlugin.getInstance();
    private ConfigFile mainConfig = main.getMainConfig();
    private ConfigFile langConfig = main.getLanguageConfig();

    @EventHandler
    public void onPlayerInteractClaim(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().equals(Faction.getWand())) {
            event.setCancelled(true);

            Profile profile = Profile.getByPlayer(player);
            ClaimProfile claimProfile = profile.getClaimProfile();

            if (claimProfile == null) {
                if (profile.getFaction() != null) {
                    claimProfile = new ClaimProfile(player, profile.getFaction());
                }
                else {
                    return;
                }
            }

            final Faction faction = claimProfile.getFaction();

            if (faction instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction) faction;

                if (playerFaction.getLeader() != player.getUniqueId() && !playerFaction.getOfficers().contains(player.getUniqueId()) && !us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.DEVELOPER)) {
                    player.getInventory().removeItem(Faction.getWand());
                    claimProfile.removePillars();
                    return;
                }
            }

            if (event.getAction().name().contains("BLOCK")) {
                claimProfile.setResetClicked(false);

                final Material material = Material.valueOf(mainConfig.getString("FACTION_CLAIMING.CLAIM_PILLAR.TYPE"));
                final int data = mainConfig.getInt("FACTION_CLAIMING.CLAIM_PILLAR.DATA");
                final Location location = event.getClickedBlock().getLocation();
                int toDisplay = 0;

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    toDisplay = 1;
                }
                else {
                    if (!faction.getClaims().isEmpty() && faction instanceof PlayerFaction && !faction.isNearBorder(location)) {
                        player.sendMessage(langConfig.getString("ERROR.MUST_CLAIM_CLOSER"));
                        return;
                    }
                }

                final ClaimPillar pillar = claimProfile.getPillars()[toDisplay];
                final String message = (langConfig.getString("FACTION_CLAIM.SET_POSITION_" + (toDisplay + 1)).replace("%X_POS%", location.getBlockX() + "").replace("%Z_POS%", location.getBlockZ() + ""));

                for (Claim claim : Claim.getClaims()) {
                    if (claim.isInside(location) && (!(faction instanceof SystemFaction))) {
                        player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_HERE"));
                        return;
                    }

                    if (claim.isNearby(location, mainConfig.getInt("FACTION_CLAIMING.BUFFER")) && claim.getFaction() != faction && (!(faction instanceof SystemFaction))) {
                        player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_BUFFER"));
                        return;
                    }
                }

                if (toDisplay == 1) {
                    final ClaimProfile finalClaimProfile = claimProfile;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ClaimPillar secondPillar = finalClaimProfile.getPillars()[0];
                            ClaimPillar firstPillar = new ClaimPillar(player, location);

                            if (secondPillar != null) {
                                Location cornerOne = firstPillar.getLocation();
                                Location cornerTwo = secondPillar.getLocation();
                                Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());

                                int width = (int) cornerThree.distance(cornerOne) + 1;
                                int length = (int) cornerThree.distance(cornerTwo) + 1;

                                if (width < mainConfig.getInt("FACTION_CLAIMING.MIN_SIZE") || length < mainConfig.getInt("FACTION_CLAIMING.MIN_SIZE")) {
                                    player.sendMessage(langConfig.getString("ERROR.CLAIM_TOO_SMALL"));
                                    return;
                                }

                                player.sendMessage(message);
                                player.sendMessage(langConfig.getString("FACTION_CLAIM.BOTH_POSITIONS_SET").replace("%COST%", calculateCosts(firstPillar, secondPillar) + "").replace("%LENGTH%", length + "").replace("%WIDTH%", width + "").replace("%TOTAL_BLOCKS%", length * width + ""));
                            }
                            else {
                                player.sendMessage(message);
                            }

                            if (pillar != null) {
                                if (pillar.getLocation().equals(location)) {
                                    return;
                                }
                                else {
                                    pillar.remove();
                                }
                            }

                            finalClaimProfile.getPillars()[1] = firstPillar.show(material, data);
                        }
                    }.runTaskLaterAsynchronously(main, 1L);
                }
                else {
                    ClaimPillar secondPillar = claimProfile.getPillars()[1];
                    ClaimPillar firstPillar = new ClaimPillar(player, location);

                    if (secondPillar != null) {
                        Location cornerOne = firstPillar.getLocation();
                        Location cornerTwo = secondPillar.getLocation();
                        Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
                        int width = (int) cornerThree.distance(cornerOne) + 1;
                        int length = (int) cornerThree.distance(cornerTwo) + 1;

                        if (width < mainConfig.getInt("FACTION_CLAIMING.MIN_SIZE") || length < mainConfig.getInt("FACTION_CLAIMING.MIN_SIZE")) {
                            player.sendMessage(langConfig.getString("ERROR.CLAIM_TOO_SMALL"));
                            return;
                        }

                        player.sendMessage(message);
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.BOTH_POSITIONS_SET").replace("%COST%", calculateCosts(firstPillar, secondPillar) + "").replace("%LENGTH%", length + "").replace("%WIDTH%", width + "").replace("%TOTAL_BLOCKS%", length * width + ""));
                    }
                    else {
                        player.sendMessage(message);
                    }

                    if (pillar != null) {
                        if (pillar.getLocation().equals(location)) {
                            return;
                        }
                        else {
                            pillar.remove();
                        }
                    }

                    claimProfile.getPillars()[0] = firstPillar.show(material, data);
                }

                return;
            }

            if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
                if (claimProfile.getPillars()[0] == null || claimProfile.getPillars()[1] == null) {
                    player.sendMessage(langConfig.getString("ERROR.INVALID_SELECTION"));
                    return;
                }

                ClaimPillar firstPillar = claimProfile.getPillars()[0];
                ClaimPillar secondPillar = claimProfile.getPillars()[1];
                Location cornerOne = firstPillar.getLocation();
                Location cornerTwo = secondPillar.getLocation();
                Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
                Location cornerFour = new Location(cornerOne.getWorld(), cornerTwo.getBlockX(), 0, cornerOne.getBlockZ());

                if (faction instanceof PlayerFaction) {
                    for (Claim claim : Claim.getClaims()) {
                        if (claim.overlaps(firstPillar.getLocation().getBlockX(), firstPillar.getLocation().getBlockZ(), secondPillar.getLocation().getBlockX(), secondPillar.getLocation().getBlockZ())) {
                            player.sendMessage(langConfig.getString("ERROR.CANNOT_OVERCLAIM"));
                            return;
                        }

                        if (claim.isNearby(cornerOne, mainConfig.getInt("FACTION_CLAIMING.BUFFER")) && claim.getFaction() != faction) {
                            player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_BUFFER"));
                            return;
                        }

                        if (claim.isNearby(cornerTwo, mainConfig.getInt("FACTION_CLAIMING.BUFFER")) && claim.getFaction() != faction) {
                            player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_BUFFER"));
                            return;
                        }

                        if (claim.isNearby(cornerThree, mainConfig.getInt("FACTION_CLAIMING.BUFFER")) && claim.getFaction() != faction) {
                            player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_BUFFER"));
                            return;
                        }

                        if (claim.isNearby(cornerFour, mainConfig.getInt("FACTION_CLAIMING.BUFFER")) && claim.getFaction() != faction) {
                            player.sendMessage(langConfig.getString("ERROR.CANNOT_CLAIM_BUFFER"));
                            return;
                        }
                    }
                }

                int price = calculateCosts(firstPillar, secondPillar);

                if (faction instanceof PlayerFaction) {
                    PlayerFaction playerFaction = (PlayerFaction) faction;

                    if (playerFaction.getBalance() < price && !us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.DEVELOPER)) {
                        player.sendMessage(langConfig.getString("ERROR.FACTION_NOT_ENOUGH_MONEY"));
                        return;
                    }

                    playerFaction.setBalance(playerFaction.getBalance() - price);
                    playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_CLAIM_LAND").replace("%PLAYER%", player.getName()));
                }

                new Claim(faction, new int[]{firstPillar.getLocation().getBlockX(), secondPillar.getLocation().getBlockX(), firstPillar.getLocation().getBlockZ(), secondPillar.getLocation().getBlockZ()}, firstPillar.getLocation().getWorld().getName());
                claimProfile.removePillars();
                player.getInventory().remove(Faction.getWand());
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (claimProfile.getPillars()[0] == null && claimProfile.getPillars()[1] == null) {
                    player.sendMessage(langConfig.getString("ERROR.INVALID_SELECTION"));
                    return;
                }

                if (claimProfile.isResetClicked()) {
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.SELECTION_RESET"));
                    claimProfile.setResetClicked(false);
                    claimProfile.removePillars();
                }
                else {
                    claimProfile.setResetClicked(true);
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CLICK_TO_RESET_SELECTION"));
                }
            }
        }
    }

    private int calculateCosts(ClaimPillar firstPillar, ClaimPillar secondPillar) {
        Location cornerOne = firstPillar.getLocation();
        Location cornerTwo = secondPillar.getLocation();
        Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
        int width = (int) cornerThree.distance(cornerOne) + 1;
        int length = (int) cornerThree.distance(cornerTwo) + 1;
        return (int) (width * length * mainConfig.getDouble("FACTION_CLAIMING.PRICE_MULTIPLIER"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().removeItem(Faction.getWand());
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity().getType() == EntityType.ITEM_FRAME) {
            Player player = (Player) event.getDamager();
            Profile profile = Profile.getByPlayer(player);

            if (profile.isInAdminMode()) {
                return;
            }

            Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                    return;
                }

                if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                    if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getEntity().getLocation()) >= 302) {
                        return;
                    }
                }

                if (profile.getFaction() == null || !faction.equals(profile.getFaction())) {
                    if (faction instanceof SystemFaction) {
                        SystemFaction systemFaction = (SystemFaction) faction;
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                    }
                    else {
                        if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                        }
                        else {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                return;
            }

            if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getEntity().getLocation()) >= 302) {
                    return;
                }
            }

            if (profile.getFaction() == null || !faction.equals(profile.getFaction())) {
                if (faction instanceof SystemFaction) {
                    SystemFaction systemFaction = (SystemFaction) faction;
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                }
                else {
                    if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                    }
                    else {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        Block block = event.getBlock();

        if (Claim.getProminentClaimAt(block.getLocation()) == null) {
            for (Block other : event.getBlocks()) {
                if (Claim.getProminentClaimAt(other.getLocation()) != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
        Block block = event.getBlock();

        if (Claim.getProminentClaimAt(block.getLocation()) == null) {
            if (Claim.getProminentClaimAt(event.getRetractLocation()) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();
            Profile profile = Profile.getByPlayer(player);

            if (profile.isInAdminMode()) {
                return;
            }

            Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                    return;
                }

                if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                    if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getEntity().getLocation()) >= 302) {
                        return;
                    }
                }

                if (profile.getFaction() == null || !faction.equals(profile.getFaction())) {
                    if (faction instanceof SystemFaction) {
                        SystemFaction systemFaction = (SystemFaction) faction;
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                    }
                    else {
                        if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                        }
                        else {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()) {
            if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(Faction.getWand().getItemMeta().getDisplayName())) {
                event.getItemDrop().remove();

                ClaimProfile profile = Profile.getByPlayer(event.getPlayer()).getClaimProfile();

                if (profile != null) {
                    for (ClaimPillar pillar : profile.getPillars()) {
                        if (pillar != null) {
                            pillar.remove();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamageInSafezone(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damager;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
                else {
                    return;
                }
            }
            else {
                return;
            }

            Player damaged = (Player) event.getEntity();
            Profile damagerProfile = Profile.getByPlayer(damager);
            Claim damagerClaim = Claim.getProminentClaimAt(damager.getLocation());

            if (!damagerProfile.isInAdminMode() && damagerClaim != null && damagerClaim.isInside(damager.getLocation()) && damagerClaim.getFaction() instanceof SystemFaction) {
                SystemFaction systemFaction = (SystemFaction) damagerClaim.getFaction();

                if (!(systemFaction.isDeathban())) {
                    damager.sendMessage(langConfig.getString("FACTION_CLAIM.DAMAGER_IN_SAFEZONE").replace("%FACTION%", systemFaction.getName()).replace("%COLOR%", systemFaction.getColor() + ""));
                    event.setCancelled(true);
                    return;
                }
            }

            Claim damagedClaim = Claim.getProminentClaimAt(damaged.getLocation());

            if (!damagerProfile.isInAdminMode() && damagedClaim != null && damagedClaim.getFaction() instanceof SystemFaction) {
                SystemFaction systemFaction = (SystemFaction) damagedClaim.getFaction();

                if (!(systemFaction.isDeathban())) {
                    damager.sendMessage(langConfig.getString("FACTION_CLAIM.DAMAGED_IN_SAFEZONE").replace("%FACTION%", systemFaction.getName()).replace("%COLOR%", systemFaction.getColor() + ""));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                return;
            }

            if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getBlock().getLocation()) >= 302) {
                    return;
                }
            }

            if (!faction.equals(profile.getFaction())) {
                if (faction instanceof SystemFaction) {
                    SystemFaction systemFaction = (SystemFaction) faction;
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                }
                else {
                    if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                    }
                    else {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                return;
            }

            if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getBlock().getLocation()) >= 302) {
                    return;
                }
            }

            if (!faction.equals(profile.getFaction())) {
                if (faction instanceof SystemFaction) {
                    SystemFaction systemFaction = (SystemFaction) faction;
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                }
                else {
                    if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                    }
                    else {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(event.getBlockClicked().getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                return;
            }

            if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getBlockClicked().getLocation()) >= 302) {
                    return;
                }
            }

            if (!faction.equals(profile.getFaction())) {
                if (faction instanceof SystemFaction) {
                    SystemFaction systemFaction = (SystemFaction) faction;
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                }
                else {
                    if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                    }
                    else {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(event.getBlockClicked().getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                return;
            }

            if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(event.getBlockClicked().getLocation()) >= 302) {
                    return;
                }
            }

            if (!faction.equals(profile.getFaction())) {
                if (faction instanceof SystemFaction) {
                    SystemFaction systemFaction = (SystemFaction) faction;
                    player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                }
                else {
                    if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                    }
                    else {
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                    }
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (event.getRightClicked() instanceof Hanging) {
            Entity entity = event.getRightClicked();
            Claim claim = Claim.getProminentClaimAt(entity.getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                if (profile.isInAdminMode()) {
                    return;
                }

                if(entity instanceof Sign) {
                    return;
                }

                if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                    return;
                }

                if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                    if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(entity.getLocation()) >= 302) {
                        return;
                    }
                }

                if (profile.getFaction() == null || !faction.equals(profile.getFaction())) {
                    if (faction instanceof SystemFaction) {
                        SystemFaction systemFaction = (SystemFaction) faction;
                        player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                    }
                    else {
                        if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ALLY").replace("%FACTION%", faction.getName()));
                        }
                        else {
                            player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_BUILD_ENEMY").replace("%FACTION%", faction.getName()));
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getState() instanceof ContainerBlock || block.getState().getData() instanceof Redstone || block.getState().getData() instanceof Openable) {
                Claim claim = Claim.getProminentClaimAt(block.getLocation());

                if (claim != null) {
                    Faction faction = claim.getFaction();

                    if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                        return;
                    }

                    if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                        if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(block.getLocation()) >= 302) {
                            return;
                        }
                    }

                    if (!faction.equals(profile.getFaction())) {

                        if (faction instanceof SystemFaction) {
                            SystemFaction systemFaction = (SystemFaction) faction;

                            if(block.getType() != Material.TRAPPED_CHEST || block.getType() == Material.ENDER_CHEST) {
                                player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_INTERACT_SYSTEM").replace("%COLOR%", systemFaction.getColor() + "").replace("%FACTION%", systemFaction.getName()));
                            }
                        }
                        else {
                            if (faction instanceof PlayerFaction && profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                                player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_INTERACT_ALLY").replace("%FACTION%", faction.getName()));
                            }
                            else {
                                player.sendMessage(langConfig.getString("FACTION_CLAIM.CANNOT_INTERACT_ENEMY").replace("%FACTION%", faction.getName()));
                            }
                        }

                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPhsycialInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInAdminMode()) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            Claim claim = Claim.getProminentClaimAt(block.getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                if (faction instanceof PlayerFaction && ((PlayerFaction) faction).isRaidable()) {
                    return;
                }

                if(faction instanceof SystemFaction && faction.getName().equalsIgnoreCase("Warzone")) {

                    if(this.getSpawnLocation() != null && this.getSpawnLocation().distance(block.getLocation()) >= 302) {
                        return;
                    }
                }

                if (!faction.equals(profile.getFaction())) {
                    if (!(faction instanceof SystemFaction)) {
                        event.setCancelled(true);
                    }
                    else {
                        if (block.getType() == Material.SOIL) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryInreact(InventoryMoveItemEvent event) {
        if (event.getItem() == Faction.getWand()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInreact(InventoryClickEvent event) {
        if (event.getInventory().contains(Faction.getWand())) {
            event.getInventory().remove(Faction.getWand());

            Player player = (Player) event.getWhoClicked();
            ClaimProfile profile = Profile.getByPlayer(player).getClaimProfile();

            if (profile != null) {
                for (ClaimPillar pillar : profile.getPillars()) {
                    if (pillar != null) {
                        pillar.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && event.getEntity() instanceof Monster) {
            Claim claim = Claim.getProminentClaimAt(event.getLocation());

            if (claim != null) {
                if (claim.getFaction() instanceof SystemFaction) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private Location getSpawnLocation() {
        SystemFaction faction = SystemFaction.getByName("Spawn");
        if (faction != null && faction.getHome() != null) {
            return LocationSerialization.deserializeLocation(faction.getHome());
        }
        return null;
    }
}
