package com.probablycoding.bukkit.playersimulator;

import java.util.LinkedList;

public class TPSCheck
		implements Runnable
{
	private long last = System.currentTimeMillis();
	public LinkedList<Long> history = new LinkedList();

	public void run()
	{
		long now = System.currentTimeMillis();
		long duration = now - this.last;
		if (duration < 1000L) {
			duration = 1000L;
		}
		this.history.add(Long.valueOf(duration));
		if (this.history.size() > 10) {
			this.history.poll();
		}
		this.last = now;
	}
}