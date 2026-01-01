package ch.scaille.gui.swing.bindings;

import javax.swing.JList;

import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class JListSelectionBinding<T extends @Nullable Object> implements IComponentBinding<T> {

    private final JList<T> list;
    @Nullable
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
            if (!Objects.equals(list.getSelectedValue(), value)) {
                Objects.requireNonNull(link, "addComponentValueChangeListener was never called")
                        .setValueFromComponent(list, list.getSelectedValue());
            }
        }
    }

    @Override
    public String toString() {
        return "Selection of " + SwingExt.nameOf(list);
    }
}
