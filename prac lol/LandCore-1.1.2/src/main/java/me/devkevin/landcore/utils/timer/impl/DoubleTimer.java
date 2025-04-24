package me.devkevin.landcore.utils.timer.impl;

import me.devkevin.landcore.utils.timer.AbstractTimer;

import java.util.concurrent.TimeUnit;

public class DoubleTimer extends AbstractTimer {
    public DoubleTimer(int seconds) {
        super(TimeUnit.SECONDS, seconds);
    }

    @Override
    public String formattedExpiration() {
        double seconds = (expiry - System.currentTimeMillis()) / 1000.0;
        return String.format("%.1f seconds", seconds);
    }
}
