package hr.fer.ooup.lab4.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hr.fer.ooup.lab4.listener.DocumentModelListener;
import hr.fer.ooup.lab4.listener.GraphicalObjectListener;
import hr.fer.ooup.lab4.objects.GraphicalObject;

public class DocumentModel {
	public final static double SELECTION_PROXIMITY = 10;

	// Kolekcija svih grafièkih objekata:
	private List<GraphicalObject> objects = new ArrayList<>();
	// Read-Only proxy oko kolekcije grafièkih objekata:
	private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
	// Kolekcija prijavljenih promatraèa:
	private List<DocumentModelListener> listeners = new ArrayList<>();
	// Kolekcija selektiranih objekata:
	private List<GraphicalObject> selectedObjects = new ArrayList<>();
	// Read-Only proxy oko kolekcije selektiranih objekata:
	private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);

	// Promatraè koji æe biti registriran nad svim objektima crteža...
	private final GraphicalObjectListener goListener = new GraphicalObjectListener() {

		@Override
		public void graphicalObjectChanged(GraphicalObject go) {
			notifyListeners();
		}

		@Override
		public void graphicalObjectSelectionChanged(GraphicalObject go) {
			int index = selectedObjects.indexOf(go);
			if (go.isSelected()) {
				if (index == -1) {
					selectedObjects.add(go);
				}
			} else {
				if (index != -1) {
					selectedObjects.remove(index);
				}
			}
			notifyListeners();
		}
	};

	public void deselectAll() {
		while (selectedObjects.size() > 0) {
			selectedObjects.get(0).setSelected(false);
		}
	}

	// Konstruktor...
	public DocumentModel() {

	}

	// Brisanje svih objekata iz modela (pazite da se sve potrebno odregistrira)
	// i potom obavijeste svi promatraèi modela
	public void clear() {
		selectedObjects.clear();
		for (GraphicalObject go : objects) {
			go.removeGraphicalObjectListener(goListener);
		}
		objects.clear();
		notifyListeners();
	}

	// Dodavanje objekta u dokument (pazite je li veæ selektiran; registrirajte
	// model kao promatraèa)
	public void addGraphicalObject(GraphicalObject obj) {
		if (obj.isSelected()) {
			selectedObjects.add(obj);
		}
		objects.add(obj);
		obj.addGraphicalObjectListener(goListener);
		notifyListeners();

	}

	// Uklanjanje objekta iz dokumenta (pazite je li veæ selektiran;
	// odregistrirajte model kao promatraèa)
	public void removeGraphicalObject(GraphicalObject obj) {
		obj.setSelected(false);
		obj.removeGraphicalObjectListener(goListener);
		objects.remove(obj);
		notifyListeners();
	}

	// Vrati nepromjenjivu listu postojeæih objekata (izmjene smiju iæi samo
	// kroz metode modela)
	public List<GraphicalObject> list() {
		return roObjects;
	}

	// Prijava...
	public void addDocumentModelListener(DocumentModelListener l) {
		listeners.add(l);
	}

	// Odjava...
	public void removeDocumentModelListener(DocumentModelListener l) {
		listeners.remove(l);
	}

	// Obavještavanje...
	public void notifyListeners() {
		for (DocumentModelListener documentModelListener : listeners) {
			documentModelListener.documentChange();
		}
	}

	// Vrati nepromjenjivu listu selektiranih objekata
	public List<GraphicalObject> getSelectedObjects() {
		return roSelectedObjects;
	}

	// Pomakni predani objekt u listi objekata na jedno mjesto kasnije...
	// Time æe se on iscrtati kasnije (pa æe time možda veæi dio biti vidljiv)
	public void increaseZ(GraphicalObject go) {
		int index = objects.indexOf(go);
		if (index == -1) {
			return;
		}
		if (index < objects.size() - 1) {
			objects.set(index, objects.get(index + 1));
			objects.set(index + 1, go);
		}
		notifyListeners();
	}

	// Pomakni predani objekt u listi objekata na jedno mjesto ranije...
	public void decreaseZ(GraphicalObject go) {
		int index = objects.indexOf(go);
		if (index == -1) {
			return;
		}
		if (index > 0) {
			objects.set(index, objects.get(index - 1));
			objects.set(index - 1, go);
		}
		notifyListeners();
	}

	// Pronaði postoji li u modelu neki objekt koji klik na toèku koja je
	// predana kao argument selektira i vrati ga ili vrati null. Toèka selektira
	// objekt kojemu je najbliža uz uvjet da ta udaljenost nije veæa od
	// SELECTION_PROXIMITY. Status selektiranosti objekta ova metoda NE dira.
	public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
		double min = Double.MAX_VALUE;
		GraphicalObject result = null;
		for (GraphicalObject go : objects) {
			double dist = go.selectionDistance(mousePoint);
			if (dist <= SELECTION_PROXIMITY) {
				if (dist < min) {
					result = go;
				}
			}
		}
		return result;
	}

	public List<GraphicalObject> objectsWithPoint(Point mousePoint) {
		List<GraphicalObject> gObjects = new ArrayList<GraphicalObject>();
		for (GraphicalObject go : objects) {
			if (go.selectionDistance(mousePoint) <= SELECTION_PROXIMITY / 2) {
				gObjects.add(go);
			}
		}
		return gObjects;
	}

	// Pronaði da li u predanom objektu predana toèka miša selektira neki
	// hot-point.
	// Toèka miša selektira onaj hot-point objekta kojemu je najbliža uz uvjet
	// da ta
	// udaljenost nije veæa od SELECTION_PROXIMITY. Vraæa se indeks hot-pointa
	// kojeg bi predana toèka selektirala ili -1 ako takve nema. Status
	// selekcije
	// se pri tome NE dira.
	public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {
		for (int i = 0; i < object.getNumberOfHotPoints(); i++) {
			if (object.getHotPointDistance(i, mousePoint) <= SELECTION_PROXIMITY) {
				return i;
			}
		}
		return -1;
	}
}
