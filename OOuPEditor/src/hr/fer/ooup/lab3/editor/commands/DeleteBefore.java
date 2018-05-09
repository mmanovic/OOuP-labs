package hr.fer.ooup.lab3.editor.commands;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab3.editor.gui.Location;
import hr.fer.ooup.lab3.editor.gui.TextEditorModel;

public class DeleteBefore implements EditAction {
	private TextEditorModel model;
	private List<String> previousLines;
	private Location previousCursorLocation;

	public DeleteBefore(TextEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	public void executeDo() {
		Location cursor = model.getCursorLocation();
		List<String> lines = model.getLines();
		previousLines = new ArrayList<String>(lines);
		previousCursorLocation = new Location(cursor);
		if (lines.isEmpty() || (cursor.getX() == 0 && cursor.getY() == 0)) {
			return;
		}
		String line = lines.get(cursor.getX());
		if (cursor.getY() == 0) {
			int previousLineLength = lines.get(cursor.getX() - 1).length();
			lines.set(cursor.getX() - 1, lines.get(cursor.getX() - 1) + line);
			lines.remove(cursor.getX());
			cursor.update(-1, previousLineLength);

		} else {
			StringBuilder sb = new StringBuilder(lines.get(cursor.getX()));
			sb.deleteCharAt(cursor.getY() - 1);
			lines.set(cursor.getX(), sb.toString());
			cursor.update(0, -1);
		}

		model.notifyCursorObservers();
		model.notifyTextObservers();
	}

	@Override
	public void executeUndo() {
		model.setLines(previousLines);
		model.setCursorLocation(previousCursorLocation);
		model.notifyCursorObservers();
		model.notifyTextObservers();
	}

}
