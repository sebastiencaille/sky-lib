package ch.skymarshall.tcwriter.pilot.swing;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.Assert;

import ch.skymarshall.util.helpers.NoExceptionCloseable;

public class SwingGuiPilot extends ch.skymarshall.tcwriter.pilot.GuiPilot {

	private final WeakHashMap<String, JComponent> cache = new WeakHashMap<>();
	private final Container root;

	public SwingGuiPilot(final Container root) {
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
	 * Search for a component by scanning the components hierarchy, starting from
	 * the root component
	 *
	 * @param <T>
	 * @param container the scanned component
	 * @param clazz     the class of the searched component
	 * @return
	 */
	public <T extends JComponent> Optional<T> search(final Class<T> clazz) {
		return search(new HashSet<>(), root, clazz, c -> true, s -> !s.isEmpty()).stream().findAny();
	}

	public <T extends JComponent> Optional<T> search(final Class<T> clazz, final Predicate<T> filter) {
		return search(new HashSet<>(), root, clazz, filter, s -> !s.isEmpty()).stream().findAny();
	}

	public <T extends JComponent> Set<T> searchAll(final Class<T> clazz, final Predicate<T> filter) {
		return search(new HashSet<>(), root, clazz, filter, s -> false);
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
	public <T> Set<T> search(final Set<T> result, final Container container, final Class<T> clazz,
			final Predicate<T> filter, final Predicate<Set<T>> searchFinished) {
		checkSwingThread();
		for (final Component child : container.getComponents()) {
			if (clazz.isInstance(child) && filter.test(clazz.cast(child))) {
				result.add(clazz.cast(child));
				if (searchFinished.test(result)) {
					return result;
				}
			}
			if (child instanceof Container) {
				search(result, (Container) child, clazz, filter, searchFinished);
				if (searchFinished.test(result)) {
					return result;
				}
			}
		}
		return result;
	}

	public <T extends JComponent> T getComponent(final String name, final Class<T> clazz)
			throws NoSuchComponentException {
		checkSwingThread();
		JComponent cachedComponent = cache.get(name);
		if (cachedComponent == null) {
			scan();
			cachedComponent = cache.get(name);
		}
		if (cachedComponent == null) {
			throw new NoSuchComponentException("Not found: " + name);
		}
		return clazz.cast(cachedComponent);
	}

	public void withDialog(final Runnable runnable, final Predicate<JDialogPilot> dialogHandler) {
		try (NoExceptionCloseable dialogCloseable = JDialogPilot.withDialog(dialogHandler)) {
			runnable.run();
		}
	}

	public void withSwing(final Runnable runnable, final Predicate<JDialogPilot> dialogHandler) {
		try (NoExceptionCloseable dialogCloseable = JDialogPilot.withDialog(dialogHandler)) {
			SwingUtilities.invokeAndWait(runnable);
		} catch (final InvocationTargetException e) {
			throw new AssertionError(e.getCause());
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			Assert.assertNull("Unexpected error while executing selection", e);
		}
	}

	public void withSwing(final Runnable runnable) {
		withSwing(runnable, null);
	}

	protected void checkSwingThread() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Not in Swing thread");
		}
	}

	public String dumpHierarchy() {
		final StringBuilder result = new StringBuilder();
		dumpHierarchy(root, result, "  ");
		return result.toString();
	}

	public void dumpHierarchy(final Container container, final StringBuilder result, final String indent) {
		Arrays.stream(container.getComponents()).forEach(c -> {
			result.append(indent).append(c.getClass()).append('[');
			if (c instanceof JLabel) {
				result.append((((JLabel) c).getText()));
			} else if (c instanceof JTextComponent) {
				result.append((((JTextComponent) c).getText()));
			} else if (c instanceof AbstractButton) {
				result.append((((AbstractButton) c).getText()));
			}
			result.append("]\n");
			if (Container.class.isInstance(c)) {
				dumpHierarchy((Container) c, result, indent + "  ");
			}
		});

	}

}
