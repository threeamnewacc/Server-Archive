package me.devkevin.practice.match.timer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Timer {

	@Getter
	protected final String name;
	@Getter
	protected final long defaultCooldown;

	public final String getDisplayName() {
		return this.name;
	}
}