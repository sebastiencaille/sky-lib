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
 * TestCompile.cpp
 *
 *  Created on: Apr 6, 2012
 *      Author: scaille
 */

#include <vector>
#include <algorithm>

#include <iostream>

#include <list_model.hh>

using namespace org_skymarshall_util_hmi;
using namespace std;
using namespace __gnu_cxx;

class my_data {
private:
	int m_id;
	string m_value;
public:
	my_data(int _id, const char* _value) :
					m_id(_id),
					m_value(string(_value)) {
		cout << "Creating" << m_id << hex << this << endl;
	}

	my_data(my_data& data) :
					m_id(data.m_id),
					m_value(data.m_value) {

	}

	~my_data() {
		cout << "Deleting " << m_id << hex << endl;
	}

	int getId() const {
		return m_id;
	}

	void setId(int _id) {
		m_id = _id;
	}

	string& getValue() {
		return m_value;
	}

	my_data& operator=(const my_data& _data) {
		m_id = _data.m_id;
		return *this;
	}

};

typedef list_model<my_data*> my_data_list;
typedef list_model_view<my_data*> my_data_list_view;

class my_data_comparator:
		public my_data_list_view::comparator {
public:
	virtual int compare(my_data* const _o1, my_data* const _o2) const {
		return _o1->getId() - _o2->getId();
	}
};

class my_data_comparator_const:
		public my_data_list_view::comparator {
public:
	int compare(my_data const* const & _o1, my_data const* const & _o2) const {
		return _o1->getId() - _o2->getId();
	}
};

class my_data_tunings:
		public my_data_list::object_tunings {
public:
	bool equals(my_data* _val1, my_data* _val2) const {
		return _val1->getId() == _val2->getId();
	}

	string str(my_data* _value) const {
		stringstream ss;
		ss << _value->getId();
		return ss.str();
	}

};

int main(int argc, char **argv) {
	my_data_comparator comparator;
	my_data_list model(list_model<my_data*>::sorted(&comparator));
	model.set_tunings(model.make_ptr(new my_data_tunings()));
	my_data_list sub_model(model);

	model.insert(new my_data(3, "value3"));
	model.insert(new my_data(2, "value2"));
	model.insert(new my_data(1, "value1"));
	cout << "--> 1 2 3" << endl;
	for (int i = 0; i < sub_model.get_size(); i++) {
		cout << sub_model[i]->getValue() << endl;
	}

	my_data* sample = new my_data(2, "");

	my_data* edited;
	model.find_for_edition(sample, &edited);
	edited->setId(4);
	model.stop_editing_value();

	cout << "--> 1 3 2" << endl;
	for (int i = 0; i < sub_model.get_size(); i++) {
		cout << sub_model[i]->getValue() << endl;
	}

	my_data* removed;
	sample->setId(4);
	model.remove(sample, &removed);

	delete sample;
	delete removed;

	cout << "--> 1 3" << endl;
	for (int i = 0; i < sub_model.get_size(); i++) {
		cout << sub_model[i]->getValue() << endl;
	}
	return 0;
}

