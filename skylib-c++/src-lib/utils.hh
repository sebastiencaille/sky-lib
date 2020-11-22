/*
 * utils.hh
 *
 *  Created on: Jan 24, 2020
 *      Author: scaille
 */

#ifndef UTILS_HH_
#define UTILS_HH_

#include <vector>

namespace ch_skymarshall::util {

#ifdef DEBUG_DESTR
#define DESTR_LOG(p) cout << p << endl;
#define DESTR_WITH_LOG(p) { DESTR_LOG(p); }
#endif

#ifndef DEBUG_DESTR
#define DESTR_LOG(p) (void)0
#define DESTR_WITH_LOG(p) = default;;
#endif

using std::vector;

template<class Input, class UnaryFunction>
constexpr UnaryFunction for_each_vector(vector<Input>& vec, UnaryFunction f) {
	typename vector<Input>::iterator iter;
	for (Input& input: vec) {
		f(input);
	}
	return f; // implicit move since C++11
}
}


#endif /* UTILS_HH_ */
