package hr.fer.ooup.lab4.objects;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab4.gui.GeometryUtil;
import hr.fer.ooup.lab4.gui.Point;
import hr.fer.ooup.lab4.listener.GraphicalObjectListener;

public abstract class AbstractGraphicalObject implements GraphicalObject {
	protected Point[] hotPoints;
	protected boolean[] hotPointSelected;
	protected boolean selected;
	protected List<GraphicalObjectListener> listeners;

	public AbstractGraphicalObject(Point[] hotPoints) {
		this.hotPoints = hotPoints;
		hotPointSelected = new boolean[hotPoints.length];
		listeners = new ArrayList<GraphicalObjectListener>();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		notifySelectionListeners();
	}

	@Override
	public int getNumberOfHotPoints() {
		return hotPoints.length;
	}

	@Override
	public Point getHotPoint(int index) {
		if (index < hotPoints.length && index >= 0) {
			return hotPoints[index];
		}
		return null;
	}

	@Override
	public void setHotPoint(int index, Point point) {
		if (index < hotPoints.length && index >= 0) {
			hotPoints[index] = point;
		}
		notifyListeners();
	}

	@Override
	public boolean isHotPointSelected(int index) {
		if (index < hotPointSelected.length && index >= 0) {
			return hotPointSelected[index];
		}
		return false;
	}

	@Override
	public void setHotPointSelected(int index, boolean selected) {
		if (index < hotPointSelected.length && index >= 0) {
			hotPointSelected[index] = selected;
		}
	}

	@Override
	public double getHotPointDistance(int index, Point mousePoint) {
		if (index < hotPoints.length && index >= 0) {
			return GeometryUtil.distanceFromPoint(hotPoints[index], mousePoint);
		}
		return Double.MAX_VALUE;
	}

	@Override
	public void translate(Point delta) {
		for (Point point : hotPoints) {
			point.translate(delta);
		}
		notifyListeners();
	}

	@Override
	public void addGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);
	}

	@Override
	public void removeGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.remove(l);
	}

	public void notifyListeners() {
		for (GraphicalObjectListener listener : listeners) {
			listener.graphicalObjectChanged(this);
		}
	}

	public void notifySelectionListeners() {
		for (GraphicalObjectListener listener : listeners) {
			listener.graphicalObjectSelectionChanged(this);
		}
	}

}
