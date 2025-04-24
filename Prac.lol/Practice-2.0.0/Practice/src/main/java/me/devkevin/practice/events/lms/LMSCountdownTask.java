package me.devkevin.practice.events.lms;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;

import java.util.Arrays;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class LMSCountdownTask extends EventCountdownTask {

    public LMSCountdownTask(PracticeEvent event) {
        super(event, 45);
    }

    @Override
    public boolean shouldAnnounce(int timeUntilStart) {
        return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return getEvent().getPlayers().size() >= 2;
    }

    @Override
    public void onCancel() {
        getEvent().sendMessage(CC.RED + "There were not enough players to start the event.");
        getEvent().end();
        getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}

