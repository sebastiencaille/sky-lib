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

#include "GtkBindings.hh"

namespace org_skymarshall_util_hmi_gtk {

using namespace org_skymarshall_util_hmi;

EntryBinding::EntryBinding(Gtk::Entry& _entry) :
				m_entry(_entry) {
}

EntryBinding::~EntryBinding() {
}

void EntryBinding::on_changed_signal() {
	Glib::ustring text(m_entry.get_text());
	m_converter->set_value_from_component(get_component(), text);
}

void EntryBinding::add_component_value_change_listener(converter_to<Glib::ustring>* _converter) {
	m_converter = _converter;
	m_entry.signal_changed().connect(sigc::mem_fun(*this, &EntryBinding::on_changed_signal));
}

void EntryBinding::set_component_value(property* _source, Glib::ustring _value) {
	m_entry.set_text(_value);
}

void* EntryBinding::get_component() {
	return (void*) &m_entry;
}

LabelBinding::LabelBinding(Gtk::Label& _label) :
				m_label(_label) {
}

LabelBinding::~LabelBinding() {
}

void LabelBinding::add_component_value_change_listener(converter_to<Glib::ustring>* _converter) {
}

void LabelBinding::set_component_value(property* _source, Glib::ustring _value) {
	m_label.set_text(_value);
}

void* LabelBinding::get_component() {
	return (void*) &m_label;
}

}
