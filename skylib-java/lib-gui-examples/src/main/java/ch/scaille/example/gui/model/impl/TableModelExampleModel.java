package ch.scaille.example.gui.model.impl;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.views.DynamicListView;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.BooleanProperty;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.javabeans.properties.PropertiesAggregator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static ch.scaille.example.gui.model.impl.TableModelExampleView.NATURAL_ORDER;
import static ch.scaille.example.gui.model.impl.TableModelExampleView.REVERSE_ORDER;

@NullMarked
public class TableModelExampleModel {

	public record ViewFilter(boolean reverseOrder, boolean enableFilter)
			implements DynamicListView.View<TestObject> {

		@Override
		public int compare(TestObject o1, TestObject o2) {
			return (reverseOrder ? REVERSE_ORDER : NATURAL_ORDER).compare(o1, o2);
		}

		@Override
		public boolean test(TestObject o) {
			return !enableFilter || o.getASecondValue() % 2 == 0;
		}
	}

	private final IPropertiesGroup changeSupport = PropertyChangeSupportController.mainGroup(this);

	public final BooleanProperty reverseOrder = new BooleanProperty("Order", changeSupport);

	public final BooleanProperty enableFilter = new BooleanProperty("Filter", changeSupport);

	public final PropertiesAggregator<ViewFilter> viewFilter = new PropertiesAggregator<ViewFilter>("viewParameters", changeSupport)
			.add(reverseOrder, enableFilter,
					(reverseOrderVal, enableFilterVal) -> new ViewFilter(reverseOrderVal.get(), enableFilterVal.get()));

	public final ObjectProperty<@Nullable TestObject> objectSelection = new ObjectProperty<>("Selection", changeSupport, null);

	public void setCreated() {
		changeSupport.transmitChangesBothWays();
	}

}
