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
#include "gtk_entry_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace std;
using Glib::ustring;

class gui_pilot_exception: public std::exception {
private:
	const basic_string<char> m_msg;
public:
	gui_pilot_exception(const basic_string<char> _msg) :
			exception(), m_msg(_msg) {
	}

	~gui_pilot_exception()
	DESTR_WITH_LOG("~gui_pilot_exception")
};

class gtk_gui_pilot {

private:
	Gtk::Window *m_window;

	Gtk::Widget* find(Gtk::Widget *_widget, ustring &_name);

public:
	gtk_gui_pilot(Gtk::Window *_window);
	virtual ~gtk_gui_pilot();

	shared_ptr<gtk_entry_pilot> entry(string _name);

};
}
#endif /* GTKGUIPILOT_H_ */

