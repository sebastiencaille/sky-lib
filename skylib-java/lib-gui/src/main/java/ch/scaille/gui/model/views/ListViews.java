package ch.scaille.gui.model.views;

import java.util.Comparator;
import java.util.function.Predicate;

public interface ListViews {

	static <U> IListView<U> sortedFiltered(final Comparator<? super U> comparator, final Predicate<? super U> filter) {
		return new ListView<>(comparator, filter);
	}
	
	static <U> IListView<U> sorted(final Comparator<? super U> comparator) {
		return sortedFiltered(comparator, null);
	}

	static <U> IListView<U> filtered(final Predicate<? super U> filter) {
		return sortedFiltered(null, filter);
	}

	static <U extends Comparable<? super U>> IListView<U> sorted() {
		return sorted(Comparator.naturalOrder());
	}
	
	static <U> IListView<U> inherited() {
		return new ListView<>(null, null);
	}

}
