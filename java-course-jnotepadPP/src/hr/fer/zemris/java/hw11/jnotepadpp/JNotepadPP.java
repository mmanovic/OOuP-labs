package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.swing.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.swing.LJLabel;
import hr.fer.zemris.java.hw11.jnotepadpp.local.swing.LocalizableAction;

/**
 * Razred koji nasljeđuje {@link JFrame} i sadrži glavnu metodu koja pokreće
 * aplikaciju koja predstavlja unaprijeđeni notepad.Dodatne mogućnosti ovog
 * razreda su otvaranje više tekstualnih datoteka u istom prozoru putem kartica.
 * Također ova aplikacija podržava uređivanje novih i otvaranje starih
 * tekstualnih datoteka te njihovu pohranu. Aplikacija nudi osnovne alate za
 * uređivanje teksta među njima su osnovni kao što je kopiranje i izrezivanje
 * odabranog teksta te neki dodatni alati koji mijenjanju veličinu slova u
 * datoteci, sortiranje linija teksta, izbacivanje duplih linija itd.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class JNotepadPP extends JFrame {
	/** Serijski broj */
	private static final long serialVersionUID = 1L;
	/**
	 * Varijabla koja pohranjuje instancu razreda {@link JTabbedPane} koji
	 * omogućuje otvaranje pojedine tekstualne datoteke zasebno u svakoj
	 * kartici.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * Lista trenutno otvorenih kartica koje u sebi pohranjuju instancu razreda
	 * {@link JTextArea} koji nudi osnovne funkcije uređivanja teksta.
	 */
	private List<JTextArea> editors = new ArrayList<>();
	/**
	 * Lista instanci razreda {@link Path} koji za svaku karticu tj. uređivač
	 * teksta {@link JTextArea} pohranjuje njoj pridruženu stazu do datoteke.
	 * Ako trenutno otvorena nova datoteka nije spremljena lista za taj uređivač
	 * teksta pohranjuje vrijednost null.
	 */
	private List<Path> openedFiles = new ArrayList<>();
	/**
	 * Varijabla koja čuva ikonu koja predstavlja crveni disk tj. da je datoteka
	 * modificirana i da sadržaj nije spremljen.
	 */
	private ImageIcon redDisk;
	/**
	 * Varijabla koja čuva ikonu koja predstavlja zeleni disk tj. da sadržaj
	 * tekstualne datoteke nije modificiran.
	 */
	private ImageIcon greenDisk;
	/**
	 * Instanca razreda {@link JToolBar} koji pohranjuje komponente koji
	 * predstavljaju osnovne podatke kao što su duljina teksta, položaj kursora
	 * i trenutno vrijeme.
	 */
	private JToolBar statusBar;
	/**
	 * Instanca razreda {@link Sat} koji predstavlja komponentu koja u sebi
	 * prikazuje trenutni datum i vrijeme u tekstualnom formatu.
	 */
	private Sat sat;
	/**
	 * Instanca razreda {@link FormLocalizationProvider} koja predstavlja
	 * lokalizacijski poslužitelj preko kojeg dohvaćamo trenutni jezik
	 * aplikacije.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);

	/**
	 * Osnovni konstruktor koji otvara prozor i dodaje potrebne komponente.
	 */
	public JNotepadPP() {
		super();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLocation(20, 20);
		setSize(700, 500);
		getContentPane().setLayout(new BorderLayout());
		setTitle("Notepad++");
		tabbedPane = new JTabbedPane();
		statusBar = new JToolBar();
		statusBar.setLayout(new GridLayout(1, 3));
		statusBar.setFloatable(true);
		sat = new Sat();
		initGUI();
		addWindowListener();

	}

	/**
	 * Pomoćna metoda koju koristi konstruktor za inicijalizaciju i dodavanje
	 * pojedinih komponenti u trenutno stvoreni prozor tj. instance razreda
	 * {@link JFrame}
	 */
	private void initGUI() {
		IconImageLoader loader = new IconImageLoader();
		redDisk = loader.createImageIcon("icons/redDisk.png");
		greenDisk = loader.createImageIcon("icons/greenDisk.png");

		statusBar.add(new LJLabel("length", flp));
		statusBar.addSeparator();
		statusBar.add(new JLabel("Ln:0 Col:0 Sel:0"));
		statusBar.addSeparator();
		statusBar.add(sat);
		// promatrač koji ažurira pojedine komponente pri promjeni kartice tj.
		// naslov prozora i trenutni položaj kursora
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if (index != -1) {
					JTextArea editor = editors.get(index);
					if (openedFiles.get(index) == null) {
						setTitle(tabbedPane.getTitleAt(index) + " -Notepad++");
						editor.getCaretListeners()[0].caretUpdate(new MyCaretEvent(editor.getCaret()));
					} else {
						setTitle(openedFiles.get(index).toString() + "-Notepad++");
						editor.getCaretListeners()[0].caretUpdate(new MyCaretEvent(editor.getCaret()));
					}
				} else {
					setTitle("Notepad++");
				}

			}
		});

		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(statusBar, BorderLayout.PAGE_END);

		createActions();
		createMenus();
		createToolBars();

	}

	/**
	 * Pomoćna metoda konstruktora koja dodaje instancu sučelja
	 * {@link WindowListener} koji predstavlja akcije pri otvaranju i zatvaranju
	 * prozora.
	 */
	private void addWindowListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int size = editors.size();
				// iteriramo po svim karticama
				for (int i = 0; i < size; i++) {
					if (tabbedPane.getIconAt(i).equals(redDisk)) {
						int odluka = JOptionPane.showConfirmDialog(JNotepadPP.this,
								flp.getString("fileSave") + tabbedPane.getTitleAt(i) + "?", flp.getString("sysMessage"),
								JOptionPane.YES_NO_CANCEL_OPTION);
						if (odluka == JOptionPane.CANCEL_OPTION) {
							return;
						} else if (odluka == JOptionPane.YES_OPTION) {
							tabbedPane.setSelectedIndex(i);
							saveDocumentAction.actionPerformed(null);
						}
					}
				}
				sat.stop();
				dispose();
			}
		});
	}

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju akcija je otvaranje novog dokumenta u
	 * novoj kartici.
	 */
	private LocalizableAction openNewDocumentAction = new LocalizableAction("new", "openNewDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// novi uređivač i njegovi pripadni promatrači
			JTextArea editor = new JTextArea();
			editor.getDocument().addDocumentListener(new MyDocumentListerner());
			editor.addCaretListener(new MyCaretListener());
			editors.add(editor);

			openedFiles.add(null);
			tabbedPane.addTab(flp.getString("new"), greenDisk, new JScrollPane(editor), flp.getString("new"));
			tabbedPane.setSelectedIndex(editors.size() - 1);
			// omogućuju se ostale akcije
			saveAsDocumentAction.setEnabled(true);
			saveDocumentAction.setEnabled(true);
			documentInfoAction.setEnabled(true);
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente. U ovom slučaju akcija je otvaranje pohranjene
	 * tekstualne datoteke na disku u novoj kartici ovog prozora.
	 */
	private Action openDocumentAction = new LocalizableAction("open", "openDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea editor = new JTextArea();

			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();
			if (!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("file") + fileName.getAbsolutePath() + flp.getString("notExist"),
						flp.getString("error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] okteti;
			try {
				okteti = Files.readAllBytes(filePath);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("readFileError") + fileName.getAbsolutePath() + ".", flp.getString("error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// stvaranje novog uređivača i dodavanje promatrača
			String tekst = new String(okteti, StandardCharsets.UTF_8);
			editor.setText(tekst);
			editor.getDocument().addDocumentListener(new MyDocumentListerner());
			editor.addCaretListener(new MyCaretListener());
			editors.add(editor);

			openedFiles.add(filePath);
			tabbedPane.addTab(fileName.getName(), greenDisk, new JScrollPane(editor), fileName.toString());
			tabbedPane.setSelectedIndex(editors.size() - 1);
			// omogući ostale akcije
			saveAsDocumentAction.setEnabled(true);
			saveDocumentAction.setEnabled(true);
			documentInfoAction.setEnabled(true);
		}
	};

	/**
	 * Razred koji predstavlja implementaciju sučelja {@link DocumentListener}
	 * tj. promatrač kojeg dodajemo u svaki novostvoreni uređivač teksta. Ovaj
	 * promatrač ažurira trenutne ikone u karticama u slučaju promjene u
	 * otvorenoj datoteci.
	 * 
	 * @author Mato Manovic
	 * @version 1.0
	 */
	private class MyDocumentListerner implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			tabbedPane.setIconAt(tabbedPane.getSelectedIndex(), redDisk);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			tabbedPane.setIconAt(tabbedPane.getSelectedIndex(), redDisk);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	}

	/**
	 * Razred koji predstavlja implementaciju sučelja {@link CaretListener} tj.
	 * promatrač koji obavlja ažuriranje prozora u slučaju promjene kursora u
	 * nekom uređivaču teksta.
	 * 
	 * @author Mato Manovic
	 * @version 1.0
	 */
	private class MyCaretListener implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			Document doc = editor.getDocument();
			try {
				String text = doc.getText(0, e.getDot());
				// trenutni redak, stupac kursora i veličina označenog teksta
				int lines = text.replaceAll("[^\n]", "").length() + 1;
				int column = e.getDot() - text.lastIndexOf('\n');
				int selected = Math.abs(e.getDot() - e.getMark());
				// ako je označen nekakav tekst onda omogući pojedine akcije,
				// inače onemogući
				if (selected > 0) {
					copyAction.setEnabled(true);
					cutAction.setEnabled(true);
					uppercaseAction.setEnabled(true);
					lowercaseAction.setEnabled(true);
					invertCaseAction.setEnabled(true);
					ascendingSortAction.setEnabled(true);
					descendingSortAction.setEnabled(true);
					uniqueAction.setEnabled(true);
				} else {
					copyAction.setEnabled(false);
					cutAction.setEnabled(false);
					uppercaseAction.setEnabled(false);
					lowercaseAction.setEnabled(false);
					invertCaseAction.setEnabled(false);
					ascendingSortAction.setEnabled(false);
					descendingSortAction.setEnabled(false);
					uniqueAction.setEnabled(false);
				}
				// ažuriranje prikaza statusne trake.
				String message = "Ln:" + lines + " Col:" + column + " Sel:" + selected;
				((JLabel) statusBar.getComponent(2)).setText(message);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			((JLabel) statusBar.getComponent(0)).setText(flp.getString("length") + ":" + editor.getText().length());

		}

	}

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju akcija koju obavljamo je spremanje
	 * dokumenta pod nekim novim imenom. U slučaju pokušaja prekopiranja preko
	 * već postojeće datoteke aplikacija traži dozvolu korisnika.
	 */
	private Action saveAsDocumentAction = new LocalizableAction("saveAs", "saveAsDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = tabbedPane.getSelectedIndex();
			JTextArea editor = editors.get(index);

			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Save document");
			if (jfc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(JNotepadPP.this, flp.getString("notSave"), flp.getString("warning"),
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Path openedFilePath = jfc.getSelectedFile().toPath();
			if (Files.exists(openedFilePath)) {
				int decision = JOptionPane.showConfirmDialog(JNotepadPP.this,
						flp.getString("overwrite") + openedFilePath.toString(), flp.getString("warning"),
						JOptionPane.YES_NO_OPTION);
				if (decision == JOptionPane.NO_OPTION) {
					return;
				}
			}

			byte[] podatci = editor.getText().getBytes(StandardCharsets.UTF_8);
			try {
				Files.write(openedFilePath, podatci);
			} catch (IOException el) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("readFileError") + openedFilePath.getFileName().toString() + ".",
						flp.getString("error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			tabbedPane.setIconAt(index, greenDisk);
			tabbedPane.setTitleAt(index, openedFilePath.getFileName().toString());
			tabbedPane.setToolTipTextAt(index, openedFilePath.toString());
			setTitle(openedFilePath.toString());
			openedFiles.set(index, openedFilePath);
			JOptionPane.showMessageDialog(JNotepadPP.this, flp.getString("saved"), flp.getString("info"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to pohrana trenutno otvorene
	 * datoteke. U slučaju da je u kartici otvorena nova datoteka aplikacija
	 * traži pod kojim imenom želimo spremiti novostvorenu datoteku.
	 */
	private Action saveDocumentAction = new LocalizableAction("save", "saveDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = tabbedPane.getSelectedIndex();
			JTextArea editor = editors.get(index);

			Path openedFilePath = openedFiles.get(index);
			if (openedFilePath == null) {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Save document");
				if (jfc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(JNotepadPP.this, flp.getString("notSave"), flp.getString("warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				openedFilePath = jfc.getSelectedFile().toPath();
				openedFiles.set(index, openedFilePath);
				tabbedPane.setTitleAt(index, openedFilePath.getFileName().toString());
				tabbedPane.setToolTipTextAt(index, openedFilePath.toString());
				setTitle(openedFilePath.toString());
			}
			byte[] podatci = editor.getText().getBytes(StandardCharsets.UTF_8);
			try {
				Files.write(openedFilePath, podatci);
			} catch (IOException el) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("readFileError") + openedFilePath.toString() + ".", flp.getString("error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			tabbedPane.setIconAt(index, greenDisk);
			JOptionPane.showMessageDialog(JNotepadPP.this, flp.getString("saved"), flp.getString("info"),
					JOptionPane.INFORMATION_MESSAGE);

		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente. U ovom slučaju je to akcija zatvaranja trenutno
	 * otvorene kartice. U slučaju da je datoteka modificirana aplikacija pita
	 * korisnika želi li spremiti trenutnu datoteku prije njenog zatvaranja.
	 */
	private Action closeDocumentAction = new LocalizableAction("close", "closeDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = tabbedPane.getSelectedIndex();
			if (index == -1) {
				return;
			}
			if (tabbedPane.getIconAt(index).equals(redDisk)) {
				int decision = JOptionPane.showConfirmDialog(JNotepadPP.this, flp.getString("fileSave") + "?",
						flp.getString("sysMessage"), JOptionPane.YES_NO_OPTION);
				if (decision == JOptionPane.YES_OPTION) {
					saveDocumentAction.actionPerformed(null);
				}
			}
			tabbedPane.remove(index);
			editors.remove(index);
			openedFiles.remove(index);
			// onemogući pojedine akcije ako nema više otvorenih kartica
			if (editors.size() == 0) {
				saveDocumentAction.setEnabled(false);
				saveAsDocumentAction.setEnabled(false);
				documentInfoAction.setEnabled(false);
			}
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju to je akcija kopiranja označenog
	 * teksta tj. spremanje trenutno označenog teksta u međuspremnik.
	 */
	private Action copyAction = new LocalizableAction("copy", "copyDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			editor.copy();

		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju to je akcija izrezivanja trenutno
	 * označenog teksta i i spremanje označenog teksta u međuspremnik.
	 */
	private Action cutAction = new LocalizableAction("cut", "cutDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			editor.cut();
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to umetanje teksta spremljenog u
	 * međuspremniku računala.
	 */
	private Action pasteAction = new LocalizableAction("paste", "pasteDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (tabbedPane.getSelectedIndex() == -1) {
				return;
			}
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			editor.paste();
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to dohvat osnovnih informacija o
	 * trenutnom tekstu u trenutno otvorenoj kartici.Podaci su ukupan broj
	 * znakova, broj znakova bez praznina i broj redova.
	 */
	private Action documentInfoAction = new LocalizableAction("info", "infoDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			int nonSpaceChars = editor.getText().replaceAll("\\s", "").length();
			String message = flp.getString("info1") + " " + editor.getText().length() + " " + flp.getString("info2")
					+ " " + nonSpaceChars + " " + flp.getString("info3") + " " + editor.getLineCount() + " "
					+ flp.getString("info4");
			JOptionPane.showMessageDialog(JNotepadPP.this, message);

		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovo slučaju je to akcija izlazka iz aplikacije.
	 */
	private Action exitAction = new LocalizableAction("exit", "exitDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// pokrećemo zatvaranje prozora
			JNotepadPP.this.dispatchEvent(new WindowEvent(JNotepadPP.this, WindowEvent.WINDOW_CLOSING));
		}
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to akcija pretvorbe svih slova
	 * označenog teksta u velika slova.
	 */
	private Action uppercaseAction = new LocalizableAction("uppercase", "upperDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			String selected = editor.getSelectedText();
			try {
				editor.getDocument().remove(editor.getSelectionStart(), selected.length());
				editor.getDocument().insertString(editor.getSelectionStart(), selected.toUpperCase(), null);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		};
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to akcija pretvorbe svih slova
	 * označenog teksta u mala slova.
	 */
	private Action lowercaseAction = new LocalizableAction("lowercase", "lowerDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			String selected = editor.getSelectedText();
			try {
				editor.getDocument().remove(editor.getSelectionStart(), selected.length());
				editor.getDocument().insertString(editor.getSelectionStart(), selected.toLowerCase(), null);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		};
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente. U ovom slučaju je to akcija koja sva mala slova u
	 * označenog teksta pretvara u velika i obrnuto.
	 */
	private Action invertCaseAction = new LocalizableAction("invertcase", "invertDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			String selected = editor.getSelectedText();
			try {
				editor.getDocument().remove(editor.getSelectionStart(), selected.length());
				editor.getDocument().insertString(editor.getSelectionStart(), TextUtility.changeCase(selected), null);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

		};

	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente. U ovom slučaju je to ulazno sortiranje odabranih
	 * linija teksta.Ukoliko je odabran dio retka nekog teksta uzet će se u
	 * obzir cijeli redak.
	 */
	private Action ascendingSortAction = new LocalizableAction("ascending", "ascDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			TextUtility.sortLines(editor, true);
		};
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente. U ovom slučaju je to silazno sortiranje odabranih
	 * linija teksta.Ukoliko je odabran dio retka nekog teksta uzet će se u
	 * obzir cijeli redak.
	 */
	private Action descendingSortAction = new LocalizableAction("descending", "descDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			TextUtility.sortLines(editor, false);
		};
	};

	/**
	 * Instanca razreda {@link LocalizableAction} koji predstavlja akciju
	 * pojedine komponente.U ovom slučaju je to akcija koja izbacuje sve duple
	 * retke u odabranom tekstu. Ukoliko je odabran dio retka uzimamo kao da je
	 * odabran cijeli.
	 */
	private Action uniqueAction = new LocalizableAction("unique", "uniqueDescr", flp) {
		/** serijski broj */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JTextArea editor = editors.get(tabbedPane.getSelectedIndex());
			TextUtility.removeDuplicate(editor);
		};
	};

	/**
	 * Pomoćna metoda koja obavlja dodavanje pojedinih izbornika i njihovih
	 * podkomponenata u meni prozora.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(new LocalizableAction("file", "fileDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		menuBar.add(fileMenu);
		// dodajemo elemente izbornika File
		fileMenu.add(new JMenuItem(openNewDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.add(new JMenuItem(documentInfoAction));
		fileMenu.add(new JMenuItem(exitAction));

		JMenu editMenu = new JMenu(new LocalizableAction("edit", "editDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		menuBar.add(editMenu);
		// elementi izbornika Edit
		editMenu.add(new JMenuItem(copyAction));
		editMenu.add(new JMenuItem(pasteAction));
		editMenu.add(new JMenuItem(cutAction));

		JMenu languageMenu = new JMenu(new LocalizableAction("language", "languageMenuDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		menuBar.add(languageMenu);
		// izbornici za jezik aplikacije
		JMenuItem item1 = new JMenuItem("hr");
		JMenuItem item2 = new JMenuItem("de");
		JMenuItem item3 = new JMenuItem("en");

		item1.addActionListener(e -> {
			LocalizationProvider.getInstance().setLanguage("hr");
		});
		item2.addActionListener(e -> {
			LocalizationProvider.getInstance().setLanguage("de");
		});
		item3.addActionListener(e -> {
			LocalizationProvider.getInstance().setLanguage("en");
		});
		languageMenu.add(item1);
		languageMenu.add(item2);
		languageMenu.add(item3);
		// meni Tools i njegove podkomponente
		JMenu toolsMenu = new JMenu(new LocalizableAction("tools", "toolsDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		menuBar.add(toolsMenu);

		JMenu changeCaseMenu = new JMenu(new LocalizableAction("change", "changeDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		toolsMenu.add(changeCaseMenu);
		changeCaseMenu.add(new JMenuItem(uppercaseAction));
		changeCaseMenu.add(new JMenuItem(lowercaseAction));
		changeCaseMenu.add(new JMenuItem(invertCaseAction));

		JMenu sortMenu = new JMenu(new LocalizableAction("sort", "sortDescr", flp) {
			/** Serijski broj */
			private static final long serialVersionUID = 1L;
		});
		toolsMenu.add(sortMenu);
		sortMenu.add(new JMenuItem(ascendingSortAction));
		sortMenu.add(new JMenuItem(descendingSortAction));

		toolsMenu.add(new JMenuItem(uniqueAction));
		this.setJMenuBar(menuBar);

	}

	/**
	 * Pomoćna metoda koja dodaje komponente za izravno obavljanje pojedinih
	 * akcija tj. alatnu traku.
	 */
	private void createToolBars() {
		JToolBar toolBar = new JToolBar(flp.getString("tools"));
		toolBar.setFloatable(true);

		toolBar.add(new JButton(openNewDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(saveAsDocumentAction));
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.add(new JButton(documentInfoAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(exitAction));

		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}

	/**
	 * Pomoćna metoda koja za sve akcije definira skup tipaka kojima izravno
	 * pokrećemo obavljanje nekih akcija.
	 */
	private void createActions() {
		openNewDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		openNewDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveDocumentAction.setEnabled(false);

		saveAsDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control alt S"));
		saveAsDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		saveAsDocumentAction.setEnabled(false);

		closeDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
		closeDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);

		copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		copyAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		copyAction.setEnabled(false);

		cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		cutAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		cutAction.setEnabled(false);

		pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		pasteAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);

		documentInfoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		documentInfoAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		documentInfoAction.setEnabled(false);

		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control E"));
		exitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);

		uppercaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F3"));
		uppercaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		uppercaseAction.setEnabled(false);
		lowercaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F4"));
		lowercaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
		lowercaseAction.setEnabled(false);
		invertCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F3"));
		invertCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		invertCaseAction.setEnabled(false);

		ascendingSortAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
		ascendingSortAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
		ascendingSortAction.setEnabled(false);
		descendingSortAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control D"));
		descendingSortAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		descendingSortAction.setEnabled(false);

		uniqueAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control K"));
		uniqueAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		uniqueAction.setEnabled(false);
	}

	/**
	 * Početna točka nakon pokretanja programa.
	 * 
	 * @param args
	 *            Ne očekuju se.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new JNotepadPP().setVisible(true);
		});
	}
}
