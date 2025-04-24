package club.minemen.events.util.region;

import club.minemen.core.util.CustomLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CuboidRegion implements Region {

	private CustomLocation pointOne, pointTwo;

	@Override
	public boolean contains(CustomLocation customLocation) {
		if (!customLocation.getWorld().equals(this.pointOne.getWorld())) {
			return false;
		}

		return this.contains(customLocation.getX(), customLocation.getY(), customLocation.getZ());
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return x >= this.pointOne.getX() && x <= this.pointTwo.getX() + 1 &&
		       y >= this.pointOne.getY() && y <= this.pointTwo.getY() &&
		       z >= this.pointOne.getZ() && z <= this.pointTwo.getZ() + 1;
	}
}
