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
package ch.skymarshall.gui.model;

public class ListModelAdapter<T> implements IListModelListener<T> {
	@Override
	public void mutates() {
		// no op
	}

	@Override
	public void valuesSet(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void valuesCleared(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void valuesAdded(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void valuesRemoved(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void editionCancelled(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void editionStarted(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void editionStopping(final ListEvent<T> event) {
		// no op
	}

	@Override
	public void editionStopped(final ListEvent<T> event) {
		// no op
	}

}