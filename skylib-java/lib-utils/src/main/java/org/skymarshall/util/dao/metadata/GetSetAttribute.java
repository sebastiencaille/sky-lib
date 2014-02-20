/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
/*
 * Copyright (c) 2008, Caille Sebastien
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the owner nor the names of its contributors may be 
 *    used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.skymarshall.util.dao.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This class allows accessing an attribute through its get/set methods
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 */
public class GetSetAttribute<T> extends AbstractAttributeMetaData<T> {

    protected final Method setter;
    protected final Method getter;

    public GetSetAttribute(final String name, final Method getter, final Method setter) {
        super(name, getter.getReturnType());
        this.getter = getter;
        this.setter = setter;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }

    @Override
    public Object getValueOf(final T _from) {
        try {
            return getter.invoke(_from);
        } catch (final Exception e) {
            throw new IllegalStateException("Unable to get object", e);
        }
    }

    @Override
    public void setValueOf(final T _to, final Object _value) {
        try {
            setter.invoke(_to, _value);
        } catch (final Exception e) {
            throw new IllegalStateException("Unable to set object", e);
        }
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
        return getter.getAnnotation(annotation);
    }

    @Override
    public Class<?> getDeclaringType() {
        return setter.getDeclaringClass();
    }

    @Override
    public Type getGenericType() {
        return getter.getGenericReturnType();
    }

    @Override
    public String getCodeName() {
        return getName();
    }

    @Override
    public String toString() {
        return name + '(' + type.getName() + ')';
    }

    @Override
    public int getModifier() {
        return getter.getModifiers();
    }
}
