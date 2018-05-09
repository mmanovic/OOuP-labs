package hr.fer.zemris.java.hw11.jnotepadpp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;

/**
 * Pomoćni razred koji sadrži statičke metode koje obavljaju operaciju
 * uređivanja teksta u zadanoj instanci {@link JTextArea}.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class TextUtility {
	/**
	 * Pomoćna metoda koja obavlja sortiranje redaka odabranog teksta u zadanom
	 * uređivaču teksta. Također metoda prima zastavicu koja označava želimo li
	 * sortirati uzlazno.
	 * 
	 * @param editor
	 *            Instanca razreda {@link JTextArea} u kojemu sortiramo tekst.
	 * @param ascending
	 *            Želimo li sortirati retke uzlazno.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public static void sortLines(JTextArea editor, boolean ascending) {
		if (editor == null) {
			throw new IllegalArgumentException("Editor cannot be null.");
		}
		String text = editor.getText();
		String[] lines = text.split("\n");
		try {
			// početni i zadnji redak odabranog teksta
			int startLine = editor.getLineOfOffset(editor.getSelectionStart());
			int endLine = editor.getLineOfOffset(editor.getSelectionEnd());
			if (endLine == lines.length) {
				endLine--;
			}
			// dodajemo retke u listu koju sortiramo
			List<String> sortedLines = new ArrayList<>();
			for (int i = startLine; i <= endLine; i++) {
				sortedLines.add(lines[i]);
			}

			Collator collator = Collator.getInstance(new Locale(LocalizationProvider.getInstance().getLanguage()));
			sortedLines.sort((a, b) -> {
				if (ascending) {
					return collator.compare(a, b);
				} else {
					return -collator.compare(a, b);
				}
			});
			// spajamo sortirane retke i ažuriramo trenutni tekst
			StringBuilder builder = new StringBuilder();
			for (String line : sortedLines) {
				builder.append(line).append("\n");
			}
			editor.replaceRange(builder.toString(), editor.getLineStartOffset(startLine),
					editor.getLineEndOffset(endLine));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pomočna metoda koja uklanja sve duple retke u označenom tekstu. Ukoliko
	 * je označen dio retka uzima se cijeli redak.
	 * 
	 * @param editor
	 *            Instanca razreda {@link JTextArea} u kojemu uklanjamo duple
	 *            retke.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public static void removeDuplicate(JTextArea editor) {
		if (editor == null) {
			throw new IllegalArgumentException("Editor cannot be null.");
		}
		String[] lines = editor.getText().split("\n");
		try {
			// početni i zadnji redak označenog teksta
			int startLine = editor.getLineOfOffset(editor.getSelectionStart());
			int endLine = editor.getLineOfOffset(editor.getSelectionEnd());
			if (endLine == lines.length) {
				endLine--;
			}
			Set<String> unique = new LinkedHashSet<>();
			for (int i = startLine; i <= endLine; i++) {
				unique.add(lines[i]);
			}
			// spajamo retke
			StringBuilder builder = new StringBuilder();
			for (String line : unique) {
				builder.append(line).append("\n");
			}
			editor.replaceRange(builder.toString(), editor.getLineStartOffset(startLine),
					editor.getLineEndOffset(endLine));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Pomoćna metoda koja obavlja invertiranje veličine slova u zadanom tekstu.
	 * 
	 * @param text
	 *            Tekst kojemu invertiramo veličinu slova.
	 * @return Novostvoreni tekst.
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public static String changeCase(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Obtained text has null value.");
		}
		char[] znakovi = text.toCharArray();
		for (int i = 0; i < znakovi.length; i++) {
			char c = znakovi[i];
			if (Character.isLowerCase(c)) {
				znakovi[i] = Character.toUpperCase(c);
			} else {
				znakovi[i] = Character.toLowerCase(c);
			}
		}
		return new String(znakovi);
	}
}
