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
