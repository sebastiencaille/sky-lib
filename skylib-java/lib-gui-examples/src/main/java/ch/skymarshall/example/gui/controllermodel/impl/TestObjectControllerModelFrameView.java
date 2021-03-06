/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.example.gui.controllermodel.impl;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class TestObjectControllerModelFrameView extends JFrame {

	public TestObjectControllerModelFrameView(final TestObjectControllerModelController controller) {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JTable table = new JTable(controller.getTableModel());
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		final JButton button = new JButton("Commit");
		button.addActionListener(controller.getCommitAction());
		getContentPane().add(button, BorderLayout.SOUTH);

		controller.getScopedChangeSupport().attachAll();

		validate();
		pack();

	}

}
