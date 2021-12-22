package ch.scaille.util.helpers;

public interface NoExceptionCloseable extends AutoCloseable {

	@Override
	void close();

}
