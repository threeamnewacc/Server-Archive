package me.devkevin.landcore.utils.timer;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTimer implements Timer {
    private final long ms;
    protected long expiry;

    protected AbstractTimer(TimeUnit unit, int amount) {
        this.ms = unit.toMillis(amount);
    }

    @Override
    public boolean isActive() {
        return isActive(true);
    }

    @Override
    public boolean isActive(boolean autoReset) {
        boolean active = System.currentTimeMillis() < expiry;

        if (autoReset && !active) {
            reset();
        }

        return active;
    }

    @Override
    public void reset() {
        this.expiry = System.currentTimeMillis() + ms;
    }
}
