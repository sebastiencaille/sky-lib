package ch.scaille.javabeans.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IPropertiesGroup;

/**
 * Property containing a list of Objects.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> type of the contained objects
 */
public class ListProperty<T> extends ObjectProperty<List<T>> {
	
	private class ObservableArrayList extends ArrayList<T> {
		
		@Override
		public boolean add(T e) {
			final var res = super.add(e);
			fireArtificialChange(this);
			return res;
		}
		
		@Override
		public void add(int index, T element) {
			super.add(index, element);
			fireArtificialChange(this);
		}
		
		@Override
		public T remove(int index) {
			final var remove = super.remove(index);
			fireArtificialChange(this);
			return remove;
		}
		
		@Override
		public boolean addAll(Collection<? extends T> toAdd) {
			final var res = super.addAll(toAdd);
			fireArtificialChange(this);
			return res;
		}
	}
	
	public ListProperty(final String name, final IPropertiesOwner model) {
		super(name, model);
		super.setObjectValue(this, new ObservableArrayList());
	}

	public ListProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport);
		super.setObjectValue(this, new ObservableArrayList());
	}

	@SafeVarargs
	@Override
	public final ListProperty<T> configureTyped(final Consumer<AbstractTypedProperty<List<T>>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
	}

}
