// File generated from template 2022/01/22 06:07:30
package ch.scaille.example.gui;

import java.util.Arrays;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IComponentBinding;
import ch.scaille.gui.mvc.IComponentLink;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.gui.mvc.factories.Persisters;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister;
import ch.scaille.gui.mvc.properties.AbstractProperty;
import ch.scaille.gui.mvc.properties.Configuration;
import ch.scaille.gui.mvc.properties.IntProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;

public class TestObjectGuiModel extends GuiModel implements IObjectGuiModel<TestObject> {

	private final ObjectProviderPersister.CurrentObjectProvider<TestObject> currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider<>();

	public static final String ASECOND_VALUE = "ASecondValue";

	public static final String AFIRST_VALUE = "AFirstValue";

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;

	protected final AbstractProperty[] allProperties;

	public TestObjectGuiModel(final String prefix, ModelConfiguration config) {
		super(config.ifNotSet(() -> GuiModel.createErrorProperty(prefix + "TestObject-Error", config)));
		aSecondValueProperty = new IntProperty(prefix + ASECOND_VALUE, this).configureTyped(
				Configuration.persistent(currentObjectProvider,
						Persisters.getSet(TestObject::getASecondValue, TestObject::setASecondValue)),
				implicitConverters(TestObject.class, ASECOND_VALUE, java.lang.Integer.class));
		aFirstValueProperty = new ObjectProperty<java.lang.String>(prefix + AFIRST_VALUE, this).configureTyped(
				Configuration.persistent(currentObjectProvider,
						Persisters.getSet(TestObject::getAFirstValue, TestObject::setAFirstValue)),
				implicitConverters(TestObject.class, AFIRST_VALUE, java.lang.String.class));

		allProperties = new AbstractProperty[] { aSecondValueProperty, aFirstValueProperty };
	}

	public TestObjectGuiModel(ModelConfiguration config) {
		this("", config);
	}

	public Class<?> getContainerClass() {
		return TestObject.class;
	}

	public IntProperty getASecondValueProperty() {
		return aSecondValueProperty;
	}

	public ObjectProperty<java.lang.String> getAFirstValueProperty() {
		return aFirstValueProperty;
	}

	@Override
	public void load() {
		try {
			getPropertySupport().transmitAllToComponentOnly();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			getPropertySupport().enableAllTransmit();
		}
	}

	@Override
	public void save() {
		Arrays.stream(allProperties).forEach(AbstractProperty::save);
	}

	@Override
	public void setCurrentObject(final TestObject value) {
		currentObjectProvider.setObject(value);
	}

	public IComponentBinding<TestObject> loadBinding() {
		return new IComponentBinding<>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<TestObject> link) {
				// nope
			}

			@Override
			public void removeComponentValueChangeListener() {
				// nope
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final TestObject value) {
				if (value != null) {
					setCurrentObject(value);
					load();
				}
			}
		};
	}
}
