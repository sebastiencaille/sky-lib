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

#include <iostream>

#include "property_manager.hh"
#include "property.hh"



namespace org_skymarshall_util_hmi {

using namespace std;

property_manager::property_manager() {
}

void property_manager::add_listener(const string& _name, property_listener* _listener) {

	listeners_const_iter iter = m_propertyListeners.find(_name);
	listener_list_type* plist;
	if (iter == m_propertyListeners.end()) {
		plist = new list<property_listener*>();
		m_propertyListeners[_name] = plist;
	} else {
		plist = (*iter).second;
	}
	plist->push_back(_listener);
}

void property_manager::remove_listener(const string& _name, property_listener* _listener) {
	listeners_const_iter iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	listener_list_type* plist = (*iter).second;
	plist->remove(_listener);
	if (plist->empty()) {
		m_propertyListeners.erase(_name);
		delete plist;
	}
}

void property_manager::remove_listener(const string& _name, property_listener_ref _listener) {
	remove_listener(_name, (property_listener*) _listener);
}

void property_manager::fire_property_changed(source_ptr _source, const string& _name, const void* _oldValue,
		const void* _newValue) const {
	const listeners_const_iter iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	listener_list_type * plist = (*iter).second;
	listener_list_type::iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		property_listener* propertyListener = *listener;
		propertyListener->fire(_source, _name, _oldValue, _newValue);
	}
}

void property_manager::fire_before_property_changed(source_ptr _source, property* _property) const {
	const listeners_const_iter iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	listener_list_type * plist = (*iter).second;
	listener_list_type::iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		property_listener* propertyListener = *listener;
		propertyListener->before_change(_source, _property);
	}
}

void property_manager::fire_after_property_changed(source_ptr _source, property* _property) const {
	const listeners_const_iter iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	listener_list_type * plist = (*iter).second;
	listener_list_type::iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		property_listener* propertyListener = *listener;
		propertyListener->after_change(_source, _property);
	}
}

void property_manager::dump() const {
	for (listeners_const_iter liter = m_propertyListeners.begin(); liter != m_propertyListeners.end(); liter++) {

		cout << (*liter).first << ": " << endl;

		listener_list_type* plist = (*liter).second;
		listener_list_type::iterator listener;
		for (listener = plist->begin(); listener != plist->end(); listener++) {
			cout << "  " << hex << *listener << endl;
		}
	}

}

}
