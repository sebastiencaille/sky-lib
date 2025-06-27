package ch.scaille.example.gui.controllermodel.impl;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class TestObjectControllerModelFrameView extends JFrame {

	public TestObjectControllerModelFrameView(final TestObjectControllerModelController controller) {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final var table = new JTable(controller.getTableModel());
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		final var button = new JButton("Commit");
		button.addActionListener(controller.getCommitAction());
		getContentPane().add(button, BorderLayout.SOUTH);

		controller.getScopedChangeSupport().flushChanges();

		validate();
		pack();

	}

}
