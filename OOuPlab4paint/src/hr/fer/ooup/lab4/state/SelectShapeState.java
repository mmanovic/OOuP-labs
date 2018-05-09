package hr.fer.ooup.lab4.state;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab4.gui.DocumentModel;
import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.gui.Rectangle;
import hr.fer.ooup.lab4.objects.CompositeObject;
import hr.fer.ooup.lab4.objects.GraphicalObject;
import hr.fer.ooup.lab4.renderer.Renderer;

public class SelectShapeState extends IdleState {
	private static int HOT_POINT = 3;
	private DocumentModel model;
	private GraphicalObject selected;
	private int selHotPoint = -1;

	public SelectShapeState(DocumentModel model) {
		super();
		this.model = model;
	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_UP) {
			for (GraphicalObject go : model.getSelectedObjects()) {
				go.translate(new Point(0, -1));
			}
		} else if (keyCode == KeyEvent.VK_DOWN) {
			for (GraphicalObject go : model.getSelectedObjects()) {
				go.translate(new Point(0, 1));
			}
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			for (GraphicalObject go : model.getSelectedObjects()) {
				go.translate(new Point(1, 0));
			}
		} else if (keyCode == KeyEvent.VK_LEFT) {
			for (GraphicalObject go : model.getSelectedObjects()) {
				go.translate(new Point(-1, 0));
			}
		} else if (keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_ADD) {
			if (selected != null) {
				model.increaseZ(selected);
			}
		} else if (keyCode == KeyEvent.VK_MINUS || keyCode == KeyEvent.VK_SUBTRACT) {
			if (selected != null) {
				model.decreaseZ(selected);
			}
		} else if (keyCode == KeyEvent.VK_G) {
			List<GraphicalObject> selectedObjects = model.getSelectedObjects();
			if (selectedObjects.size() > 1) {
				List<GraphicalObject> compositeObjects = new ArrayList<>();
				for (GraphicalObject go : selectedObjects) {
					compositeObjects.add(go);
				}
				for (GraphicalObject go : compositeObjects) {
					model.removeGraphicalObject(go);
				}
				GraphicalObject go = new CompositeObject(compositeObjects, true);
				model.addGraphicalObject(go);
			}

		} else if (keyCode == KeyEvent.VK_U) {
			List<GraphicalObject> ungroup = model.getSelectedObjects();
			if (ungroup.size() == 1 && ungroup.get(0).getShapeName().equals("Composite")) {
				CompositeObject composite = (CompositeObject) ungroup.get(0);
				List<GraphicalObject> objects = composite.getObjects();
				model.removeGraphicalObject(composite);
				for (GraphicalObject go : objects) {
					go.setSelected(true);
					model.addGraphicalObject(go);
				}
			}

		}
	}

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject go = model.findSelectedGraphicalObject(mousePoint);
		selected = go;
		if (go == null) {
			model.deselectAll();
			return;
		}

		if (ctrlDown) {
			selected = null;
			go.setSelected(!go.isSelected());
		} else {
			model.deselectAll();
			go.setSelected(true);

		}
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		if (selected == null) {
			return;
		}
		if (selHotPoint >= 0) {
			selected.setHotPoint(selHotPoint, mousePoint);
		} else {
			int index = model.findSelectedHotPoint(selected, mousePoint);
			if (index != -1) {
				selected.setHotPointSelected(index, true);
				selHotPoint = index;
				selected.setHotPoint(index, mousePoint);
			}
		}
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		if (selected != null) {
			selected.setHotPointSelected(selHotPoint, false);
			selHotPoint--;
		}
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
		if (!go.isSelected()) {
			return;
		}
		Rectangle rect = go.getBoundingBox();

		r.drawLine(new Point(rect.getX(), rect.getY() + rect.getHeight()),
				new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));
		r.drawLine(new Point(rect.getX() + rect.getWidth(), rect.getY()),
				new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));
		r.drawLine(new Point(rect.getX(), rect.getY()), new Point(rect.getX() + rect.getWidth(), rect.getY()));
		r.drawLine(new Point(rect.getX(), rect.getY()), new Point(rect.getX(), rect.getY() + rect.getHeight()));

		if (selected != null && selected.equals(go)) {
			for (int i = 0; i < go.getNumberOfHotPoints(); i++) {
				Point point = go.getHotPoint(i);
				r.drawLine(new Point(point.getX() - HOT_POINT, point.getY() + HOT_POINT),
						new Point(point.getX() + HOT_POINT, point.getY() + HOT_POINT));
				r.drawLine(new Point(point.getX() + HOT_POINT, point.getY() - HOT_POINT),
						new Point(point.getX() + HOT_POINT, point.getY() + HOT_POINT));
				r.drawLine(new Point(point.getX() - HOT_POINT, point.getY() - HOT_POINT),
						new Point(point.getX() + HOT_POINT, point.getY() - HOT_POINT));
				r.drawLine(new Point(point.getX() - HOT_POINT, point.getY() - HOT_POINT),
						new Point(point.getX() - HOT_POINT, point.getY() + HOT_POINT));
			}

		}
	}

	@Override
	public void onLeaving() {
		model.deselectAll();
	}
}
