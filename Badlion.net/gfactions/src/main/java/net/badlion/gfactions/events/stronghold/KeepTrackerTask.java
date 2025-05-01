package net.badlion.gfactions.events.stronghold;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.managers.FactionPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KeepTrackerTask extends BukkitRunnable {

	private Keep keep;

	private Player capper;
	private Faction capperFaction;

	private Faction oldFaction;

	private int stealAmount = -1;
	private int captureTotal;
	private int pointsCaptured = 0;

	private double captureLengthModifier = 1D;

	public KeepTrackerTask(Keep keep) {
		this.keep = keep;
	}

	@Override
	public void run() {
		// Is capping enabled?
		if (!GFactions.plugin.getStrongholdConfig().isCaptureEnabled()) {
			return;
		}

		Player oldCapper = this.capper;

		// Is someone already capping?
		if (this.capper != null) {
			// Are they still on the capzone?
			if (!this.capper.isDead() && Gberry.isLocationInBetween(this.keep.getCorner1(), this.keep.getCorner2(), this.capper.getLocation())) {
				// Add a point
				this.pointsCaptured++;

				// Steal cap check
				if (this.stealAmount != -1) {
					// Only give the steal amount if all old capping faction's faction members are off the capzone
					if (this.oldFaction != null) {
						boolean giveStealAmount = true;
						for (Player oldFactionMember : this.oldFaction.getOnlinePlayers()) {
							if (!oldFactionMember.isDead() && Gberry.isLocationInBetween(this.keep.getCorner1(), this.keep.getCorner2(), oldFactionMember.getLocation())) {
								giveStealAmount = false;
								break;
							}
						}

						if (giveStealAmount) {
							this.captureTotal = this.captureTotal - this.stealAmount;

							// Less than 0 check
							if (this.captureTotal < 0) {
								this.captureTotal = 0;
							}

							// Reset this so we don't check it later on
							this.stealAmount = -1;
							this.oldFaction = null;
						}
					}
				}

				// Update the score on the scoreboard
                // TODO: Re-structure this to be nicer
				for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                    FactionPlayerManager.getPlayer(pl.getUniqueId()).getStrongholdStuff().put(this.keep.getName(), this.capperFaction.getTag() + " " + (this.captureTotal - this.pointsCaptured) + "s");
				}

				if (this.captureTotal - this.pointsCaptured == 0) {
					Gberry.broadcastMessage(this.capper.getName() + " has capped " + this.keep.getName() + "!");

					this.keep.setKeepTrackerTask(null);
					this.keep.setOwner(this.capperFaction);
					this.keep.setCapped(true);

					// Disable capping for this keep now
					this.cancel();

					// Check if all keeps have been capped
					boolean allCapped = true;
					for (Keep keep : Keep.getKeeps()) {
						if (keep.getKeepTrackerTask() != null) {
							allCapped = false;
							break;
						}
					}

					// End stronghold if all keeps have been captured
					if (allCapped) {
						GFactions.plugin.getStronghold().stop(false);
					}
				}
				return;
			} else { // No longer in the cap zone
				this.capper = null;
				this.capperFaction = null;
			}
		}

        // TODO: REMOVE PVP PROT IF THEY ARE IN STRONGHOLD AREA
		// See if another player is in the capzone
		for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
			if (!pl.isDead() && Gberry.isLocationInBetween(this.keep.getCorner1(), this.keep.getCorner2(), pl.getLocation())) {
				Faction newPlayerFaction = FPlayers.i.get(pl).getFaction();
				if (newPlayerFaction != null && !newPlayerFaction.getId().equals("0")) {
					// New player/faction capping, setup their stuff
					this.capper = pl;
					this.capperFaction = newPlayerFaction;

					// Setup the capture length modifier
					this.updateCaptureLengthModifiers();

					// Calculate their capture time left
					if (oldCapper != null) {
						this.captureTotal = (int) Math.ceil(GFactions.plugin.getStrongholdConfig().getCaptureLength(this.capperFaction) * this.captureLengthModifier);

						// Calculate the steal amount
						this.stealAmount = (int) Math.ceil(GFactions.plugin.getStrongholdConfig().getCaptureStealValue() * this.pointsCaptured);

						// Only give the steal amount if all old capping faction's faction members are off the capzone
						this.oldFaction = FPlayers.i.get(oldCapper).getFaction();
						if (this.oldFaction != null) {
							boolean giveStealAmount = true;
							for (Player oldFactionMember : this.oldFaction.getOnlinePlayers()) {
								if (!oldFactionMember.isDead() && Gberry.isLocationInBetween(this.keep.getCorner1(), this.keep.getCorner2(), oldFactionMember.getLocation())) {
									giveStealAmount = false;
									break;
								}
							}

							if (giveStealAmount) {
								this.captureTotal = this.captureTotal - this.stealAmount;

								// Reset this so we don't check it later on
								this.stealAmount = -1;
							}
						}

						Gberry.broadcastMessage(pl.getName() + " has stole the cap for " + this.keep.getName() + " from " + oldCapper.getName() + "!");
					} else {
						this.captureTotal = (int) Math.ceil(GFactions.plugin.getStrongholdConfig().getCaptureLength(this.capperFaction) * this.captureLengthModifier);

						Gberry.broadcastMessage(pl.getName() + " has started capping " + this.keep.getName() + "!");
					}

					// Reset points captured
					this.pointsCaptured = 0;

					for (Player pl2 : GFactions.plugin.getServer().getOnlinePlayers()) {
                        FactionPlayerManager.getPlayer(pl2.getUniqueId()).getStrongholdStuff().put(this.keep.getName(), this.capperFaction.getTag() + " " + (this.captureTotal - this.pointsCaptured + "s"));
                    }

					return;
				} else {
					pl.sendMessage("You cannot cap the keep if you're not in a faction!");
				}
			}
		}

		// Did old capper lose cap and no one else took over?
		if (oldCapper != null) {
			Gberry.broadcastMessage(oldCapper.getName() + " is no longer capping " + this.keep.getName() + "!");
		}

		// No one is on the cap zone, add a score to show this
		for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
            FactionPlayerManager.getPlayer(pl.getUniqueId()).getStrongholdStuff().put(this.keep.getName(), "None");
		}
	}

	public void updateCaptureLengthModifiers() {
		this.captureLengthModifier = 1D;

		if (this.capper != null && GFactions.plugin.getStrongholdConfig().isBuffsEnabled()) {
			// Add buff if faction too big or too small
			int factionSize = this.capperFaction.getFPlayers().size();
			if (factionSize >= GFactions.plugin.getStrongholdConfig().getOverPopulationLimit()) {
				this.captureLengthModifier += GFactions.plugin.getStrongholdConfig().getCaptureLengthOverPopulationBuff();
			} else if (factionSize <= GFactions.plugin.getStrongholdConfig().getUnderPopulationLimit()) {
				this.captureLengthModifier += GFactions.plugin.getStrongholdConfig().getCaptureLengthUnderPopulationBuff();
			}

			// Do they already control a keep? If so, add buff
			for (Keep keep : Keep.getKeeps()) {
				if (keep.getOwner() == this.capperFaction) {
					this.captureLengthModifier += GFactions.plugin.getStrongholdConfig().getCaptureLengthOverExtendedOffense();
					break;
				}
			}

			// Is the keep they're trying to capture controlled by faction that controls 2 or more keeps? If so, add buff
			for (Keep keep : Keep.getKeeps()) {
				if (this.keep.getOwner() != null && this.keep.getOwner() == keep.getOwner()) {
					this.captureLengthModifier += GFactions.plugin.getStrongholdConfig().getCaptureLengthOverExtendedDefense();
					break;
				}
			}

			// Did the controlling faction pay the deterioration price?
			if (this.keep.getDeteriorationApplied() != null && !this.keep.getDeteriorationApplied()) {
				this.captureLengthModifier += GFactions.plugin.getStrongholdConfig().getDeteriorationBuff();
			}
		}
	}

	public Player getCapper() {
		return capper;
	}

	public void setPointsCaptured(int pointsCaptured) {
		this.pointsCaptured = pointsCaptured;
	}

}
