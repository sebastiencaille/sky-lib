package ch.scaille.javabeans;

public interface IPropertyController {
	/**
	 * Detaches all the properties of this scope.
	 */
	void bufferizeChanges();
	
	void transmitChangesOnlyToComponent();

	void transmitChangesBothWays();
	
	void disposeBindings();
	
	void flushChanges();

}
