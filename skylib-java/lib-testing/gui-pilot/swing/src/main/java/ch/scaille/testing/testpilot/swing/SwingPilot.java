package ch.scaille.testing.testpilot.swing;

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

import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.ModalDialogDetector.PollingResult;

/**
 * Allows to pilot a Swing component
 */
public class SwingPilot extends ch.scaille.testing.testpilot.GuiPilot {

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
		// Closed by waitModalDialogHandled
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
			if (child instanceof JComponent component && child.getName() != null) {
				cache.put(child.getName(), component);
			}
			if (child instanceof Container childContainer) {
				scan(childContainer);
			}
		}
	}

	/**
	 * Search for a component by scanning the components hierarchy, starting from
	 * the root component
	 *
	 * @param clazz the class of the searched component
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
			if (child instanceof Container childContainer) {
				search(result, childContainer, clazz, filter, searchFinished);
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
			final var text = switch(c) {
				case JLabel label -> label.getText();
				case JTextComponent textComponent -> textComponent.getText();
				case AbstractButton button -> button.getText();
				default -> c.getClass().getSimpleName();
			};
			result.append(text).append("]\n");
			if (c instanceof Container childContainer) {
				dumpHierarchy(childContainer, result, indent + "  ");
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
