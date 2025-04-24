package me.devkevin.practice.events.tntrun;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;

import java.util.Arrays;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TntRunCountdownTask extends EventCountdownTask {

    public TntRunCountdownTask(PracticeEvent event) {
        super(event, 60);
    }

    @Override
    public boolean shouldAnnounce(int timeUntilStart) {
        return Arrays.asList(45, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return getEvent().getPlayers().size() >= 2;
    }

    @Override
    public void onCancel() {
        getEvent().sendMessage(CC.RED + "Not enough players. Event has been cancelled");
        getEvent().end();
        this.getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}
