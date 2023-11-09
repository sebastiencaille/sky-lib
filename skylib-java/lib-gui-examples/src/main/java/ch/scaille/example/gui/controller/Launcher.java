package ch.scaille.example.gui.controller;

import java.awt.EventQueue;

import ch.scaille.example.gui.controller.impl.ControllerExampleController;
import ch.scaille.example.gui.controller.impl.ControllerExampleView;

public interface Launcher {

	static void main(final String[] args) {
		final var controller = new ControllerExampleController();
		EventQueue.invokeLater(() -> new ControllerExampleView(controller).setVisible(true));
	}
}
