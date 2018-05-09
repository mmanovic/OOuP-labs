package hr.fer.ooup.lab3.editor.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import hr.fer.ooup.lab3.editor.commands.ClearDocument;
import hr.fer.ooup.lab3.editor.commands.DeleteAfter;
import hr.fer.ooup.lab3.editor.commands.DeleteBefore;
import hr.fer.ooup.lab3.editor.commands.DeleteRange;
import hr.fer.ooup.lab3.editor.commands.EditAction;
import hr.fer.ooup.lab3.editor.commands.InsertText;
import hr.fer.ooup.lab3.editor.manager.UndoManager;
import hr.fer.ooup.lab3.editor.observers.CursorObserver;
import hr.fer.ooup.lab3.editor.observers.TextObserver;

public class TextEditorModel {
	private List<String> lines;
	private LocationRange selectionRange;
	private Location cursorLocation = new Location(0, 0);
	private List<CursorObserver> cursorObservers;
	private List<TextObserver> textObservers;
	private UndoManager undoManager;

	public TextEditorModel(String tekst) {
		this.lines = new ArrayList<>(Arrays.asList(tekst.split("\n")));
		cursorObservers = new ArrayList<>();
		textObservers = new ArrayList<>();
		undoManager = UndoManager.getInstance();
	}

	// uredivanje teksta

	public void insert(char c) {
		EditAction action = new InsertText(this, c);
		action.executeDo();
		undoManager.push(action);
	}

	public void insert(String text) {
		EditAction action = new InsertText(this, text);
		action.executeDo();
		undoManager.push(action);
	}

	public void deleteBefore() {
		EditAction action = new DeleteBefore(this);
		action.executeDo();
		undoManager.push(action);
	}

	public void deleteAfter() {
		EditAction action = new DeleteAfter(this);
		action.executeDo();
		undoManager.push(action);
	}

	public void deleteRange() {
		EditAction action = new DeleteRange(this);
		action.executeDo();
		undoManager.push(action);
	}

	public void moveCursorToDocumentStart() {
		cursorLocation.setLocation(0, 0);
		notifyCursorObservers();
	}

	public void moveCursorToDocumentEnd() {
		int index = lines.size() - 1;
		cursorLocation.setLocation(index, lines.get(index).length());
		notifyCursorObservers();
	}
	
	public void clear() {
		EditAction action = new ClearDocument(this);
		action.executeDo();
		undoManager.push(action);
	}

	// OBSERVERI
	public void addTextObserver(TextObserver observer) {
		if (!textObservers.contains(observer)) {
			textObservers.add(observer);
		}
	}

	public void removeTextObserver(TextObserver observer) {
		textObservers.remove(observer);
	}

	public void notifyTextObservers() {
		for (TextObserver entry : textObservers) {
			entry.updateText();
		}
	}

	public void addCursorObserver(CursorObserver observer) {
		cursorObservers.add(observer);
	}

	public void removeCursorObserver(CursorObserver observer) {
		cursorObservers.remove(observer);
	}

	public void notifyCursorObservers() {
		for (CursorObserver entry : cursorObservers) {
			entry.updateCursorLocation(cursorLocation);
		}
	}

	public void moveCursorLeft() {
		if (cursorLocation.getY() == 0) {
			if (cursorLocation.getX() != 0) {
				cursorLocation.setLocation(cursorLocation.getX() - 1, lines.get(cursorLocation.getX() - 1).length());
			} else {
				return;
			}
		} else {
			cursorLocation.update(0, -1);
		}
		notifyCursorObservers();
	}

	public void moveCursorRight() {
		int lineLength = lines.get(cursorLocation.getX()).length();
		if (cursorLocation.getY() == lineLength) {
			if (cursorLocation.getX() != lines.size() - 1) {
				cursorLocation.setLocation(cursorLocation.getX() + 1, 0);
			} else {
				return;
			}
		} else {
			cursorLocation.update(0, 1);
		}
		notifyCursorObservers();
	}

	public void moveCursorUp() {
		if (cursorLocation.getX() != 0) {
			String line = lines.get(cursorLocation.getX() - 1);
			if (cursorLocation.getY() > line.length()) {
				cursorLocation.setLocation(cursorLocation.getX() - 1, line.length());
			} else {
				cursorLocation.update(-1, 0);
			}
			notifyCursorObservers();
		}
	}

