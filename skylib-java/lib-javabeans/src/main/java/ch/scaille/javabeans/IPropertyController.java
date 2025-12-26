package ch.scaille.javabeans;

public interface IPropertyController {
	/**
	 * Detaches all the properties of this scope.
	 */
	void stopTransmit();
	
	void transmitChangesOnlyToComponent();

	void transmitChangesBothWays();
	
	void disposeBindings();
	
	void flushChanges();

}
