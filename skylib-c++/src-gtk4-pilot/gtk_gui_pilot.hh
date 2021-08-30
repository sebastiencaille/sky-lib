/*
 * gtk_pilot.h
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#ifndef GTKGUIPILOT_H_
#define GTKGUIPILOT_H_

#include <memory>
#include <gtkmm.h>

#include <utils.hh>
#include <types.hh>
#include "gtk_entry_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace std;
using Glib::ustring;

class gui_pilot_exception: public ch_skymarshall::gui::gui_exception {
public:
	gui_pilot_exception(const string_view &_msg) :
			gui_exception(_msg) {
	}

	~gui_pilot_exception() DESTR_WITH_LOG("~gui_pilot_exception")
};

class gtk_gui_pilot {

private:
	Gtk::Window *m_window;

	Gtk::Widget* find(Gtk::Widget *_widget, const ustring &_name);

public:
	gtk_gui_pilot(Gtk::Window *_window);
	virtual ~gtk_gui_pilot();

	Gtk::Widget* find_widget(const string &_name);
	shared_ptr<gtk_entry_pilot> entry(const string &_name);

};
}
#endif /* GTKGUIPILOT_H_ */

