package org.skymarshall.hmi.model.views;

import org.skymarshall.hmi.model.IFilter;

public interface IDynamicFilter<T> extends
        IFilter<T> {

    void attach(IListViewOwner<T> viewOwner);

}
