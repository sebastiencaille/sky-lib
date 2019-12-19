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
#include "input_error_property_impl.hh"
#include "int_converters.hh"
#include "glib_converter.hh"
#include "gtk_bindings.hh"

#include "list_model.hh"

using namespace org_skymarshall_util_hmi;
using namespace org_skymarshall_util_hmi_glib;
using namespace org_skymarshall_util_hmi_gtk;

using namespace std;
using namespace __gnu_cxx;

class HelloWorld: public Gtk::Window {

private:

	class TestStringPropertyListener {

		int m_i;

	public:
		TestStringPropertyListener(int _i):	m_i(_i) {
		}

		void propertyChanged(source_ptr _source, const string& _name,
				const void* _oldValue, const void* _newValue) {
			cout << " TestStringPropertyListener " << m_i << " - "
					<< *(string*) _oldValue << " -> " << *(string*) _newValue
					<< endl;
		}

	};

	class dep_test: public binding_chain_dependency {

		TestStringPropertyListener m_testListener;
		property_listener_dispatcher m_listener;
		binding_chain_controller* m_controller;

	public:
		dep_test() :
				m_testListener(1),
				m_listener([this] (source_ptr source, const string& name,
						const void* oldValue, const void* newValue)
						{ this->m_testListener.propertyChanged(source,name,oldValue, newValue);}),
				m_controller(NULL) {
		}

		void register_dep(binding_chain_controller *_controller) {
			m_controller = _controller;
			_controller->get_property().add_listener(&m_listener);
		}

		void unbind() {
			m_controller->get_property().remove_listener(&m_listener);
		}

	};
public:
	HelloWorld();

	void init(controller_property<string>& _testProperty1,
			controller_property<int>& _testProperty2,
			input_error_property& _errorProperty);

	virtual ~HelloWorld();

	void apply_action(property_group_actions _action, const property* _property);

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

	typedef list<binding_chain_controller*>::iterator binding_chain_controller_iter;
	list<binding_chain_controller*> m_bindings;
};

HelloWorld::HelloWorld() :
		m_box(Gtk::ORIENTATION_VERTICAL) {
}

void HelloWorld::init(controller_property<string>& _testProperty1,
		controller_property<int>& _testProperty2,
		input_error_property& _errorProperty) {
	// Sets the border width of the window.
	set_border_width(10);

	add(m_box);

	m_bindings.push_back(
			_testProperty1.bind(new string_to_ustring())->bind(
					new entry_binding(m_entry))->add_dependency(
					new dep_test()));
	m_box.pack_start(m_entry);

	action_dependency<HelloWorld>* dep = new action_dependency<HelloWorld>(
			&_testProperty1, [this](property_group_actions group, const property* action) { this->apply_action(group, action); } );

	m_bindings.push_back(
			_testProperty1.bind(new string_to_ustring())->bind(
					new label_binding(m_label))->add_dependency(dep));
	m_box.pack_start(m_label);

	m_bindings.push_back(
			_testProperty2.bind(new int_to_string())->bind(
					new string_to_ustring())->bind(
					new entry_binding(m_intEntry)));
	m_box.pack_start(m_intEntry);
	m_bindings.push_back(
			_testProperty2.bind(new int_to_string())->bind(
					new string_to_ustring())->bind(
					new label_binding(m_intLabel)));
	m_box.pack_start(m_intLabel);

	m_bindings.push_back(
			_errorProperty.bind(new logic_error_to_string())->bind(
					new string_to_ustring())->bind(
					new label_binding(m_error)));
	m_box.pack_start(m_error);


	m_label.show();
	m_entry.show();
	m_intEntry.show();
	m_intLabel.show();
	m_error.show();
	m_box.show();

}

HelloWorld::~HelloWorld() {
	for (binding_chain_controller_iter iter = m_bindings.begin();
			iter != m_bindings.end(); iter++) {
		(*iter)->unbind();
		delete *iter;
	}
	m_bindings.clear();
}

void HelloWorld::apply_action(property_group_actions _action,
		const property* _property) {
	switch (_action) {
	case BEFORE_FIRE:
		cout << "BEFORE: " << _property->name() << endl;
		break;
	case AFTER_FIRE:
		cout << "AFTER: " << _property->name() << endl;
		break;
	}
}

void HelloWorld::on_button_clicked() {
	std::cout << "Hello World" << std::endl;
}

typedef list_model<int> int_model;

int main(int argc, char *argv[]) {

	int_model int_list(int_model::sorted([](int i1, int i2) { return i1 - i2; }));
	int_list.insert(2);
	int_list.insert(1);
	cout << int_list.get_element_at(0) << " " << int_list.get_element_at(1) << std::endl;

	Glib::RefPtr<Gtk::Application> app = Gtk::Application::create(argc, argv,
			"org.gtkmm.example");

	property_manager manager;
	input_error_property errorProperty(string("Errors"), manager);

	controller_property<string> testProperty1(string("TestProp1"), manager,
			string(""), &errorProperty);

	controller_property<int> testProperty2("TestProp2", manager, 0,
			&errorProperty);
	HelloWorld helloworld;
	helloworld.init(testProperty1, testProperty2, errorProperty);

	testProperty2.set(NULL, 1);
	return app->run(helloworld);
}

