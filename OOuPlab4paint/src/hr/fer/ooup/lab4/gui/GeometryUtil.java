package hr.fer.ooup.lab4.gui;

import java.awt.geom.Line2D;

public class GeometryUtil {
	public static double distanceFromPoint(Point point1, Point point2) {
		return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
	}

	public static double distanceFromLineSegment(Point s, Point e, Point p) {
		return Line2D.ptSegDist(s.getX(), s.getY(), e.getX(), e.getY(), p.getX(), p.getY());
	}
}
