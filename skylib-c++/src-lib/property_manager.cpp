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
#include <memory>

#include "property_manager.hh"
#include "property.hh"
#include "utils.hh"


namespace ch_skymarshall::gui {

using namespace std;

property_manager::property_manager() = default;

property_manager::~property_manager() DESTR_WITH_LOG("~property_manager")

void property_manager::add_listener(const string& _name, shared_ptr<property_listener> _listener) {
	listeners_const_iter iter = m_propertyListeners.find(_name);
	shared_ptr<listener_list_type> plist;
	if (iter == m_propertyListeners.end()) {
		plist = make_shared<listener_list_type>();
		m_propertyListeners[_name] = plist;
	} else {
		plist = (*iter).second;
	}
	plist->push_back(_listener);
}

void property_manager::remove_listener(const string& _name, weak_ptr<property_listener> _listener) {
	listeners_const_iter iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	shared_ptr<listener_list_type> plist = (*iter).second;
	plist->remove(_listener.lock());
	if (plist->empty()) {
		m_propertyListeners.erase(_name);
	}
}

void property_manager::remove_listener(const string& _name, property_listener_ref _listener) {
	remove_listener(_name, (property_listener*) _listener);
}

void property_manager::remove_listeners(const string& _name) {
	m_propertyListeners.erase(_name);
}

void property_manager::fire_property_changed(source_ptr _source, const string& _name, const void* _oldValue,
		const void* _newValue) const {
	const listeners_const_iter iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	shared_ptr<listener_list_type> const plist((*iter).second);
	listener_list_type::const_iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->fire(_source, _name, _oldValue, _newValue);
	}
}

void property_manager::fire_before_property_changed(source_ptr _source, property* _property) const {
	const listeners_const_iter iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	shared_ptr<listener_list_type> const plist((*iter).second);
	listener_list_type::const_iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->before_change(_source, _property);
	}
}

void property_manager::fire_after_property_changed(source_ptr _source, property* _property) const {
	const listeners_const_iter iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	shared_ptr<listener_list_type> const plist((*iter).second);
	listener_list_type::const_iterator listener;
	for (listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->after_change(_source, _property);
	}
}

void property_manager::dump() const {
	for (const std::pair<string, shared_ptr<listener_list_type>>& mapListener: m_propertyListeners) {
		shared_ptr<listener_list_type const> plist = mapListener.second;
		listener_list_type::const_iterator listener;
		for (listener = plist->begin(); listener != plist->end(); listener++) {
			cout << "  " << hex << *listener << endl;
		}
	}

}

}
