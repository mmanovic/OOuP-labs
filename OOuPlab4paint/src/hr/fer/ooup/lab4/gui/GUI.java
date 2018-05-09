package hr.fer.ooup.lab4.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.ooup.lab4.listener.DocumentModelListener;
import hr.fer.ooup.lab4.objects.CompositeObject;
import hr.fer.ooup.lab4.objects.GraphicalObject;
import hr.fer.ooup.lab4.objects.LineSegment;
import hr.fer.ooup.lab4.objects.Oval;
import hr.fer.ooup.lab4.renderer.G2DRenderer;
import hr.fer.ooup.lab4.renderer.Renderer;
import hr.fer.ooup.lab4.renderer.SVGRenderer;
import hr.fer.ooup.lab4.state.AddShapeState;
import hr.fer.ooup.lab4.state.EraserState;
import hr.fer.ooup.lab4.state.IdleState;
import hr.fer.ooup.lab4.state.SelectShapeState;
import hr.fer.ooup.lab4.state.State;

public class GUI extends JFrame {
	private DocumentModel model;
	private Canvas canvas;
	private List<GraphicalObject> prototypes;
	private Map<String, GraphicalObject> protoMap = new HashMap<>();
	private State currentState;

	public GUI(List<GraphicalObject> objects) {
		this.prototypes = objects;
		this.model = new DocumentModel();
		this.currentState = new IdleState();
		for (GraphicalObject go : objects) {
			protoMap.put(go.getShapeID(), go);
		}
		GraphicalObject composite = new CompositeObject();
		protoMap.put(composite.getShapeID(), composite);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocation(50, 50);
		setSize(640, 640);
		setTitle("VectorGraphicEditor");
		getContentPane().setLayout(new BorderLayout());
		initGUI();
	}

	private void initGUI() {
		canvas = new Canvas();
		canvas.setFocusable(true);
		add(canvas, BorderLayout.CENTER);
		model.addDocumentModelListener(canvas);
		addToolBar();
		addListeners();
	}

	private Action svgExportAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Save document");
			if (jfc.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(GUI.this, "Nothing selected to export.", "warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			String path = jfc.getSelectedFile().getPath();
			if (!path.endsWith(".svg")) {
				path += ".svg";
			}
			SVGRenderer svgRenderer = new SVGRenderer(path);
			for (GraphicalObject go : model.list()) {
				go.render(svgRenderer);
			}

			try {
				svgRenderer.close();
				JOptionPane.showMessageDialog(GUI.this, "SVG file generated.", "INFO", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				return;
			}
		}

	};

	private Action saveAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Save document");
			if (jfc.showSaveDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(GUI.this, "Nothing selected to save.", "warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			String path = jfc.getSelectedFile().getPath();
			List<String> rows = new ArrayList<String>();
			for (GraphicalObject go : model.list()) {
				go.save(rows);
			}
			try {
				FileWriter fw = new FileWriter(new File(path));
				for (String row : rows) {
					fw.write(row + "\n");
				}
				fw.flush();
				fw.close();
				JOptionPane.showMessageDialog(GUI.this, "File generated.", "INFO", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
			}
		}
	};

	private Action loadAction = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(GUI.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();
			if (!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(GUI.this, "File " + fileName.getAbsolutePath() + " notExist", "error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
				Stack<GraphicalObject> stack = new Stack<GraphicalObject>();

				for (String line : lines) {
					String[] tmp = line.split(" ", 2);
					GraphicalObject go = protoMap.get(tmp[0]);
					go.duplicate().load(stack, tmp[1]);
				}
				for (GraphicalObject go : stack) {
					model.addGraphicalObject(go);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(GUI.this, "Invalid format of file.", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	};

	private void addToolBar() {
		JToolBar tools = new JToolBar();
		loadAction.putValue(Action.NAME, "Uèitaj");
		tools.add(loadAction);
		saveAction.putValue(Action.NAME, "Pohrani");
		tools.add(saveAction);
		svgExportAction.putValue(Action.NAME, "SVG export");
		tools.add(svgExportAction);
		for (GraphicalObject go : prototypes) {
			Action action = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentState.onLeaving();
					currentState = new AddShapeState(model, go);
				}
			};
			action.putValue(Action.NAME, go.getShapeName());
			tools.add(action);
		}
		Action action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new SelectShapeState(model);
			}
		};
		action.putValue(Action.NAME, "Selektiraj");
		tools.add(action);

		action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new EraserState(model);
			}
		};
		action.putValue(Action.NAME, "Brisalo");
		tools.add(action);
		add(tools, BorderLayout.NORTH);
	}

	private void addListeners() {

		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					currentState.onLeaving();
					currentState = new IdleState();
				} else {
					currentState.keyPressed(e.getKeyCode());
				}
			}
		});

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				currentState.mouseDown(new Point(e.getPoint().x, e.getPoint().y), e.isShiftDown(), e.isControlDown());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				currentState.mouseUp(new Point(e.getPoint().x, e.getPoint().y), e.isShiftDown(), e.isControlDown());
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentState.mouseDragged(new Point(e.getPoint().x, e.getPoint().y));
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				List<GraphicalObject> objects = new ArrayList<GraphicalObject>();
				objects.add(new LineSegment());
				objects.add(new Oval());
				new GUI(objects).setVisible(true);
			}
		});
	}

	private class Canvas extends JComponent implements DocumentModelListener {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			requestFocusInWindow();
			Renderer renderer = new G2DRenderer((Graphics2D) g);
			for (GraphicalObject go : model.list()) {
				go.render(renderer);
				currentState.afterDraw(renderer, go);
			}
			currentState.afterDraw(renderer);
		}

		@Override
		public void documentChange() {
			this.repaint();
		}
	}
}
