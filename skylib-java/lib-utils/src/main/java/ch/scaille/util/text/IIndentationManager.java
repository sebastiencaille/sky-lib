package ch.scaille.util.text;

/**
 * Methods to be implemented by an indentation manager.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public interface IIndentationManager {

	/**
	 * Increase the indentation level
	 */
	void indent();

	/**
	 * Decrease the indentation level
	 */
	void unindent();

	/**
	 * Gets the current indentation
	 */
	String getIndentation();

}
