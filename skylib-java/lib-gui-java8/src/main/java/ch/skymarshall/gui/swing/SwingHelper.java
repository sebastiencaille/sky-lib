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
package ch.skymarshall.gui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.ImageIcon;

import ch.skymarshall.gui.mvc.IPropertyEventListener;
import ch.skymarshall.gui.mvc.PropertyEvent.EventKind;

public interface SwingHelper {

	public static ImageIcon iconFromStream(final Supplier<InputStream> inSupplier) throws IOException {
		try (InputStream in = inSupplier.get()) {
			return iconFromStream(in);
		}
	}

	public static ImageIcon iconFromStream(final InputStream in) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("Stream must not be null");
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		in.close();
		return new ImageIcon(out.toByteArray());
	}

	public static ActionListener actionListener(final Consumer<ActionEvent> c) {
		return c::accept;
	}
	
	public static IPropertyEventListener checkSwingThread() {
		return (c,e) -> {
			if (e.getKind() == EventKind.BEFORE && e.getProperty().getTransmitMode().toComponent && !EventQueue.isDispatchThread()) {
					throw new IllegalStateException("Property " + e.getProperty().getName() + " fired out of Swing thread");
			}
		};
	}
}
