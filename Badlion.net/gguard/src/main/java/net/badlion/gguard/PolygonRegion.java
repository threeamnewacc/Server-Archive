package net.badlion.gguard;

import net.badlion.gguard.util.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class PolygonRegion extends AbstractRegion {

	private String worldName;

	private List<Vector2D> points = new ArrayList<>();

	public PolygonRegion(String regionName, String worldName, List<Vector2D> points) {
		super(regionName);

		this.worldName = worldName;

		this.points = new ArrayList<>(points);

		recalculate();
	}

	@Override
	public boolean isLocationInProtectedRegion(UnsafeLocation location) {
		if (!location.getWorldName().equals(this.worldName)) {
			return false;
		}

		return this.contains(location);
	}

	@Override
	public boolean isLocationPvPInProtectedRegion(UnsafeLocation location) {
		if (!location.getWorldName().equals(this.worldName)) {
			return false;
		}

		return this.contains(location);
	}

	/**
	 * Recalculate the bounding box of this polygonal region. This should be
	 * called after points have been changed.
	 */
	protected void recalculate() {
		int minX = this.points.get(0).getBlockX();
		int minZ = this.points.get(0).getBlockZ();
		int maxX = this.points.get(0).getBlockX();
		int maxZ = this.points.get(0).getBlockZ();

		for (Vector2D v : this.points) {
			int x = v.getBlockX();
			int z = v.getBlockZ();
			if (x < minX) minX = x;
			if (z < minZ) minZ = z;
			if (x > maxX) maxX = x;
			if (z > maxZ) maxZ = z;
		}
	}

	private boolean contains(UnsafeLocation location) {
		if (this.points.size() < 3) {
			return false;
		}
		int targetX = location.getBlockX(); // Width
		int targetY = location.getBlockY(); // Height
		int targetZ = location.getBlockZ(); // Depth

		boolean inside = false;
		int npoints = this.points.size();
		int xNew, zNew;
		int xOld, zOld;
		int x1, z1;
		int x2, z2;
		long crossproduct;
		int i;

		xOld = this.points.get(npoints - 1).getBlockX();
		zOld = this.points.get(npoints - 1).getBlockZ();

		for (i = 0; i < npoints; ++i) {
			xNew = this.points.get(i).getBlockX();
			zNew = this.points.get(i).getBlockZ();
			//Check for corner
			if (xNew == targetX && zNew == targetZ) {
				return true;
			}
			if (xNew > xOld) {
				x1 = xOld;
				x2 = xNew;
				z1 = zOld;
				z2 = zNew;
			} else {
				x1 = xNew;
				x2 = xOld;
				z1 = zNew;
				z2 = zOld;
			}
			if (x1 <= targetX && targetX <= x2) {
				crossproduct = ((long) targetZ - (long) z1) * (long) (x2 - x1)
						- ((long) z2 - (long) z1) * (long) (targetX - x1);
				if (crossproduct == 0) {
					if ((z1 <= targetZ) == (targetZ <= z2)) return true; // On the edge
				} else if (crossproduct < 0 && (x1 != targetX)) {
					inside = !inside;
				}
			}
			xOld = xNew;
			zOld = zNew;
		}

		return inside;
	}

}
