package ch.scaille.example.gui.controllermodel;

import java.awt.EventQueue;

import ch.scaille.example.gui.controllermodel.impl.TestObjectControllerModelController;
import ch.scaille.example.gui.controllermodel.impl.TestObjectControllerModelFrameView;

public interface Launcher {

	static void main(final String[] args) {
		EventQueue.invokeLater(() -> new TestObjectControllerModelFrameView(new TestObjectControllerModelController())
				.setVisible(true));
	}
}
