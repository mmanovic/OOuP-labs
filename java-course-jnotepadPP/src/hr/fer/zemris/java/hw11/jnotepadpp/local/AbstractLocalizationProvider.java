package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraktni razred koji predstavlja jednu implementaciju sučelja
 * {@link ILocalizationProvider} koji nudi implementaciju metoda za dodavanje i
 * uklanjanje instanci sučelja {@link ILocalizationListener}.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {
	/**
	 * Interna lista koja pohranjuje instance sučelja
	 * {@link ILocalizationListener}.
	 */
	List<ILocalizationListener> listeners = new ArrayList<>();

	public void addLocalizationListener(ILocalizationListener listener) {
		checkNull(listener);
		listeners.add(listener);
	}

	public void removeLocalizationListener(ILocalizationListener listener) {
		checkNull(listener);
		listeners.remove(listener);
	}

	/**
	 * Metoda koja dojavljuje slušačima promjenu.
	 */
	public void fire() {
		for (ILocalizationListener listener : listeners) {
			listener.localizationChanged();
		}
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
