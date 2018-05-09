package hr.fer.zemris.java.hw11.jnotepadpp.local.swing;

import javax.swing.JLabel;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;

/**
 * Razred koji predstavlja modifikaciju razreda {@link JLabel} tj. povezuje ga
 * sa poslužiteljem predstavljen razredom {@link ILocalizationProvider}.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class LJLabel extends JLabel {

	/**
	 * Serijski broj.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * OSnovni konstruktor koji dobiva ključ teksta kojeg labela prikazuje i
	 * poslužitelj iz kojeg dobiva tekst.Konstruktor dodaje akciju promjene
	 * teksta labele nakon što se dogodi promjena jezika u poslužitelju.
	 * 
	 * @param keyName
	 *            Ključ teksta labele.
	 * @param provider
	 *            Poslužitelj za dohvat teksta labele.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public LJLabel(String keyName, ILocalizationProvider provider) {
		super();
		if (keyName == null || provider == null) {
			throw new IllegalArgumentException("KeyName or provider cannot has null value.");
		}
		setText(provider.getString(keyName) + ":0");
		provider.addLocalizationListener(() -> {
			setText(provider.getString(keyName) + getText().substring(getText().indexOf(":"), getText().length()));
		});
	}

}
