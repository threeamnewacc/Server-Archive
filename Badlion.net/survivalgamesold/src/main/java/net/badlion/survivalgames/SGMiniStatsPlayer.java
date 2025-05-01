package net.badlion.survivalgames;

import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.MiniStatsPlayerCreator;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

			SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(uuid);

			sgMiniStatsPlayer.addTier1Opened(sgPlayer.getTier1ChestsOpened());
			sgMiniStatsPlayer.addTier2Opened(sgPlayer.getTier2ChestsOpened());

			return sgMiniStatsPlayer;
		}

	}

    private int tier1Opened;
    private int tier2Opened;

    public SGMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
        super(uuid, rs);

        this.numOfInsertParams += 2;

        if (rs == null) {
            return;
        }

        this.tier1Opened = rs.getInt("tier1_opened");
        this.tier2Opened = rs.getInt("tier2_opened");
    }

    protected String getUpdateClause() {
        return super.getUpdateClause() + ", tier1_opened = ?, tier2_opened = ?";
    }

    protected String getInsertClause() {
        return super.getInsertClause() + ", tier1_opened, tier2_opened";
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        super.setUpdateParams(ps);

        ps.setInt(this.paramNumber++, this.tier1Opened);
        ps.setInt(this.paramNumber++, this.tier2Opened);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            Field[] allFields = MiniStatsPlayer.class.getDeclaredFields();
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

    public int getTier1Opened() {
        return tier1Opened;
    }

    public int getTier2Opened() {
        return tier2Opened;
    }

    public void addTier1Opened(int num) {
        this.tier1Opened += num;
    }

    public void addTier2Opened(int num) {
        this.tier2Opened += num;
    }
}
