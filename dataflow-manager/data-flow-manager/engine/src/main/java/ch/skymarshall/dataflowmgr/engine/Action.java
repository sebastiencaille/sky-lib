package ch.skymarshall.dataflowmgr.engine;

public interface Action<InputDo> {
	Object execute(InputDo input); 
}
