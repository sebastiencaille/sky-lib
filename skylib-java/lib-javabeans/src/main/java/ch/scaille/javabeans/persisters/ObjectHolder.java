package ch.scaille.javabeans.persisters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public
class ObjectHolder<T> implements IObjectProvider<T> {
	private T object;
}