package ch.scaille.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.TupleStream;
import ch.scaille.util.helpers.TupleStream.Tuple;

class TupleStreamTest {

	@Test
	void testMap() {
		Assertions.assertEquals(List.of(2, 3, 4),
				Stream.of(1, 2, 3)
						.map(TupleStream.of(i -> i + 1))
						.map(Tuple::y)
						.toList());
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4),
				Stream.of(1, 2, 3)
						.flatMap(TupleStream.ofList(i -> List.of(i, i + 1)))
						.map(Tuple::y)
						.toList());
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4),
				Stream.of(1, 2, 3)
						.flatMap(TupleStream.ofStream(i -> Stream.of(i, i + 1)))
						.map(Tuple::y)
						.toList());

	}

	@Test
	void testCollect() {
		List<Integer> result1 = new ArrayList<>();
		Stream.of(1, 2, 3).collect(TupleStream.streamOf(i -> i + 1)).forEach((x, y) -> result1.add(y));
		Assertions.assertEquals(List.of(2, 3, 4), result1);

		List<Integer> result2 = new ArrayList<>();
		Stream.of(1, 2, 3)
				.collect(TupleStream.streamOfList(i -> List.of(i, i + 1)))
				.forEach((x, y) -> result2.add(y));
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4), result2);
	}

}
