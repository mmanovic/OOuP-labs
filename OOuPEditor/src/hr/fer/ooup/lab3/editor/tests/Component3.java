package hr.fer.ooup.lab3.editor.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Component3 extends JComponent {
	public Component3() {

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.YELLOW);
		setForeground(Color.YELLOW);
		Dimension dim = getSize();
		g.setColor(Color.RED);
		g.drawLine(dim.width / 2, 0, dim.width / 2, dim.height);
		g.drawLine(0, dim.height / 2, dim.width, dim.height / 2);
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}

}
