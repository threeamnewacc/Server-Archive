package com.massivecraft.factions.type;

public abstract class ChatCallback {

	public static final int TTL = 30000; // milliseconds this calback is valid for
	private final long expiry;

	public ChatCallback() {
		expiry = System.currentTimeMillis() + TTL;
	}

	public boolean expired() {
		return System.currentTimeMillis() > expiry;
	}

	public abstract void run(String message);
}
