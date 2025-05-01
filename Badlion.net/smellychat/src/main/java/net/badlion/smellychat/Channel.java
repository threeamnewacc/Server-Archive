package net.badlion.smellychat;

public class Channel {

	private String name;
	private String identifier;
	private String color;

	public Channel(String name, String identifier, String color) {
		this.name = name;
		this.identifier = identifier;
		this.color = "§" + color;
	}

	public String getName() {
		return name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getColor() {
		return color;
	}

}
