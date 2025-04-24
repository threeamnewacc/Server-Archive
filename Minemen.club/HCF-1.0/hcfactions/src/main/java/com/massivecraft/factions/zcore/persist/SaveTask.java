package com.massivecraft.factions.zcore.persist;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.zcore.MPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SaveTask implements Runnable {

	private static boolean running = false;

	private final MPlugin p;

	public void run() {
		if (!p.getAutoSave() || running) {
			return;
		}

		running = true;

		HCFactions.getInstance().preAutoSave();

		EM.saveAllToDisc();

		HCFactions.getInstance().postAutoSave();

		running = false;
	}

}
