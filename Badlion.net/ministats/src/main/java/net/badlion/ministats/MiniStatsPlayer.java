package net.badlion.ministats;

import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MiniStatsPlayer {

    protected int paramNumber = 1;
    protected int numOfInsertParams = 17;

    private String uuid;
    private int kills;
    private int deaths;
    private double kdr;
    private int wins;
    private int losses;
    private long timePlayed;
    private double damageDealt;
    private double damageTaken;
    private int highestKillStreak;
    private int swordHits;
    private int swordSwings;
    private double swordAccuracy;
    private int swordBlocks;
    private int bowPunches;
    private int arrowsShot;
    private int arrowsHit;
    private double arrowAccuracy;

    public MiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
	    this.uuid = uuid.toString();

        if (rs == null) {
            return;
        }

        this.kills = rs.getInt("kills");
        this.deaths = rs.getInt("deaths");
        this.kdr = rs.getDouble("kdr");
        this.wins = rs.getInt("wins");
        this.losses = rs.getInt("losses");
        this.timePlayed = rs.getLong("time_played");
        this.damageDealt = rs.getDouble("damage_dealt");
        this.damageTaken = rs.getDouble("damage_taken");
        this.highestKillStreak = rs.getInt("highest_kill_streak");
        this.swordHits = rs.getInt("sword_hits");
        this.swordSwings = rs.getInt("sword_swings");
        this.swordAccuracy = rs.getDouble("sword_accuracy");
        this.swordBlocks = rs.getInt("sword_blocks");
        this.bowPunches = rs.getInt("bow_punches");
        this.arrowsShot = rs.getInt("arrows_shot");
        this.arrowsHit = rs.getInt("arrows_hit");
        this.arrowAccuracy = rs.getDouble("arrow_accuracy");
    }

    public void updateWithPlayerData(PlayerData playerData) {
	    this.addKills(playerData.getKills());
	    this.addDeaths(playerData.getDeaths());
        this.calculateKdr();

        if (playerData.isWonGame()) {
            this.addWin();
        } else {
            this.addLoss();
        }

        this.addTimePlayed(playerData.getTotalTimePlayed());
        this.addDamageDealt(playerData.getDamageDealt());
        this.addDamageTaken(playerData.getDamageTaken());
        this.checkNewKillStreak(playerData.getHighestKillStreak());
        this.addSwordHits(playerData.getSwordHits());
        this.addSwordSwings(playerData.getSwordSwings());
        this.calculateSwordAccuracy();
        this.addSwordBlocks(playerData.getSwordBlocks());
        this.addBowPunches(playerData.getBowPunches());
        this.addArrowsShot(playerData.getArrowsFired());
        this.addArrowsHit(playerData.getArrowsHitTarget());
        this.calculateArrowAccuracy();
    }

    public String getUpdateQuery() {
        String sql = "UPDATE " + MiniStats.TABLE_NAME + " SET " + this.getUpdateClause() + " WHERE uuid = ?;\n";
        sql += "INSERT INTO " + MiniStats.TABLE_NAME + " (" + this.getInsertClause() + ") SELECT " + this.getSelectClause() + " WHERE NOT EXISTS ";
        sql += "(SELECT 1 FROM " + MiniStats.TABLE_NAME + " WHERE uuid = ?);";

        return sql;
    }

    protected String getUpdateClause() {
        return "kills = ?, deaths = ?, kdr = ?, wins = ?, losses = ?, time_played = ?, damage_dealt = ?, damage_taken = ?, highest_kill_streak = ?, " +
                "sword_hits = ?, sword_swings = ?, sword_accuracy = ?, sword_blocks = ?, bow_punches = ?, arrows_shot = ?, arrows_hit = ?, arrow_accuracy = ?";
    }

    protected String getInsertClause() {
        return "uuid, kills, deaths, kdr, wins, losses, time_played, damage_dealt, damage_taken, highest_kill_streak, sword_hits, sword_swings, sword_accuracy, " +
                       "sword_blocks, bow_punches, arrows_shot, arrows_hit, arrow_accuracy";
    }

    private String getSelectClause() {
        StringBuilder builder = new StringBuilder();

        // One extra for uuid
        builder.append("?, ");
        for (int i = 0; i < this.numOfInsertParams; i++) {
            builder.append("?, ");
        }

        return builder.substring(0, builder.length() - 2);
    }

    public void setPreparedStatementParams(PreparedStatement ps) throws SQLException {
        this.setUpdateParams(ps);
        ps.setString(this.paramNumber++, this.uuid);
        this.setInsertParams(ps);
        ps.setString(this.paramNumber++, this.uuid);
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        ps.setInt(this.paramNumber++, this.kills);
        ps.setInt(this.paramNumber++, this.deaths);
        ps.setDouble(this.paramNumber++, this.kdr);
        ps.setInt(this.paramNumber++, this.wins);
        ps.setInt(this.paramNumber++, this.losses);
        ps.setLong(this.paramNumber++, this.timePlayed);
        ps.setDouble(this.paramNumber++, this.damageDealt);
        ps.setDouble(this.paramNumber++, this.damageTaken);
        ps.setInt(this.paramNumber++, this.highestKillStreak);
        ps.setInt(this.paramNumber++, this.swordHits);
        ps.setInt(this.paramNumber++, this.swordSwings);
        ps.setDouble(this.paramNumber++, this.swordAccuracy);
        ps.setInt(this.paramNumber++, this.swordBlocks);
        ps.setInt(this.paramNumber++, this.bowPunches);
        ps.setInt(this.paramNumber++, this.arrowsShot);
        ps.setInt(this.paramNumber++, this.arrowsHit);
        ps.setDouble(this.paramNumber++, this.arrowAccuracy);
    }

    private void setInsertParams(PreparedStatement ps) throws SQLException {
        ps.setString(this.paramNumber++, this.uuid);
        this.setUpdateParams(ps);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            Field[] allFields = MiniStatsPlayer.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(MiniStatsPlayer.class)) {
                    jsonObject.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getKdr() {
        return kdr;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public int getHighestKillStreak() {
        return highestKillStreak;
    }

    public int getSwordHits() {
        return swordHits;
    }

    public int getSwordSwings() {
        return swordSwings;
    }

    public double getSwordAccuracy() {
        return swordAccuracy;
    }

    public int getSwordBlocks() {
        return swordBlocks;
    }

    public int getBowPunches() {
        return bowPunches;
    }

    public int getArrowsShot() {
        return arrowsShot;
    }

    public int getArrowsHit() {
        return arrowsHit;
    }

    public double getArrowAccuracy() {
        return arrowAccuracy;
    }

    public void addKills(int num) {
        this.kills += num;
    }

    public void addDeaths(int num) {
        this.deaths += num;
    }

    public void calculateKdr() {
        if (this.deaths == 0L) {
            this.kdr = this.kills;
        } else if (this.kills == 0L) {
            this.kdr = 0;
        } else {
            this.kdr = (float) this.kills / this.deaths;
        }
    }

    public void addWin() {
        this.wins += 1;
    }

    public void addLoss() {
        this.losses += 1;
    }

    public void addTimePlayed(long num) {
        this.timePlayed += num;
    }

    public void addDamageDealt(double num) {
        this.damageDealt += num;
    }

    public void addDamageTaken(double num) {
        this.damageTaken += num;
    }

    public void checkNewKillStreak(int num) {
        if (this.highestKillStreak < num) {
            this.highestKillStreak = num;
        }
    }

    public void addSwordHits(int num) {
        this.swordHits += num;
    }

    public void addSwordSwings(int num) {
        this.swordSwings += num;
    }

    public void calculateSwordAccuracy() {
        if (this.swordSwings > 0) {
            this.swordAccuracy = (double) this.swordHits / this.swordSwings;
        }
    }

    public void addSwordBlocks(int num) {
        this.swordBlocks += num;
    }

    public void addBowPunches(int num) {
        this.bowPunches += num;
    }

    public void addArrowsShot(int num) {
        this.arrowsShot += num;
    }

    public void addArrowsHit(int num) {
        this.arrowsHit += num;
    }

    public void calculateArrowAccuracy() {
        if (this.arrowsShot > 0) {
            this.arrowAccuracy = (double) this.arrowsHit / this.arrowsShot;
        }
    }

}
