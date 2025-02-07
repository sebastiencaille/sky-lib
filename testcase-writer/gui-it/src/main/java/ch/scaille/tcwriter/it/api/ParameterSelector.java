package ch.scaille.tcwriter.it.api;

import java.util.function.Function;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.it.TCWriterPage;
import ch.scaille.tcwriter.pilot.swing.JTablePoller;

@TCApi(description = "Action parameter selection", humanReadable = "Action parameter selection", isSelector = true)
public interface ParameterSelector extends Function<TCWriterPage, JTablePoller> {

	@TCApi(description = "Current selector", humanReadable = "|the selector")
    static ParameterSelector currentSelector() {
		return p -> p.selectorValue;
	}

	@TCApi(description = "Current parameter", humanReadable = "|the parameter")
    static ParameterSelector parameter() {
		return p -> p.parameters0Value;
	}

}
