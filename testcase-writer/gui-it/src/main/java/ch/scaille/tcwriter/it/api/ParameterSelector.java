package ch.scaille.tcwriter.it.api;

import java.util.function.Function;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.it.TCWriterPage;
import ch.scaille.tcwriter.pilot.swing.JTablePilot;

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
