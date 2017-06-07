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

import java.io.IOException;
import java.io.OutputStream;

/**
 * To output formatted text.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 */
public class TextFormatter {

    public interface IOutput {
        void append(String str) throws IOException;

        void append(char c) throws IOException;
    }

    public static IOutput output(final StringBuilder builder) {
        return new IOutput() {

            @Override
            public void append(final char c) {
                builder.append(c);
            }

            @Override
            public void append(final String str) {
                builder.append(str);
            }

            @Override
            public String toString() {
                return builder.toString();
            }
        };
    }

    public static IOutput output(final OutputStream stream) {
        return new IOutput() {

            @Override
            public void append(final char c) throws IOException {
                stream.write((byte) c);
            }

            @Override
            public void append(final String str) throws IOException {
                stream.write(str.getBytes());
            }
        };
    }

    private IIndentationManager indentationManager = new CharIndentationManager();
    private final IOutput       output;

    public TextFormatter(final IOutput output) {
        this.output = output;
    }

    public void setIndentationManager(final IIndentationManager indentationManager) {
        this.indentationManager = indentationManager;
    }

    public void indent() {
        indentationManager.indent();
    }

    public TextFormatter unindent() {
        indentationManager.unindent();
        return this;
    }

    public TextFormatter appendIndented(final String string) throws IOException {
        output.append(indentationManager.getIndentation());
        output.append(string);
        return this;
    }

    public TextFormatter appendIndentedLine(final String string) throws IOException {
        output.append(indentationManager.getIndentation());
        output.append(string);
        output.append('\n');
        return this;
    }

    public TextFormatter append(final String str) throws IOException {
        output.append(str);
        return this;
    }

    public void add(final String string) throws IOException {
        output.append(string);

    }

    public TextFormatter newLine() throws IOException {
        output.append('\n');
        return this;
    }

    public IOutput getOutput() {
        return output;
    }

}
