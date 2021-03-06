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

#include <glibmm.h>
#include <gtkmm.h>

#include <binding_interface.hh>

namespace ch_skymarshall::gui::gtk4 {

class entry_binding:
		public component_binding<Glib::ustring> {
private:
	Gtk::Entry& m_entry;
	weak_ptr<component_link<Glib::ustring>> m_componentLink;
	sigc::connection m_connection;
	void on_changed_signal();
public:
	explicit entry_binding(Gtk::Entry& entry);
	virtual ~entry_binding();
	virtual void add_component_value_change_listener(weak_ptr<component_link<Glib::ustring>> _componentLink) final;
	virtual void remove_component_value_change_listener();
	virtual void set_component_value(property& _source, Glib::ustring _value);
	virtual source_ptr get_component();

	static shared_ptr<component_binding<Glib::ustring>> of(Gtk::Entry& entry) {
		return make_shared<entry_binding>(entry);
	}
};

class label_binding:
		public component_binding<Glib::ustring> {
private:
	Gtk::Label& m_label;
	void on_changed_signal();
public:
	explicit label_binding(Gtk::Label& entry);
	virtual ~label_binding();
	virtual void add_component_value_change_listener(weak_ptr<component_link<Glib::ustring>> _componentLink) final;
	virtual void remove_component_value_change_listener();
	virtual void set_component_value(property& _source, Glib::ustring _value);
	virtual source_ptr get_component();

	static shared_ptr<component_binding<Glib::ustring>> of(Gtk::Label& entry) {
		return make_shared<label_binding>(entry);
	}
};

}

#endif /* GTKBINDINGS_H_ */
