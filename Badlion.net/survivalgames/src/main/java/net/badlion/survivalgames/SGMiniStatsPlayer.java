package net.badlion.survivalgames;

import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.MiniStatsPlayerCreator;
import net.badlion.ministats.PlayerData;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SGMiniStatsPlayer extends MiniStatsPlayer {

	public static class SGMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet rs) {
			SGMiniStatsPlayer sgMiniStatsPlayer;
			try {
				sgMiniStatsPlayer = new SGMiniStatsPlayer(uuid, rs);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(uuid);
			sgMiniStatsPlayer.incrementChestsOpened(1, sgPlayer.getNumberTierChestsOpened(1));
			sgMiniStatsPlayer.incrementChestsOpened(2, sgPlayer.getNumberTierChestsOpened(2));
			sgMiniStatsPlayer.incrementSupplyDropsOpened(sgPlayer.getNumberSupplyDropsOpened());

			return sgMiniStatsPlayer;
		}

	}

	private int supplyDropsOpened = 0;

	private Map<Integer, Integer> tierChestsOpened = new HashMap<>();

	public SGMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
		super(uuid, rs);

		this.numOfInsertParams += 3;

		if (rs != null) {
			this.tierChestsOpened.put(1, rs.getInt("tier1_opened"));
			this.tierChestsOpened.put(2, rs.getInt("tier2_opened"));

			this.supplyDropsOpened = rs.getInt("supply_drops_opened");
		} else {
			this.tierChestsOpened.put(1, 0);
			this.tierChestsOpened.put(2, 0);
		}
	}

	@Override
	public void updateWithPlayerData(PlayerData playerData) {
		super.updateWithPlayerData(playerData);

		this.setNumberOfTierChestsOpened(1, ((SGPlayer) playerData).getNumberTierChestsOpened(1));
		this.setNumberOfTierChestsOpened(2, ((SGPlayer) playerData).getNumberTierChestsOpened(2));
		this.setNumberOfSupplyDropsOpened(((SGPlayer) playerData).getNumberSupplyDropsOpened());
	}

	public void incrementChestsOpened(int tier, int amount) {
		this.tierChestsOpened.put(tier, this.tierChestsOpened.get(tier) + amount);
	}

	public int getNumberOfTierChestsOpened(int tier) {
		return this.tierChestsOpened.get(tier);
	}

	public void setNumberOfTierChestsOpened(int tier, int n) {
		this.tierChestsOpened.put(tier, n);
	}

	public void incrementSupplyDropsOpened(int amount) {
		this.supplyDropsOpened += amount;
	}

	public int getNumberOfSupplyDropsOpened() {
		return this.supplyDropsOpened;
	}

	public void setNumberOfSupplyDropsOpened(int n) {
		this.supplyDropsOpened = n;
	}

	protected String getUpdateClause() {
		return super.getUpdateClause() + ", tier1_opened = ?, tier2_opened = ?, supply_drops_opened = ?";
	}

	protected String getInsertClause() {
		return super.getInsertClause() + ", tier1_opened, tier2_opened, supply_drops_opened";
	}

	protected void setUpdateParams(PreparedStatement ps) throws SQLException {
		super.setUpdateParams(ps);

		ps.setInt(this.paramNumber++, this.tierChestsOpened.get(1));
		ps.setInt(this.paramNumber++, this.tierChestsOpened.get(2));
		ps.setInt(this.paramNumber++, this.supplyDropsOpened);
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();

		try {
			Field[] allFields = SGMiniStatsPlayer.class.getDeclaredFields();
			for (Field field : allFields) {
				if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(SGMiniStatsPlayer.class)) {
					jsonObject.put(field.getName(), field.get(this));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

}
