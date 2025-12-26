package ch.scaille.gui.swing.factories;

import ch.scaille.gui.model.IListModelListener;
import ch.scaille.gui.model.ListEvent;
import ch.scaille.gui.model.ListModel;
import ch.scaille.javabeans.IBindingChainDependency;
import ch.scaille.javabeans.IBindingControl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Allow defining dependencies between bindings.
 */
@NullMarked
public final class BindingDependencies {

	private BindingDependencies() {
	}
	
	public static class PreserveOnUpdateOfListModel<T> implements IBindingChainDependency, IListModelListener<T> {

		private final ch.scaille.gui.model.ListModel<T> model;
		@Nullable
		private IBindingControl bindingControl;

		public PreserveOnUpdateOfListModel(final ListModel<T> model) {
			this.model = model;
		}

		@Override
		public void register(final IBindingControl bindingControl) {
			this.bindingControl = bindingControl;
			model.addListener(this);
		}

		@Override
		public void unbind() {
			model.removeListener(this);
		}

		@Override
		public void mutates() {
			Objects.requireNonNull(bindingControl, "register was not called").pauseBinding();
		}

		@Override
		public void mutated() {
			reattach();
		}

		@Override
		public void valuesSet(final ListEvent<T> event) {
			reattach();
		}

		@Override
		public void valuesCleared(final ListEvent<T> event) {
			reattach();
		}

		@Override
		public void valuesAdded(final ListEvent<T> event) {
			reattach();
		}

		@Override
		public void valuesRemoved(final ListEvent<T> event) {
			reattach();
		}

		private void reattach() {
			bindingControl.resumeBinding();
		}

		@Override
		public String toString() {
			return "Preserve on update of " + model;
		}

	}

	/**
	 * Restores the selection once some properties are fired.
	 */
	public static <T> IBindingChainDependency preserveOnUpdateOf(final ListModel<T> model) {
		return new PreserveOnUpdateOfListModel<>(model);
	}
}
