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

#include <GlibConverter.hh>
#include <GtkBindings.hh>

#include "controller_property.hh"
#include "input_error_property_impl.hh"

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
		TestStringPropertyListener(int _i) {
			m_i = _i;
		}

		void propertyChanged(const void* _source, const string& _name,
				const void* _oldValue, const void* _newValue) {
			cout << " TestStringPropertyListener " << m_i << " - "
					<< *(string*) _oldValue << " -> " << *(string*) _newValue
					<< endl;
		}

	};

	class dep_test: public binding_chain_dependency {

		TestStringPropertyListener m_testListener;
		property_listener_func_type<TestStringPropertyListener> m_listener;
		binding_chain_controller* m_controller;

	public:
		dep_test() :
				m_testListener(1), m_listener(m_testListener,
						&TestStringPropertyListener::propertyChanged), m_controller(
				NULL) {
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
	HelloWorld(controller_property<string>& _testProperty1);

	virtual ~HelloWorld();

	void apply_action(property_group_actions _action,
			const property* _property);

protected:
	//Signal handlers:
	void on_button_clicked();

	//Member widgets:
	Gtk::Entry m_entry;
	Gtk::Label m_label;
	Gtk::Box m_box;

	typedef list<binding_chain_controller*>::iterator binding_chain_controller_iter;
	list<binding_chain_controller*> m_bindings;
};

HelloWorld::HelloWorld(controller_property<string>& _testProperty1) :
		m_box(Gtk::ORIENTATION_VERTICAL) {
	// Sets the border width of the window.
	set_border_width(10);

	add(m_box);

	m_bindings.push_back(
			_testProperty1.bind(new string_to_ustring_converter())->bind(
					new entry_binding(m_entry))->add_dependency(
					new dep_test()));
	m_box.pack_start(m_entry);

	action_dependency<HelloWorld>* dep = new action_dependency<HelloWorld>(
			&_testProperty1,
			new action_func_type<HelloWorld>(this, &HelloWorld::apply_action));
	m_bindings.push_back(
			_testProperty1.bind(new string_to_ustring_converter())->bind(
					new label_binding(m_label))->add_dependency(dep));
	m_box.pack_start(m_label);

	m_label.show();
	m_entry.show();
	m_box.show();
}

HelloWorld::~HelloWorld() {
	for (binding_chain_controller_iter iter = m_bindings.begin();
			iter != m_bindings.end(); iter++) {
		(*iter)->unbind();
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

int main(int argc, char *argv[]) {

	Glib::RefPtr<Gtk::Application> app = Gtk::Application::create(argc, argv,
			"org.gtkmm.example");

	property_manager manager;
	input_error_property* errorProperty = new input_error_property(
			string("Errors"), manager);

	controller_property<string> testProperty1(string("TestProp1"), manager,
			string(""), errorProperty);
	HelloWorld helloworld(testProperty1);

	controller_property<int> testProperty2("TestProp2", manager, 0,
			errorProperty)		;
	testProperty2.set(NULL, 1);

	return app->run(helloworld);
}

