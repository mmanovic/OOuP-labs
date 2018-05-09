package hr.fer.ooup.lab4.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.gui.Rectangle;
import hr.fer.ooup.lab4.listener.GraphicalObjectListener;
import hr.fer.ooup.lab4.renderer.Renderer;

public class CompositeObject extends AbstractGraphicalObject {
	private List<GraphicalObject> objects;

	public CompositeObject() {
		super(new Point[] {});
		objects = new ArrayList<>();
	}

	public CompositeObject(List<GraphicalObject> objects, boolean selected) {
		super(new Point[] {});
		this.objects = objects;
		this.selected = selected;
		listeners = new ArrayList<GraphicalObjectListener>();
	}

	@Override
	public void translate(Point delta) {
		for (GraphicalObject object : objects) {
			object.translate(delta);
		}
		notifyListeners();
	}

	public List<GraphicalObject> getObjects() {
		return objects;
	}

	public void setObjects(List<GraphicalObject> objects) {
		this.objects = objects;
	}

	@Override
	public Rectangle getBoundingBox() {
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (GraphicalObject go : objects) {
			Rectangle rect = go.getBoundingBox();
			minX = Integer.min(minX, rect.getX());
			maxX = Integer.max(maxX, rect.getX() + rect.getWidth());
			minY = Integer.min(minY, rect.getY());
			maxY = Integer.max(maxY, rect.getY() + rect.getHeight());
		}

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		double res = Double.MAX_VALUE;
		for (GraphicalObject go : objects) {
			res = Double.min(res, go.selectionDistance(mousePoint));
		}
		return res;
	}

	@Override
	public String getShapeName() {
		return "Composite";
	}

	@Override
	public GraphicalObject duplicate() {
		List<GraphicalObject> duplicates = new ArrayList<>();
		for (GraphicalObject go : objects) {
			duplicates.add(go.duplicate());
		}
		return new CompositeObject(duplicates, false);
	}

	@Override
	public void render(Renderer r) {
		for (GraphicalObject object : objects) {
			object.render(r);
		}
	}

	@Override
	public String getShapeID() {
		return "@COMP";
	}

	@Override
	public void save(List<String> rows) {
		for (GraphicalObject go : objects) {
			go.save(rows);
		}
		rows.add(getShapeID() + " " + objects.size());
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		int n = Integer.parseInt(data.trim());
		for (int i = 0; i < n; i++) {
			objects.add(stack.pop());
		}
		stack.push(this);
	}

}
