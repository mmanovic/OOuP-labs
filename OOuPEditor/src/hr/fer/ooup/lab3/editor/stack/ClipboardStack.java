package hr.fer.ooup.lab3.editor.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hr.fer.ooup.lab3.editor.observers.ClipboardObserver;

public class ClipboardStack {
	private Stack<String> texts;
	private List<ClipboardObserver> clipboardObservers;

	public ClipboardStack() {
		texts = new Stack<String>();
		clipboardObservers = new ArrayList<ClipboardObserver>();
	}

	public boolean isEmpty() {
		return !texts.isEmpty();
	}

	public String peek() {
		return texts.peek();
	}

	public String pop() {
		String text = texts.pop();
		notifyObservers();
		return text;
	}

	public void push(String text) {
		texts.push(text);
		notifyObservers();
	}

	public void addClipboardObserver(ClipboardObserver observer) {
		if (!clipboardObservers.contains(observer)) {
			clipboardObservers.add(observer);
		}
	}

	public void removeClipobardObserver(ClipboardObserver observer) {
		clipboardObservers.remove(observer);
	}

	private void notifyObservers() {
		for (ClipboardObserver clipboardObserver : clipboardObservers) {
			clipboardObserver.updateClipboard();
		}
	}
}
