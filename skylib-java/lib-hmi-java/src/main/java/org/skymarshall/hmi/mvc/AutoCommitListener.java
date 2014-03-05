package org.skymarshall.hmi.mvc;


public class AutoCommitListener implements
        IPropertyEventListener {
    private final Object autoCommitObject;

    public AutoCommitListener(final Object autoCommitObject) {
        this.autoCommitObject = autoCommitObject;
    }

    @Override
    public void propertyModified(final Object caller, final PropertyEvent event) {
        event.getProperty().saveInto(autoCommitObject);
    }
}
