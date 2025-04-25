package cc.patrone.practice.wrapper;

import cc.patrone.practice.kit.PlayerKit;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class CustomKitWrapper {
	private final Map<Integer, PlayerKit> kits = new HashMap<>();

	public void setKit(int index, PlayerKit kit) {
		kits.put(index, kit);
	}

	public boolean hasKit(int index) {
		return kits.get(index) != null;
	}

	public PlayerKit getKit(int index) {
		return kits.get(index);
	}

	public void removeKit(int index) {
		kits.remove(index);
	}
}
