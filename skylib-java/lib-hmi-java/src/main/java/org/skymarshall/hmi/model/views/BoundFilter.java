package org.skymarshall.hmi.model.views;

import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public abstract class BoundFilter<DataType, FilterObjectType> implements
        IDynamicFilter<DataType>,
        IComponentBinding<FilterObjectType> {

    private FilterObjectType         filterObject;
    private IListViewOwner<DataType> owner;

    protected abstract boolean accept(DataType value, FilterObjectType filter);

    public BoundFilter() {
    }

    protected FilterObjectType getFilterObject() {
        return filterObject;
    }

    @Override
    public void attach(final IListViewOwner<DataType> viewOwner) {
        this.owner = viewOwner;
    }

    @Override
    public Object getComponent() {
        return this;
    }

    @Override
    public void addComponentValueChangeListener(final IComponentLink<FilterObjectType> link) {
        // Read only
    }

    @Override
    public void setComponentValue(final AbstractProperty source, final FilterObjectType value) {
        filterObject = value;
        owner.viewUpdated();
    }

    @Override
    public boolean accept(final DataType value) {
        if (filterObject == null) {
            // not yet initialized
            return false;
        }
        return accept(value, filterObject);
    }

}
