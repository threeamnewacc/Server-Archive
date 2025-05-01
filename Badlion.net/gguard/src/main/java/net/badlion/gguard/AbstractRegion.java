package net.badlion.gguard;

public abstract class AbstractRegion {

	// Name
	private String regionName;

	// Flags
	private boolean allowBrokenBlocks;
	private boolean allowPlacedBlocks;
	private boolean allowFire;
	private boolean allowIceMelt;
	private boolean allowTNTExplosions;
	private boolean allowCreeperExplosions;
	private boolean allowTNTBlockDamage;
	private boolean allowCreeperBlockDamage;
	private boolean allowPistonUsage;
	private double damageMultiplier;
	private boolean changeMobDamageToPlayer;
	private boolean allowedBucketPlacements;
	private boolean allowBlockMovement; // usually lava/water
	private boolean allowCreatureSpawn;
	private boolean healPlayers;
	private boolean feedPlayers;
	private boolean allowEnderPearls;
	private boolean allowMobDamage;
	private boolean allowEndermanMoveBlocks;
	private boolean allowPotionEffects;
	private boolean allowChestInteraction;
	private boolean allowHangingItems;
	private boolean allowPVP;
	private boolean allowItemInteraction;
    private boolean allowPlantGrowth;
    private boolean allowPlantSpread;
    private boolean allowFireSpread;
	private boolean allowPlayerSleep;
	private boolean allowBlockChangesByEntities;
	private boolean allowLeafDecay;
	private boolean allowBoneMealUsage;
	private boolean overrideChestUsage;
    private boolean allowEnderPearlIn;
    private boolean allowFireIgniteByPlayer;
	private boolean allowPlantTrampling;
	private boolean allowPlayerPickupItems;
	private String disallowedPlaceBlocks;
	private String disallowedBreakBlocks;

	public AbstractRegion(String regionName) {
		this.regionName = regionName;

		// Set all flags to true by default
		this.allowBrokenBlocks = true;
		this.allowCreeperBlockDamage = true;
		this.allowCreeperExplosions = true;
		this.allowFire = true;
		this.allowIceMelt = true;
		this.allowPlacedBlocks = true;
		this.allowTNTBlockDamage = true;
		this.allowTNTExplosions = true;
		this.allowPistonUsage = true;
		this.changeMobDamageToPlayer = false; // exception
		this.allowedBucketPlacements = true;
		this.allowBlockMovement = true;
		this.allowCreatureSpawn = true;
		this.healPlayers = true;
		this.feedPlayers = true;
		this.allowEnderPearls = true;
		this.allowMobDamage = true;
		this.allowEndermanMoveBlocks = true;
		this.allowPotionEffects = true;
		this.allowChestInteraction = true;
		this.allowHangingItems = true;
		this.allowPVP = true;
		this.allowItemInteraction = true;
        this.allowPlantGrowth = true;
        this.allowPlantSpread = true;
        this.allowFireSpread = true;
		this.allowPlayerSleep = true;
		this.allowBlockChangesByEntities = true;
		this.allowLeafDecay = true;
		this.allowBoneMealUsage = true;
		this.overrideChestUsage = true;
        this.allowEnderPearlIn = true;
        this.allowFireIgniteByPlayer = true;
		this.allowPlantTrampling = true;
		this.allowPlayerPickupItems = true;
		this.disallowedBreakBlocks = "";
		this.disallowedPlaceBlocks = "";
	}

	public abstract boolean isLocationInProtectedRegion(UnsafeLocation location);

	public abstract boolean isLocationPvPInProtectedRegion(UnsafeLocation location);

