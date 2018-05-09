package hr.fer.zemris.java.hw11.jnotepadpp;

import javax.swing.event.CaretEvent;
import javax.swing.text.Caret;

/**
 * Razred koji predstavlja jedan događaj tj. promjenu kursora u aplikaciji
 * {@link JNotepadPP}.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class MyCaretEvent extends CaretEvent {
	/**
	 * Instanca razreda {@link Caret} koji predstavlja poziciju kursora.
	 */
	private Caret caret;

	/**
	 * Osnovni konstruktor koji inicijalizira za zadanu poziciju kursora.
	 * 
	 * @param caret
	 *            Instanca koja predstavlja poziciju kursora.
	 * 
	 */
	public MyCaretEvent(Caret caret) {
		super(caret);
		this.caret = caret;
	}

	/**
	 * Da ne baca warning.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getDot() {
		return caret.getDot();
	}

	@Override
	public int getMark() {
		return caret.getMark();
	}

}
