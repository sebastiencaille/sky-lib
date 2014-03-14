package org.skymarshall.hmi.model.views;

import org.skymarshall.hmi.model.IFilter;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class PropertyFilter<DataType, FilterType extends IFilter<DataType>> implements
        IFilter<DataType> {

    private final ObjectProperty<FilterType> filter;

    public PropertyFilter(final ObjectProperty<FilterType> filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(final DataType object) {
        return filter.getValue().accept(object);
    }

}
