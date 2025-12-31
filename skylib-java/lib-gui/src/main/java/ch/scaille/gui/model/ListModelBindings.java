package ch.scaille.gui.model;

import java.util.Collection;

import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.mvc.factories.ComponentBindings;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ListModelBindings {

	static <T> IComponentBinding<IListView<T>> view(final ListModel<T> model) {
		return ComponentBindings.listen(model, (c, p, t) -> c.setView(t));
	}

	static <T> IComponentBinding<Collection<T>> values(final ListModel<T> model) {
		return new IComponentBinding<>() {

			@Nullable
			private IListModelListener<T> listener;

			@Override
			public void addComponentValueChangeListener(final IComponentLink<Collection<T>> link) {
				listener = IListModelListener.editionStopped(e -> link.setValueFromComponent(model, model.values()));
				model.addListener(listener);
			}

			@Override
			public void setComponentValue(final IComponentChangeSource source, @Nullable final Collection<T> value) {
				model.setValues(value);
			}

			@Override
			public void removeComponentValueChangeListener() {
				model.removeListener(listener);
			}

		};
	}
}