	public void moveCursorDown() {
		if (cursorLocation.getX() != lines.size() - 1) {
			String line = lines.get(cursorLocation.getX() + 1);
			if (cursorLocation.getY() > line.length()) {
				cursorLocation.setLocation(cursorLocation.getX() + 1, line.length());
			} else {
				cursorLocation.update(1, 0);
			}
			notifyCursorObservers();
		}
	}

	public Iterator<String> allLines() {
		return linesRange(0, lines.size());
	}

	public Iterator<String> linesRange(int index1, int index2) {
		if (index1 < 0 || index2 > lines.size()) {
			throw new IndexOutOfBoundsException("Indexes must be at range of number lines.");
		}
		List<String> tmp = new ArrayList<>();
		for (int i = index1; i < index2; i++) {
			tmp.add(lines.get(i));
		}
		return tmp.iterator();
	}

	public List<String> getLines() {
		return lines;
	}

	// upravljanje selekcijom

	public LocationRange getSelectionRange() {
		return selectionRange;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public void setCursorLocation(Location cursorLocation) {
		this.cursorLocation = cursorLocation;
	}

	public void setSelectionRange(LocationRange selectionRange) {
		this.selectionRange = selectionRange;
	}

	public Location getCursorLocation() {
		return cursorLocation;
	}

	public boolean hasSelection() {
		return selectionRange != null;
	}

	public void addSelectionOnLeft() {
		Location oldCursor = new Location(cursorLocation);
		moveCursorLeft();
		setNewRange(oldCursor);
	}

	public void addSelectionRight() {
		Location oldCursor = new Location(cursorLocation);
		moveCursorRight();
		setNewRange(oldCursor);
	}

	public void addSelectionUp() {
		Location oldCursor = new Location(cursorLocation);
		moveCursorUp();
		setNewRange(oldCursor);
	}

	public void addSelectionDown() {
		Location oldCursor = new Location(cursorLocation);
		moveCursorDown();
		setNewRange(oldCursor);
	}

	private void setNewRange(Location oldCursor) {
		if (oldCursor.equals(cursorLocation)) {
			return;
		}
		if (selectionRange == null) {
			selectionRange = new LocationRange();
			if (oldCursor.getX() < cursorLocation.getX()) {
				selectionRange.setStart(oldCursor);
				selectionRange.setEnd(new Location(cursorLocation));
			} else if (oldCursor.getX() > cursorLocation.getX()) {
				selectionRange.setStart(new Location(cursorLocation));
				selectionRange.setEnd(oldCursor);
			} else {
				if (oldCursor.getY() < cursorLocation.getY()) {
					selectionRange.setStart(oldCursor);
					selectionRange.setEnd(new Location(cursorLocation));
				} else {
					selectionRange.setStart(new Location(cursorLocation));
					selectionRange.setEnd(oldCursor);
				}
			}
		} else {
			Location cursorLocation2;
			if (selectionRange.getStart().equals(oldCursor)) {
				cursorLocation2 = new Location(selectionRange.getEnd());
			} else {
				cursorLocation2 = new Location(selectionRange.getStart());
			}
			if (cursorLocation2.equals(cursorLocation)) {
				selectionRange = null;
				return;
			}
			selectionRange = new LocationRange();
			selectionRange = new LocationRange();
			if (cursorLocation2.getX() < cursorLocation.getX()) {
				selectionRange.setStart(cursorLocation2);
				selectionRange.setEnd(new Location(cursorLocation));
			} else if (cursorLocation2.getX() > cursorLocation.getX()) {
				selectionRange.setStart(new Location(cursorLocation));
				selectionRange.setEnd(cursorLocation2);
			} else {
				if (cursorLocation2.getY() < cursorLocation.getY()) {
					selectionRange.setStart(cursorLocation2);
					selectionRange.setEnd(new Location(cursorLocation));
				} else {
					selectionRange.setStart(new Location(cursorLocation));
					selectionRange.setEnd(cursorLocation2);
				}
			}
		}
	}

	public String getSelectionText() {
		if (selectionRange != null) {
			int numOfLines = selectionRange.getEnd().getX() - selectionRange.getStart().getX();
			Location start = selectionRange.getStart();
			Location end = selectionRange.getEnd();

			if (numOfLines == 0) {
				return lines.get(start.getX()).substring(start.getY(), end.getY());
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(lines.get(start.getX()).substring(start.getY())).append(System.lineSeparator());
				for (int i = start.getX() + 1; i < end.getX(); i++) {
					sb.append(lines.get(i)).append(System.lineSeparator());
				}
				sb.append(lines.get(end.getX()).substring(0, end.getY()));
				return sb.toString();
			}
		} else {
			return "";
		}
	}
}
