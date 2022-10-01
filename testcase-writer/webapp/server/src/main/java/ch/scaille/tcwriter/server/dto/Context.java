package ch.scaille.tcwriter.server.dto;

public class Context {

	private final Identity identity;
	private String dictionaryName;

	public Context() {
		identity = null;
	}

	public Context(Identity identity) {
		this.identity = identity;
	}

	public Identity getIdentity() {
		return identity;
	}

	public String getDictionary() {
		return dictionaryName;
	}

	public void setDictionary(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public Context derive() {
		Context copy = new Context(this.identity);
		copy.setDictionary(dictionaryName);
		return copy;
	}

	@Override
	public String toString() {
		return String.format("[id: %s, dictionaryName: %s ]", identity, dictionaryName);
	}

}
