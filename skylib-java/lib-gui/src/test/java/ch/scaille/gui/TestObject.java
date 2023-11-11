package ch.scaille.gui;

public class TestObject {

	private int val;

	public TestObject(final int val) {
		super();
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	@Override
	public boolean equals(final Object obj) {
		return val == ((TestObject) obj).val;
	}

	@Override
	public String toString() {
		return String.valueOf(val);
	}

	@Override
	public int hashCode() {
		return val;
	}
}
