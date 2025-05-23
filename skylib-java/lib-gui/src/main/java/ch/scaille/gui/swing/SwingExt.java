package ch.scaille.gui.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.properties.IPropertyEventListener;
import ch.scaille.util.helpers.JavaExt;

public interface SwingExt {

	static ImageIcon iconFromStream(final Supplier<InputStream> inSupplier) throws IOException {
		try (var inStream = inSupplier.get()) {
			return iconFromStream(inStream);
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
			if (e.kind() == EventKind.BEFORE && e.property().getTransmitMode().toComponent
					&& !EventQueue.isDispatchThread()) {
				throw new IllegalStateException("Property " + e.property().getName() + " fired out of Swing thread");
			}
		};
	}

	/**
	 * Bridge between ActionListener and Lambda function
	 */
	static ActionListener action(Consumer<ActionEvent> consumer) {
		return consumer::accept;
	}

	/**
	 * Bridge between ActionListener and Lambda function
	 */
	static ActionListener action(Runnable runnable) {
		return e -> runnable.run();
	}

	/*
	 * Computes the length of a text
	 */
	static int computeTextWidth(Component component, String text) {
		final var metrics = component.getFontMetrics(component.getFont());
		return SwingUtilities.computeStringWidth(metrics, text);
	}

	static String nameOf(final Component component) {
		if (component.getName() != null) {
			return component.getClass().getSimpleName() + ':' + component.getName();
		}
		return component.toString();
	}
}
