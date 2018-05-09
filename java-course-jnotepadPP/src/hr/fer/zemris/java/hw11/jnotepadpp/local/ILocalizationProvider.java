package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Sučelje koje definira metode koje jedan poslužitelj mora imati za dojavu
 * promjene jezika u aplikaciji od strane korisnika.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public interface ILocalizationProvider {
	/**
	 * Metoda koja dodaje promatrača koji prate trenutni jezik u aplikaciji.
	 * 
	 * @param listener
	 *            Instanca sučelja {@link ILocalizationListener} koji
	 *            predstavlja slušača.
	 */
	void addLocalizationListener(ILocalizationListener listener);

	/**
	 * Metoda koja uklanja promatrača koji prati trenutni jezik u aplikaciji.
	 * 
	 * @param listener
	 *            Instanca sučelja {@link ILocalizationListener} koji
	 *            predstavlja slušača.
	 */
	void removeLocalizationListener(ILocalizationListener listener);

	/**
	 * Metoda koja za zadani ključ vraća riječ pridruženoj zadanom ključu.Riječ
	 * koju vraćamo je riječ pridružena ključu od trenutnog jezika u aplikaciji.
	 * 
	 * @param string
	 *            String koji predstavlja ključ za dohvat neke riječi.
	 * @return Riječ pridružena ključu iz trenutnog jezika aplikacije.
	 */
	String getString(String string);
}
