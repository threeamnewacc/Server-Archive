package net.badlion.skywars;

import net.badlion.ministats.MiniStatsPlayer;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SWMiniStatsPlayer extends MiniStatsPlayer {

    private int tier1Opened;
    private int tier2Opened;
    private int snowEggShot;
    private int snowEggHit;
    private double snowEggAccuracy;
    private int levels;
    private int mobsSpawned;
    private int blocksPlaced;

    public SWMiniStatsPlayer(ResultSet rs) throws SQLException {
        super(rs);

        this.numOfInsertParams += 8;

        if (rs == null) {
            return;
        }

        this.tier1Opened = rs.getInt("tier1_opened");
        this.tier2Opened = rs.getInt("tier2_opened");
        this.snowEggShot = rs.getInt("snow_egg_shot");
        this.snowEggHit = rs.getInt("snow_egg_hit");
        this.snowEggAccuracy = rs.getDouble("snow_egg_accuracy");
        this.levels = rs.getInt("levels");
        this.mobsSpawned = rs.getInt("mobs_spawned");
        this.blocksPlaced = rs.getInt("blocks_placed");
    }

    protected String getUpdateClause() {
        return super.getUpdateClause() + ", tier1_opened = ?, tier2_opened = ?, snow_egg_shot = ?, snow_egg_hit = ?, snow_egg_accuracy = ?, " +
                       "levels = ?, mobs_spawned = ?, blocks_placed = ?";
    }

    protected String getInsertClause() {
        return super.getInsertClause() + ", tier1_opened, tier2_opened, snow_egg_shot, snow_egg_hit, snow_egg_accuracy, levels, mobs_spawned, blocks_placed";
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        super.setUpdateParams(ps);

        ps.setInt(this.paramNumber++, this.tier1Opened);
        ps.setInt(this.paramNumber++, this.tier2Opened);
        ps.setInt(this.paramNumber++, this.snowEggShot);
        ps.setInt(this.paramNumber++, this.snowEggHit);
        ps.setDouble(this.paramNumber++, this.snowEggAccuracy);
        ps.setInt(this.paramNumber++, this.levels);
        ps.setInt(this.paramNumber++, this.mobsSpawned);
        ps.setInt(this.paramNumber++ , this.blocksPlaced);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            Field[] allFields = SWMiniStatsPlayer.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(SWMiniStatsPlayer.class)) {
                    jsonObject.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void addTier1Opened(int num) {
        this.tier1Opened += num;
    }

    public void addTier2Opened(int num) {
        this.tier2Opened += num;
    }

    public void addSnowEggShot(int num) {
        this.snowEggShot += num;

        if (this.snowEggShot > 0) {
            this.snowEggAccuracy = (double) this.snowEggHit / this.snowEggShot;
        }
    }

    public void addSnowEggHit(int num) {
        this.snowEggHit += num;

        if (this.snowEggShot > 0) {
            this.snowEggAccuracy = (double) this.snowEggHit / this.snowEggShot;
        }
    }

    public void addLevels(int num) {
        this.levels += num;
    }

    public void addMobsSpawned(int num) {
        this.mobsSpawned += num;
    }

    public void addBlocksPlaced(int num) {
        this.blocksPlaced += num;
    }
}
