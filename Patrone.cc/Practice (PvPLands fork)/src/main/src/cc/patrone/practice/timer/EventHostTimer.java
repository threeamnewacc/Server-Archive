package cc.patrone.practice.timer;

import zone.potion.utils.timer.impl.IntegerTimer;
import java.util.concurrent.TimeUnit;

public class EventHostTimer extends IntegerTimer {
	public EventHostTimer() {
		super(TimeUnit.MINUTES, 5);
	}
}
