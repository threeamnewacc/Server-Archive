package club.minemen.events.util.region;

import club.minemen.core.util.CustomLocation;

public interface Region {

	boolean contains(CustomLocation customLocation);

	boolean contains(double x, double y, double z);

}
