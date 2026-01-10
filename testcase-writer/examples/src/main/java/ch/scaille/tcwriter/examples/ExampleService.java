package ch.scaille.tcwriter.examples;

import lombok.Getter;

public class ExampleService {

	public enum ItemKind {
		COFFEE_MACHINE, TEA_POT
	}

	public enum State {
		COMPULSIVE_BUYING, ON_INTERNET, IN_SHOP, WAIT_FOR_DELIVERY, DELIVERED, GOT_PACKAGE
	}

	private State currentState = State.COMPULSIVE_BUYING;
	@Getter
    private ItemKind ownedItem = null;

    public void openBrowser() {
		ensureInState(State.COMPULSIVE_BUYING);
		currentState = State.ON_INTERNET;
	}

	public void goToShop() {
		ensureInState(State.COMPULSIVE_BUYING);
		currentState = State.IN_SHOP;
	}

	public void buy(final ItemKind newItem) {
		ownedItem = newItem;
		if (currentState == State.ON_INTERNET) {
			currentState = State.WAIT_FOR_DELIVERY;
		} else {
			ensureInState(State.IN_SHOP);
			currentState = State.GOT_PACKAGE;
		}
	}

	public void delivered() {
		ensureInState(State.WAIT_FOR_DELIVERY);
		currentState = State.DELIVERED;
	}

	public void getPackage() {
		ensureInState(State.DELIVERED);
		currentState = State.GOT_PACKAGE;
	}

	private void ensureInState(final State expected) {
		if (currentState != expected) {
			throw new IllegalStateException("Not in expected state. Expected " + expected + ", found " + currentState);
		}
	}

	public void reset() {
		currentState = State.COMPULSIVE_BUYING;
		ownedItem = null;
	}

}
