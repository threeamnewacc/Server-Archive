package us.zonix.core.util;

import java.util.UUID;

public class UUIDType {

	public static UUID fromString(String uuid) {
		if (uuid == null) {
			return null;
		}
		else {
			return UUID.fromString(uuid);
		}
	}

}
