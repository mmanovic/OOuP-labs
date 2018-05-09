package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Sučelje koje predstavlja jednog promatrača u aplikaciji koji ažurira tekst u
 * pojedinoj komponenti nakon što je poslužitelj dojavio promjenu jezika u
 * aplikaciji.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public interface ILocalizationListener {
	/**
	 * Metoda koja ažurira tekst komponente prema trenutnom jeziku.
	 */
	void localizationChanged();

}
