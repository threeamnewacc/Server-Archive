package net.badlion.gguard;

import java.util.ArrayList;

public class ProtectedRegion extends AbstractRegion {

	/**
	 * This class represents a rectangular region. The name of this
	 * class is ProtectedRegion in order to preserve all the old usages.
	 */

	private UnsafeLocation location1;
	private UnsafeLocation location2;

	// Recursion crap
	private ArrayList<ProtectedRegion> protectedRegions = new ArrayList<>();

	public ProtectedRegion(String regionName, UnsafeLocation location1, UnsafeLocation location2) {
		super(regionName);

		// Fix the locations myself if I need to
		double smallX, largeX, smallY, largeY, smallZ, largeZ;

		if (location1.getX() <= location2.getX()) {
			smallX = location1.getX();
			largeX = location2.getX();
		} else {
			smallX = location2.getX();
			largeX = location1.getX();
		}

		if (location1.getY() <= location2.getY()) {
			smallY = location1.getY();
			largeY = location2.getY();
		} else {
			smallY = location2.getY();
			largeY = location1.getY();
		}

		if (location1.getZ() <= location2.getZ()) {
			smallZ = location1.getZ();
			largeZ = location2.getZ();
		} else {
			smallZ = location2.getZ();
			largeZ = location1.getZ();
		}

		// Ok regions are fixed now, re-create the actual region
		this.location1 = new UnsafeLocation(location1.getWorldName(), smallX, smallY, smallZ);
		this.location2 = new UnsafeLocation(location1.getWorldName(), largeX, largeY, largeZ);
	}

	@Override
	public boolean isLocationInProtectedRegion(UnsafeLocation location) {
		if (!location.getWorldName().equals(this.location1.getWorldName())) {
			return false;
		}

		return ((location.getX() >= this.location1.getX() && location.getX() <= this.location2.getX()) &&
				(location.getY() >= this.location1.getY() && location.getY() <= this.location2.getY()) &&
				(location.getZ() >= this.location1.getZ() && location.getZ() <= this.location2.getZ()));
	}

	@Override
	public boolean isLocationPvPInProtectedRegion(UnsafeLocation location) {
		if (!location.getWorldName().equals(this.location1.getWorldName())) {
			return false;
		}

		return ((location.getX() >= this.location1.getX() && location.getX() <= (this.location2.getX() + 1)) &&
				(location.getY() >= this.location1.getY() && location.getY() <= this.location2.getY()) &&
				(location.getZ() >= this.location1.getZ() && location.getZ() <= (this.location2.getZ() + 1)));
	}

	public UnsafeLocation getLocation1() {
		return this.location1;
	}

	public void setLocation1(UnsafeLocation location1) {
		this.location1 = this.location1;
	}

	public UnsafeLocation getLocation2() {
		return this.location2;
	}

	public void setLocation2(UnsafeLocation location2) {
		this.location2 = this.location2;
	}

	public ArrayList<ProtectedRegion> getProtectedRegions() {
		return this.protectedRegions;
	}

	public void setProtectedRegions(ArrayList<ProtectedRegion> protectedRegions) {
		this.protectedRegions = protectedRegions;
	}

}
