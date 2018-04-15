package org.skymarshall.example.hmi.model.impl;

import org.skymarshall.example.hmi.TestObject;
import org.skymarshall.hmi.model.views.AbstractDynamicView;
import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.mvc.IComponentBinding;

public class DynamicView extends AbstractDynamicView<TestObject> implements IListView<TestObject> {

	private boolean filter;
	private boolean sortReverseOrder;

	public DynamicView() {
		super();
	}

	@Override
	public boolean accept(final TestObject object) {
		return !filter || object.aSecondValue % 2 == 0;
	}

	@Override
	public int compare(final TestObject o1, final TestObject o2) {
		return (sortReverseOrder) ? o2.aSecondValue - o1.aSecondValue : o1.aSecondValue - o2.aSecondValue;
	}

	public IComponentBinding<Boolean> enableFilter() {
		return viewUpdate(enabled -> filter = enabled);
	}

	public IComponentBinding<Boolean> reverseOrder() {
		return viewUpdate(enabled -> sortReverseOrder = enabled);
	}

}
