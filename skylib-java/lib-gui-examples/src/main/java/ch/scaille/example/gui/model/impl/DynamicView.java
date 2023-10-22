package ch.scaille.example.gui.model.impl;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.views.AbstractDynamicView;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.javabeans.IComponentBinding;

public class DynamicView extends AbstractDynamicView<TestObject> implements IListView<TestObject> {

	private boolean filter;
	private boolean sortReverseOrder;

	public DynamicView() {
		super();
	}

	@Override
	public boolean accept(final TestObject object) {
		return !filter || object.getASecondValue() % 2 == 0;
	}

	@Override
	public int compare(final TestObject o1, final TestObject o2) {
		return (sortReverseOrder) ? o2.getASecondValue() - o1.getASecondValue()
				: o1.getASecondValue() - o2.getASecondValue();
	}

	public IComponentBinding<Boolean> enableFilter() {
		return refreshWhenUpdated(enabled -> filter = enabled);
	}

	public IComponentBinding<Boolean> reverseOrder() {
		return refreshWhenUpdated(reversed -> sortReverseOrder = reversed);
	}

}
