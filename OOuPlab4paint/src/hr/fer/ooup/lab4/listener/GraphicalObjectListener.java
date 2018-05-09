package hr.fer.ooup.lab4.listener;

import hr.fer.ooup.lab4.objects.GraphicalObject;

public interface GraphicalObjectListener {
	// Poziva se kad se nad objektom promjeni bio što...
	void graphicalObjectChanged(GraphicalObject go);

	// Poziva se iskljuèivo ako je nad objektom promjenjen status selektiranosti
	// (baš objekta, ne njegovih hot-point-a).
	void graphicalObjectSelectionChanged(GraphicalObject go);
}
