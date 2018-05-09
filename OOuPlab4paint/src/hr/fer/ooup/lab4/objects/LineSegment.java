package hr.fer.ooup.lab4.objects;

import java.util.List;
import java.util.Stack;

import hr.fer.ooup.lab4.gui.GeometryUtil;
import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.gui.Rectangle;
import hr.fer.ooup.lab4.renderer.Renderer;

public class LineSegment extends AbstractGraphicalObject {
	public LineSegment() {
		super(new Point[] { new Point(0, 0), new Point(10, 0) });
	}

	public LineSegment(Point hotPoint1, Point hotPoint2) {
		super(new Point[] { hotPoint1, hotPoint2 });
	}

	@Override
	public Rectangle getBoundingBox() {
		Point point1 = getHotPoint(0);
		Point point2 = getHotPoint(1);
		int x, y;
		if (point1.getX() < point2.getX()) {
			x = point1.getX();
		} else {
			x = point2.getX();
		}
		if (point1.getY() < point2.getY()) {
			y = point1.getY();
		} else {
			y = point2.getY();
		}
		return new Rectangle(x, y, Math.abs(point1.getX() - point2.getX()), Math.abs(point1.getY() - point2.getY()));
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		return GeometryUtil.distanceFromLineSegment(getHotPoint(0), getHotPoint(1), mousePoint);
	}

	@Override
	public String getShapeName() {
		return "Linija";
	}

	@Override
	public GraphicalObject duplicate() {
		return new LineSegment(new Point(getHotPoint(0)), new Point(getHotPoint(1)));
	}

	@Override
	public void render(Renderer r) {
		r.drawLine(getHotPoint(0), getHotPoint(1));
	}

	@Override
	public String getShapeID() {
		return "@LINE";
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
