package net.badlion.gguard.util;

public class Vector2D {

	protected final double x;
	protected final double z;

	public Vector2D(double x, double z) {
		this.x = x;
		this.z = z;
	}

	public Vector2D(Vector2D other) {
		this.x = other.getX();
		this.z = other.getZ();
	}

	public double getX() {
		return this.x;
	}

	public int getBlockX() {
		return (int) Math.round(this.x);
	}

	public Vector2D setX(double x) {
		return new Vector2D(x, this.z);
	}

	public double getZ() {
		return this.z;
	}

	public int getBlockZ() {
		return (int) Math.round(this.z);
	}

	public Vector2D setZ(double z) {
		return new Vector2D(this.x, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2D)) {
			return false;
		}

		Vector2D other = (Vector2D) obj;
		return other.getX() == this.x && other.getZ() == this.z;

	}

	@Override
	public int hashCode() {
		return ((new Double(this.x)).hashCode() >> 13) ^
				(new Double(this.z)).hashCode();
	}

}