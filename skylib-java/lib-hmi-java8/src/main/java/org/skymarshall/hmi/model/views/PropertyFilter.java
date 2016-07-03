package org.skymarshall.hmi.model.views;

import java.util.function.Predicate;

import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class PropertyFilter<DataType, FilterType extends Predicate<DataType>>
		implements Predicate<DataType> {

	private final ObjectProperty<FilterType> filter;

	public PropertyFilter(final ObjectProperty<FilterType> filter) {
		this.filter = filter;
	}

	@Override
	public boolean test(final DataType object) {
		return filter.getValue().test(object);
	}

}
