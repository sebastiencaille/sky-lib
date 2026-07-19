package ch.scaille.dataflowmgr.examples.simple.annotations;

import ch.scaille.dataflowmgr.model.Mapper;

/**
 * TODO: generate a map from apis
 */
public enum ExampleApis implements Mapper {
	
	SIMPLE_FLOW_CONDITIONS("ch.scaille.dataflowmgr.examples.simple.SimpleFlowConditions", "simpleFlowConditions"),
	MUST_MUTATE("mustMutate"), 

	SIMPLE_SERVICE("ch.scaille.dataflowmgr.examples.simple.SimpleService", "simpleService"),
	INIT("init"), 
	MUTATE("mutate"), 
	KEEP_AS_IS("keepAsIs"), 
	
	SIMPLE_EXTERNAL_ADAPTER("ch.scaille.dataflowmgr.examples.simple.SimpleExternalAdapter", "simpleExternalAdapter"),
	GET_MUTATION("getMutation"),
	DISPLAY("display");


	private final String symbol;
	private final String alias;
	
	ExampleApis(String symbol, String alias) {
		this.symbol = symbol;
		this.alias = alias;
	}
	
	ExampleApis(String symbol) {
		this.symbol = symbol;
		this.alias = symbol;
	}
	
	
	public String alias() {
		return alias;
	}
	
	public String symbol() {
		return symbol;
	}
}
