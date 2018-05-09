package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Razred koji predstavlja most između glavnog poslužitelja i nekog prozora
 * aplikacije. Ovaj razred sadrži dvije metode koje omogućavaju uspostavu i
 * prekid veze između prozora aplikacije i poslužitelja iz razloga da glavni
 * poslužitelj ne sadrži reference na prozor aplikacije koje je zatvorena.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {
	/**
	 * Varijabla koja pohranjuje poslužitelja roditelja tj. glavnog
	 * poslužitelja.
	 */
	private ILocalizationProvider parent;
	/**
	 * Zastavica koja pamti da li je prozor aplikacije povezan sa glavnim
	 * poslužiteljem ili ne.
	 */
	private boolean connected;
	/**
	 * Interna lista koja pohranjuje promtrače spojene na ovaj poslužitelj.
	 */
	private List<ILocalizationListener> listeners;
	/**
	 * Promatrač koji prati promjene u glavnom poslužitelju i dojavljuje
	 * promatračima ovog poslužitelja promjenu.
	 */
	private ILocalizationListener listener;

	/**
	 * Osnovni konstruktor koji prima instancu glavnog poslužitelja.
	 * 
	 * @param parent
	 *            Glavni poslužitelj.
	 */
	public LocalizationProviderBridge(ILocalizationProvider parent) {
		super();
		this.parent = parent;
		this.listeners = new ArrayList<>();
	}

	/**
	 * Metoda koja prekida vezu aplikacije tj. promatrača i glavnog
	 * poslužitelja.
	 */
	public void disconnect() {
		connected = false;
		parent.removeLocalizationListener(listener);
	}

	/**
	 * Metoda koja uspostavlja vezu aplikacije tj. promatrača i glavnog
	 * poslužitelja.
	 */
	public void connect() {
		if (!connected) {
			connected = true;
			listener = () -> {
				for (ILocalizationListener entry : listeners) {
					entry.localizationChanged();
				}
			};
			parent.addLocalizationListener(listener);
		}

	}

	public void addLocalizationListener(ILocalizationListener listener) {
		checkNull(listener);
		listeners.add(listener);
	}

	public void removeLocalizationListener(ILocalizationListener listener) {
		checkNull(listener);
		listeners.remove(listener);
	}

	@Override
	public String getString(String key) {
		return parent.getString(key);

	}

	/**
	 * Pomoćna metoda koja provjerava da li argument ima null vrijednost.
	 * 
	 * @param listener
	 *            Argument koji provjeravamo.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	private void checkNull(ILocalizationListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot has null value.");
		}
	}
}
