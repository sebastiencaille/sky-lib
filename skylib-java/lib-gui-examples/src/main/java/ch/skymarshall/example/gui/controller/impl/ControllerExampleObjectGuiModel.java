// File generated from template
package ch.skymarshall.example.gui.controller.impl;

import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IObjectGuiModel;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.properties.Properties;
import ch.skymarshall.gui.mvc.properties.ErrorProperty;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.persisters.GetSetAccess;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;


public class ControllerExampleObjectGuiModel extends GuiModel implements IObjectGuiModel<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String STRING_PROP = "StringProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	protected final ObjectProperty<ch.skymarshall.example.gui.TestObject> testObjectPropProperty;
	

    public ControllerExampleObjectGuiModel(final String prefix, final IScopedSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
		booleanPropProperty = Properties.of(new BooleanProperty(prefix + "-BooleanProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject,java.lang.Boolean>access((o) -> o::isBooleanProp, (o) ->o::setBooleanProp))).setErrorNotifier(errorProperty).getProperty();
		intPropProperty = Properties.of(new IntProperty(prefix + "-IntProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject,java.lang.Integer>access((o) -> o::getIntProp, (o) ->o::setIntProp))).setErrorNotifier(errorProperty).getProperty();
		stringPropProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-StringProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject,java.lang.String>access((o) -> o::getStringProp, (o) ->o::setStringProp))).setErrorNotifier(errorProperty).getProperty();
		testObjectPropProperty = Properties.of(new ObjectProperty<ch.skymarshall.example.gui.TestObject>(prefix + "-TestObjectProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject,ch.skymarshall.example.gui.TestObject>access((o) -> o::getTestObjectProp, (o) ->o::setTestObjectProp))).setErrorNotifier(errorProperty).getProperty();
		
    }

    public ControllerExampleObjectGuiModel(final String prefix, final GuiController controller) {
        this(prefix, controller.getPropertySupport(), GuiModel.createErrorProperty(prefix + "-Error", controller.getPropertySupport()));
    }

    public ControllerExampleObjectGuiModel(final GuiController controller) {
        this("ControllerExampleObject", controller.getPropertySupport(), GuiModel.createErrorProperty("ControllerExampleObject-Error", controller.getPropertySupport()));
    }

    public ControllerExampleObjectGuiModel(final String prefix, final IScopedSupport propertySupport) {
        this(prefix, propertySupport, GuiModel.createErrorProperty(prefix + "-Error", propertySupport));
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
		booleanPropProperty.load(this);intPropProperty.load(this);stringPropProperty.load(this);testObjectPropProperty.load(this);
    }

    @Override
    public void save() {
		booleanPropProperty.save();intPropProperty.save();stringPropProperty.save();testObjectPropProperty.save();
    }

    @Override
    public void setCurrentObject(final ch.skymarshall.example.gui.controller.impl.ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject> binding() {
        return new IComponentBinding<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ch.skymarshall.example.gui.controller.impl.ControllerExampleObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final ch.skymarshall.example.gui.controller.impl.ControllerExampleObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
