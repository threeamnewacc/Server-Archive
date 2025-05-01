package net.badlion.gfactions;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FightParticipant {

	private Battle battle;
	private Map<String, Double> mapOfDamageTakenByPlayer = new HashMap<>();
	private Map<String, Double> mapOfDamageTakenByPlayerThisLife = new HashMap<>();
	private Map<String, Long> mapOfLastHitTimeByPlayer = new HashMap<>();
	private Map<String, Double> mapOfHealsTakenByPlayer = new HashMap<>();
	private Map<String, Double> mapOfHealsTakenByPlayerThisLife = new HashMap<>();
	private int kills = 0;
	private int assists = 0;
	private int arrowsShot = 0;
	private int arrowsShotThisLife = 0;
	private Map<PotionEffectType, Integer> debuffsThrown = new HashMap<>();
	private Map<PotionEffectType, Integer> debuffsThrownThisLife = new HashMap<>();
	private Map<PotionEffectType, Integer> debuffsTaken = new HashMap<>();
	private Map<PotionEffectType, Integer> debuffsTakenThisLife = new HashMap<>();
	private double totalDamageTakenThisLife = 0;
	private double totalDamageTaken = 0;
	private int deaths = 0; // most likely will be 1, but might not be sometimes
	private double totalFallDamageTakenThisLife = 0;
	private double totalFallDamage = 0;
	private int totalArmorDamage = 0;
	private int totalArmorDamageGiven = 0;
	private int totalArmorDamageThisLife = 0;
	private int totalArmorDamageGivenThisLife = 0;
	private double totalFoodEaten = 0;
	private double totalFoodEatenThisLife = 0;
	private int godApplesEaten = 0;
	private int godApplesEatenThisLife = 0;
	private int goldenApplesEaten = 0;
	private int goldenApplesEatenThisLife = 0;
	private int potionsDrank = 0;
	private int potionsDrankThisLife = 0;
	private int timeOnKoth = 0;
	private int timeOnKothThisLife = 0;
	private int currentKillStreak = 0;
	private int maxKillStreak = 0;
	private ArrayList<PotionEffect> potionEffects = new ArrayList<>();
	private ArrayList<PotionEffect> potionEffectsThisLife = new ArrayList<>();
	private ArrayList<FightTime> fightTimes = new ArrayList<>();
	private ArrayList<Life> lifeInfo = new ArrayList<>();
	private long lastHitTime = 0;

	public static class FightTime {

		private long enterTime;
		private long exitTime;

		public FightTime(long enterTime) {
			this.enterTime = enterTime;
		}

		public long getEnterTime() {
			return enterTime;
		}

		public void setEnterTime(long enterTime) {
			this.enterTime = enterTime;
		}

		public long getExitTime() {
			return exitTime;
		}

		public void setExitTime(long exitTime) {
			this.exitTime = exitTime;
		}
	}

	public static class Life {

		private FightParticipant fightParticipant;
		private long deathTime;
		private ItemStack killerItem;
		private List<ItemStack> drops;
		private Map<String, Double> mapOfDamageTakenByPlayer = new HashMap<>();
		private Map<String, Double> mapOfHealsTakenByPlayer = new HashMap<>();
		private Map<PotionEffectType, Integer> debuffsThrown = new HashMap<>();
		private Map<PotionEffectType, Integer> debuffsTaken = new HashMap<>();
		private double totalDamageTaken = 0;
		private int arrowsShot = 0;
		private int timeOnKoth = 0;
		private double totalFoodEaten = 0;
		private int godApplesEaten = 0;
		private int goldenApplesEaten = 0;
		private double totalFallDamage = 0;
		private int totalArmorDamage = 0;
		private int totalArmorDamageGiven = 0;
		private int potionsDrankThisLife = 0;
		private ArrayList<PotionEffect> potionEffects = new ArrayList<>();

		public Life(FightParticipant fightParticipant, long deathTime, ItemStack killerItem, List<ItemStack> drops) {
			this.fightParticipant = fightParticipant;
			this.deathTime = deathTime;
			this.killerItem = killerItem;
			this.drops = drops;

			// Copy data
			this.mapOfDamageTakenByPlayer = this.fightParticipant.getMapOfDamageTakenByPlayerThisLife();
			this.mapOfHealsTakenByPlayer = this.fightParticipant.getMapOfHealsTakenByPlayerThisLife();
			this.debuffsThrown = this.fightParticipant.getDebuffsThrownThisLife();
			this.debuffsTaken = this.fightParticipant.getDebuffsTakenThisLife();
			this.totalDamageTaken = this.fightParticipant.getTotalDamageTakenThisLife();
			this.arrowsShot = this.fightParticipant.getArrowsShotThisLife();
			this.timeOnKoth = this.fightParticipant.getTimeOnKothThisLife();
			this.totalFoodEaten = this.fightParticipant.getTotalFoodEatenThisLife();
			this.godApplesEaten = this.fightParticipant.getGodApplesEatenThisLife();
			this.totalFallDamage = this.fightParticipant.getTotalFallDamageTakenThisLife();
			this.totalArmorDamage = this.fightParticipant.getTotalArmorDamageThisLife();
			this.totalArmorDamageGiven = this.fightParticipant.getTotalArmorDamageGivenThisLife();
			this.potionsDrankThisLife = this.fightParticipant.getPotionsDrankThisLife();
			this.potionEffects = this.fightParticipant.getPotionEffectsThisLife();

			// Reset data for this life
			this.fightParticipant.setMapOfDamageTakenByPlayerThisLife(new HashMap<String, Double>());
			this.fightParticipant.setMapOfHealsTakenByPlayerThisLife(new HashMap<String, Double>());
			this.fightParticipant.setDebuffsThrownThisLife(new HashMap<PotionEffectType, Integer>());
			this.fightParticipant.setDebuffsTakenThisLife(new HashMap<PotionEffectType, Integer>());
			this.fightParticipant.setTotalDamageTakenThisLife(0);
			this.fightParticipant.setArrowsShotThisLife(0);
			this.fightParticipant.setTotalFoodEatenThisLife(0);
			this.fightParticipant.setGodApplesEatenThisLife(0);
			this.fightParticipant.setTotalFallDamageTakenThisLife(0);
			this.fightParticipant.setTotalArmorDamageThisLife(0);
			this.fightParticipant.setTotalArmorDamageGivenThisLife(0);
			this.fightParticipant.setPotionsDrankThisLife(0);
			this.fightParticipant.setPotionEffectsThisLife(new ArrayList<PotionEffect>());
		}

		public long getDeathTime() {
			return deathTime;
		}

		public void setDeathTime(long deathTime) {
			this.deathTime = deathTime;
		}

		public ItemStack getKillerItem() {
			return killerItem;
		}

		public void setKillerItem(ItemStack killerItem) {
			this.killerItem = killerItem;
		}

		public List<ItemStack> getDrops() {
			return drops;
		}

		public void setDrops(List<ItemStack> drops) {
			this.drops = drops;
		}

		public FightParticipant getFightParticipant() {
			return fightParticipant;
		}

		public void setFightParticipant(FightParticipant fightParticipant) {
			this.fightParticipant = fightParticipant;
		}

		public Map<String, Double> getMapOfDamageTakenByPlayer() {
			return mapOfDamageTakenByPlayer;
		}

		public void setMapOfDamageTakenByPlayer(Map<String, Double> mapOfDamageTakenByPlayer) {
			this.mapOfDamageTakenByPlayer = mapOfDamageTakenByPlayer;
		}

		public Map<String, Double> getMapOfHealsTakenByPlayer() {
			return mapOfHealsTakenByPlayer;
		}

		public void setMapOfHealsTakenByPlayer(Map<String, Double> mapOfHealsTakenByPlayer) {
			this.mapOfHealsTakenByPlayer = mapOfHealsTakenByPlayer;
		}

		public Map<PotionEffectType, Integer> getDebuffsThrown() {
			return debuffsThrown;
		}

		public void setDebuffsThrown(Map<PotionEffectType, Integer> debuffsThrown) {
			this.debuffsThrown = debuffsThrown;
		}

		public Map<PotionEffectType, Integer> getDebuffsTaken() {
			return debuffsTaken;
		}

		public void setDebuffsTaken(Map<PotionEffectType, Integer> debuffsTaken) {
			this.debuffsTaken = debuffsTaken;
		}

		public double getTotalDamageTaken() {
			return totalDamageTaken;
		}

		public void setTotalDamageTaken(double totalDamageTaken) {
			this.totalDamageTaken = totalDamageTaken;
		}

		public int getArrowsShot() {
			return arrowsShot;
		}

		public void setArrowsShot(int arrowsShot) {
			this.arrowsShot = arrowsShot;
		}

		public int getTimeOnKoth() {
			return timeOnKoth;
		}

		public void setTimeOnKoth(int timeOnKoth) {
			this.timeOnKoth = timeOnKoth;
		}

		public double getTotalFoodEaten() {
			return totalFoodEaten;
		}

		public void setTotalFoodEaten(double totalFoodEaten) {
			this.totalFoodEaten = totalFoodEaten;
		}

		public int getGodApplesEaten() {
			return godApplesEaten;
		}

		public void setGodApplesEaten(int godApplesEaten) {
			this.godApplesEaten = godApplesEaten;
		}

		public int getGoldenApplesEaten() {
			return goldenApplesEaten;
		}

		public void setGoldenApplesEaten(int goldenApplesEaten) {
			this.goldenApplesEaten = goldenApplesEaten;
		}

		public double getTotalFallDamage() {
			return totalFallDamage;
		}

		public void setTotalFallDamage(double totalFallDamage) {
			this.totalFallDamage = totalFallDamage;
		}

		public int getTotalArmorDamage() {
			return totalArmorDamage;
		}

		public void setTotalArmorDamage(int totalArmorDamage) {
			this.totalArmorDamage = totalArmorDamage;
		}

		public int getTotalArmorDamageGiven() {
			return totalArmorDamageGiven;
		}

		public void setTotalArmorDamageGiven(int totalArmorDamageGiven) {
			this.totalArmorDamageGiven = totalArmorDamageGiven;
		}

		public ArrayList<PotionEffect> getPotionEffects() {
			return potionEffects;
		}

		public void setPotionEffects(ArrayList<PotionEffect> potionEffects) {
			this.potionEffects = potionEffects;
		}

		public int getPotionsDrankThisLife() {
			return potionsDrankThisLife;
		}

		public void setPotionsDrankThisLife(int potionsDrankThisLife) {
			this.potionsDrankThisLife = potionsDrankThisLife;
		}
	}

	public FightParticipant() {
		this.battle = null;
	}

	public Battle getBattle() {
		return battle;
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public Map<String, Double> getMapOfDamageTakenByPlayer() {
		return mapOfDamageTakenByPlayer;
	}

	public void setMapOfDamageTakenByPlayer(Map<String, Double> mapOfDamageTakenByPlayer) {
		this.mapOfDamageTakenByPlayer = mapOfDamageTakenByPlayer;
	}

	public Map<String, Long> getMapOfLastHitTimeByPlayer() {
		return mapOfLastHitTimeByPlayer;
	}

	public void setMapOfLastHitTimeByPlayer(Map<String, Long> mapOfLastHitTimeByPlayer) {
		this.mapOfLastHitTimeByPlayer = mapOfLastHitTimeByPlayer;
	}

	public Map<String, Double> getMapOfHealsTakenByPlayer() {
		return mapOfHealsTakenByPlayer;
	}

	public void setMapOfHealsTakenByPlayer(Map<String, Double> mapOfHealsTakenByPlayer) {
		this.mapOfHealsTakenByPlayer = mapOfHealsTakenByPlayer;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public int getArrowsShot() {
		return arrowsShot;
	}

	public void setArrowsShot(int arrowsShot) {
		this.arrowsShot = arrowsShot;
	}

	public Map<PotionEffectType, Integer> getDebuffsThrown() {
		return debuffsThrown;
	}

	public void setDebuffsThrown(Map<PotionEffectType, Integer> debuffsThrown) {
		this.debuffsThrown = debuffsThrown;
	}

	public Map<PotionEffectType, Integer> getDebuffsTaken() {
		return debuffsTaken;
	}

	public void setDebuffsTaken(Map<PotionEffectType, Integer> debuffsTaken) {
		this.debuffsTaken = debuffsTaken;
	}

	public double getTotalDamageTaken() {
		return totalDamageTaken;
	}

	public void setTotalDamageTaken(double totalDamageTaken) {
		this.totalDamageTaken = totalDamageTaken;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public double getTotalFallDamage() {
		return totalFallDamage;
	}

	public void setTotalFallDamage(double totalFallDamage) {
		this.totalFallDamage = totalFallDamage;
	}

	public int getTotalArmorDamage() {
		return totalArmorDamage;
	}

	public void setTotalArmorDamage(int totalArmorDamage) {
		this.totalArmorDamage = totalArmorDamage;
	}

	public int getTotalArmorDamageGiven() {
		return totalArmorDamageGiven;
	}

	public void setTotalArmorDamageGiven(int totalArmorDamageGiven) {
		this.totalArmorDamageGiven = totalArmorDamageGiven;
	}

	public double getTotalFoodEaten() {
		return totalFoodEaten;
	}

	public void setTotalFoodEaten(double totalFoodEaten) {
		this.totalFoodEaten = totalFoodEaten;
	}

	public int getGodApplesEaten() {
		return godApplesEaten;
	}

	public void setGodApplesEaten(int godApplesEaten) {
		this.godApplesEaten = godApplesEaten;
	}

	public int getGoldenApplesEaten() {
		return goldenApplesEaten;
	}

	public void setGoldenApplesEaten(int goldenApplesEaten) {
		this.goldenApplesEaten = goldenApplesEaten;
	}

	public int getPotionsDrank() {
		return potionsDrank;
	}

	public void setPotionsDrank(int potionsDrank) {
		this.potionsDrank = potionsDrank;
	}

	public int getTimeOnKoth() {
		return timeOnKoth;
	}

	public void setTimeOnKoth(int timeOnKoth) {
		this.timeOnKoth = timeOnKoth;
	}

	public int getCurrentKillStreak() {
		return currentKillStreak;
	}

	public void setCurrentKillStreak(int currentKillStreak) {
		this.currentKillStreak = currentKillStreak;
	}

	public int getMaxKillStreak() {
		return maxKillStreak;
	}

	public void setMaxKillStreak(int maxKillStreak) {
		this.maxKillStreak = maxKillStreak;
	}

	public ArrayList<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

	public void setPotionEffects(ArrayList<PotionEffect> potionEffects) {
		this.potionEffects = potionEffects;
	}

	public ArrayList<FightTime> getFightTimes() {
		return fightTimes;
	}

	public void setFightTimes(ArrayList<FightTime> fightTimes) {
		this.fightTimes = fightTimes;
	}

	public ArrayList<Life> getDeathInfo() {
		return lifeInfo;
	}

	public void setDeathInfo(ArrayList<Life> lifeInfo) {
		this.lifeInfo = lifeInfo;
	}

	public Map<String, Double> getMapOfDamageTakenByPlayerThisLife() {
		return mapOfDamageTakenByPlayerThisLife;
	}

	public void setMapOfDamageTakenByPlayerThisLife(Map<String, Double> mapOfDamageTakenByPlayerThisLife) {
		this.mapOfDamageTakenByPlayerThisLife = mapOfDamageTakenByPlayerThisLife;
	}

	public Map<String, Double> getMapOfHealsTakenByPlayerThisLife() {
		return mapOfHealsTakenByPlayerThisLife;
	}

	public void setMapOfHealsTakenByPlayerThisLife(Map<String, Double> mapOfHealsTakenByPlayerThisLife) {
		this.mapOfHealsTakenByPlayerThisLife = mapOfHealsTakenByPlayerThisLife;
	}

	public int getArrowsShotThisLife() {
		return arrowsShotThisLife;
	}

	public void setArrowsShotThisLife(int arrowsShotThisLife) {
		this.arrowsShotThisLife = arrowsShotThisLife;
	}

	public Map<PotionEffectType, Integer> getDebuffsThrownThisLife() {
		return debuffsThrownThisLife;
	}

	public void setDebuffsThrownThisLife(Map<PotionEffectType, Integer> debuffsThrownThisLife) {
		this.debuffsThrownThisLife = debuffsThrownThisLife;
	}

	public Map<PotionEffectType, Integer> getDebuffsTakenThisLife() {
		return debuffsTakenThisLife;
	}

	public void setDebuffsTakenThisLife(Map<PotionEffectType, Integer> debuffsTakenThisLife) {
		this.debuffsTakenThisLife = debuffsTakenThisLife;
	}

	public double getTotalDamageTakenThisLife() {
		return totalDamageTakenThisLife;
	}

	public void setTotalDamageTakenThisLife(double totalDamageTakenThisLife) {
		this.totalDamageTakenThisLife = totalDamageTakenThisLife;
	}

	public double getTotalFallDamageTakenThisLife() {
		return totalFallDamageTakenThisLife;
	}

	public void setTotalFallDamageTakenThisLife(double totalFallDamageTakenThisLife) {
		this.totalFallDamageTakenThisLife = totalFallDamageTakenThisLife;
	}

	public int getTotalArmorDamageThisLife() {
		return totalArmorDamageThisLife;
	}

	public void setTotalArmorDamageThisLife(int totalArmorDamageThisLife) {
		this.totalArmorDamageThisLife = totalArmorDamageThisLife;
	}

	public int getTotalArmorDamageGivenThisLife() {
		return totalArmorDamageGivenThisLife;
	}

	public void setTotalArmorDamageGivenThisLife(int totalArmorDamageGivenThisLife) {
		this.totalArmorDamageGivenThisLife = totalArmorDamageGivenThisLife;
	}

	public double getTotalFoodEatenThisLife() {
		return totalFoodEatenThisLife;
	}

	public void setTotalFoodEatenThisLife(double totalFoodEatenThisLife) {
		this.totalFoodEatenThisLife = totalFoodEatenThisLife;
	}

	public int getGodApplesEatenThisLife() {
		return godApplesEatenThisLife;
	}

	public void setGodApplesEatenThisLife(int godApplesEatenThisLife) {
		this.godApplesEatenThisLife = godApplesEatenThisLife;
	}

	public int getGoldenApplesEatenThisLife() {
		return goldenApplesEatenThisLife;
	}

	public void setGoldenApplesEatenThisLife(int goldenApplesEatenThisLife) {
		this.goldenApplesEatenThisLife = goldenApplesEatenThisLife;
	}

	public int getPotionsDrankThisLife() {
		return potionsDrankThisLife;
	}

	public void setPotionsDrankThisLife(int potionsDrankThisLife) {
		this.potionsDrankThisLife = potionsDrankThisLife;
	}

	public int getTimeOnKothThisLife() {
		return timeOnKothThisLife;
	}

	public void setTimeOnKothThisLife(int timeOnKothThisLife) {
		this.timeOnKothThisLife = timeOnKothThisLife;
	}

	public ArrayList<PotionEffect> getPotionEffectsThisLife() {
		return potionEffectsThisLife;
	}

	public void setPotionEffectsThisLife(ArrayList<PotionEffect> potionEffectsThisLife) {
		this.potionEffectsThisLife = potionEffectsThisLife;
	}

	public long getLastHitTime() {
		return lastHitTime;
	}

	public void setLastHitTime(long lastHitTime) {
		this.lastHitTime = lastHitTime;
	}

	public ArrayList<Life> getLifeInfo() {
		return lifeInfo;
	}

	public void setLifeInfo(ArrayList<Life> lifeInfo) {
		this.lifeInfo = lifeInfo;
	}
}
