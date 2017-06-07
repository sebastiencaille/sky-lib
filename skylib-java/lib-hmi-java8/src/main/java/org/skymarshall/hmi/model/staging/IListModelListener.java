/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model.staging;

import java.util.EventListener;

/**
 * Listener over dynamic list events.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public interface IListModelListener<T> extends
        EventListener {

    void mutates();

    void valuesSet(ListEvent<T> event);

    void valuesCleared(ListEvent<T> event);

    void valuesAdded(ListEvent<T> event);

    void valuesRemoved(ListEvent<T> event);

    void editionCancelled(ListEvent<T> event);

    void editionsStarted(ListEvent<T> event);

    void editionsStopping(ListEvent<T> event);

    void editionsStopped(ListEvent<T> event);

}
