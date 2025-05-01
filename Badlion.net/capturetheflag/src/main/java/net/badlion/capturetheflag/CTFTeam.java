package net.badlion.capturetheflag;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CTFTeam extends MPGTeam {

	private int score = 0;

	//private boolean flagCaptured = false;

	private Location flagLocation;
	private FlagState flagState = FlagState.IDLE;

	// IDLE - Unused, in this state before the game starts
	// BASE - At it's own base
	// CAPTURED - Carried by an opposing team's player
	// DROPPED - Located somewhere else but the base
	// RESPAWN - After being successfully captured, the flag is getting respawned
	public enum FlagState {
		IDLE, BASE, TAKEN, DROPPED, RESPAWN
	}

	public CTFTeam(ChatColor color) {
	 	super(color);
	}

	public void addScore(int amount) {
		this.score += amount;
	}

	public int getScore() {
		return this.score;
	}

	public FlagState getFlagState() {
		return flagState;
	}

	public void setFlagState(FlagState state) {
		this.flagState = state;
	}

	public boolean isFlagTaken() {
		return this.flagState == FlagState.TAKEN;
	}

	@Override
	public boolean add(MPGPlayer player) {
		CTFPlayer ctfPlayer = (CTFPlayer) player;
		ctfPlayer.setTeam(this);
		return super.add(ctfPlayer);
	}

	@Override
	public boolean remove(MPGPlayer player) {
		CTFPlayer ctfPlayer = (CTFPlayer) player;
		ctfPlayer.setTeam(null);
		return super.remove(ctfPlayer);
	}

	public Location getFlagLocation() {
		return this.flagLocation;
	}

	public void setFlagLocation(Location location) {
		this.flagLocation = location;
	}

	public Location getBaseFlagLocation() {
		return CTF.getInstance().getCTFGame().getWorld().getFlagLocation(this);
	}


	public void removeFlag() {
		if (this.getFlagLocation() != null) {
			Block block = this.getFlagLocation().getBlock();
			block.setType(Material.AIR);
		}
	}

	public void placeFlag() {
		if (this.getFlagLocation() != null) {
			Block block = this.getFlagLocation().getBlock();
			block.setType(Material.WOOL);
			block.setData((byte) this.getWoolColorFromChatColor(this.getColor()));
		}
	}

	public short getWoolColorFromChatColor(ChatColor chatColor) {
		switch(chatColor){
			case AQUA: return 3;
			case BLACK: return 15;
			case BLUE: return 9;
			case DARK_BLUE: return 11;
			case GRAY: return 8;
			case DARK_GRAY: return 7;
			case DARK_GREEN: return 13;
			case DARK_PURPLE: return 10;
			case RED: return 14;
			case GOLD: return 1;
			case GREEN: return 5;
			case LIGHT_PURPLE: return 2;
			case WHITE: return 0;
			case YELLOW: return 4;
			default: return 0;
		}
	}

	public boolean isInCaptureRegion(Player player) {
		if (this.getFlagState() == FlagState.BASE) {
			Location[] captureRegion = CTF.getInstance().getCTFGame().getWorld().getCaptureRegion(this);
			return Gberry.isLocationInBetween(captureRegion[0], captureRegion[1], player.getLocation());
		} else if (this.getFlagState() == FlagState.DROPPED) {
			Location center = this.getFlagLocation();
			Location corner1 = new Location(center.getWorld(), center.getX()-2, center.getY(), center.getZ()-2);
			Location corner2 = new Location(center.getWorld(), center.getX()+3, center.getY(), center.getZ()+3);
			return Gberry.isLocationInBetween(corner1, corner2, player.getLocation());
		} else {
			return false;
		}

	}

	public boolean isInBaseRegion(Player player) {
		Location[] captureRegion = CTF.getInstance().getCTFGame().getWorld().getCaptureRegion(this);
		return Gberry.isLocationInBetween(captureRegion[0], captureRegion[1], player.getLocation());
	}

	public ItemStack getFlagItem() {
		ItemStack flag = new ItemStack(Material.WOOL, 1, this.getWoolColorFromChatColor(this.getColor()));
		ItemMeta itemMeta = flag.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + "Team Flag");
		List<String> lore = new ArrayList<>();
		lore.add(this.getColor() + this.getColor().name() + "'s Team Flag!");
		lore.add(ChatColor.GRAY + "Dropping this will drop the flag!");
		lore.add(ChatColor.GRAY + "Bring this to your base for a point!");
		itemMeta.setLore(lore);
		flag.setItemMeta(itemMeta);
		return flag;
	}

}
