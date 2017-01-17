package ch.skymarshall.dataflowmgr.model;

import java.util.function.Function;

public abstract class FlowAction<InputDataType extends FlowData, OutputDataType extends FlowData>
		implements Function<InputDataType, OutputDataType> {

	protected static NoData NO_DATA = NoData.NO_DATA;

}
