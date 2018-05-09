package hr.fer.ooup.lab4.objects;

import java.util.List;
import java.util.Stack;

import hr.fer.ooup.lab4.gui.GeometryUtil;
import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.gui.Rectangle;
import hr.fer.ooup.lab4.renderer.Renderer;

public class Oval extends AbstractGraphicalObject {

	public Oval() {
		super(new Point[] { new Point(0, 10), new Point(10, 0) });
	}

	public Oval(Point low, Point right) {
		super(new Point[] { low, right });
	}

	@Override
	public Rectangle getBoundingBox() {
		Point low = getHotPoint(0);
		Point right = getHotPoint(1);
		int x, y;
		if (right.getX() < low.getX()) {
			x = right.getX();
		} else {
			x = 2 * low.getX() - right.getX();
		}
		if (low.getY() < right.getY()) {
			y = low.getY();
		} else {
			y = 2 * right.getY() - low.getY();
		}
		int width = 2 * Math.abs(right.getX() - low.getX());
		int height = 2 * Math.abs(low.getY() - right.getY());

		return new Rectangle(x, y, width, height);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		Point low = getHotPoint(0);
		Point right = getHotPoint(1);
		int a = right.getX() - low.getX();
		int b = low.getY() - right.getY();
		double elipseEquation = Math.pow(mousePoint.getX() - low.getX(), 2) / Math.pow(a, 2)
				+ Math.pow(mousePoint.getY() - right.getY(), 2) / Math.pow(b, 2);
		if (elipseEquation <= 1) {
			return 0;
		}
		Point[] points = getPoints(50);
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double distance = GeometryUtil.distanceFromPoint(points[i], mousePoint);
			min = Double.min(distance, min);
		}
		return min;
	}

	@Override
	public String getShapeName() {
		return "Oval";
	}

	@Override
	public GraphicalObject duplicate() {
		return new Oval(new Point(getHotPoint(0)), new Point(getHotPoint(1)));
	}

	@Override
	public void render(Renderer r) {
		r.fillPolygon(getPoints(100));
	}

	private Point[] getPoints(int numOfPoints) {
		Point low = getHotPoint(0);
		Point right = getHotPoint(1);
		int a = right.getX() - low.getX();
		int b = low.getY() - right.getY();

		Point[] points = new Point[numOfPoints];
		for (int i = 0; i < numOfPoints; i++) {
			double t = (2 * Math.PI / numOfPoints) * i;
			int x = (int) (a * Math.cos(t)) + low.getX();
			int y = (int) (b * Math.sin(t)) + right.getY();
			points[i] = new Point(x, y);
		}
		return points;
	}

	@Override
	public String getShapeID() {
		return "@OVAL";
	}

	@Override
	public void save(List<String> rows) {
		Point s = getHotPoint(0);
		Point e = getHotPoint(1);
		rows.add(getShapeID() + " " + s.getX() + " " + s.getY() + " " + e.getX() + " " + e.getY());
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		String[] cor = data.trim().split(" ");
		setHotPoint(0, new Point(Integer.parseInt(cor[0]), Integer.parseInt(cor[1])));
		setHotPoint(1, new Point(Integer.parseInt(cor[2]), Integer.parseInt(cor[3])));
		stack.push(this);
	}

}
