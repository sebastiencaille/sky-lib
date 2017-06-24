/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.engine;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collector;

public interface StreamHelper {

	public static class ZeroOrOne<T> {
		private Optional<T> value = Optional.empty();
		private int count;

		public T getValue() {
			return value.orElse(null);
		}

		void setValue(final T newValue) {
			if (newValue == null) {
				return;
			}
			count++;
			if (!value.isPresent()) {
				this.value = Optional.of(newValue);
			}
		}

		public <E extends Exception> Optional<T> orElseThrow(final IntFunction<E> supplier) throws E {
			if (count > 1) {
				throw supplier.apply(count);
			}
			return value;
		}
	}

	/**
	 * Collector that allows to check for zero or one value available
	 *
	 * @return
	 */
	public static <E> Collector<E, ?, ZeroOrOne<E>> zeroOrOne() {
		return Collector.of(ZeroOrOne::new, ZeroOrOne::setValue, (left, right) -> {
			left.setValue(right.getValue());
			return left;
		});
	}

}
