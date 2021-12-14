package ch.skymarshall.tcwriter.it.api;

import java.util.function.Function;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.it.TCWriterPage;
import ch.skymarshall.tcwriter.pilot.swing.JTablePilot;

@TCApi(description = "Action parameter selection", humanReadable = "Action parameter selection", isSelector = true)
public interface ParameterSelector extends Function<TCWriterPage, JTablePilot> {

	@TCApi(description = "Current selector", humanReadable = "|the selector")
	public static ParameterSelector selector() {
		return p -> p.selectorValue;
	}

	@TCApi(description = "Current parameter", humanReadable = "|the parameter")
	public static ParameterSelector parameter() {
		return p -> p.parameters0Value;
	}

}
