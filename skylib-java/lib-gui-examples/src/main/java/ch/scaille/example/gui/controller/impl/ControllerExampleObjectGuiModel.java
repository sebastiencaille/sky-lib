package ch.scaille.example.gui.controller.impl;

import javax.annotation.processing.Generated;

import java.util.Arrays;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.Configuration;
import ch.scaille.javabeans.persisters.IPersisterFactory.ObjectHolder;
import ch.scaille.javabeans.persisters.Persisters;
import ch.scaille.javabeans.properties.BooleanProperty;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.javabeans.properties.IntProperty;

@Generated(value = "ch.scaille.gui.mvc.GuiModelGenerator", date = "2023/12/10 21:22", comments = "-sp ch.scaille.example.gui -s /home/scaille/src/github/sky-lib/skylib-java/lib-gui-examples/target/classes -t /home/scaille/src/github/sky-lib/skylib-java/lib-gui-examples/src/main/java")
public class ControllerExampleObjectGuiModel extends GuiModel implements IObjectGuiModel<ch.scaille.example.gui.controller.impl.ControllerExampleObject> {
   
    private final ObjectHolder<ch.scaille.example.gui.controller.impl.ControllerExampleObject> currentObjectProvider = new ObjectHolder<>();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String STRING_PROP = "StringProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	protected final ObjectProperty<ch.scaille.example.gui.TestObject> testObjectPropProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public ControllerExampleObjectGuiModel(final String prefix, ModelConfiguration config) {
		super(config.ifNotSet(()->	GuiModel.createErrorProperty(prefix + "ControllerExampleObject-Error", config)));
		booleanPropProperty = new BooleanProperty(prefix + BOOLEAN_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(ControllerExampleObject::isBooleanProp, ControllerExampleObject::setBooleanProp)),
			implicitConverters(ControllerExampleObject.class, BOOLEAN_PROP, java.lang.Boolean.class));
		intPropProperty = new IntProperty(prefix + INT_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(ControllerExampleObject::getIntProp, ControllerExampleObject::setIntProp)),
			implicitConverters(ControllerExampleObject.class, INT_PROP, java.lang.Integer.class));
		stringPropProperty = new ObjectProperty<java.lang.String>(prefix + STRING_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(ControllerExampleObject::getStringProp, ControllerExampleObject::setStringProp)),
			implicitConverters(ControllerExampleObject.class, STRING_PROP, java.lang.String.class));
		testObjectPropProperty = new ObjectProperty<ch.scaille.example.gui.TestObject>(prefix + TEST_OBJECT_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(ControllerExampleObject::getTestObjectProp, ControllerExampleObject::setTestObjectProp)),
			implicitConverters(ControllerExampleObject.class, TEST_OBJECT_PROP, ch.scaille.example.gui.TestObject.class));
		
		allProperties = new AbstractProperty[]{booleanPropProperty, intPropProperty, stringPropProperty, testObjectPropProperty};
    }
            
    public ControllerExampleObjectGuiModel(ModelConfiguration config) {
    	this("", config);
    }

	public Class<?> getContainerClass() {
		return ch.scaille.example.gui.controller.impl.ControllerExampleObject.class;
	}

	public BooleanProperty getBooleanPropProperty() {
	    return booleanPropProperty;
	}
	public IntProperty getIntPropProperty() {
	    return intPropProperty;
	}
	public ObjectProperty<java.lang.String> getStringPropProperty() {
	    return stringPropProperty;
	}
	public ObjectProperty<ch.scaille.example.gui.TestObject> getTestObjectPropProperty() {
	    return testObjectPropProperty;
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
    public void setCurrentObject(final ch.scaille.example.gui.controller.impl.ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ch.scaille.example.gui.controller.impl.ControllerExampleObject> loadBinding() {
        return new IComponentBinding<>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ch.scaille.example.gui.controller.impl.ControllerExampleObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final ch.scaille.example.gui.controller.impl.ControllerExampleObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
