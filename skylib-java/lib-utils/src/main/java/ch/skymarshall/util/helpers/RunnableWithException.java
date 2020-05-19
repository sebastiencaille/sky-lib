package ch.skymarshall.util.helpers;

@FunctionalInterface
public interface RunnableWithException<E extends Exception, F extends Exception> {

	public void run() throws E, F;
}
