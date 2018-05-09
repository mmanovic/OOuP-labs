package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Razred koji predstavlja komponentu koja u sebi prikazuje trenutni datum i
 * vrijeme.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class Sat extends JComponent {
	/** Serijski broj. */
	private static final long serialVersionUID = 1L;
	/**
	 * String koji predstavlja trenutno vrijeme u tekstualnom formatu.
	 */
	volatile String vrijeme;
	/**
	 * Zastavica koja označava zahtjev za zaustavljanje vremena.
	 */
	volatile boolean stopRequested;
	/**
	 * Instanca koja pohranjuje trenutno lokalno vrijeme.
	 */
	DateTimeFormatter formater = DateTimeFormatter.ofPattern("HH:mm:ss");
	/**
	 * Instanca koja pohranjuje trenutni datum.
	 */
	DateTimeFormatter formater2 = DateTimeFormatter.ofPattern("yyyy/MM/dd ");

	/**
	 * Osnovni konstruktor ove komponente koji stvara novu dretvu koja pokreće
	 * prikaz vremena.
	 */
	public Sat() {
		updateTime();
		Thread t = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(500);
				} catch (Exception ex) {
				}
				if (stopRequested)
					break;
				SwingUtilities.invokeLater(() -> {
					updateTime();
				});
			}
		});

		t.setDaemon(true);
		t.start();
	}

	/**
	 * Metoda koja zaustavlja prikaz vremena.
	 */
	public void stop() {
		stopRequested = true;
	}

	/**
	 * Pomoćna metoda koja ažurira trenutni datum i vrijeme u komponenti.
	 */
	private void updateTime() {
		vrijeme = formater2.format(LocalDate.now());
		vrijeme += formater.format(LocalTime.now());
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Insets ins = getInsets();
		Dimension dim = getSize();
		Rectangle r = new Rectangle(ins.left, ins.top, dim.width - ins.left - ins.right,
				dim.height - ins.top - ins.bottom);
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(r.x, r.y, r.width, r.height);
		}

		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(vrijeme);
		int h = fm.getAscent();

		g.drawString(vrijeme, r.x + r.width - w, r.y + r.height - (r.height - h) / 2);
	}

}
