package ch.skymarshall.dataflowmgr.engine;

public class ContextObject<T> {

	private T value;
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	
}
