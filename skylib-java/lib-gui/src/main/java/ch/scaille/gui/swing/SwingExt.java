package ch.scaille.gui.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.properties.IPropertyEventListener;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.Logs;

public class SwingExt {

	private static final String TRANSLATIONS_APPLICATION = "translations/application";

	private SwingExt() {
		// noop
	}

	public static ImageIcon iconFromStream(final Supplier<InputStream> inSupplier) throws IOException {
		try (var inStream = inSupplier.get()) {
			return iconFromStream(inStream);
		}
	}

	public static ImageIcon iconFromStream(final InputStream in) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("Stream must not be null");
		}
		return new ImageIcon(JavaExt.read(in));
	}

	public static IPropertyEventListener checkSwingThread() {
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
	public static ActionListener action(Consumer<ActionEvent> consumer) {
		return consumer::accept;
	}

	/**
	 * Bridge between ActionListener and Lambda function
	 */
	public static ActionListener action(Runnable runnable) {
		return e -> runnable.run();
	}

	/*
	 * Computes the length of a text
	 */
	public static int computeTextWidth(Component component, String text) {
		final var metrics = component.getFontMetrics(component.getFont());
		return SwingUtilities.computeStringWidth(metrics, text);
	}

	public static String nameOf(final Component component) {
		if (component.getName() != null) {
			return component.getClass().getSimpleName() + ':' + component.getName();
		}
		return component.toString();
	}

	private static ResourceBundle defaultBundle;
	private static UnaryOperator<String> labelProvider = key -> {
		try {
			return Objects.requireNonNull(defaultBundle, "Bundle not configured").getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	};

	static {
		try {
			loadBundle(TRANSLATIONS_APPLICATION);
		} catch (MissingResourceException r) {
			Logs.of(SwingExt.class).info("Default bundle not found: " + TRANSLATIONS_APPLICATION);
			defaultBundle = null;
		}
	}

	public static void setLabelProvider(UnaryOperator<String> labelProvider) {
		SwingExt.labelProvider = labelProvider;
	}

	public static void loadBundle(String bundle) {
		defaultBundle = ResourceBundle.getBundle(bundle);
	}

	public static String getText(String key) {
		return labelProvider.apply(key);
	}

	public static void configureTableHeaders(JTable table) {
		table.setTableHeader(new JTableHeader(table.getColumnModel()) {
			@Override
			public String getToolTipText(MouseEvent e) {
				int colIndex = columnModel.getColumnIndexAtX(e.getPoint().x);
				int index = columnModel.getColumn(colIndex).getModelIndex();
				return getText(columnKey(table, columnModel.getColumn(index), ".tooltip"));
			}
		});
		table.getColumnModel().getColumns().asIterator().forEachRemaining(column -> column.setHeaderValue(getText(columnKey(table, column))));
	}

	private static String columnKey(JTable table, TableColumn column, String... suffix) {
		return String.format("%s.%s%s", table.getName(), column.getIdentifier(), String.join("", suffix));
	}
}