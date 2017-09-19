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
#include "property.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

property::property(const string& _name, property_manager& _manager) :
		m_name(_name), m_manager(_manager) {
}

property::property(const char* _name, property_manager& _manager) :
		m_name(string(_name)), m_manager(_manager) {
}

property::~property() {

}

void property::attach() {
	m_attached = true;
}

const string& property::name() const {
	return m_name;
}

void property::add_listener(property_listener* _listener) {
	m_manager.add_listener(m_name, _listener);
}

void property::remove_listener(property_listener* _listener) {
	m_manager.remove_listener(m_name, _listener);
}

void property::remove_listener(property_listener_ref _listener) {
	m_manager.remove_listener(m_name, (property_listener*)_listener);
}


}
