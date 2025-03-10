package ch.scaille.gui.swing.bindings;

import javax.swing.JList;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;

public class JListSelectionBinding<T> extends ComponentBindingAdapter<T> {

	private final JList<T> list;
	private IComponentLink<T> link;

	public JListSelectionBinding(final JList<T> component) {
		this.list = component;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> fromLink) {
		this.link = fromLink;
		list.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				fromLink.setValueFromComponent(list, list.getSelectedValue());
			}
		});
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final T value) {
		if (!source.isModifiedBy(list)) {
			list.setSelectedValue(value, true);
			if (list.getSelectedValue() == null) {
				link.setValueFromComponent(list, null);
			}
		}
	}

	@Override
	public String toString() {
		return "Selection of " + SwingExt.nameOf(list);
	}
}
