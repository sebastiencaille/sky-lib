package ch.skymarshall.dataflowmgr.engine.data;

public class Greetings {

	private String text;
	private boolean evil;

	public Greetings(String text, boolean evil) {
		this.text = text;
		this.evil = evil;
	}

	public String getText() {
		return text;
	}

	public boolean isEvil() {
		return evil;
	}

}
