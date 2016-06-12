package ch.skymarshall.dataflowmgr.engine.data;

public class GreetingsCtxt {

	public Mood mood;
	public Greetings salutation;
	public String result;

	public GreetingsCtxt(Mood mood) {
		this.mood = mood;
	}

	public Mood getMood() {
		return mood;
	}

	public void setMood(Mood mood) {
		this.mood = mood;
	}

	public Greetings getGreetings() {
		return salutation;
	}

	public void setGreetings(Greetings greetings) {
		this.salutation = greetings;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
