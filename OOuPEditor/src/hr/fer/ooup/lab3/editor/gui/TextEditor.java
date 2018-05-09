package hr.fer.ooup.lab3.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import hr.fer.ooup.lab3.editor.manager.UndoManager;
import hr.fer.ooup.lab3.editor.observers.ClipboardObserver;
import hr.fer.ooup.lab3.editor.observers.CursorObserver;
import hr.fer.ooup.lab3.editor.observers.StackObserver;
import hr.fer.ooup.lab3.editor.observers.TextObserver;
import hr.fer.ooup.lab3.editor.plugins.Plugin;
import hr.fer.ooup.lab3.editor.stack.ClipboardStack;

public class TextEditor extends JFrame {
	TextPanel panel = new TextPanel();
	private UndoManager undoManager;
	TextEditorModel model = new TextEditorModel("Neki\npocetni\ntekst.");
	private JScrollPane scrollPane = new JScrollPane();
	private static final int MARGINA = 5;
	private static final int LINIJA_RAZMAK = 20;
	private Path openedFile;
	private StatusLabel statusLabel;
	private Map<String, Plugin> extensions = new HashMap<>();
	private ClipboardStack clipboard = new ClipboardStack();

	public TextEditor() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocation(20, 50);
		setSize(800, 600);
		setTitle("TextEditor");
		undoManager = UndoManager.getInstance();
		getContentPane().setLayout(new BorderLayout());
		initGUI();
	}

	private void initGUI() {
		addComponents();
		addObservers();
		createActions();
		createMenus();
		createToolBars();
	}

	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu file = new JMenu("File");
		file.add(new JMenuItem(openDocumentAction));
		file.add(new JMenuItem(saveDocumentAction));
		file.addSeparator();
		file.add(new JMenuItem(exitAction));

		menuBar.add(file);

		JMenu edit = new JMenu("Edit");
		edit.add(new JMenuItem(undoAction));
		edit.add(new JMenuItem(redoAction));
		edit.add(new JMenuItem(cutAction));
		edit.add(new JMenuItem(copyAction));
		edit.add(new JMenuItem(pasteAction));
		edit.add(new JMenuItem(pasteAndTakeAction));
		edit.add(new JMenuItem(deleteSelectionAction));
		edit.add(new JMenuItem(clearAction));

		menuBar.add(edit);

		JMenu move = new JMenu("Move");
		move.add(new JMenuItem(cursorToStart));
		move.add(new JMenuItem(cursorToEnd));
		menuBar.add(move);

		List<Plugin> plugins = loadPlugins();
		if (!plugins.isEmpty()) {
			JMenu extensionsMenu = new JMenu("Plugins");
			for (Plugin plugin : plugins) {
				extensions.put(plugin.getName(), plugin);
				Action action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						extensions.get(e.getActionCommand()).execute(model, undoManager, clipboard);
					}
				};

				action.putValue(Action.NAME, plugin.getName());
				action.putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
				extensionsMenu.add(new JMenuItem(action));
			}
			menuBar.add(extensionsMenu);
		}
		setJMenuBar(menuBar);
	}

	private List<Plugin> loadPlugins() {
		File pluginsDirectory = new File("plugins/");
		File[] files = pluginsDirectory.listFiles();
		List<Plugin> plugins = new ArrayList<Plugin>();
		try {
			URLClassLoader urlClassLoader = new URLClassLoader(
					new URL[] { /* pluginsDirectory.toURI().toURL() */ });
			for (File file : files) {
				String[] tmp = file.toString().split("\\\\");
				String className = tmp[tmp.length - 1].substring(0, tmp[tmp.length - 1].length() - 6);
				className = "hr.fer.ooup.lab3.editor.plugins." + className;
				plugins.add(((Class<Plugin>) urlClassLoader.loadClass(className)).newInstance());
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return plugins;
	}

	private void createToolBars() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.add(new JButton(undoAction));
		toolBar.add(new JButton(redoAction));
		toolBar.setFloatable(true);
		add(toolBar, BorderLayout.NORTH);
	}

	private void createActions() {
		cutAction.putValue(Action.NAME, "Cut");
		cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		cutAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		cutAction.setEnabled(false);

		copyAction.putValue(Action.NAME, "Copy");
		copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		copyAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		copyAction.setEnabled(false);

		pasteAction.putValue(Action.NAME, "Paste");
		pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		pasteAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
		pasteAction.setEnabled(false);
		clipboard.addClipboardObserver(pasteAction);

		pasteAndTakeAction.putValue(Action.NAME, "Paste and Take");
		pasteAndTakeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift V"));
		pasteAndTakeAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
		pasteAndTakeAction.setEnabled(false);
		clipboard.addClipboardObserver(pasteAndTakeAction);

		undoAction.putValue(Action.NAME, "Undo");
		undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
		undoAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Z);

		redoAction.putValue(Action.NAME, "Redo");
		redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
		redoAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Y);

		undoAction.setEnabled(false);
		undoManager.addUndoStackObserver(undoAction);
		redoAction.setEnabled(false);
		undoManager.addRedoStackObserver(redoAction);

		cursorToStart.putValue(Action.NAME, "Cursor to document start");
		cursorToEnd.putValue(Action.NAME, "Cursor to document end");
		clearAction.putValue(Action.NAME, "Clear document");
		deleteSelectionAction.putValue(Action.NAME, "Delete selection");
		deleteSelectionAction.setEnabled(false);

		openDocumentAction.putValue(Action.NAME, "Open");
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

		saveDocumentAction.putValue(Action.NAME, "Save");
		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);

		exitAction.putValue(Action.NAME, "Exit");
	}

	private Action exitAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			TextEditor.this.dispatchEvent(new WindowEvent(TextEditor.this, WindowEvent.WINDOW_CLOSING));
		}
	};
	private Action openDocumentAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(TextEditor.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();
			if (!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(TextEditor.this, "File " + fileName.getAbsolutePath() + " notExist",
						"error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] okteti;
			try {
				okteti = Files.readAllBytes(filePath);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(TextEditor.this,
						"Error during read file " + fileName.getAbsolutePath() + ".", "error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String text = new String(okteti, StandardCharsets.UTF_8);
			model = new TextEditorModel(text);
			model.addCursorObserver(new CursorObserver() {
				@Override
				public void updateCursorLocation(Location loc) {
					panel.revalidate();
					panel.repaint();
				}
			});

			model.addTextObserver(new TextObserver() {
				@Override
				public void updateText() {
					panel.revalidate();
					panel.repaint();
				}
			});
			model.addCursorObserver(statusLabel);
			model.addTextObserver(statusLabel);
			openedFile = filePath;
		}
	};

	private Action saveDocumentAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (openedFile == null) {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Save document");
				if (jfc.showSaveDialog(TextEditor.this) != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(TextEditor.this, "Nothing was save.", "warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				openedFile = jfc.getSelectedFile().toPath();
			}
			StringBuilder sb = new StringBuilder();
			Iterator<String> iterator = model.allLines();
			while (iterator.hasNext()) {
				sb.append(iterator.next()).append(System.lineSeparator());
			}
			byte[] podatci = sb.toString().getBytes(StandardCharsets.UTF_8);
			try {
				Files.write(openedFile, podatci);
			} catch (IOException el) {
				JOptionPane.showMessageDialog(TextEditor.this, "Read file error " + openedFile.toString() + ".",
						"error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(TextEditor.this, "File is saved.", "info", JOptionPane.INFORMATION_MESSAGE);
		}
	};

	private Action cutAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			clipboard.push(model.getSelectionText());
			model.deleteRange();
			panel.revalidate();
			panel.repaint();
		}
	};
	private Action copyAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			clipboard.push(model.getSelectionText());
		}
	};
	private PasteAction pasteAction = new PasteAction();
	private PasteAndTakeAction pasteAndTakeAction = new PasteAndTakeAction();

	class PasteAction extends AbstractAction implements ClipboardObserver {
		@Override
		public void actionPerformed(ActionEvent e) {
			model.insert(clipboard.peek());
			panel.revalidate();
			panel.repaint();
		}

		@Override
		public void updateClipboard() {
			if (clipboard.isEmpty()) {
				pasteAction.setEnabled(true);
			} else {
				pasteAction.setEnabled(false);
			}
		}
	}

	class PasteAndTakeAction extends AbstractAction implements ClipboardObserver {
		@Override
		public void actionPerformed(ActionEvent e) {
			model.insert(clipboard.pop());
			panel.revalidate();
			panel.repaint();
		}

		@Override
		public void updateClipboard() {
			if (clipboard.isEmpty()) {
				pasteAndTakeAction.setEnabled(true);
			} else {
				pasteAndTakeAction.setEnabled(false);
			}
		}
	}

	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();

	class UndoAction extends AbstractAction implements StackObserver {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			undoManager.undo();
			panel.revalidate();
			panel.repaint();
		}

		@Override
		public void updateStack(boolean isEmpty) {
			if (isEmpty) {
				undoAction.setEnabled(false);
			} else {
				undoAction.setEnabled(true);
			}
		}
	}

	class RedoAction extends AbstractAction implements StackObserver {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			undoManager.redo();
			panel.revalidate();
			panel.repaint();
		}

		@Override
		public void updateStack(boolean isEmpty) {
			if (isEmpty) {
				redoAction.setEnabled(false);
			} else {
				redoAction.setEnabled(true);
			}
		}

	}

	private Action cursorToStart = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.moveCursorToDocumentStart();
		}
	};

	private Action cursorToEnd = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.moveCursorToDocumentEnd();
		}
	};

	private Action clearAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.clear();
			panel.revalidate();
			panel.repaint();
		}
	};

	private Action deleteSelectionAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.deleteRange();
		}
	};

	private void addObservers() {
		model.addCursorObserver(new CursorObserver() {
			@Override
			public void updateCursorLocation(Location loc) {
				panel.revalidate();
				panel.repaint();
			}
		});

		model.addTextObserver(new TextObserver() {
			@Override
			public void updateText() {
				panel.revalidate();
				panel.repaint();
			}
		});

		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.revalidate();
				panel.repaint();
			}
		});
		timer.start();

	}

	private void addComponents() {
		scrollPane.setViewportView(panel);
		add(scrollPane, BorderLayout.CENTER);
		statusLabel = new StatusLabel();
		model.addCursorObserver(statusLabel);
		model.addTextObserver(statusLabel);
		add(statusLabel, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new TextEditor().setVisible(true);
		});
	}

	class StatusLabel extends JLabel implements CursorObserver, TextObserver {
		private Location cursorLocation;
		private int linesNumber;

		public StatusLabel() {
			this.cursorLocation = model.getCursorLocation();
			update();
		}

		@Override
		public void updateText() {
			cursorLocation = model.getCursorLocation();
			update();
		}

		@Override
		public void updateCursorLocation(Location loc) {
			cursorLocation = loc;
			update();
		}

		public void update() {
			linesNumber = model.getLines().size();
			this.setText("Line " + (cursorLocation.getX() + 1) + ":  Column " + (cursorLocation.getY() + 1)
					+ ":  Number of lines " + linesNumber);
		}
	}

	class TextPanel extends JComponent {
		int width;
		int height;

		public TextPanel() {
			setFocusable(true);
			requestFocusInWindow();
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent key) {
					switch (key.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						if (key.isShiftDown()) {
							model.addSelectionOnLeft();
						} else {
							model.moveCursorLeft();
							model.setSelectionRange(null);
						}
						break;

					case KeyEvent.VK_RIGHT:
						if (key.isShiftDown()) {
							model.addSelectionRight();
						} else {
							model.moveCursorRight();
							model.setSelectionRange(null);
						}
						break;

					case KeyEvent.VK_UP:
						if (key.isShiftDown()) {
							model.addSelectionUp();
						} else {
							model.moveCursorUp();
							model.setSelectionRange(null);
						}
						break;

					case KeyEvent.VK_DOWN:
						if (key.isShiftDown()) {
							model.addSelectionDown();
						} else {
							model.moveCursorDown();
							model.setSelectionRange(null);
						}
						break;

					case KeyEvent.VK_BACK_SPACE:
						if (model.hasSelection()) {
							model.deleteRange();
						} else {
							model.deleteBefore();
						}
						break;

					case KeyEvent.VK_DELETE:
						if (model.hasSelection()) {
							model.deleteRange();
						} else {
							model.deleteAfter();
						}
						break;
					default:
						if (!key.isActionKey() && !key.isMetaDown() && key.getKeyCode() != KeyEvent.VK_SHIFT
								&& key.getKeyCode() != KeyEvent.VK_ALT && key.getKeyCode() != KeyEvent.VK_ALT_GRAPH
								&& !key.isControlDown() && key.getKeyCode() != KeyEvent.VK_ESCAPE) {
							model.insert(key.getKeyChar());
						}
						break;
					}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			this.requestFocusInWindow();
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			FontMetrics fm = g2d.getFontMetrics();

			if (model.hasSelection()) {
				cutAction.setEnabled(true);
				copyAction.setEnabled(true);
				deleteSelectionAction.setEnabled(true);
				LocationRange selection = model.getSelectionRange();
				int numOfLines = selection.getEnd().getX() - selection.getStart().getX();

				Location start = selection.getStart();
				Location end = selection.getEnd();
				Color old = g2d.getColor();
				g2d.setColor(Color.BLUE);
				Rectangle2D rect = null;
				String line = null;

				if (numOfLines == 0) {
					line = model.getLines().get(start.getX());
					rect = fm.getStringBounds(line.substring(start.getY(), end.getY()), g2d);
					g2d.fillRect(MARGINA + fm.stringWidth(line.substring(0, start.getY())),
							3 + start.getX() * LINIJA_RAZMAK, (int) rect.getWidth(), fm.getHeight());
				} else {
					line = model.getLines().get(start.getX());
					rect = fm.getStringBounds(line.substring(start.getY()), g2d);
					g2d.fillRect(MARGINA + fm.stringWidth(line.substring(0, start.getY())),
							3 + start.getX() * LINIJA_RAZMAK, (int) rect.getWidth(), fm.getHeight());
					for (int i = start.getX() + 1; i < end.getX(); i++) {
						line = model.getLines().get(i);
						rect = fm.getStringBounds(line, g2d);
						g2d.fillRect(MARGINA, 3 + i * LINIJA_RAZMAK, (int) rect.getWidth(), fm.getHeight());
					}

					line = model.getLines().get(end.getX());
					rect = fm.getStringBounds(line.substring(0, end.getY()), g2d);
					g2d.fillRect(MARGINA, 3 + end.getX() * LINIJA_RAZMAK, (int) rect.getWidth(), fm.getHeight());
				}

				g2d.setColor(old);
			} else {
				cutAction.setEnabled(false);
				deleteSelectionAction.setEnabled(false);
				copyAction.setEnabled(false);
			}

			String line;
			Iterator<String> iterator = model.allLines();
			int i = 0;
			width = 0;
			for (i = 0; iterator.hasNext(); i++) {
				line = iterator.next();
				g2d.drawString(line, MARGINA, i * LINIJA_RAZMAK + fm.getHeight());
				int lineWidth = (int) fm.getStringBounds(line, g2d).getWidth();
				if (lineWidth > width) {
					width = lineWidth;
				}
			}
			height = i * LINIJA_RAZMAK + fm.getHeight();
			width += 3 * MARGINA;
			Location location = model.getCursorLocation();
			line = "";
			if (model.getLines().size() > 0) {
				line = model.getLines().get(location.getX());
				line = line.substring(0, location.getY());
			}
			int x = MARGINA + fm.stringWidth(line);
			int y2 = LINIJA_RAZMAK * location.getX() + fm.getHeight();
			int y1 = y2 - LINIJA_RAZMAK * 2 / 3;
			g2d.drawLine(x, y1, x, y2);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}
	}

}
