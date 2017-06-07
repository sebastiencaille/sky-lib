/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.util.text;

/**
 * Provides indentation with chars followed by an arrow.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class ArrowIndentationManager implements
        IIndentationManager {

    private final String indentation;

    private String       currentIndentation = "";

    private int          level              = 0;

    public ArrowIndentationManager() {
        this(' ', 4);
    }

    public ArrowIndentationManager(final char c, final int length) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(c);
        }
        indentation = builder.toString();
    }

    @Override
    public void indent() {
        currentIndentation += indentation;
        level++;
    }

    @Override
    public void unindent() {
        currentIndentation = currentIndentation.substring(indentation.length());
        level--;
    }

    @Override
    public String getIndentation() {
        if (level == 0) {
            return "--> ";
        }
        return currentIndentation;
    }

}
