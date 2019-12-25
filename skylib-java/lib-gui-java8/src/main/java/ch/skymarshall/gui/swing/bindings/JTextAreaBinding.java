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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextArea;

import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public class JTextAreaBinding extends DefaultComponentBinding<String> {

	private final JTextArea textArea;
	private final boolean readOnly;

	public JTextAreaBinding(final JTextArea component) {
		this.textArea = component;
		readOnly = false;
	}

	public JTextAreaBinding(final JTextArea component, final boolean readOnly) {
		this.textArea = component;
		this.readOnly = readOnly;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<String> converter) {
		if (readOnly) {
			return;
		}
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				converter.setValueFromComponent(textArea, textArea.getText());
			}
		});
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final String value) {
		if (value != null) {
			textArea.setText(value);
		} else {
			textArea.setText("");
		}
	}

	@Override
	public String toString() {
		return "Value of " + SwingBindings.nameOf(textArea);
	}

}
