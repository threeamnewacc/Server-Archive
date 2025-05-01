package net.badlion.arenarender;

import net.badlion.gberry.Gberry;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Arena {

	private int minZ = Integer.MAX_VALUE;
	private int maxZ = Integer.MIN_VALUE;
	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private Location origin;
	private String arenaName;
	private String schematicName;
	private Location warp1;
	private Location warp2;
	private String arenaTypes = "";
	private List<JSONArray> chunkList = new ArrayList();

	public Arena(String arenaName, String schematicName, Location warp1, Location warp2, String arenaTypes) {
		this.arenaName = arenaName;
		this.schematicName = schematicName;
		this.warp1 = warp1;
		this.warp2 = warp2;
		this.arenaTypes = arenaTypes;
	}

	public String getArenaName() {
		return this.arenaName;
	}

	public String getSchematicName() {
		return this.schematicName;
	}

	public Location getWarp1() {
		return this.warp1;
	}

	public Location getWarp2() {
		return this.warp2;
	}

	public Location getWarp1Origin() {
		int x = this.origin.getBlockX() + this.warp1.getBlockX();
		int y = this.origin.getBlockY() + this.warp1.getBlockY();
		int z = this.origin.getBlockZ() + this.warp1.getBlockZ();

		return new Location(this.warp1.getWorld(), x, y, z, this.warp1.getYaw(), this.warp1.getPitch());
	}

	public Location getWarp2Origin() {
		int x = this.origin.getBlockX() + this.warp2.getBlockX();
		int y = this.origin.getBlockY() + this.warp2.getBlockY();
		int z = this.origin.getBlockZ() + this.warp2.getBlockZ();

		return new Location(this.warp2.getWorld(), x, y, z, this.warp2.getYaw(), this.warp2.getPitch());
	}

	public void setOrigin(Location location) {
		this.origin = location;
	}

	public Location getOrigin() {
		return this.origin;
	}

	public String getArenaTypesString() {
		return this.arenaTypes;
	}

	public int getArenaLengthX() {
		return 0;
	}

	public int getArenaLengthZ() {
		return 0;
	}

	public List<JSONArray> getChunkList() {
		return this.chunkList;
	}

	public void checkForMinMaxLocation(int x, int y, int z) {
		if (this.maxX < x) {
			this.maxX = x;
		}
		if (this.maxY < y) {
			this.maxY = y;
		}
		if (this.maxZ < z) {
			this.maxZ = z;
		}
		if (this.minX > x) {
			this.minX = x;
		}
		if (this.minY > y) {
			this.minY = y;
		}
		if (this.minZ > z) {
			this.minZ = z;
		}
	}

	public void generateRenderConfig() {
		JSONObject data = new JSONObject();
		for (Object keyValue : ArenaRender.defaultRederConfig.keySet()) {
			String key = (String) keyValue;
			if ((!key.equals("name")) && (!key.equals("chunkList")) && (!key.equals("camera"))) {
				data.put(key, ArenaRender.defaultRederConfig.get(key));
			}
		}
		data.put("name", getArenaName());
		JSONArray array = new JSONArray();
		array.addAll(this.chunkList);
		data.put("chunkList", array);
		JSONObject cameraData = new JSONObject();
		cameraData.put("projectionMode", "PINHOLE");
		cameraData.put("fov", Double.valueOf(70.0D));
		cameraData.put("dof", Integer.valueOf(500));
		cameraData.put("focalOffset", Double.valueOf(45.0D));

		JSONObject pos = new JSONObject();

		pos.put("x", Double.valueOf(this.minX + 8.0D));
		pos.put("y", Double.valueOf(110.0D));
		pos.put("z", Double.valueOf(this.minZ + 8.0D));

		cameraData.put("position", pos);

		JSONObject orientation = new JSONObject();

		orientation.put("roll", Double.valueOf(0.0D));
		orientation.put("pitch", Double.valueOf(Math.toRadians(-75.0D)));

		double yaw = Math.toRadians(130.0D);

		orientation.put("yaw", Double.valueOf(yaw));

		cameraData.put("orientation", orientation);

		data.put("camera", cameraData);

		String jsonString = data.toJSONString();

		File jsonFile = new File(ArenaRender.renderConfigs, getSchematicName() + ".json");
		try {
			FileUtils.write(jsonFile, Gberry.formatJSON(jsonString));
		} catch (IOException e) {
			ArenaRender.getInstance().getLogger().info("Failed to write arena string " + getArenaName());
		}
	}

}
