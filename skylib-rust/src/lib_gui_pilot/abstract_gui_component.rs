//! abstract_gui_component.rs
//!
//! Rust port of `abstract_gui_component.hh` + `.cpp` (originally
//! `AbstractComponent.h`/`.cpp`).
//!
//! Original author: scaille (Feb 26, 2021)

use std::time::{Duration, Instant};

/// Port of `polling` — wraps a boolean predicate function.
pub struct Polling {
    polling_function: Box<dyn Fn() -> bool>,
}

impl Polling {
    pub fn new(f: impl Fn() -> bool + 'static) -> Self {
        Self {
            polling_function: Box::new(f),
        }
    }

    pub fn polling_function(&self) -> &dyn Fn() -> bool {
        &*self.polling_function
    }
}

/// Port of `abstract_gui_component`.
///
/// C++ makes `wait`/`executePolling` `protected virtual`, overridden by
/// `abstract_gtk_component`. Rust's trait default-method + override is the
/// direct match: implementors only need to override [`execute_polling`],
/// [`wait`]'s default busy-loop calls through it exactly like the base
/// class's `wait()` did.
///
/// Note: like the original, this default `wait` busy-polls with no sleep
/// between attempts — kept faithful rather than "improved", since
/// `executePolling` overrides (see `abstract_gtk_component`) dispatch onto
/// the GTK main loop per iteration, which already throttles the loop in
/// practice.
pub trait AbstractGuiComponent {
    /// Port of `executePolling` — default just calls the predicate.
    fn execute_polling(&self, polling: &Polling) -> bool {
        (polling.polling_function())()
    }

    /// Port of `wait`.
    fn wait(&self, polling: &Polling, duration: Duration, on_failure: impl FnOnce(&Polling)) {
        let start = Instant::now();
        while start.elapsed() < duration {
            if self.execute_polling(polling) {
                return;
            }
        }
        on_failure(polling);
    }
}
