package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.helper.ItemStackHelper;
import net.badlion.arenacommon.util.ItemStackUtil;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitRuleSet extends BukkitUtil.Listener {

	public enum KnockbackType {
		NON_SPEED(1.9, 0.50, 0.34, 0.4, 0.6, 0.125),
		SPEED_II(2.0, 0.445, 0.35, 0.4, 0.38, 0.089);

		private double knockbackFriction;
		private double knockbackHorizontal;
		private double knockbackVertical;
		private double knockbackVerticalLimit;
		private double knockbackExtraHorizontal;
		private double knockbackExtraVertical;

		private KnockbackType(double knockbackFriction, double knockbackHorizontal, double knockbackVertical,
		                      double knockbackVerticalLimit, double knockbackExtraHorizontal, double knockbackExtraVertical) {
			this.knockbackFriction = knockbackFriction;
			this.knockbackHorizontal = knockbackHorizontal;
			this.knockbackVertical = knockbackVertical;
			this.knockbackVerticalLimit = knockbackVerticalLimit;
			this.knockbackExtraHorizontal = knockbackExtraHorizontal;
			this.knockbackExtraVertical = knockbackExtraVertical;
		}

		public double getKnockbackFriction() {
			return this.knockbackFriction;
		}

		public void setKnockbackFriction(double knockbackFriction) {
			this.knockbackFriction = knockbackFriction;
		}

		public double getKnockbackHorizontal() {
			return this.knockbackHorizontal;
		}

		public void setKnockbackHorizontal(double knockbackHorizontal) {
			this.knockbackHorizontal = knockbackHorizontal;
		}

		public double getKnockbackVertical() {
			return this.knockbackVertical;
		}

		public void setKnockbackVertical(double knockbackVertical) {
			this.knockbackVertical = knockbackVertical;
		}

		public double getKnockbackVerticalLimit() {
			return this.knockbackVerticalLimit;
		}

		public void setKnockbackVerticalLimit(double knockbackVerticalLimit) {
			this.knockbackVerticalLimit = knockbackVerticalLimit;
		}

		public double getKnockbackExtraHorizontal() {
			return this.knockbackExtraHorizontal;
		}

		public void setKnockbackExtraHorizontal(double knockbackExtraHorizontal) {
			this.knockbackExtraHorizontal = knockbackExtraHorizontal;
		}

		public double getKnockbackExtraVertical() {
			return this.knockbackExtraVertical;
		}

		public void setKnockbackExtraVertical(double knockbackExtraVertical) {
			this.knockbackExtraVertical = knockbackExtraVertical;
		}

	}

	private static List<KitRuleSet> unrankedKits;
	private static Map<String, KitRuleSet> kitRuleSets = new LinkedHashMap<>(); // Linked just so we can preserve a nice order
	private static Map<Integer, KitRuleSet> kitRuleSetsIds = new LinkedHashMap<>(); // Linked just so we can preserve a nice order
	private static Map<KitRuleSet, ItemStack> kitRuleSetItemStacks = new LinkedHashMap<>(); // Linked just so we can preserve a nice order

	public static final int CUSTOM_LADDER_ID = 0;
	public static final int VANILLA_LADDER_ID = 1;
	public static final int HCF_LADDER_ID = 2;
	public static final int LEGACY_LADDER_ID = 3;
	public static final int TOURNAMENT_LADDER_ID = 4;
	public static final int ARCHER_LADDER_ID = 5;
	public static final int MINEZ_LADDER_ID = 6;
	public static final int UHC_LADDER_ID = 7;
	public static final int MINEAGE_PVP_LADDER_ID = 8;
	public static final int DIAMOND_OCN_LADDER_ID = 9;
	public static final int CHICKEN_LADDER_ID = 10;
	public static final int IRON_OCN_LADDER_ID = 11;
	public static final int SG_LADDER_ID = 12;
	public static final int ALTERNATIVE_LADDER_ID = 13;
	public static final int BADLION_FACTIONS_LADDER_ID = 14;
	public static final int ARCHON_LADDER_ID = 15;
	public static final int KOHI_LADDER_ID = 16;
	public static final int GAPPLE_LADDER_ID = 17;
	public static final int ORIGIN_MC_LADDER_ID = 18;
	public static final int NO_DEBUFF_LADDER_ID = 19;
	public static final int GOLD_OCN_LADDER_ID = 20;
	public static final int ADVANCED_UHC_LADDER_ID = 21;
	public static final int IRON_SOUP_LADDER_ID = 22;
	public static final int BUFF_SOUP_LADDER_ID = 23;
	public static final int HORSE_LADDER_ID = 24;
	public static final int TANK_LADDER_ID = 25;
	public static final int BUILD_UHC_LADDER_ID = 26;
	public static final int SKYWARS_LADDER_ID = 27;
	public static final int COMBO_LADDER_ID = 28;
	public static final int SPLEEF_LADDER_ID = 29;
	public static final int SG_BUILD_UHC_LADDER_ID = 30;
	public static final int IRON_BUILD_UHC_LADDER_ID = 31;
	public static final int COMBO_BUILD_UHC_LADDER_ID = 32;
	public static final int AXE_PVP_LADDER_ID = 33;

	public static final String ARCHER_LADDER_NAME = "Archer";
	public static final String ADVANCED_UHC_LADDER_NAME = "AdvancedUHC";
	public static final String BUILD_UHC_LADDER_NAME = "BuildUHC";
	public static final String IRON_BUILD_UHC_LADDER_NAME = "IronBuildUHC";
	public static final String COMBO_LADDER_NAME = "Combo";
	public static final String COMBO_BUILD_UHC_LADDER_NAME = "ComboBuildUHC";
	public static final String CUSTOM_LADDER_NAME = "Custom";
	public static final String DIAMOND_LADDER_NAME = "Diamond";
	public static final String EVENT_LADDER_NAME = "Event";
	public static final String GAPPLE_LADDER_NAME = "GApple";
	public static final String HORSE_LADDER_NAME = "Horse";
	public static final String IRON_LADDER_NAME = "Iron";
	public static final String IRON_SOUP_LADDER_NAME = "IronSoup";
	public static final String KOHI_LADDER_NAME = "Debuff";
	public static final String LEGACY_LADDER_NAME = "Legacy";
	public static final String SG_LADDER_NAME = "SG";
	public static final String NODEBUFF_LADDER_NAME = "NoDebuff";
	public static final String MINEZ_LADDER_NAME = "MineZ";
	public static final String SKYWARS_LADDER_NAME = "SkyWars";
	public static final String SPLEEF_LADDER_NAME = "Spleef";
	public static final String UHC_LADDER_NAME = "UHC";
	public static final String VANILLA_LADDER_NAME = "Vanilla";
	public static final String SG_BUILD_UHC_LADDER_NAME = "SG/BuildUHC";
	public static final String AXE_PVP_LADDER_NAME = "AxePvP";

	public static final ArcherRuleSet archerRuleSet = new ArcherRuleSet(KitRuleSet.ARCHER_LADDER_ID, KitRuleSet.ARCHER_LADDER_NAME);
	public static final AdvancedUHCRuleSet advancedUHCRuleSet = new AdvancedUHCRuleSet(KitRuleSet.ADVANCED_UHC_LADDER_ID, KitRuleSet.ADVANCED_UHC_LADDER_NAME);
	public static final BuildUHCRuleSet buildUHCRuleSet = new BuildUHCRuleSet(KitRuleSet.BUILD_UHC_LADDER_ID, KitRuleSet.BUILD_UHC_LADDER_NAME);
	public static final IronBuildUHCRuleSet ironBuildUHCRuleSet = new IronBuildUHCRuleSet(KitRuleSet.IRON_BUILD_UHC_LADDER_ID, KitRuleSet.IRON_BUILD_UHC_LADDER_NAME);
	public static final ComboRuleSet comboRuleSet = new ComboRuleSet(KitRuleSet.COMBO_LADDER_ID, KitRuleSet.COMBO_LADDER_NAME);
	public static final ComboBuildUHCRuleSet comboBuildUHCRuleSet = new ComboBuildUHCRuleSet(KitRuleSet.COMBO_BUILD_UHC_LADDER_ID, KitRuleSet.COMBO_BUILD_UHC_LADDER_NAME);
	public static final CustomRuleSet customRuleSet = new CustomRuleSet(KitRuleSet.CUSTOM_LADDER_ID, KitRuleSet.CUSTOM_LADDER_NAME);
	public static final DiamondOCNRuleSet diamondOCNRuleSet = new DiamondOCNRuleSet(KitRuleSet.DIAMOND_OCN_LADDER_ID, KitRuleSet.DIAMOND_LADDER_NAME);
	public static final GodAppleRuleSet godAppleRuleSet = new GodAppleRuleSet(KitRuleSet.GAPPLE_LADDER_ID, KitRuleSet.GAPPLE_LADDER_NAME);
	public static final HorseRuleSet horseRuleSet = new HorseRuleSet(KitRuleSet.HORSE_LADDER_ID, KitRuleSet.HORSE_LADDER_NAME);
	public static final IronOCNRuleSet ironOCNRuleSet = new IronOCNRuleSet(KitRuleSet.IRON_OCN_LADDER_ID, KitRuleSet.IRON_LADDER_NAME);
	public static final IronSoupRuleSet ironSoupRuleSet = new IronSoupRuleSet(KitRuleSet.IRON_SOUP_LADDER_ID, KitRuleSet.IRON_SOUP_LADDER_NAME);
	public static final KohiRuleSet kohiRuleSet = new KohiRuleSet(KitRuleSet.KOHI_LADDER_ID, KitRuleSet.KOHI_LADDER_NAME);
	public static final LegacyRuleSet legacyRuleSet = new LegacyRuleSet(KitRuleSet.LEGACY_LADDER_ID, KitRuleSet.LEGACY_LADDER_NAME);
	public static final SGRuleSet sgRuleSet = new SGRuleSet(KitRuleSet.SG_LADDER_ID, KitRuleSet.SG_LADDER_NAME);
	public static final NoDebuffRuleSet noDebuffRuleSet = new NoDebuffRuleSet(KitRuleSet.NO_DEBUFF_LADDER_ID, KitRuleSet.NODEBUFF_LADDER_NAME);
	public static final MineZRuleSet minezRuleSet = new MineZRuleSet(KitRuleSet.MINEZ_LADDER_ID, KitRuleSet.MINEZ_LADDER_NAME);
	public static final SkyWarsRuleSet skyWarsRuleSet = new SkyWarsRuleSet(KitRuleSet.SKYWARS_LADDER_ID, KitRuleSet.SKYWARS_LADDER_NAME);
	public static final SpleefRuleSet spleefRuleSet = new SpleefRuleSet(KitRuleSet.SPLEEF_LADDER_ID, KitRuleSet.SPLEEF_LADDER_NAME);
	public static final UHCRuleSet uhcRuleSet = new UHCRuleSet(KitRuleSet.UHC_LADDER_ID, KitRuleSet.UHC_LADDER_NAME);
	public static final VanillaRuleSet vanillaRuleSet = new VanillaRuleSet(KitRuleSet.VANILLA_LADDER_ID, KitRuleSet.VANILLA_LADDER_NAME);
	public static final AxePvPRuleSet axePvPRuleSet = new AxePvPRuleSet(KitRuleSet.AXE_PVP_LADDER_ID, KitRuleSet.AXE_PVP_LADDER_NAME);

	// Special ladder registration
	static {
		List<KitRuleSet> kits = new ArrayList<>();
		kits.add(KitRuleSet.sgRuleSet);
		kits.add(KitRuleSet.buildUHCRuleSet);
	}

	static {
		kitRuleSets.put(archerRuleSet.getName(), archerRuleSet);
		kitRuleSets.put(advancedUHCRuleSet.getName(), advancedUHCRuleSet);
		kitRuleSets.put(buildUHCRuleSet.getName(), buildUHCRuleSet);
		kitRuleSets.put(ironBuildUHCRuleSet.getName(), ironBuildUHCRuleSet);
		kitRuleSets.put(comboRuleSet.getName(), comboRuleSet);
		kitRuleSets.put(comboBuildUHCRuleSet.getName(), comboBuildUHCRuleSet);
		kitRuleSets.put(customRuleSet.getName(), customRuleSet);
		kitRuleSets.put(diamondOCNRuleSet.getName(), diamondOCNRuleSet);
		kitRuleSets.put(godAppleRuleSet.getName(), godAppleRuleSet);
		kitRuleSets.put(horseRuleSet.getName(), horseRuleSet);
		kitRuleSets.put(ironOCNRuleSet.getName(), ironOCNRuleSet);
		kitRuleSets.put(ironSoupRuleSet.getName(), ironSoupRuleSet);
		kitRuleSets.put(kohiRuleSet.getName(), kohiRuleSet);
		kitRuleSets.put(legacyRuleSet.getName(), legacyRuleSet);
		kitRuleSets.put(sgRuleSet.getName(), sgRuleSet);
		kitRuleSets.put(noDebuffRuleSet.getName(), noDebuffRuleSet);
		kitRuleSets.put(minezRuleSet.getName(), minezRuleSet);
		kitRuleSets.put(skyWarsRuleSet.getName(), skyWarsRuleSet);
		kitRuleSets.put(spleefRuleSet.getName(), spleefRuleSet);
		kitRuleSets.put(uhcRuleSet.getName(), uhcRuleSet);
		kitRuleSets.put(vanillaRuleSet.getName(), vanillaRuleSet);
		kitRuleSets.put(axePvPRuleSet.getName(), axePvPRuleSet);

		kitRuleSetsIds.put(archerRuleSet.getId(), archerRuleSet);
		kitRuleSetsIds.put(advancedUHCRuleSet.getId(), advancedUHCRuleSet);
		kitRuleSetsIds.put(buildUHCRuleSet.getId(), buildUHCRuleSet);
		kitRuleSetsIds.put(ironBuildUHCRuleSet.getId(), ironBuildUHCRuleSet);
		kitRuleSetsIds.put(comboRuleSet.getId(), comboRuleSet);
		kitRuleSetsIds.put(comboBuildUHCRuleSet.getId(), comboBuildUHCRuleSet);
		kitRuleSetsIds.put(customRuleSet.getId(), customRuleSet);
		kitRuleSetsIds.put(diamondOCNRuleSet.getId(), diamondOCNRuleSet);
		kitRuleSetsIds.put(godAppleRuleSet.getId(), godAppleRuleSet);
		kitRuleSetsIds.put(horseRuleSet.getId(), horseRuleSet);
		kitRuleSetsIds.put(ironOCNRuleSet.getId(), ironOCNRuleSet);
		kitRuleSetsIds.put(ironSoupRuleSet.getId(), ironSoupRuleSet);
		kitRuleSetsIds.put(kohiRuleSet.getId(), kohiRuleSet);
		kitRuleSetsIds.put(legacyRuleSet.getId(), legacyRuleSet);
		kitRuleSetsIds.put(sgRuleSet.getId(), sgRuleSet);
		kitRuleSetsIds.put(noDebuffRuleSet.getId(), noDebuffRuleSet);
		kitRuleSetsIds.put(minezRuleSet.getId(), minezRuleSet);
		kitRuleSetsIds.put(skyWarsRuleSet.getId(), skyWarsRuleSet);
		kitRuleSetsIds.put(spleefRuleSet.getId(), spleefRuleSet);
		kitRuleSetsIds.put(uhcRuleSet.getId(), uhcRuleSet);
		kitRuleSetsIds.put(vanillaRuleSet.getId(), vanillaRuleSet);
		kitRuleSetsIds.put(axePvPRuleSet.getId(), axePvPRuleSet);

	}


	// Limited time
	//public static final ChickenRuleSet chickenRuleSet = new ChickenRuleSet(KitRuleSet.CHICKEN_LADDER_ID, "Chicken");

	//public static final BuffSoupRuleSet buffSoupRuleSet = new BuffSoupRuleSet(KitRuleSet.BUFF_SOUP_LADDER_ID, "BuffSoup");
	//public static final GoldOCNRuleSet goldOCNRuleSet = new GoldOCNRuleSet(KitRuleSet.GOLD_OCN_LADDER_ID, "Gold");
	//public static final MineZRuleSet mineZRuleSet = new MineZRuleSet(KitRuleSet.MINEZ_LADDER_ID, "MineZ");
	//public static final HCFRuleSet hcfRuleSet = new HCFRuleSet(KitRuleSet.HCF_LADDER_ID, "HCF");
	//public static final BadlionFactionsRuleSet badlionFactionsRuleSet = new BadlionFactionsRuleSet(KitRuleSet.BADLION_FACTIONS_LADDER_ID, "BadlionFactions");
	//public static final ArchonRuleSet archonRuleSet = new ArchonRuleSet(KitRuleSet.ARCHON_LADDER_ID, "Archon");
	//public static final AlternativeRuleSet alternativeRuleSet = new AlternativeRuleSet(KitRuleSet.ALTERNATIVE_LADDER_ID, "Alternative");
	//public static final MineagePvPRuleSet mineagePvPRuleSet = new MineagePvPRuleSet(KitRuleSet.MINEAGE_PVP_LADDER_ID, "MineagePvP");
	//public static final OriginMCRuleSet originMCRuleSet = new OriginMCRuleSet(KitRuleSet.ORIGIN_MC_LADDER_ID, "OriginMC");
	//public static final TournamentRuleSet tournamentRuleSet = new TournamentRuleSet(KitRuleSet.TOURNAMENT_LADDER_ID, "Tournament");

	private int id;
	private String name;
	private boolean usesCustomChests;
	private boolean allowsExtraShields;
	private boolean allowsExtraArmorSets;

	private ArenaCommon.ArenaType arenaType;
	protected ItemStack kitItem;

	private KnockbackType knockbackType;

	protected int maxNoDamageTicks = 20;

	protected boolean enabledInDuels = false;
	protected boolean enabledInEvents = true;

	public Inventory kitCreationInventory;

	public Set<PotionEffect> potionEffects = new HashSet<>();

	public final ItemStack[] defaultArmorKit = new ItemStack[4];
	public final ItemStack[] defaultInventoryKit = new ItemStack[36];

	public final List<ItemStack> tdmKillRewardItems = new ArrayList<>();

	public Map<Enchantment, Integer> validEnchantments = new HashMap<>();

	// Info signs
	protected String[] info1Sign = new String[4];
	protected String[] info2Sign = new String[4];
	protected String[] info3Sign = new String[4];
	protected String[] info4Sign = new String[4];
	protected String[] info5Sign = new String[4];
	protected String[] info6Sign = new String[4];
	protected String[] info7Sign = new String[4];
	protected String[] info8Sign = new String[4];
	protected String[] info9Sign = new String[4];

	public KitRuleSet(int id, String name, ArenaCommon.ArenaType arenaType, KnockbackType knockbackType, boolean usesCustomChests, boolean allowsExtraArmorSets) {
		this.id = id;
		this.name = name;
		this.arenaType = arenaType;
		this.knockbackType = knockbackType;
		this.usesCustomChests = usesCustomChests;
		this.allowsExtraArmorSets = allowsExtraArmorSets;

		this.kitItem = new ItemStack(Material.BOOK);

		this.kitCreationInventory = Gberry.plugin.getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

		this.createInventoryItem();

		KitRuleSet.kitRuleSets.put(this.name, this);
	}

	public KitRuleSet(int id, String name, ItemStack kitItem, ArenaCommon.ArenaType arenaType, KnockbackType knockbackType, boolean usesCustomChests, boolean allowsExtraArmorSets) {
		this.id = id;
		this.name = name;
		this.kitItem = kitItem;
		this.arenaType = arenaType;
		this.knockbackType = knockbackType;
		this.usesCustomChests = usesCustomChests;
		this.allowsExtraArmorSets = allowsExtraArmorSets;

		this.kitCreationInventory = Gberry.plugin.getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

		this.createInventoryItem();

		KitRuleSet.kitRuleSets.put(this.name, this);
	}

	private void createInventoryItem() {
		this.kitItem = ItemStackUtil.createItem(this.kitItem.getType(), this.kitItem.getDurability(),
				ChatColor.GREEN + this.getName(), ChatColor.YELLOW + "Middle click to preview kit");

		KitRuleSet.kitRuleSetItemStacks.put(this, this.kitItem);
	}

	public void applyKnockbackToPlayer(Player player) {
		player.setMaximumNoDamageTicks(this.maxNoDamageTicks);

		player.setKnockbackFriction(this.knockbackType.getKnockbackFriction());
		player.setKnockbackHorizontal(this.knockbackType.getKnockbackHorizontal());
		player.setKnockbackVertical(this.knockbackType.getKnockbackVertical());
		player.setKnockbackVerticalLimit(this.knockbackType.getKnockbackVerticalLimit());
		player.setKnockbackExtraHorizontal(this.knockbackType.getKnockbackExtraHorizontal());
		player.setKnockbackExtraVertical(this.knockbackType.getKnockbackExtraVertical());
	}

	public boolean canBreakBlock(Block block) {
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(block.getLocation(), GGuard.getInstance().getProtectedRegions());

		return region == null || !region.getRegionName().equals("spawn");
	}

	public boolean checkForExtraShieldsOrArmorSets(Player player) {
		boolean hasExtraShieldsOrArmorSets = false;

		// Check inventory
		for (int x = 0; x < 36; x++) {
			ItemStack item = player.getInventory().getItem(x);

			if (item == null || item.getType().equals(Material.AIR)) continue;

			try {
				if (ItemStackHelper.getItemStackTypes().get(item.getType()).equals(ItemStackHelper.ItemType.ARMOR)
				        /*|| ItemStackHelper.getItemStackTypes().get(item.getType()).equals(ItemStackHelper.ItemType.SHIELD)*/) {
					// Remove extra armor
					player.getInventory().setItem(x, null);

					if (!hasExtraShieldsOrArmorSets) {
						hasExtraShieldsOrArmorSets = true;
					}
				}
			} catch (NullPointerException e) {
				player.sendMessage(ChatColor.RED + "You had an illegal item in your inventory, it has been removed.");
				player.getInventory().remove(item);
				Bukkit.getLogger().info(this.getName() + " with item " + item.toString());
				e.printStackTrace();
			}
		}

		return hasExtraShieldsOrArmorSets;
	}

	public boolean validateKit(Player player) {
		boolean invalidItemOrEnchantment = false;

		// Check armor
		for (int x = 100; x < 104; x++) {
			boolean bool = this.validateItem(player, x);

			if (bool && !invalidItemOrEnchantment) {
				invalidItemOrEnchantment = true;
			}
		}

		// Check inventory
		for (int x = 0; x < 36; x++) {
			boolean bool = this.validateItem(player, x);

			if (bool && !invalidItemOrEnchantment) {
				invalidItemOrEnchantment = true;
			}
		}

		return invalidItemOrEnchantment;
	}

	private boolean validateItem(Player player, int inventorySlot) {
		boolean flag = false;

		// Hack since we can't getItem(armor slots)
		ItemStack item = null;
		if (inventorySlot == 103) item = player.getInventory().getHelmet();
		else if (inventorySlot == 102) item = player.getInventory().getChestplate();
		else if (inventorySlot == 101) item = player.getInventory().getLeggings();
		else if (inventorySlot == 100) item = player.getInventory().getBoots();
		else item = player.getInventory().getItem(inventorySlot);

		if (item == null) return false;

		// Does the item have enchants?
		if (item.getEnchantments().size() > 0) {
			for (Enchantment enchantment : item.getEnchantments().keySet()) {
				// Is this enchantment in our validation lists?
				Integer highestLevel = this.validEnchantments.get(enchantment);
				if (highestLevel != null) {
					// Enchantment level too high?
					if (item.getEnchantments().get(enchantment) > highestLevel) {
						item.removeEnchantment(enchantment);
						item.addEnchantment(enchantment, highestLevel);

						flag = true;
					}
				} else {
					// Clear enchant
					item.removeEnchantment(enchantment);

					flag = true;
				}
			}
		}

		// Always update their inventory
		if (flag) {
			player.updateInventory();
		}

		return flag;
	}

	public List<ItemStack> getTDMKillRewardItems() {
		return tdmKillRewardItems;
	}

	public void sendMessages(Player player) {

	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ItemStack getKitItem() {
		return kitItem;
	}

	public boolean isEnabledInDuels() {
		return enabledInDuels;
	}

	public boolean isEnabledInEvents() {
		return enabledInEvents;
	}

	public boolean usesCustomChests() {
		return usesCustomChests;
	}

	public boolean allowsExtraShields() {
		return allowsExtraArmorSets;
	}

	public boolean allowsExtraArmorSets() {
		return allowsExtraArmorSets;
	}

	public Inventory getKitCreationInventory() {
		return kitCreationInventory;
	}

	public Set<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

	public ItemStack[] getDefaultArmorKit() {
		return defaultArmorKit;
	}

	public ItemStack[] getDefaultInventoryKit() {
		return defaultInventoryKit;
	}


	public Map<Enchantment, Integer> getValidEnchantments() {
		return validEnchantments;
	}

	public String[] getInfo1Sign() {
		return info1Sign;
	}

	public String[] getInfo2Sign() {
		return info2Sign;
	}

	public String[] getInfo3Sign() {
		return info3Sign;
	}

	public String[] getInfo4Sign() {
		return info4Sign;
	}

	public String[] getInfo5Sign() {
		return info5Sign;
	}

	public String[] getInfo6Sign() {
		return info6Sign;
	}

	public String[] getInfo7Sign() {
		return info7Sign;
	}

	public String[] getInfo8Sign() {
		return info8Sign;
	}

	public String[] getInfo9Sign() {
		return info9Sign;
	}

	public ArenaCommon.ArenaType getArenaType() {
		return arenaType;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static List<KitRuleSet> getAllKitRuleSets() {
		List<KitRuleSet> kitRuleSetList = new LinkedList<>();
		for (KitRuleSet kitRuleSet : KitRuleSet.kitRuleSets.values()) {
			kitRuleSetList.add(kitRuleSet);
		}

		return Collections.unmodifiableList(kitRuleSetList);
	}

	public static List<KitRuleSet> getUnrankedKits() {
		return unrankedKits;
	}

	public static void setUnrankedKits(List<KitRuleSet> unrankedKits) {
		KitRuleSet.unrankedKits = unrankedKits;
	}

	public static KitRuleSet getKitRuleSet(String kitName) {
		return KitRuleSet.kitRuleSets.get(kitName);
	}

	public static KitRuleSet getKitRuleSet(Integer kitId) {
		return KitRuleSet.kitRuleSetsIds.get(kitId);
	}

	public static KitRuleSet getKitRuleSet(ItemStack itemStack) {
		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
			String displayName = itemStack.getItemMeta().getDisplayName().substring(2);

			KitRuleSet kitRuleSet = KitRuleSet.kitRuleSets.get(displayName);

			if (kitRuleSet == null) { // Must be a custom or event kit rule set
				kitRuleSet = KitRuleSet.kitRuleSets.get(displayName.split(" ")[0]);
			}

			return kitRuleSet;
		}
		return null;
	}

	public static ItemStack getKitRuleSetItem(String kitName) {
		return KitRuleSet.kitRuleSetItemStacks.get(KitRuleSet.kitRuleSets.get(kitName)).clone();
	}

	public static ItemStack getKitRuleSetItem(KitRuleSet kitRuleSet) {
		return KitRuleSet.kitRuleSetItemStacks.get(kitRuleSet).clone();
	}

}
