package hr.fer.ooup.lab3.editor.tests;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

public class Component2 extends JComponent {
	String tekst1;
	String tekst2;

	public Component2(String tekst1, String tekst2) {
		super();
		this.tekst1 = tekst1;
		this.tekst2 = tekst2;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(tekst1, 0, fm.getHeight());
		g.drawString(tekst2, 0, fm.getHeight() * 2);
	}
}
