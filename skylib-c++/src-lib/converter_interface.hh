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

class conversion_exception: logic_error {
	conversion_exception(const string& msg) :
			logic_error(msg) {
	}

};

/**
 * Converters
 */

template<class _FT, class _TT> class binding_converter {
public:
	virtual const _FT convert_component_value_to_property_value(
			const _TT _componentValue) = 0;
	virtual const _TT convert_property_value_to_component_value(
			const _FT _propertyValue) = 0;

protected:
	virtual ~binding_converter() {
	}

};

/** Binding to component */

template<class _CT> class component_link {
public:
	virtual void set_value_from_component(void* component,
			_CT _componentValue) = 0;
	virtual void reload_component_value() = 0;
	virtual void unbind() = 0;
protected:
	~component_link() {
	}

};

/**
 * Definition of component bindings
 */
template<class _CT> class component_binding {

public:

	virtual void add_component_value_change_listener(
			component_link<_CT>* _converter) = 0;

	virtual void remove_component_value_change_listener() = 0;


	virtual void set_component_value(property& _source, _CT _value) = 0;

	virtual void* get_component() = 0;

	virtual ~component_binding() {
	}
};

}

#endif /* CONVERTERINTERFACE_HH_ */