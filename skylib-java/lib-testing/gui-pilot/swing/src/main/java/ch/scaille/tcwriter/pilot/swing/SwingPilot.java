package ch.scaille.tcwriter.pilot.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.PollingResult;

/**
 * Allows to pilot a Swing component
 */
@SuppressWarnings("java:S5960")
public class SwingPilot extends ch.scaille.tcwriter.pilot.GuiPilot {

	private final WeakHashMap<String, JComponent> cache = new WeakHashMap<>();
	private final Container root;

	public SwingPilot(final Container root) {
		this.root = root;
	}

	@Override
	protected ModalDialogDetector.Builder createDefaultModalDialogDetector() {
		return SwingModalDialogDetector.defaultDetector();
	}

	public void expectModalDialog(final Function<SwingModalDialogDetector, PollingResult> check) {
		expectModalDialog(SwingModalDialogDetector.withHandler(check));
	}

	/**
	 * Caches the components by scanning the components hierarchy, starting from the
	 * root
	 */
	public void scan() {
		SwingHelper.checkSwingThread();
		cache.clear();
		scan(root);
	}

	/**
	 * Caches the components by scanning a components hierarchy, starting from
	 * container
	 *
	 * @param container the scanned component
	 */
	public void scan(final Container container) {
		for (final var child : container.getComponents()) {
			if (child instanceof JComponent) {
				final var jChild = (JComponent) child;
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
	 * @param clazz     the class of the searched component
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
	 * Search for a component by scanning a component hierarchy, starting from a
	 * container
	 *
	 * @param container the scanned component
	 * @param clazz     the class of the searched component
	 */
	public <T extends Component> Set<T> search(final Set<T> result, final Container container, final Class<T> clazz,
			final Predicate<T> filter, final Predicate<Set<T>> searchFinished) {
		SwingHelper.checkSwingThread();
		for (final var child : container.getComponents()) {
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
		SwingHelper.checkSwingThread();
		var cachedComponent = cache.get(name);
		if (cachedComponent == null) {
			scan();
			cachedComponent = cache.get(name);
		}
		if (cachedComponent == null) {
			throw new NoSuchComponentException("Not found: " + name);
		}
		return clazz.cast(cachedComponent);
	}

	public String dumpHierarchy() {
		final var result = new StringBuilder();
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
			if (c instanceof Container) {
				dumpHierarchy((Container) c, result, indent + "  ");
			}
		});

	}

	public <C extends PagePilot> C page(Function<SwingPilot, C> pageFactory) {
		final var page = pageFactory.apply(this);
		page.initialize();
		return page;
	}

	public JButtonPoller button(final String name) {
		return new JButtonPoller(this, name);
	}

	public JLabelPoller label(final String name) {
		return new JLabelPoller(this, name);
	}

	public JListPoller list(final String name) {
		return new JListPoller(this, name);
	}

	public JTablePoller table(final String name) {
		return new JTablePoller(this, name);
	}

	public JTextFieldPoller text(final String name) {
		return new JTextFieldPoller(this, name);
	}

	public JToggleButtonPoller toggleButton(final String name) {
		return new JToggleButtonPoller(this, name);
	}

}
