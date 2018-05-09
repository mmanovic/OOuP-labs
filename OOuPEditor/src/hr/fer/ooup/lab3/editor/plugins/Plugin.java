package hr.fer.ooup.lab3.editor.plugins;

import hr.fer.ooup.lab3.editor.gui.TextEditorModel;
import hr.fer.ooup.lab3.editor.manager.UndoManager;
import hr.fer.ooup.lab3.editor.stack.ClipboardStack;

public interface Plugin {

	public String getName();
	public String getDescription();
	public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack);
}
