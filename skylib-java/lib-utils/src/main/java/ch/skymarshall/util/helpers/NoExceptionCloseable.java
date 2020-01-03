package ch.skymarshall.util.helpers;

public interface NoExceptionCloseable extends AutoCloseable {

	@Override
	void close();

}
