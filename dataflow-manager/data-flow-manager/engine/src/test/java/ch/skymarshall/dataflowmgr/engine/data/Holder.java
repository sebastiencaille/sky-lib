package ch.skymarshall.dataflowmgr.engine.data;

public  class Holder<T> {
	T data;

	public void set(T data) {
		this.data = data;
	}

	public T get() {
		return data;
	}
}
