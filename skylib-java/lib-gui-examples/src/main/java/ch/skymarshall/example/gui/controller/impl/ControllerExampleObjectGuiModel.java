// File generated from template
package ch.skymarshall.example.gui.controller.impl;

import java.util.Arrays;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.IObjectGuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.ErrorProperty;
import ch.skymarshall.gui.mvc.properties.Configuration;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;


public class ControllerExampleObjectGuiModel extends GuiModel implements IObjectGuiModel<ControllerExampleObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider<ControllerExampleObject> currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider<>();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String STRING_PROP = "StringProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	protected final ObjectProperty<ch.skymarshall.example.gui.TestObject> testObjectPropProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public ControllerExampleObjectGuiModel(final String prefix, final IScopedSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
		booleanPropProperty = new BooleanProperty(prefix + "-BooleanProp",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::isBooleanProp, ControllerExampleObject::setBooleanProp)), Configuration.errorNotifier(errorProperty));
		intPropProperty = new IntProperty(prefix + "-IntProp",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getIntProp, ControllerExampleObject::setIntProp)), Configuration.errorNotifier(errorProperty));
		stringPropProperty = new ObjectProperty<java.lang.String>(prefix + "-StringProp",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getStringProp, ControllerExampleObject::setStringProp)), Configuration.errorNotifier(errorProperty));
		testObjectPropProperty = new ObjectProperty<ch.skymarshall.example.gui.TestObject>(prefix + "-TestObjectProp",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getTestObjectProp, ControllerExampleObject::setTestObjectProp)), Configuration.errorNotifier(errorProperty));
		
		allProperties = new AbstractProperty[]{booleanPropProperty, intPropProperty, stringPropProperty, testObjectPropProperty};
    }

    public ControllerExampleObjectGuiModel(final String prefix, final GuiController controller) {
        this(prefix, controller.getScopedChangeSupport(), GuiModel.createErrorProperty(prefix + "-ControllerExampleObject-Error", controller.getScopedChangeSupport()));
    }

    public ControllerExampleObjectGuiModel(final GuiController controller) {
        this("ControllerExampleObject", controller.getScopedChangeSupport(), GuiModel.createErrorProperty("ControllerExampleObject-Error", controller.getScopedChangeSupport()));
    }

    public ControllerExampleObjectGuiModel(final String prefix, final IScopedSupport propertySupport) {
        this(prefix, propertySupport, GuiModel.createErrorProperty(prefix + "-ControllerExampleObject-Error", propertySupport));
    }

    public ControllerExampleObjectGuiModel(final IScopedSupport propertySupport) {
        this("ControllerExampleObject", propertySupport, GuiModel.createErrorProperty("ControllerExampleObject-Error", propertySupport));
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
	public ObjectProperty<ch.skymarshall.example.gui.TestObject> getTestObjectPropProperty() {
	    return testObjectPropProperty;
	}
	

    @Override
    public void load() {
    	try {
    		propertySupport.transmitAllToComponentOnly();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			propertySupport.enableAllTransmit();
		}
    }

    @Override
    public void save() {
		Arrays.stream(allProperties).forEach(AbstractProperty::save);
    }

    @Override
    public void setCurrentObject(final ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ControllerExampleObject> loadBinding() {
        return new IComponentBinding<ControllerExampleObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ControllerExampleObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final ControllerExampleObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
