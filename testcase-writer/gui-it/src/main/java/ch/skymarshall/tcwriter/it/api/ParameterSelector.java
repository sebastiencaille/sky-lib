package ch.skymarshall.tcwriter.it.api;

import ch.skymarshall.tcwriter.annotations.TCApi;

@TCApi(description = "Action parameter selection", humanReadable = "Action parameter selection", isSelector = true)
public class ParameterSelector {

	private final String tableName;

	public ParameterSelector(final String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	@TCApi(description = "Current selector", humanReadable = "|the selector")
	public static ParameterSelector selector() {
		return new ParameterSelector("selector-valueTable");
	}

	@TCApi(description = "Current parameter", humanReadable = "|the parameter")
	public static ParameterSelector parameter() {
		return new ParameterSelector("param0-valueTable");
	}

}
