package org.skymarshall.hmi.model.views;

import java.util.Comparator;
import java.util.function.Predicate;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class ListViews {
	public static <U> IListView<U> sorted(final Comparator<U> comparator) {
		return new ListView<>(comparator, null);
	}

	public static <U extends Comparable<U>> IListView<U> sorted(final Class<U> clazz) {
		return new ListView<>((u1, u2) -> u1.compareTo(u2), null);
	}

	public static <U> IListView<U> sortedFiltered(final Comparator<U> comparator, final Predicate<U> filter) {
		return new ListView<>(comparator, filter);
	}

	public static <U> IListView<U> filtered(final Predicate<U> filter) {
		return new ListView<>(null, filter);
	}

	/**
	 * Creates a filter from a property, which object is the filter itself
	 *
	 * @param filter
	 * @return
	 */
	public static <DataType, FilterType extends Predicate<DataType>> Predicate<DataType> filter(
			final ObjectProperty<FilterType> filter) {
		return new PropertyFilter<>(filter);
	}

	public static <U> IListView<U> inherited() {
		return new ListView<>(null, null);
	}

}
