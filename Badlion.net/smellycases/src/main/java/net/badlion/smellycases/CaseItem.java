package net.badlion.smellycases;

import net.badlion.gberry.Gberry;
import net.badlion.smellycases.managers.CaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CaseItem {

	private String name;
	private String prizeName;
	private ItemStack itemStack;

	private CaseItemRarity caseItemRarity;

	private CaseTier tier;
	private Gberry.ServerType serverType;

	private List<ItemStack> items = new ArrayList<>();

	public CaseItem(String name, ItemStack itemStack, CaseItemRarity caseItemRarity, CaseTier tier, Gberry.ServerType serverType, ItemStack... itemStacks) {
		this.name = name;
		this.itemStack = itemStack;

		this.caseItemRarity = caseItemRarity;

		this.tier = tier;
		this.serverType = serverType;

		if (itemStacks == null || Arrays.asList(itemStacks).isEmpty()) {
			this.items.add(itemStack);
		} else {
			Collections.addAll(this.items, itemStacks);
		}

		CaseManager.addCaseItem(this);
	}

	public abstract void rewardPlayer(Player player);

	public String getName() {
		return name;
	}

	public String getPrizeName() {
		if (this.prizeName == null) {
			return this.name;
		}

		return this.prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public CaseItemRarity getCaseItemRarity() {
		return caseItemRarity;
	}

	public CaseTier getTier() {
		return tier;
	}

	public Gberry.ServerType getServerType() {
		return serverType;
	}

	public List<ItemStack> getItems() {
		return items;
	}

}