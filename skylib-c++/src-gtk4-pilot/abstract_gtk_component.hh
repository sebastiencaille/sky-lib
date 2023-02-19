/*
 * AbstractGtkComponent.h
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#ifndef ABSTRACTGTKCOMPONENT_H_
#define ABSTRACTGTKCOMPONENT_H_

#include <memory>
#include <gtkmm.h>

#include "abstract_gui_component.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace ch_skymarshall::gui::pilot;

class gtk_gui_pilot;

class abstract_gtk_component: public abstract_gui_component {

protected:
	gtk_gui_pilot* const m_gui_pilot;
	string const m_name;

	bool isEditable(Gtk::Widget *widget);

public:
	abstract_gtk_component(gtk_gui_pilot* _gui_pilot, const string &_name);
	~abstract_gtk_component() override;

	bool executePolling(polling &_polling) override;
};
}

#endif /* ABSTRACTGTKCOMPONENT_H_ */
