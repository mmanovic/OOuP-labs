package hr.fer.ooup.lab4.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hr.fer.ooup.lab4.gui.DocumentModel;
import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.objects.GraphicalObject;
import hr.fer.ooup.lab4.renderer.Renderer;

public class EraserState extends IdleState {
	private DocumentModel model;
	private List<Point> points = new ArrayList<>();

	public EraserState(DocumentModel model) {
		super();
		this.model = model;
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		points.add(mousePoint);
		model.notifyListeners();
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		points.add(mousePoint);
		Set<GraphicalObject> objectsToDelete = new HashSet<>();
		for (Point point : points) {
			objectsToDelete.addAll(model.objectsWithPoint(point));
		}
		for (GraphicalObject go : objectsToDelete) {
			model.removeGraphicalObject(go);
		}
		points.clear();
		model.notifyListeners();
	}

	@Override
	public void afterDraw(Renderer r) {
		for (int i = 0; i < points.size() - 1; i++) {
			r.drawLine(points.get(i), points.get(i + 1));
		}
	}
}
