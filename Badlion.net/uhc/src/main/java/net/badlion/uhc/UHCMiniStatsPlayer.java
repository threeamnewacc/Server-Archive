package net.badlion.uhc;

import net.badlion.ministats.MiniStats;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.MiniStatsPlayerCreator;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UHCMiniStatsPlayer extends MiniStatsPlayer {

	public static class UHCMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet resultSet) {
			UHCMiniStatsPlayer uhcMiniStatsPlayer;
			try {
				uhcMiniStatsPlayer = new UHCMiniStatsPlayer(uuid, resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			// Add UHC Stats
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
			if (uhcPlayer != null && uhcPlayer.trackStats()) {
				uhcMiniStatsPlayer.addLevels(uhcPlayer.getLevels());
				uhcMiniStatsPlayer.addHeartsHealed(uhcPlayer.getHeartsHealed());
				uhcMiniStatsPlayer.addHorsesTamed(uhcPlayer.getHorses());
				uhcMiniStatsPlayer.addFallDamage(uhcPlayer.getHorses());
				uhcMiniStatsPlayer.addAbsorptionHearts(uhcPlayer.getAbsorptionHearts());
				uhcMiniStatsPlayer.addGoldenHeads(uhcPlayer.getGoldenHeads());
				uhcMiniStatsPlayer.addGoldenApples(uhcPlayer.getGoldenApples());
				uhcMiniStatsPlayer.addNetherPortals(uhcPlayer.getNetherPortalsEntered());
				uhcMiniStatsPlayer.addEndPortals(uhcPlayer.getEndPortalsEntered());

				uhcMiniStatsPlayer.setBlocksBroken(MiniStats.mergeJSON(uhcMiniStatsPlayer.getBlocksBroken(), uhcPlayer.getBlocksBroken()));
				uhcMiniStatsPlayer.setAnimalMobs(MiniStats.mergeJSON(uhcMiniStatsPlayer.getAnimalMobs(), uhcPlayer.getAnimalMobsKilled()));
				uhcMiniStatsPlayer.setPotions(MiniStats.mergeJSON(uhcMiniStatsPlayer.getPotions(), uhcPlayer.getPotions()));
			}

			return uhcMiniStatsPlayer;
		}

	}

    private int levels;
    private int heartsHealed;
    private int horsesTamed;
    private double fallDamage;
    private int absorptionHearts;
    private int goldenHeads;
    private int goldenApples;
    private int netherPortals;
    private int endPortals;
    private JSONObject blocksBroken = new JSONObject();
    private JSONObject animalMobs = new JSONObject();
    private JSONObject potions = new JSONObject();

    public UHCMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
        super(uuid, rs);

	    this.numOfInsertParams += 12;

	    if (rs == null) {
		    return;
	    }

        this.levels = rs.getInt("levels");
        this.heartsHealed = rs.getInt("hearts_healed");
        this.horsesTamed = rs.getInt("horses_tamed");
        this.fallDamage = rs.getDouble("fall_damage");
        this.absorptionHearts = rs.getInt("absorption_hearts");
        this.goldenHeads = rs.getInt("golden_heads");
        this.goldenApples = rs.getInt("golden_apples");
        this.netherPortals = rs.getInt("nether_portals");
        this.endPortals = rs.getInt("end_portals");
        this.blocksBroken = (JSONObject) JSONValue.parse(rs.getString("blocks_broken"));
        this.animalMobs = (JSONObject) JSONValue.parse(rs.getString("animal_mobs"));
        this.potions = (JSONObject) JSONValue.parse(rs.getString("potions"));
    }

    protected String getUpdateClause() {
        return super.getUpdateClause() + ", levels = ?, hearts_healed = ?, horses_tamed = ?, fall_damage = ?, absorption_hearts = ?, golden_heads = ?, " +
                       "golden_apples = ?, nether_portals = ?, end_portals = ?, blocks_broken = ?, animal_mobs = ?, potions = ?";
    }

    protected String getInsertClause() {
        return super.getInsertClause() + ", levels, hearts_healed, horses_tamed, fall_damage, absorption_hearts, golden_heads, golden_apples, nether_portals, " +
                       "end_portals, blocks_broken, animal_mobs, potions";
    }

    protected void setUpdateParams(PreparedStatement ps) throws SQLException {
        super.setUpdateParams(ps);

        ps.setInt(this.paramNumber++, this.levels);
        ps.setInt(this.paramNumber++, this.heartsHealed);
        ps.setInt(this.paramNumber++, this.horsesTamed);
        ps.setDouble(this.paramNumber++, this.fallDamage);
        ps.setInt(this.paramNumber++, this.absorptionHearts);
        ps.setInt(this.paramNumber++, this.goldenHeads);
        ps.setInt(this.paramNumber++, this.goldenApples);
        ps.setInt(this.paramNumber++, this.netherPortals);
        ps.setInt(this.paramNumber++, this.endPortals);

        // Make Postgresql compatible objects
        PGobject blocksJsonObject = new PGobject();
        blocksJsonObject.setType("json");
        blocksJsonObject.setValue(this.blocksBroken.toJSONString());

        PGobject animalMobsJsonObject = new PGobject();
        animalMobsJsonObject.setType("json");
        animalMobsJsonObject.setValue(this.animalMobs.toJSONString());

        PGobject potionsJsonObject = new PGobject();
        potionsJsonObject.setType("json");
        potionsJsonObject.setValue(this.potions.toJSONString());

        ps.setObject(this.paramNumber++, blocksJsonObject);
        ps.setObject(this.paramNumber++, animalMobsJsonObject);
        ps.setObject(this.paramNumber++, potionsJsonObject);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            Field[] allFields = UHCMiniStatsPlayer.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getDeclaringClass().equals(UHCMiniStatsPlayer.class)) {
                    jsonObject.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public int getLevels() {
        return levels;
    }

    public int getHeartsHealed() {
        return heartsHealed;
    }

    public int getHorsesTamed() {
        return horsesTamed;
    }

    public double getFallDamage() {
        return fallDamage;
    }

    public int getAbsorptionHearts() {
        return absorptionHearts;
    }

    public int getGoldenHeads() {
        return goldenHeads;
    }

    public int getGoldenApples() {
        return goldenApples;
    }

    public int getNetherPortals() {
        return netherPortals;
    }

    public int getEndPortals() {
        return endPortals;
    }

    public JSONObject getBlocksBroken() {
        return blocksBroken;
    }

    public JSONObject getAnimalMobs() {
        return animalMobs;
    }

    public JSONObject getPotions() {
        return potions;
    }

    public void addLevels(int num) {
        this.levels += num;
    }

    public void addHeartsHealed(int num) {
        this.heartsHealed += num;
    }

    public void addHorsesTamed(int num) {
        this.horsesTamed += num;
    }

    public void addFallDamage(double num) {
        this.fallDamage += num;
    }

    public void addAbsorptionHearts(int num) {
        this.absorptionHearts += num;
    }

    public void addGoldenHeads(int num) {
        this.goldenHeads += num;
    }

    public void addGoldenApples(int num) {
        this.goldenApples += num;
    }

    public void addNetherPortals(int num) {
        this.netherPortals += num;
    }

    public void addEndPortals(int num) {
        this.endPortals += num;
    }

    public void setBlocksBroken(JSONObject blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void setAnimalMobs(JSONObject animalMobs) {
        this.animalMobs = animalMobs;
    }

    public void setPotions(JSONObject potions) {
        this.potions = potions;
    }
}
