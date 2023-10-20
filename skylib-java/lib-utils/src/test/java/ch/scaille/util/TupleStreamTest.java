package ch.scaille.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.TupleStream;
import ch.scaille.util.helpers.TupleStream.Tuple;

class TupleStreamTest {

	@Test
	void testMap() {
		Assertions.assertEquals(List.of(2, 3, 4),
				List.of(1, 2, 3)
						.stream()
						.map(TupleStream.of(i -> i + 1))
						.map(Tuple::getY)
						.collect(Collectors.toList()));
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4),
				List.of(1, 2, 3)
						.stream()
						.flatMap(TupleStream.ofList(i -> List.of(i, i + 1)))
						.map(Tuple::getY)
						.collect(Collectors.toList()));
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4),
				List.of(1, 2, 3)
						.stream()
						.flatMap(TupleStream.ofStream(i -> List.of(i, i + 1).stream()))
						.map(Tuple::getY)
						.collect(Collectors.toList()));

	}

	@Test
	void testCollect() {
		List<Integer> result1 = new ArrayList<>();
		List.of(1, 2, 3).stream().collect(TupleStream.streamOf(i -> i + 1)).forEach((x, y) -> result1.add(y));
		Assertions.assertEquals(List.of(2, 3, 4), result1);

		List<Integer> result2 = new ArrayList<>();
		List.of(1, 2, 3)
				.stream()
				.collect(TupleStream.streamOfList(i -> List.of(i, i + 1)))
				.forEach((x, y) -> result2.add(y));
		Assertions.assertEquals(List.of(1, 2, 2, 3, 3, 4), result2);
	}

}
