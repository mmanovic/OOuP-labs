package hr.fer.ooup.lab4.gui;

public class Point {
	private int x;
	private int y;

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point translate(Point dp) {
		x += dp.x;
		y += dp.y;
		return new Point(x + dp.x, y + dp.y);
	}

	public Point difference(Point p) {
		return new Point(x - p.x, y - p.y);
	}

}
