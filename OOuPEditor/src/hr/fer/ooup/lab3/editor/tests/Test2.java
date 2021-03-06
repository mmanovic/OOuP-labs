package hr.fer.ooup.lab3.editor.tests;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Test2 extends JFrame {
	public Test2() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocation(20, 50);
		setSize(300, 200);
		setTitle("Moj prvi prozor!");
		getContentPane().setLayout(new BorderLayout());
		initGUI();
	}

	private void initGUI() {
		add(new Component2("Proizvoljni", "Tekst"), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Test2().setVisible(true);
		});
	}
}
