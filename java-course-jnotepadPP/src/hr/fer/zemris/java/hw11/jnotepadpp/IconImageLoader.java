package hr.fer.zemris.java.hw11.jnotepadpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

/**
 * Razred koji sadrži javnu metodu za dohvaćanje ikone predstavljenom razredom
 * {@link ImageIcon}.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class IconImageLoader {
	/**
	 * Metoda koja za zadani put do slike učitava i vraća novu instancu
	 * {@link ImageIcon}.
	 * 
	 * @param path
	 *            Staza do mjesta gdje je slika pohranjena.
	 * @return Novu instancu razreda {@link ImageIcon}
	 * @throws IllegalArgumentException
	 *             Ako zadani argument ima vrijednost null.
	 */
	public ImageIcon createImageIcon(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Path has not null value.");
		}
		java.io.InputStream is = this.getClass().getResourceAsStream(path);
		if (is == null) {
			System.exit(0);
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[2048];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] bytes = buffer.toByteArray();

			is.close();
			return new ImageIcon(bytes);
		} catch (IOException ignorable) {

		}
		return null;
	}
}
