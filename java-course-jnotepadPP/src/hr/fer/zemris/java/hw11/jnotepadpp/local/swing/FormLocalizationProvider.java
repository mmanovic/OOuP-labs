package hr.fer.zemris.java.hw11.jnotepadpp.local.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProviderBridge;

/**
 * Razred koji nasljeđuje razred {@link LocalizationProviderBridge}.Razred
 * predstavlja omotač oko prozora aplikacije predstavljen razredom
 * {@link JFrame}. Ovaj razred u konstruktoru dodaje instancu sučelja
 * {@link WindowListener} zadanom prozoru. Prozor se otvaranjem automatski
 * povezuje s poslužiteljem, a pri zatvaranju se prekida veza s poslužiteljem.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {
	/**
	 * Varijabla koja pohranjuje prozor aplikacije povezan s poslužiteljem.
	 */
	JFrame frame;

	/**
	 * Osnovni konstruktor koji za zadanu instancu razreda {@link JFrame} dodaje
	 * osnovno ponašanje pri otvaranju i zatvaranju prozora.
	 * 
	 * @param provider
	 *            Poslužitelj na kojeg spajamo prozor aplikacije.
	 * @param frame
	 *            Prozor aplikacije predstavljen razredom {@link JFrame}.
	 */
	public FormLocalizationProvider(ILocalizationProvider provider, JFrame frame) {
		super(provider);
		this.frame = frame;
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
				super.windowOpened(e);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				connect();
				super.windowActivated(e);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
				super.windowClosed(e);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				disconnect();
				super.windowDeactivated(e);
			}
		});
	}
}
