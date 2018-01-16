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

#ifndef BINDINGINTERFACE_HH_
#define BINDINGINTERFACE_HH_

#include <string>
#include "typed_property.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

typedef logic_error* logic_error_ptr;

class error_notifier {
public:
	virtual ~error_notifier() {
	}
	virtual void set_error(const void *_source, const logic_error_ptr _e) = 0;
};


/**
 * Converters
 */

template<class _Ft, class _Tt> class binding_converter {
public:
	virtual const _Ft convert_component_value_to_property_value(
			const _Tt _componentValue) throw (logic_error_ptr) = 0;
	virtual const _Tt convert_property_value_to_component_value(
			const _Ft _propertyValue) throw (logic_error_ptr) = 0;

protected:
	virtual ~binding_converter() {
	}

};

class logic_error_to_string: public binding_converter<logic_error_ptr, string> {
public:
	const logic_error_ptr convert_component_value_to_property_value(
			const string _componentValue) throw (logic_error_ptr) {
		// nonsense
		return NULL;
	}
	const string convert_property_value_to_component_value(
			const logic_error_ptr _propertyValue) throw (logic_error_ptr) {
		return string(_propertyValue->what());
	}
	~logic_error_to_string() {

	}
};

/** Binding to component */

template<class _CT> class component_link {
public:
	virtual void set_value_from_component(void* _component,
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

class binding_chain_dependency;

class binding_chain_controller {
public:
	virtual void attach() = 0;

	virtual void detach() = 0;

	virtual property& get_property() = 0;

	virtual void unbind() = 0;

	virtual binding_chain_controller* add_dependency(
			binding_chain_dependency* dependency) = 0;

	virtual ~binding_chain_controller() {

	}
};

class binding_chain_dependency {
public:
	virtual void register_dep(binding_chain_controller* chain) = 0;

	virtual void unbind() = 0;

	virtual ~binding_chain_dependency() {
	}
};

/** Actions */

enum property_group_actions {
	BEFORE_FIRE, AFTER_FIRE
};

class action {
public:
	virtual ~action() {
	}

	virtual void apply(property_group_actions _action,
			const property* _property) = 0;

};

template<class _T> class action_func_type: public action {
public:
	typedef void (_T::*action_function)(property_group_actions _action,
			const property* _property);

private:
	action_function const m_action;
	_T* const m_object;

public:
	action_func_type(_T* _object, action_function _action) :
		m_action(_action), m_object(_object) {
	}

	void apply(property_group_actions _action, const property* _property) {
		(m_object->*m_action)(_action, _property);
	}

};

template<class _T> class action_dependency: public binding_chain_dependency {
private:
	property* m_targetProperty;
	action_func_type<_T>* m_action;
	property_listener_func_type<action_dependency<_T>>* m_listener = NULL;

	void action_before(const void* caller, const property* _property) {
		m_action->apply(BEFORE_FIRE, _property);
	}

	void action_after(const void* caller, const property* _property) {
		m_action->apply(AFTER_FIRE, _property);
	}

public:

	action_dependency(property* _targetProperty, action_func_type<_T>* _action) :
			m_targetProperty(_targetProperty), m_action(_action) {
	}

	void register_dep(binding_chain_controller* _chain) {
		m_listener = new property_listener_func_type<action_dependency<_T>>(
				*this, &action_dependency<_T>::action_before,
				&action_dependency<_T>::action_after);
		m_targetProperty->add_listener(m_listener);
	}

	virtual void unbind() {
		m_targetProperty->remove_listener(m_listener);
	}

	virtual ~action_dependency() {
	}
};
}
#endif /* BINDINGINTERFACE_HH_ */
