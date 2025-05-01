package net.badlion.arenacommon.settings;

public class ArenaSettings {

	public enum DuelRequestType{
		INVENTORY, CHAT;
	}

	private transient boolean hasChanged = false;

	private boolean allowDuelRequests = true;
	private boolean allowPartyRequests = true;

	private DuelRequestType duelRequestType = DuelRequestType.INVENTORY;

	private boolean enableSidebar = true;
	private boolean showPlayersInLobby = true;
	private boolean showColoredHelmInSpec = false;
	private boolean showTitles = true;
	private boolean showRankPrefix = true;

	public boolean isShowRankPrefix() {
		return showRankPrefix;
	}

	public void setShowRankPrefix(boolean showRankPrefix) {
		this.hasChanged = true;
		this.showRankPrefix = showRankPrefix;
	}

	public boolean isAllowDuelRequests() {
		return allowDuelRequests;
	}

	public void setAllowDuelRequests(boolean allowDuelRequests) {
		this.hasChanged = true;
		this.allowDuelRequests = allowDuelRequests;
	}

	public boolean isAllowPartyRequests() {
		return allowPartyRequests;
	}

	public void setAllowPartyRequests(boolean allowPartyRequests) {
		this.hasChanged = true;
		this.allowPartyRequests = allowPartyRequests;
	}

	public DuelRequestType getDuelRequestType() {
		return duelRequestType;
	}

	public void setDuelRequestType(DuelRequestType duelRequestType) {
		this.hasChanged = true;
		this.duelRequestType = duelRequestType;
	}

	public boolean isSidebarEnabled() {
		return enableSidebar;
	}

	public void setSidebarEnabled(boolean enableSidebar) {
		this.hasChanged = true;
		this.enableSidebar = enableSidebar;
	}

	public boolean showsPlayersInLobby() {
		return showPlayersInLobby;
	}

	public void setShowPlayersInLobby(boolean showPlayersInLobby) {
		this.hasChanged = true;
		this.showPlayersInLobby = showPlayersInLobby;
	}

	public boolean showsColoredHelmInSpec() {
		return showColoredHelmInSpec;
	}

	public void setShowColoredHelmInSpec(boolean showColoredHelmInSpec) {
		this.hasChanged = true;
		this.showColoredHelmInSpec = showColoredHelmInSpec;
	}

	public boolean showsTitles() {
		return showTitles;
	}

	public void setShowTitles(boolean showTitles) {
		this.hasChanged = true;
		this.showTitles = showTitles;
	}

	public boolean hasChanged(){
		return this.hasChanged;
	}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
}
