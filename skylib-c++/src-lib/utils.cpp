/*
 * utils.cpp
 *
 *  Created on: Feb 27, 2021
 *      Author: scaille
 */

#include <utils.hh>

namespace ch_skymarshall::util {

void run_in_gtk(std::function<void()> _lambda) {
	Glib::signal_idle().connect([_lambda]() {
		_lambda();
		return false;
	});
}

}
