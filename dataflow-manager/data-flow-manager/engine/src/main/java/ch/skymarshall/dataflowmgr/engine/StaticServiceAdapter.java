package ch.skymarshall.dataflowmgr.engine;

import java.util.function.Function;

public class StaticServiceAdapter<InputDo> implements Action<InputDo>{
	
	private final Function<InputDo, Object> function;
	
	
	
	public StaticServiceAdapter(Function<InputDo, Object> function) {
		this.function = function;
	}



	public Object execute(InputDo input) {
		return function.apply(input);
	};

}
