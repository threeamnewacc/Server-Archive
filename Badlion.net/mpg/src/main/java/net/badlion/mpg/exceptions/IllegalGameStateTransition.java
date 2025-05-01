package net.badlion.mpg.exceptions;

public class IllegalGameStateTransition extends RuntimeException {

	public IllegalGameStateTransition() {
		super();
	}

	public IllegalGameStateTransition(String message) {
		super(message);
	}

}