	public String getRegionName() {
		return this.regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public boolean isAllowBrokenBlocks() {
		return this.allowBrokenBlocks;
	}

	public void setAllowBrokenBlocks(boolean allowBrokenBlocks) {
		this.allowBrokenBlocks = allowBrokenBlocks;
	}

	public boolean isAllowPlacedBlocks() {
		return this.allowPlacedBlocks;
	}

	public void setAllowPlacedBlocks(boolean allowPlacedBlocks) {
		this.allowPlacedBlocks = allowPlacedBlocks;
	}

	public boolean isAllowFire() {
		return this.allowFire;
	}

	public void setAllowFire(boolean allowFire) {
		this.allowFire = allowFire;
	}

	public boolean isAllowIceMelt() {
		return this.allowIceMelt;
	}

	public void setAllowIceMelt(boolean allowIceMelt) {
		this.allowIceMelt = allowIceMelt;
	}

	public boolean isAllowTNTExplosions() {
		return this.allowTNTExplosions;
	}

	public void setAllowTNTExplosions(boolean allowTNTExplosions) {
		this.allowTNTExplosions = allowTNTExplosions;
	}

	public boolean isAllowCreeperExplosions() {
		return this.allowCreeperExplosions;
	}

	public void setAllowCreeperExplosions(boolean allowCreeperExplosions) {
		this.allowCreeperExplosions = allowCreeperExplosions;
	}

	public boolean isAllowTNTBlockDamage() {
		return this.allowTNTBlockDamage;
	}

	public void setAllowTNTBlockDamage(boolean allowTNTBlockDamage) {
		this.allowTNTBlockDamage = allowTNTBlockDamage;
	}

	public boolean isAllowCreeperBlockDamage() {
		return this.allowCreeperBlockDamage;
	}

	public void setAllowCreeperBlockDamage(boolean allowCreeperBlockDamage) {
		this.allowCreeperBlockDamage = allowCreeperBlockDamage;
	}

	public boolean isAllowPistonUsage() {
		return this.allowPistonUsage;
	}

	public void setAllowPistonUsage(boolean allowPistonUsage) {
		this.allowPistonUsage = allowPistonUsage;
	}

	public double getDamageMultiplier() {
		return this.damageMultiplier;
	}

	public void setDamageMultiplier(double damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	public boolean isAllowedBucketPlacements() {
		return this.allowedBucketPlacements;
	}

	public void setAllowedBucketPlacements(boolean allowedBucketPlacements) {
		this.allowedBucketPlacements = allowedBucketPlacements;
	}

	public boolean isAllowBlockMovement() {
		return this.allowBlockMovement;
	}

	public void setAllowBlockMovement(boolean allowBlockMovement) {
		this.allowBlockMovement = allowBlockMovement;
	}

	public boolean isAllowCreatureSpawn() {
		return this.allowCreatureSpawn;
	}

	public void setAllowCreatureSpawn(boolean allowCreatureSpawn) {
		this.allowCreatureSpawn = allowCreatureSpawn;
	}

	public boolean isHealPlayers() {
		return this.healPlayers;
	}

	public void setHealPlayers(boolean healPlayers) {
		this.healPlayers = healPlayers;
	}

	public boolean isFeedPlayers() {
		return this.feedPlayers;
	}

	public void setFeedPlayers(boolean feedPlayers) {
		this.feedPlayers = feedPlayers;
	}

	public boolean isAllowEnderPearls() {
		return this.allowEnderPearls;
	}

	public void setAllowEnderPearls(boolean allowEnderPearls) {
		this.allowEnderPearls = allowEnderPearls;
	}

	public boolean isAllowMobDamage() {
		return this.allowMobDamage;
	}

	public void setAllowMobDamage(boolean allowMobDamage) {
		this.allowMobDamage = allowMobDamage;
	}

	public boolean isAllowEndermanMoveBlocks() {
		return this.allowEndermanMoveBlocks;
	}

	public void setAllowEndermanMoveBlocks(boolean allowEndermanMoveBlocks) {
		this.allowEndermanMoveBlocks = allowEndermanMoveBlocks;
	}

	public boolean isAllowPotionEffects() {
		return this.allowPotionEffects;
	}

	public void setAllowPotionEffects(boolean allowPotionEffects) {
		this.allowPotionEffects = allowPotionEffects;
	}

	public boolean isAllowChestInteraction() {
		return this.allowChestInteraction;
	}

	public void setAllowChestInteraction(boolean allowChestInteraction) {
		this.allowChestInteraction = allowChestInteraction;
	}

	public boolean isAllowHangingItems() {
		return this.allowHangingItems;
	}

	public void setAllowHangingItems(boolean allowHangingItems) {
		this.allowHangingItems = allowHangingItems;
	}

	public boolean isAllowPVP() {
		return this.allowPVP;
	}

	public void setAllowPVP(boolean allowPVP) {
		this.allowPVP = allowPVP;
	}

	public boolean isAllowItemInteraction() {
		return this.allowItemInteraction;
	}

	public void setAllowItemInteraction(boolean allowItemInteraction) {
		this.allowItemInteraction = allowItemInteraction;
	}

	public boolean isChangeMobDamageToPlayer() {
		return this.changeMobDamageToPlayer;
	}

	public void setChangeMobDamageToPlayer(boolean changeMobDamageToPlayer) {
		this.changeMobDamageToPlayer = changeMobDamageToPlayer;
	}

    public boolean isAllowPlantGrowth() {
        return this.allowPlantGrowth;
    }

    public void setAllowPlantGrowth(boolean allowPlantGrowth) {
        this.allowPlantGrowth = allowPlantGrowth;
    }

	public boolean isAllowPlantSpread() {
		return this.allowPlantSpread;
	}

	public void setAllowPlantSpread(boolean allowPlantSpread) {
		this.allowPlantSpread = allowPlantSpread;
	}

	public boolean isAllowFireSpread() {
		return this.allowFireSpread;
	}

	public void setAllowFireSpread(boolean allowFireSpread) {
		this.allowFireSpread = allowFireSpread;
	}

	public boolean isAllowPlayerSleep() {
		return this.allowPlayerSleep;
	}

	public void setAllowPlayerSleep(boolean allowPlayerSleep) {
		this.allowPlayerSleep = allowPlayerSleep;
	}

	public boolean isAllowBlockChangesByEntities() {
		return this.allowBlockChangesByEntities;
	}

	public void setAllowBlockChangesByEntities(boolean allowBlockChangesByEntities) {
		this.allowBlockChangesByEntities = allowBlockChangesByEntities;
	}

	public boolean isAllowLeafDecay() {
		return this.allowLeafDecay;
	}

	public void setAllowLeafDecay(boolean allowLeafDecay) {
		this.allowLeafDecay = allowLeafDecay;
	}

	public boolean isAllowBoneMealUsage() {
		return this.allowBoneMealUsage;
	}

	public void setAllowBoneMealUsage(boolean allowBoneMealUsage) {
		this.allowBoneMealUsage = allowBoneMealUsage;
	}

	public boolean isOverrideChestUsage() {
		return this.overrideChestUsage;
	}

	public void setOverrideChestUsage(boolean overrideChestUsage) {
		this.overrideChestUsage = overrideChestUsage;
	}

    public boolean isAllowEnderPearlIn() {
        return this.allowEnderPearlIn;
    }

    public void setAllowEnderPearlIn(boolean allowEnderPearlIn) {
        this.allowEnderPearlIn = allowEnderPearlIn;
    }

    public boolean isAllowFireIgniteByPlayer() {
        return this.allowFireIgniteByPlayer;
    }

    public void setAllowFireIgniteByPlayer(boolean allowFireIgniteByPlayer) {
        this.allowFireIgniteByPlayer = allowFireIgniteByPlayer;
    }

	public boolean isAllowPlantTrampling() {
		return this.allowPlantTrampling;
	}

	public void setAllowPlantTrampling(boolean allowPlantTrampling) {
		this.allowPlantTrampling = allowPlantTrampling;
	}

	public void setDisallowedPlaceBlocks(String blocks) {
		this.disallowedPlaceBlocks = blocks;
	}

	public void setDisallowedBreakBlocks(String blocks) {
		this.disallowedBreakBlocks = blocks;
	}

	public String getDisallowedPlaceBlocks() {
		return this.disallowedPlaceBlocks;
	}

	public String getDisallowedBreakBlocks() {
		return this.disallowedBreakBlocks;
	}

	public boolean isAllowPlayerPickupItems() {
		return this.allowPlayerPickupItems;
	}

	public void setAllowPlayerPickupItems(boolean allowPlayerPickupItems) {
		this.allowPlayerPickupItems = allowPlayerPickupItems;
	}

}
