package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCApi;

/**
 * To edit a step (actor/action/selector/parameter
 *
 * @author scaille
 *
 */
public class StepEdition {

	private String actor;

	private String action;

	private String selector;

	private String parameter;

	private String parameterValue1;

	public String getActor() {
		return actor;
	}

	@TCApi(description = "Actor", humanReadable = "Actor")
	public void setActor(final String actor) {
		this.actor = actor;
	}

	public String getAction() {
		return action;
	}

	@TCApi(description = "Action", humanReadable = "Action")
	public void setAction(final String action) {
		this.action = action;
	}

	public String getSelector() {
		return selector;
	}

	@TCApi(description = "Selector", humanReadable = "Selector")
	public void setSelector(final String selector) {
		this.selector = selector;
	}

	public String getParameter() {
		return parameter;
	}

	@TCApi(description = "Parameter", humanReadable = "Parameter")
	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	public String getParameterValue1() {
		return parameterValue1;
	}

	@TCApi(description = "Parameter Value 1", humanReadable = "Parameter Value")
	public void setParameterValue1(String parameterValue1) {
		this.parameterValue1 = parameterValue1;	
	}
	
	@TCApi(description = "Step Edition", humanReadable = "Step Edition")
	public static StepEdition edition() {
		return new StepEdition();
	}

}
