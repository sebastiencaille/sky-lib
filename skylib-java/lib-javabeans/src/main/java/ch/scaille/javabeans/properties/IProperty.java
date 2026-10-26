package ch.scaille.javabeans.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IVetoer;

public interface IProperty extends IComponentChangeSource {

    String getName();

    void setErrorNotifier(ErrorNotifier notifier);

    void load(Object caller);

    void save();

    void addListener(IPropertyEventListener listener);

    void addListener(PropertyChangeListener valueUpdateListener);

    void removeListener(PropertyChangeListener valueUpdateListener);

    void flushChanges(Object component);

    void reset(Object caller);

    PropertyChangeEvent createRefreshChangeEvent();

    IVetoer.TransmitMode getTransmitMode();

    void setTransmitMode(IVetoer.TransmitMode transmitMode);

    boolean mustSendToComponent();

    boolean mustSendToProperty();

    IProperty configure(Consumer<IProperty>... configurer);

}
