package me.devkevin.landcore.utils.timer.impl;

import me.devkevin.landcore.utils.time.TimeUtil;
import me.devkevin.landcore.utils.timer.AbstractTimer;

import java.util.concurrent.TimeUnit;

public class IntegerTimer extends AbstractTimer {
    public IntegerTimer(TimeUnit unit, int amount) {
        super(unit, amount);
    }

    @Override
    public String formattedExpiration() {
        return TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis());
    }
}
