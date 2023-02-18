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
package ch.scaille.gui.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import ch.scaille.gui.mvc.IPropertyEventListener;
import ch.scaille.gui.mvc.PropertyEvent.EventKind;
import ch.scaille.util.helpers.JavaExt;

public interface SwingHelper {

	static ImageIcon iconFromStream(final Supplier<InputStream> inSupplier) throws IOException {
		try (InputStream in = inSupplier.get()) {
			return iconFromStream(in);
		}
	}

	static ImageIcon iconFromStream(final InputStream in) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("Stream must not be null");
		}		
		return new ImageIcon(JavaExt.read(in));
	}

	static IPropertyEventListener checkSwingThread() {
		return (c, e) -> {
			if (e.getKind() == EventKind.BEFORE && e.getProperty().getTransmitMode().toComponent
					&& !EventQueue.isDispatchThread()) {
				throw new IllegalStateException("Property " + e.getProperty().getName() + " fired out of Swing thread");
			}
		};
	}

	/**
	 * Bridge between ActionListener and Lambda function
	 * 
	 * @param consumer
	 * @return
	 */
	static ActionListener action(Consumer<ActionEvent> consumer) {
		return consumer::accept;
	}

	/**
	 * Bridge between ActionListener and Lambda function
	 * 
	 * @param consumer
	 * @return
	 */
	static ActionListener action(Runnable runnable) {
		return e -> runnable.run();
	}

	/*
	 * Computes the length of a text
	 */
	static int computeTextWidth(Component component, String text) {
		FontMetrics metrics = component.getFontMetrics(component.getFont());
		return SwingUtilities.computeStringWidth(metrics, text);
	}
}
