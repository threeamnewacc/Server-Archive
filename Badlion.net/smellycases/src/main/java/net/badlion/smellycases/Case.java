package net.badlion.smellycases;

import java.util.List;

public class Case {

	private List<String> prizes;
	private String transactionId;
	private int rareCases, superRareCases, legendaryCases;
	private int totalCases;
	private int openedCases;
	private int rareItemsReceived, superRareItemsReceived, legendaryItemsReceived;
	private boolean rare, superRare, legendary;

	public Case(String transactionId, List<String> prizes, int rareCases, int superRareCases, int legendaryCases,
	            int totalCases, int openedCases, int rareItemsReceived, int superRareItemsReceived, int legendaryItemsReceived) {
		this.transactionId = transactionId;
		this.prizes = prizes;
		this.rareCases = rareCases;
		this.superRareCases = superRareCases;
		this.legendaryCases = legendaryCases;
		this.totalCases = totalCases;
		this.openedCases = openedCases;
		this.rareItemsReceived = rareItemsReceived;
		this.superRareItemsReceived = superRareItemsReceived;
		this.legendaryItemsReceived = legendaryItemsReceived;

		int legendaryItemsToGet = this.legendaryCases - this.legendaryItemsReceived;
		this.legendary = legendaryItemsToGet > 0 && legendaryItemsToGet >= getRemainingCases();
		if (this.legendary) {
			return;
		}

		int superRareItemsToGet = this.superRareCases - this.superRareItemsReceived;
		this.superRare = superRareItemsToGet > 0 && superRareItemsToGet >= getRemainingCases();
		if (this.superRare) {
			return;
		}

		int rareItemsToGet = this.rareCases - this.rareItemsReceived;
		this.rare = rareItemsToGet > 0 && rareItemsToGet >= getRemainingCases();
	}

	public List<String> getPrizes() {
		return prizes;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public int getRareCases() {
		return rareCases;
	}

	public int getRareItemsReceived() {
		return rareItemsReceived;
	}

	public int getLegendaryCases() {
		return legendaryCases;
	}

	public boolean isLegendary() {
		return legendary;
	}

	public boolean isSuperRare() {
		return superRare;
	}

	public int getLegendaryItemsReceived() {
		return legendaryItemsReceived;
	}

	public int getSuperRareCases() {
		return superRareCases;
	}

	public int getSuperRareItemsReceived() {
		return superRareItemsReceived;
	}

	public int getTotalCases() {
		return totalCases;
	}

	public int getOpenedCases() {
		return openedCases;
	}

	public int getRemainingCases() {
		return this.totalCases - this.openedCases;
	}

	public boolean isRare() {
		return rare;
	}

}
