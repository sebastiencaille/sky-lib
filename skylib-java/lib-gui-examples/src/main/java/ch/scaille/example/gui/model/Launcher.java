package ch.scaille.example.gui.model;

import java.awt.EventQueue;

import ch.scaille.example.gui.model.impl.TableModelExampleView;

public class Launcher {

	static void main() {
		EventQueue.invokeLater(() -> new TableModelExampleView().setVisible(true));
	}
}
