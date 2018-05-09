package hr.fer.ooup.lab3.editor.gui;

public class Location {
	private int x;
	private int y;

	public Location(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Location(Location loc) {
		this.x = loc.x;
		this.y = loc.y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(Location location) {
		this.x = location.x;
		this.y = location.y;
	}

	public void update(int dx, int dy) {
		x += dx;
		y += dy;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location location = (Location) obj;
			if (this.x == location.x && this.y == location.y) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
