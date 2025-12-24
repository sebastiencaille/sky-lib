package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IComponentChangeSource {

	boolean isModifiedBy(Object caller);

}
