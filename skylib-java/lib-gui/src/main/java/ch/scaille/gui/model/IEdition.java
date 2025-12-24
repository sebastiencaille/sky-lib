package ch.scaille.gui.model;

import ch.scaille.util.helpers.JavaExt;

public interface IEdition<T> extends JavaExt.AutoCloseableNoException {

	T edited();

	@Override
	void close();

}
