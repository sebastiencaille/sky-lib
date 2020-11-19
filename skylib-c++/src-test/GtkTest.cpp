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
 * GtkTest.cpp
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */
#include <vector>
#include <gtkmm.h>
#include <iostream>

#include "controller_property.hh"
#include "binding_interface.hh"
#include "input_error_property_impl.hh"
#include "int_converters.hh"
#include "glib_converter.hh"
#include "gtk_bindings.hh"

#include <pangomm/attrlist.h>

#include "list_model.hh"

using namespace ch_skymarshall::gui;
using namespace ch_skymarshall::gui::converters;
using namespace ch_skymarshall::gui::glib;
using namespace ch_skymarshall::gui::gtk;

using namespace std;
using namespace __gnu_cxx;

class HelloWorld: public Gtk::Window {

private:

	class TestStringPropertyListener {

		int m_i;

	public:
		explicit TestStringPropertyListener(int _i) :
				m_i(_i) {
		}

		void propertyChanged(source_ptr _source, const string &_name,
				const void *_oldValue, const void *_newValue) const {
			cout << " TestStringPropertyListener " << m_i << " - "
					<< *(string*) _oldValue << " -> " << *(string*) _newValue
					<< endl;
		}

	};

	class dep_test: public binding_chain_dependency {

		TestStringPropertyListener m_testListener = TestStringPropertyListener(
				1);
		shared_ptr<property_listener_dispatcher> m_listener;
		shared_ptr<binding_chain_controller> m_controller;

	public:
		dep_test() :
				m_listener(
						make_shared<property_listener_dispatcher>(
								[this](source_ptr source, const string &name,
										const void *oldValue,
										const void *newValue) {
									this->m_testListener.propertyChanged(source,
											name, oldValue, newValue);
								})) {
		}

		void register_dep(
				shared_ptr<binding_chain_controller> _controller) final {
			m_controller = _controller;
			_controller->get_property().add_listener(m_listener);
		}

		void unbind() final {
			m_controller->get_property().remove_listener(m_listener);
		}

		static shared_ptr<binding_chain_dependency> of() {
			return make_shared<dep_test>();
		}

	};
public:
	HelloWorld();

	void init(controller_property<string> &_testProperty1,
			controller_property<int> &_testProperty2,
			shared_ptr<input_error_property> _errorProperty);

	~HelloWorld() override;

	void apply_action(property_group_actions _action,
			const property *_property);

private:
	//Signal handlers:
	void on_button_clicked();

	//Member widgets:
	Gtk::Entry m_entry;
	Gtk::Label m_label;
	Gtk::Entry m_intEntry;
	Gtk::Label m_intLabel;
	Gtk::Label m_error;
	Gtk::Box m_box;

	list<shared_ptr<binding_chain_controller>> m_bindings;
};

HelloWorld::HelloWorld() :
		m_box(Gtk::ORIENTATION_VERTICAL) {
}

void HelloWorld::init(controller_property<string> &_testProperty1,
		controller_property<int> &_testProperty2,
		shared_ptr<input_error_property> _errorProperty) {
	// Sets the border width of the window.
	set_border_width(10);

	add(m_box);

	m_bindings.push_back(
			_testProperty1.bind(string_to_ustring::of())->bind(
					entry_binding::of(m_entry))->add_dependency(
					dep_test::of()));
	m_box.pack_start(m_entry);

	shared_ptr<action_dependency<HelloWorld>> dep = make_shared<
			action_dependency<HelloWorld>>(&_testProperty1,
			[this](property_group_actions group, const property *action) {
				this->apply_action(group, action);
			});

	m_bindings.push_back(
			_testProperty1.bind(string_to_ustring::of())->bind(
					label_binding::of(m_label))->add_dependency(dep));
	m_box.pack_start(m_label);

	m_bindings.push_back(
			_testProperty2.bind(int_to_string::of())->bind(
					string_to_ustring::of())->bind(
					entry_binding::of(m_intEntry)));
	m_box.pack_start(m_intEntry);
	m_bindings.push_back(
			_testProperty2.bind(int_to_string::of())->bind(
					string_to_ustring::of())->bind(
					label_binding::of(m_intLabel)));
	m_box.pack_start(m_intLabel);

	m_bindings.push_back(
			_errorProperty->bind(logic_error_to_string::of())->bind(
					string_to_ustring::of())->bind(
					label_binding::of(m_error)));
	m_box.pack_start(m_error);

	Pango::Attribute redText = Pango::Attribute::create_attr_foreground(0xefef,
			0x2929, 0x2929);
	Pango::AttrList atrlist;
	atrlist.insert(redText);
	m_error.set_attributes(atrlist);

	m_label.show();
	m_entry.show();
	m_intEntry.show();
	m_intLabel.show();
	m_error.show();
	m_box.show();

}

HelloWorld::~HelloWorld() {
	for (shared_ptr<binding_chain_controller> ctrl : m_bindings) {
		ctrl->unbind();
	}
	m_bindings.clear();
}

void HelloWorld::apply_action(property_group_actions _action,
		const property *_property) {
	switch (_action) {
	case BEFORE_FIRE:
		cout << "BEFORE: " << _property->name() << endl;
		break;
	case AFTER_FIRE:
		cout << "AFTER: " << _property->name() << endl;
		break;
	default:
		break;
	}
}

void HelloWorld::on_button_clicked() {
	std::cout << "Hello World" << std::endl;
}

using int_model = list_model<int>;

int main(int argc, char *argv[]) {

	int_model::view_ptr view(int_model::sorted([](const int i1, const int i2) {
			return i1 - i2;
		}));

	int_model int_list(view);
	int_list.insert(2);
	int_list.insert(1);
	cout << int_list.get_element_at(0) << " " << int_list.get_element_at(1)
			<< std::endl;

	Glib::RefPtr<Gtk::Application> app = Gtk::Application::create(argc, argv,
			"org.gtkmm.example");

	property_manager manager;
	shared_ptr<input_error_property> errorProperty(make_shared<input_error_property>(string("Errors"), manager));

	controller_property<string> testProperty1(string("TestProp1"), manager,
			string(""), errorProperty);

	controller_property<int> testProperty2("TestProp2", manager, 0,
			errorProperty);

	HelloWorld helloworld;
	helloworld.init(testProperty1, testProperty2, errorProperty);

	testProperty2.set(NULL, 1);
	return app->run(helloworld);
}

