package hr.fer.ooup.lab3.editor.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class Component1 extends JComponent {

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension dim = getSize();
		g.setColor(Color.RED);
		g.drawLine(dim.width / 2, 0, dim.width / 2, dim.height);
		g.drawLine(0, dim.height / 2, dim.width, dim.height / 2);
	}
}
