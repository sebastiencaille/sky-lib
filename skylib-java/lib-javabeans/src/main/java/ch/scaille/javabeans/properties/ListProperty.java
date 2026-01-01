package ch.scaille.javabeans.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;
import org.jspecify.annotations.NullMarked;

/**
 * Property containing a list of Objects.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the contained objects
 */
@NullMarked
public class ListProperty<T> extends ObjectProperty<List<T>> {
	
	private class ObservableArrayList extends ArrayList<T> {
		
		@Override
		public boolean add(T e) {
			final var res = super.add(e);
			flushChanges(this);
			return res;
		}
		
		@Override
		public void add(int index, T element) {
			super.add(index, element);
			flushChanges(this);
		}
		
		@Override
		public T remove(int index) {
			final var remove = super.remove(index);
			flushChanges(this);
			return remove;
		}
		
		@Override
		public boolean addAll(Collection<? extends T> toAdd) {
			final var res = super.addAll(toAdd);
			flushChanges(this);
			return res;
		}
	}
	
	public ListProperty(final String name, final IPropertiesOwner model) {
		super(name, model, List.of());
		super.setObjectValue(this, new ObservableArrayList());
	}

	public ListProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport, List.of());
		super.setObjectValue(this, new ObservableArrayList());
	}

	@SafeVarargs
	@Override
	public final ListProperty<T> configureTyped(final Consumer<AbstractTypedProperty<List<T>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

}
