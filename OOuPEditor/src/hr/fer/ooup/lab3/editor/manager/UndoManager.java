package hr.fer.ooup.lab3.editor.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hr.fer.ooup.lab3.editor.commands.EditAction;
import hr.fer.ooup.lab3.editor.observers.StackObserver;

public class UndoManager {
	private static UndoManager uniqueUndoManger;

	private Stack<EditAction> undoStack;
	private Stack<EditAction> redoStack;

	private List<StackObserver> undoStackObservers;
	private List<StackObserver> redoStackObservers;

	private UndoManager() {
		undoStack = new Stack<EditAction>();
		redoStack = new Stack<EditAction>();
		undoStackObservers = new ArrayList<StackObserver>();
		redoStackObservers = new ArrayList<StackObserver>();
	}

	public static UndoManager getInstance() {
		if (uniqueUndoManger == null) {
			uniqueUndoManger = new UndoManager();
		}
		return uniqueUndoManger;
	}

	public void undo() {
		if (!undoStack.isEmpty()) {
			EditAction action = undoStack.pop();
			action.executeUndo();
			if (undoStack.isEmpty()) {
				notifyUndoStackObservers(true);
			}
			redoStack.push(action);
			notifyRedoStackObservers(false);
		} else {
			notifyUndoStackObservers(true);
		}
	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			EditAction action = redoStack.pop();
			action.executeDo();
			if (redoStack.isEmpty()) {
				notifyRedoStackObservers(true);
			}
			undoStack.push(action);
			notifyUndoStackObservers(false);
		} else {
			notifyRedoStackObservers(true);
		}
	}

	public void push(EditAction action) {
		redoStack.clear();
		undoStack.push(action);
		notifyUndoStackObservers(false);
		notifyRedoStackObservers(true);
	}

	public void addUndoStackObserver(StackObserver observer) {
		undoStackObservers.add(observer);
	}

	public void removeUndoStackObserver(StackObserver observer) {
		undoStackObservers.remove(observer);
	}

	private void notifyUndoStackObservers(boolean isEmpty) {
		for (StackObserver entry : undoStackObservers) {
			entry.updateStack(isEmpty);
		}
	}

	public void addRedoStackObserver(StackObserver observer) {
		redoStackObservers.add(observer);
	}

	public void removeRedoStackObserver(StackObserver observer) {
		redoStackObservers.remove(observer);
	}

	private void notifyRedoStackObservers(boolean isEmpty) {
		for (StackObserver entry : redoStackObservers) {
			entry.updateStack(isEmpty);
		}
	}
}
