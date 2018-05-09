package hr.fer.ooup.lab4.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

import hr.fer.ooup.lab4.gui.Point;

public class G2DRenderer implements Renderer {
	private Graphics2D g2d;

	public G2DRenderer(Graphics2D g2d) {
		super();
		this.g2d = g2d;
	}

	@Override
	public void drawLine(Point s, Point e) {
		g2d.setColor(Color.BLUE);
		g2d.drawLine(s.getX(), s.getY(), e.getX(), e.getY());
	}

	@Override
	public void fillPolygon(Point[] points) {
		int[] xCor = new int[points.length];
		int[] yCor = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			xCor[i] = points[i].getX();
			yCor[i] = points[i].getY();
		}

		
		g2d.setColor(Color.RED);
		g2d.drawPolygon(xCor, yCor, points.length);
		g2d.setColor(Color.BLUE);
		g2d.fillPolygon(xCor, yCor, points.length);
	}

}
