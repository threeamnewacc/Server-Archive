package me.devkevin.practice.profile.task;

import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;

/**
 * Copyright 12/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class DataLoaderWorkerRunnable implements Runnable {

    private final Practice plugin = Practice.getInstance();

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Profile profile : this.plugin.getProfileManager().getAllData()) {
                if (!profile.isDataLoaded()) {
                    this.plugin.getProfileManager().loadData(profile);
                    profile.setDataLoaded(true);
                }
            }
        }
    }
}
