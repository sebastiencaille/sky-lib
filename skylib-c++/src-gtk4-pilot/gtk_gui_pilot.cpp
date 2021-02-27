/*
 * gtk_pilot.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include <utils.hh>
#include "gtk_entry_pilot.hh"
#include "gtk_gui_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

gtk_gui_pilot::gtk_gui_pilot(Gtk::Window *_window) :
		m_window(_window) {

}

shared_ptr<gtk_entry_pilot> gtk_gui_pilot::entry(string _name) {
	ustring uname(_name);
	Gtk::Widget *found = find(m_window->get_child(), uname);
	if (found == NULL) {
		throw gui_pilot_exception("No widget found: " + _name);
	}

	Gtk::Entry *entry = dynamic_cast<Gtk::Entry*>(found);
	if (entry == NULL) {
		throw gui_pilot_exception("Wrong widget type: " + _name);
	}
	return make_shared<gtk_entry_pilot>(entry);
}

Gtk::Widget* gtk_gui_pilot::find(Gtk::Widget *_widget, ustring &_name) {
	if (_name == _widget->get_name()) {
		return _widget;
	}
	if (_widget->get_first_child() == NULL) {
		return NULL;
	}
	for (Gtk::Widget *child = _widget->get_first_child(); child != NULL; child =
			child->get_next_sibling()) {
		Gtk::Widget *found = find(child, _name);
		if (found != NULL) {
			return found;
		}
	}
	return NULL;
}

gtk_gui_pilot::~gtk_gui_pilot() {
	// TODO Auto-generated destructor stub
}

}
