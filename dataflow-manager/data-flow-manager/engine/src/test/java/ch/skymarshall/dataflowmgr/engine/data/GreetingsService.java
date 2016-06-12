package ch.skymarshall.dataflowmgr.engine.data;

public class GreetingsService {

	public Greetings goodMood(final Mood input) {
		return new Greetings("Hello", false);
	}

	public Greetings badMood(final Mood input) {
		return new Greetings("Goodbye", true);
	}

	public String world(final Greetings salutation) {
		return salutation.getText() + " world";
	}

	public String kitty(final Greetings salutation) {
		return salutation.getText() + " kitty";
	}

	public String greet() {
		return "Greetings";
	}

}
