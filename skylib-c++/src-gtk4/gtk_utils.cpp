/*
 * utils.cpp
 *
 *  Created on: Feb 27, 2021
 *      Author: scaille
 */

#include <gtk_utils.hh>

namespace ch_skymarshall::gui::gtk4::utils {

void run_in_gtk(std::function<void()> _lambda) {
	Glib::signal_idle().connect_once(_lambda);
}

void wait_run_in_gtk(std::function<void()> _lambda) {
	std::packaged_task<void()> task(_lambda);
	std::future<void> f1 = task.get_future();
	Glib::signal_idle().connect_once([&task] { task(); });
	f1.wait();
}


}
