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
package ch.skymarshall.dataflowmgr.model;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An abstract representation of a flow's action.
 * @author scaille
 *
 * @param <I>
 * @param <O>
 */
public abstract class FlowAction<I extends FlowData, O extends FlowData>
		implements Function<I, O> {

	protected static final NoData NO_DATA = NoData.NO_DATA;
	private final Class<I> inputClass;
	private final Supplier<I> inputDataSupplier;

	public FlowAction(final Class<I> inputClass, final Supplier<I> inputDataSupplier) {
		this.inputClass = inputClass;
		this.inputDataSupplier = inputDataSupplier;
	}

	public Class<I> getInputClass() {
		return inputClass;
	}

	public Supplier<I> getInputDataSupplier() {
		return inputDataSupplier;
	}

}
