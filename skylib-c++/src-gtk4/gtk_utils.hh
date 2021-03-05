#ifndef UTILS_HH_
#define UTILS_HH_

#include <future>

#include <glibmm.h>

namespace ch_skymarshall::gui::gtk4::utils {

void run_in_gtk(std::function<void()> _lambda);

template<typename T_return>
T_return get_run_in_gtk(std::function<T_return()> _lambda) {
	std::packaged_task<T_return()> task(_lambda);
	std::future<T_return> f1 = task.get_future();
	Glib::signal_idle().connect_once([&task] { task(); });
	return f1.get();
}

void wait_run_in_gtk(std::function<void()> _lambda);

}

#endif
