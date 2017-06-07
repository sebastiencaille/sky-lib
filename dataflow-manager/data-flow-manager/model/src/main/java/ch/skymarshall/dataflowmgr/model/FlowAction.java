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
package ch.skymarshall.dataflowmgr.model;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FlowAction<InputDataType extends FlowData, OutputDataType extends FlowData>
		implements Function<InputDataType, OutputDataType> {

	protected static NoData NO_DATA = NoData.NO_DATA;
	private final Class<InputDataType> inputClass;
	private final Supplier<InputDataType> inputDataSupplier;

	public FlowAction(final Class<InputDataType> inputClass, final Supplier<InputDataType> inputDataSupplier) {
		this.inputClass = inputClass;
		this.inputDataSupplier = inputDataSupplier;
	}

	public Class<InputDataType> getInputClass() {
		return inputClass;
	}

	public Supplier<InputDataType> getInputDataSupplier() {
		return inputDataSupplier;
	}

}
