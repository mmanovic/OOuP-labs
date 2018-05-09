package hr.fer.ooup.lab3.editor.commands;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab3.editor.gui.Location;
import hr.fer.ooup.lab3.editor.gui.LocationRange;
import hr.fer.ooup.lab3.editor.gui.TextEditorModel;

public class DeleteRange implements EditAction {
	private TextEditorModel model;
	private List<String> previousLines;
	private Location previousCursorLocation;
	private LocationRange previousSelectionRange;

	public DeleteRange(TextEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	public void executeDo() {
		List<String> lines = model.getLines();
		Location cursorLocation = model.getCursorLocation();
		LocationRange selectionRange = model.getSelectionRange();
		previousLines = new ArrayList<String>(lines);
		previousCursorLocation = new Location(cursorLocation);
		previousSelectionRange = new LocationRange(selectionRange);

		int numOfLines = selectionRange.getEnd().getX() - selectionRange.getStart().getX();
		Location start = selectionRange.getStart();
		Location end = selectionRange.getEnd();

		if (cursorLocation.equals(end)) {
			cursorLocation.setLocation(start);
		}

		if (numOfLines == 0) {
			StringBuilder sb = new StringBuilder(lines.get(start.getX()));
			sb.delete(start.getY(), end.getY());
			lines.set(start.getX(), sb.toString());
		} else {
			for (int i = start.getX() + 1; i < start.getX() + numOfLines; i++) {
				lines.remove(i);
			}

			lines.set(start.getX(), lines.get(start.getX()).substring(0, start.getY())
					+ lines.get(start.getX() + 1).substring(end.getY()));
			lines.remove(start.getX() + 1);
		}

		model.setSelectionRange(null);
		model.notifyCursorObservers();
		model.notifyTextObservers();

	}

	@Override
	public void executeUndo() {
		model.setLines(previousLines);
		model.setCursorLocation(previousCursorLocation);
		model.setSelectionRange(previousSelectionRange);
		model.notifyCursorObservers();
		model.notifyTextObservers();
	}

}
