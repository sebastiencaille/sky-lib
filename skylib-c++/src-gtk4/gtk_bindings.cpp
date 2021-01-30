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
 * GtkBindings.cpp
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */
#include "../src-gtk4/gtk_bindings.hh"

#include <iostream>

#include "utils.hh"

namespace ch_skymarshall::gui::gtk {

entry_binding::entry_binding(Gtk::Entry &_entry) :
		m_entry(_entry) {
}

entry_binding::~entry_binding() {
	DESTR_LOG("~entry_binding");
	remove_component_value_change_listener();
}

void entry_binding::on_changed_signal() {
	Glib::ustring text(m_entry.get_text());
	m_componentLink.lock()->set_value_from_component(get_component(), text);
}

void entry_binding::add_component_value_change_listener(
		weak_ptr<component_link<Glib::ustring>> _componentLink) {
	if (!m_connection.connected()) {
		m_componentLink = _componentLink;
		m_connection = m_entry.signal_changed().connect(
				sigc::mem_fun(*this, &entry_binding::on_changed_signal));
	}
}

void entry_binding::remove_component_value_change_listener() {
	m_connection.disconnect();
}

void entry_binding::set_component_value(property &_source,
		Glib::ustring _value) {
	if (m_entry.get_text() != _value) {
		m_entry.set_text(_value);
	}
}

source_ptr entry_binding::get_component() {
	return (source_ptr) &m_entry;
}

label_binding::label_binding(Gtk::Label &_label) :
		m_label(_label) {
}

label_binding::~label_binding()
DESTR_WITH_LOG("~label_binding")

void label_binding::add_component_value_change_listener(
		weak_ptr<component_link<Glib::ustring>> _componentLink) {
}

void label_binding::remove_component_value_change_listener() {
}

void label_binding::set_component_value(property &_source,
		Glib::ustring _value) {
	m_label.set_text(_value);
}

source_ptr label_binding::get_component() {
	return (source_ptr) &m_label;
}

}