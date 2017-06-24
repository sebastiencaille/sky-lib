/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model.views;

import java.util.Comparator;

import org.skymarshall.hmi.model.IFilter;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class ListViews {
    public static <U> IListView<U> sorted(final Comparator<U> comparator) {
        return new ListView<U>(comparator, null);
    }

    public static <U extends Comparable<U>> IListView<U> sorted(final Class<U> clazz) {
        return new ListView<U>(new Comparator<U>() {
            @Override
            public int compare(final U o1, final U o2) {
                return o1.compareTo(o2);
            }
        }, null);
    }

    public static <U> IListView<U> sortedFiltered(final Comparator<U> comparator, final IFilter<U> filter) {
        return new ListView<U>(comparator, filter);
    }

    public static <U> IListView<U> filtered(final IFilter<U> filter) {
        return new ListView<U>(null, filter);
    }

    /**
     * Creates a filter from a property, which object is the filter itself
     * 
     * @param filter
     * @return
     */
    public static <DataType, FilterType extends IFilter<DataType>> IFilter<DataType> filter(
            final ObjectProperty<FilterType> filter) {
        return new PropertyFilter<DataType, FilterType>(filter);
    }

    public static <U> IListView<U> inherited() {
        return new ListView<U>(null, null);
    }

}
