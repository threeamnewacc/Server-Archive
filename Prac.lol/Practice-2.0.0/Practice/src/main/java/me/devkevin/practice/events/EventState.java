package me.devkevin.practice.events;

public enum EventState {

	UNANNOUNCED, // The event hasn't even been announced yet
	WAITING, // Waiting for players to join while the event counts down to start
	STARTED // The event has started and is in progress
}
