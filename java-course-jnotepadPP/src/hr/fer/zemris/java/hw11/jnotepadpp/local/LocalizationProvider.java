package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Razred koji nasljeđuje {@link AbstractLocalizationProvider} i nudi
 * implementaciju metode getString() koja vraća za zadani ključ riječ trenutnog
 * jezika u aplikaciji.Ovaj razred omogućava stvaranje najviše jedne instance.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class LocalizationProvider extends AbstractLocalizationProvider {
	/**
	 * Trenutni jezik u aplikaciji.
	 */
	private String language;
	/**
	 * Instanca razreda {@link ResourceBundle} koja dohvaća riječ nekog jezika
	 * pridružena zadanom ključu.
	 */
	private ResourceBundle bundle;
	/**
	 * Instanca ovog razreda koji predstavlja jedini objekt kojeg će moći
	 * koristiti korisnici.
	 */
	private static LocalizationProvider provider = new LocalizationProvider();

	/**
	 * Privatni konstruktor koji inicijalizira jezik na pretpostavljeni
	 * engleski.
	 */
	private LocalizationProvider() {
		super();
		language = "en";
		Locale locale = Locale.forLanguageTag(language);
		bundle = ResourceBundle.getBundle("hr.fer.zemris.java.hw11.jnotepadpp.local.prijevodi", locale);
	}

	@Override
	public String getString(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key has not null value.");
		}
		return bundle.getString(key);
	}

	/**
	 * Javna metoda za dohvat instance ovog razreda.
	 * 
	 * @return Instancu ovog razreda.
	 */
	public static LocalizationProvider getInstance() {
		return provider;
	}

	/**
	 * Javna metoda koja postavlja novi jezik poslužitelja.
	 * 
	 * @param language
	 *            Novi jezik predstavljen domenom.
	 */
	public void setLanguage(String language) {
		this.language = language;
		Locale locale = Locale.forLanguageTag(language);
		bundle = ResourceBundle.getBundle("hr.fer.zemris.java.hw11.jnotepadpp.local.prijevodi", locale);
		fire();
	}

	/**
	 * Metoda za dohvaćanje domene trenutnog jezika poslužitelja.
	 * 
	 * @return Domenu trenutnog jezika poslužitelja.
	 */
	public String getLanguage() {
		return language;
	}
}
