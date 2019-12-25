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
package ch.skymarshall.gui.swing.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public class JTextFieldBinding extends DefaultComponentBinding<String> {

	private final JTextField textField;
	private boolean withFocusLoss = true;

	public JTextFieldBinding(final JTextField component) {
		this.textField = component;
	}

	@SuppressWarnings("serial")
	@Override
	public void addComponentValueChangeListener(final IComponentLink<String> converter) {

		final Action original = textField.getActionMap().get(JTextField.notifyAction);
		textField.getActionMap().put(JTextField.notifyAction, new AbstractAction() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				converter.setValueFromComponent(textField, textField.getText());
				original.actionPerformed(event);
			}
		});
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if (withFocusLoss) {
					converter.setValueFromComponent(textField, textField.getText());
				}
			}
		});
	}

	public JTextFieldBinding disableFocusLoss() {
		withFocusLoss = false;
		return this;
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final String value) {
		if (value != null) {
			textField.setText(value);
		} else {
			textField.setText("");
		}
	}

	@Override
	public String toString() {
		return "Value of " + textField;
	}
}
