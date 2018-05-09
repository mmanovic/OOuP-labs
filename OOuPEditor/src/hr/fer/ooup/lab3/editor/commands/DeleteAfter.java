package hr.fer.ooup.lab3.editor.commands;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab3.editor.gui.Location;
import hr.fer.ooup.lab3.editor.gui.TextEditorModel;

public class DeleteAfter implements EditAction {
	private TextEditorModel model;
	private List<String> previousLines;

	public DeleteAfter(TextEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	public void executeDo() {
		Location cursor = model.getCursorLocation();
		List<String> lines = model.getLines();
		previousLines = new ArrayList<String>(lines);
		if (lines.isEmpty()
				|| (lines.size() - 1 == cursor.getX() && lines.get(cursor.getX()).length() == cursor.getY())) {
			return;
		}
		String line = lines.get(cursor.getX());
		if (cursor.getY() == line.length()) {
			lines.set(cursor.getX(), line + lines.get(cursor.getX() + 1));
			lines.remove(cursor.getX() + 1);
		} else {
			StringBuilder sb = new StringBuilder(line);
			sb.deleteCharAt(cursor.getY());
			lines.set(cursor.getX(), sb.toString());
		}
		model.notifyTextObservers();
	}

	@Override
	public void executeUndo() {
		model.setLines(previousLines);
		model.notifyTextObservers();
	}

}
