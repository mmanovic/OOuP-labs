package hr.fer.zemris.java.hw11.jnotepadpp.local.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;

/**
 * Razred koji nasljeđuje {@link AbstractAction} i predstavlja specifičnu akciju
 * neke komponente u prozore neke aplikacije. Ova akcija pored osnovne akcije
 * uključuje i promjenu jezika teksta koje sadrže pojedine komponente.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public abstract class LocalizableAction extends AbstractAction {
	/**
	 * Da nema warninga.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Osnovni konstruktor koji prima ključeva imena i kratkog opisa komponente
	 * koja sadrži ovu akciju.Također konstruktor dobiva poslužitelj
	 * predstavljen instancom sučelja {@link ILocalizationProvider} kojemu
	 * dodajemo akcije koje će se izvršiti u slučaju promjene jezika
	 * poslužitelja.
	 * 
	 * @param keyName
	 *            Ključ imena komponente.
	 * @param keyDescr
	 *            Ključ koji predstavlja kratki opis komponente.
	 * @param provider
	 *            Poslužitelj kojemu dodajemo specifične akcije.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public LocalizableAction(String keyName, String keyDescr, ILocalizationProvider provider) {
		super();
		if (keyDescr == null || keyName == null || provider == null) {
			throw new IllegalArgumentException("Argument cannot has null value.");
		}
		putValue(Action.NAME, provider.getString(keyName));
		provider.addLocalizationListener(() -> {
			putValue(Action.NAME, provider.getString(keyName));
		});
		putValue(Action.SHORT_DESCRIPTION, provider.getString(keyDescr));
		provider.addLocalizationListener(() -> {
			putValue(Action.SHORT_DESCRIPTION, provider.getString(keyDescr));
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
