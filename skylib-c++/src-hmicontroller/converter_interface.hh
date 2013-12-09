/*
 *Copyright (c) 2013 Sebastien Caille.
 *All rights reserved.
 *
 *Redistribution and use in source and binary forms are permitted
 *provided that the above copyright notice and this paragraph are
 *duplicated in all such forms and that any documentation,
 *advertising materials, and other materials related to such
 *distribution and use acknowledge that the software was developed
 *by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *from this software without specific prior written permission.
 *THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
/*
 * Converter.hh
 *
 *  Created on: Feb 19, 2012
 *      Author: scaille
 */

#ifndef CONVERTERINTERFACE_HH_
#define CONVERTERINTERFACE_HH_

#include <string>
#include <typed_property.hh>

namespace org_skymarshall_util_hmi {

using namespace std;

class error_notifier {
public:
	virtual ~error_notifier() {
	}
	virtual void set_error(const void *_source, void* _value) = 0;
};


/**
 * Object-side definition of the converter
 */
template<class _CT> class converter_to {

protected:
	virtual ~converter_to() {
	}

public:
	virtual void set_value_from_component(void * _source, _CT& _componentValue) = 0;

};

/**
 * Definition of component bindings
 */
template<class _CT> class component_binding {
protected:
	virtual ~component_binding() {
	}
public:

	virtual void add_component_value_change_listener(converter_to<_CT>* _converter) = 0;
	virtual void set_component_value(property* _source, _CT _value) = 0;
	virtual void* get_component() = 0;

};


}

#endif /* CONVERTERINTERFACE_HH_ */
