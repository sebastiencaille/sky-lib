package ch.skymarshall.tcwriter.swingpilot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.Assert;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

public class GuiPilot {

	private final WeakHashMap<String, JComponent> cache = new WeakHashMap<>();
	private final Timer timer = new Timer();
	private DialogBoxCloser dialogBoxCloser;
	private final Container root;

	public GuiPilot(final Container root) {
		this.root = root;
	}

	/**
	 * Caches the components by scanning the components hierarchy, starting from the
	 * root
	 */
	public void scan() {
		checkSwingThread();
		cache.clear();
		final Container container = root;
		scan(container);
	}

	/**
	 * Caches the components by scanning a components hierarchy, starting from
	 * container
	 *
	 * @param container the scanned component
	 */
	public void scan(final Container container) {
		for (final Component child : container.getComponents()) {
			if (child instanceof JComponent) {
				final JComponent jChild = (JComponent) child;
				if (child.getName() != null) {
					cache.put(child.getName(), jChild);
				}
			}
			if (child instanceof Container) {
				scan((Container) child);
			}
		}
	}

	/**
	 * Search for a component by scanning a components hierarchy, starting from
	 * container
	 *
	 * @param <T>
	 * @param container the scanned component
	 * @param clazz     the class of the searched component
	 * @return
	 */
	public <T extends JComponent> Optional<T> search(final Container container, final Class<T> clazz) {
		checkSwingThread();
		for (final Component child : container.getComponents()) {
			if (clazz.isInstance(child)) {
				return Optional.of(clazz.cast(child));
			}
			if (child instanceof Container) {
				final Optional<T> found = search((Container) child, clazz);
				if (found.isPresent()) {
					return found;
				}
			}
		}
		return Optional.empty();
	}

	private class DialogBoxCloser extends TimerTask {
		private final List<String> errors = new ArrayList<>();

		@Override
		public void run() {
			SwingUtilities.invokeLater(() -> {
				for (final Window window : Window.getWindows()) {
					if (window instanceof JDialog) {
						final JDialog jDialog = (JDialog) window;
						errors.add(search(window, JLabel.class)
								.orElseThrow(() -> new IllegalStateException("JDialog: no JLabel found")).getText());
						jDialog.setVisible(false);
						jDialog.dispose();
					}
				}
			});
		}

		public void close() {
			this.cancel();
			Assert.assertEquals("Unexpected dialog boxes", "", String.join(",\n", errors));
		}

	}

	public NoExceptionCloseable withDialogBoxCloser() {
		dialogBoxCloser = new DialogBoxCloser();
		timer.schedule(dialogBoxCloser, 500, 500);
		return () -> dialogBoxCloser.close();
	}

	public <T extends JComponent> T getComponent(final String name, final Class<T> clazz) {
		checkSwingThread();
		JComponent cachedComponent = cache.get(name);
		if (cachedComponent == null) {
			scan();
			cachedComponent = cache.get(name);
		}
		if (cachedComponent == null) {
			throw new InvalidParameterException("Not found: " + name);
		}
		return clazz.cast(cachedComponent);
	}

	public void checkSelectedInList(final String componentName, final String value) {
		if (value == null) {
			return;
		}
		final JList<?> list = checkEditable(getComponent(componentName, JList.class));
		assertTrue("A row must be selected", list.getSelectedIndex() >= 0);
		assertEquals(value, list.getModel().getElementAt(list.getSelectedIndex()).toString());
	}

	/**
	 * Select a value in a list, according to it's String representation
	 *
	 * @param componentName
	 * @param value
	 */
	public void selectInList(final String componentName, final String value) {
		if (value == null) {
			return;
		}
		final JList<?> list = checkEditable(getComponent(componentName, JList.class));
		for (int i = 0; i < list.getModel().getSize(); i++) {
			if (value.equals(list.getModel().getElementAt(i).toString())) {
				list.setSelectedIndex(i);
			}
		}
		Assert.assertTrue("Value [" + componentName + ":" + value + "] must have been selected",
				list.getSelectedIndex() >= 0);
	}

	public void checkTextValue(final String componentName, final String value) {
		if (value == null) {
			return;
		}
		final JTextField textField = checkEditable(getComponent(componentName, JTextField.class));
		assertEquals(value, textField.getText());
	}

	public void setTextValue(final String componentName, final String value) {
		if (value == null) {
			return;
		}
		final JTextField textField = checkEditable(getComponent(componentName, JTextField.class));
		textField.setText(value);
	}

	private <T extends Component> T checkEditable(final T component) {
		Assert.assertTrue("Component must be enabled", component.isEnabled());
		if (component instanceof JTextComponent) {
			Assert.assertTrue("Component must be editable", ((JTextComponent) component).isEditable());
		}
		return component;
	}

	public void withSwing(final Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (final InvocationTargetException e) {
			throw new Error(e.getCause());
		} catch (final InterruptedException e) {
			Assert.assertNull("Unexpected error while executing selection", e);
		}
	}

	private void checkSwingThread() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Not in Swing thread");
		}
	}

}
