/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc;

/**
 * Base of MVC controller.
 * <p>
 * This class gives access of basic components used by the controller
 * 
 * @author Sebastien Caille
 * 
 */
public class HmiController {

    /**
     * The associated support
     */
    protected final ControllerPropertyChangeSupport propertySupport;

    public HmiController() {
        this.propertySupport = new ControllerPropertyChangeSupport(this);
    }

    public HmiController(final ControllerPropertyChangeSupport propertySupport) {
        this.propertySupport = propertySupport;
    }

    public ControllerPropertyChangeSupport getPropertySupport() {
        return propertySupport;
    }

    /**
     * Starts the controller, to be called once once all the GUIs component are
     * bound to the controller. It actually attachs all the properties, causing
     * the values to be sent to the components
     */
    public void start() {
        propertySupport.attachAll();
    }

}
