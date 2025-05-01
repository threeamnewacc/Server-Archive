package net.badlion.capturetheflag;


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

public class CTFMiniStatsPlayer extends MiniStatsPlayer {

	public static class CTFMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet rs) {
			CTFMiniStatsPlayer ctfMiniStatsPlayer;
			try {
				ctfMiniStatsPlayer = new CTFMiniStatsPlayer(uuid, rs);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			CTFPlayer ctfPlayer = (CTFPlayer) MPGPlayerManager.getMPGPlayer(uuid);
			ctfMiniStatsPlayer.addFlagHeldTime(ctfPlayer.getFlagHeldTime());
			ctfMiniStatsPlayer.addFlagHoldersKilled(ctfPlayer.getFlagHoldersKilled());
			ctfMiniStatsPlayer.addFlagsDelivered(ctfPlayer.getFlagsDelivered());
			ctfMiniStatsPlayer.addFlagsPickedUp(ctfPlayer.getFlagsPickedUp());

			return ctfMiniStatsPlayer;
		}

	}

    private int flagsPickedUp;
    private int flagsDelivered;
    private long flagHeldTime;
    private int flagHoldersKilled;

    public CTFMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
        super(uuid, rs);

        this.numOfInsertParams += 5;

        if (rs != null) {
            this.flagHeldTime = rs.getLong("flag_held_time");
            this.flagsPickedUp = rs.getInt("flags_picked_up");
            this.flagsDelivered = rs.getInt("flags_delivered");
            this.flagHoldersKilled = rs.getInt("flag_holders_killed");
        } else {
            this.flagHeldTime = 0;
            this.flagsPickedUp = 0;
            this.flagsDelivered = 0;
            this.flagHoldersKilled = 0;
        }
    }

    public void addFlagHeldTime(long amount) {
        this.flagHeldTime += amount;
    }

    public void addFlagsPickedUp(int amount) {
        this.flagsPickedUp += amount;
    }

    public void addFlagsDelivered(int amount) {
        this.flagsDelivered += amount;
    }

    public void addFlagHoldersKilled(int amount) {
        this.flagHoldersKilled += amount;
    }

    public int getFlagsPickedUp() {
        return this.flagsPickedUp;
    }

    public int getFlagsDelivered() {
        return this.flagsDelivered;
    }

    public int getFlagHoldersKilled() {
        return this.flagHoldersKilled;
    }

    public long getFlagHeldTime() {
        return this.flagHeldTime;
    }

    protected String getUpdateClause() {
        return super.getUpdateClause() + ", flag_held_time = ?, flags_picked_up = ?, flags_delivered = ?, flag_holders_killed = ?";
    }

    protected String getInsertClause() {
        return super.getInsertClause() + ", flag_held_time, flags_picked_up, flags_delivered, flag_holders_killed";
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        super.setUpdateParams(ps);

        ps.setLong(this.paramNumber++, this.flagHeldTime);
        ps.setInt(this.paramNumber++, this.flagsPickedUp);
        ps.setInt(this.paramNumber++, this.flagsDelivered);
        ps.setInt(this.paramNumber++, this.flagHoldersKilled);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            Field[] allFields = MiniStatsPlayer.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(CTFMiniStatsPlayer.class)) {
                    jsonObject.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
