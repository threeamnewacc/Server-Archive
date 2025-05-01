package net.badlion.uhcmeetup;

import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.MiniStatsPlayerCreator;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UHCMeetupMiniStatsPlayer extends MiniStatsPlayer {

	public static class UHCMeetupMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet resultSet) {
			UHCMeetupMiniStatsPlayer uhcMiniStatsPlayer;
			try {
				uhcMiniStatsPlayer = new UHCMeetupMiniStatsPlayer(uuid, resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			UHCMeetupPlayer uhcMeetupPlayer = (UHCMeetupPlayer) MPGPlayerManager.getMPGPlayer(uuid);
			uhcMiniStatsPlayer.addAbsorptionHearts(uhcMeetupPlayer.getAbsorptionHearts());
			uhcMiniStatsPlayer.addGoldenHeads(uhcMeetupPlayer.getGoldenHeadsEaten());
			uhcMiniStatsPlayer.addGoldenApples(uhcMeetupPlayer.getGoldenApplesEaten());

			return uhcMiniStatsPlayer;
		}

	}

    private int absorptionHearts;
    private int goldenHeadsEaten;
    private int goldenApplesEaten;

    public UHCMeetupMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
        super(uuid, rs);

	    this.numOfInsertParams += 3;

	    if (rs == null) {
		    return;
	    }

        this.absorptionHearts = rs.getInt("absorption_hearts");
        this.goldenHeadsEaten = rs.getInt("golden_heads");
        this.goldenApplesEaten = rs.getInt("golden_apples");
    }

    protected String getUpdateClause() {
        return super.getUpdateClause() + ", absorption_hearts = ?, golden_heads = ?, golden_apples = ?";
    }

    protected String getInsertClause() {
        return super.getInsertClause() + ", absorption_hearts, golden_heads, golden_apples";
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        super.setUpdateParams(ps);

        ps.setInt(this.paramNumber++, this.absorptionHearts);
        ps.setInt(this.paramNumber++, this.goldenHeadsEaten);
        ps.setInt(this.paramNumber++, this.goldenApplesEaten);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            Field[] allFields = UHCMeetupMiniStatsPlayer.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(UHCMeetupMiniStatsPlayer.class)) {
                    jsonObject.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public void addAbsorptionHearts(int num) {
        this.absorptionHearts += num;
    }

	public int getAbsorptionHearts() {
		return this.absorptionHearts;
	}

	public void addGoldenHeads(int num) {
        this.goldenHeadsEaten += num;
    }

	public int getGoldenHeadsEaten() {
		return this.goldenHeadsEaten;
	}

	public void addGoldenApples(int num) {
        this.goldenApplesEaten += num;
    }

	public int getGoldenApplesEaten() {
		return this.goldenApplesEaten;
	}

}
