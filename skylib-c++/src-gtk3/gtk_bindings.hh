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
 * GtkBindings.h
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#ifndef GTKBINDINGS_H_
#define GTKBINDINGS_H_

#include <binding_interface.hh>
#include <glibmm.h>
#include <gtkmm.h>

namespace org_skymarshall_util_hmi_gtk {

using namespace org_skymarshall_util_hmi;

class entry_binding:
		public component_binding<Glib::ustring> {
private:
	Gtk::Entry& m_entry;
	component_link<Glib::ustring>* m_componentLink = NULL;
	void on_changed_signal();
public:
	entry_binding(Gtk::Entry& entry);
	virtual ~entry_binding();
	virtual void add_component_value_change_listener(component_link<Glib::ustring>* _componentLink);
	virtual void remove_component_value_change_listener();
	virtual void set_component_value(property& _source, Glib::ustring _value);
	virtual source_ptr get_component();
};

class label_binding:
		public component_binding<Glib::ustring> {
private:
	Gtk::Label& m_label;
	void on_changed_signal();
public:
	label_binding(Gtk::Label& entry);
	virtual ~label_binding();
	virtual void add_component_value_change_listener(component_link<Glib::ustring>* _componentLink);
	virtual void remove_component_value_change_listener();
	virtual void set_component_value(property& _source, Glib::ustring _value);
	virtual source_ptr get_component();
};

}

#endif /* GTKBINDINGS_H_ */
