//! property_listener.rs
//!
//! Rust port of `property_listener.hh` (originally `PropertyListener.hh`).
//!
//! Original author: scaille (Apr 4, 2012)
//!
//! Depends on the sibling ports `types.rs` (for `SourcePtr` / `Property`)
//! and `utils.rs` (for `with_lazy`).

use std::any::Any;
use std::cell::RefCell;
use std::rc::{Rc, Weak};

use crate::lib_properties::{Property, PropertyListener, SourcePtr};
use crate::utils::memory::with_lazy;

/// Untyped reference to a property listener.
///
/// C++ used `void*` here. A raw pointer is kept for API parity, but
/// idiomatic Rust code should generally prefer passing around
/// `Rc<dyn PropertyListener>` / `Weak<dyn PropertyListener>` instead of
/// this alias.
pub type PropertyListenerRef = *mut ();

/// Signature of the C++ `fire_function` alias
/// (`function<void (source_ptr, const string_view&, const void*, const void*)>`).
pub type FireFn = dyn Fn(SourcePtr, &str, *const (), *const ());

/// Signature of the C++ `before_after_function` alias
/// (`function<void (source_ptr, property*)>`).
pub type BeforeAfterFn = dyn Fn(SourcePtr, &dyn Property);


/// Container bundling an instance with its callback(s), so a listener can
/// be dispatched against the correct instance/method pair.
///
/// Port of `property_listener_dispatcher`.
pub struct PropertyListenerDispatcher {
    /// Keeps the owner alive for as long as this listener is valid —
    /// equivalent to `shared_ptr<void> m_owner` in the original, which
    /// stored the result of locking the caller-supplied `weak_ptr<void>`.
    _owner: Option<Rc<dyn Any>>,
    func_fire: Option<Rc<FireFn>>,
    func_before: Option<Rc<BeforeAfterFn>>,
    func_after: Option<Rc<BeforeAfterFn>>,
}

impl PropertyListenerDispatcher {
    /// Port of the `fire_function`-only constructor.
    pub fn new_fire(owner: &Weak<dyn Any>, fire_function: Rc<FireFn>) -> Self {
        Self {
            _owner: owner.upgrade(),
            func_fire: Some(fire_function),
            func_before: None,
            func_after: None,
        }
    }

    /// Port of the before/after-function constructor.
    pub fn new_before_after(
        owner: &Weak<dyn Any>,
        before_fire_function: Rc<BeforeAfterFn>,
        after_fire_function: Rc<BeforeAfterFn>,
    ) -> Self {
        Self {
            _owner: owner.upgrade(),
            func_fire: None,
            func_before: Some(before_fire_function),
            func_after: Some(after_fire_function),
        }
    }

    /// Port of the "rebind to a new owner, copy callbacks from another
    /// dispatcher" constructor.
    pub fn from_other(owner: &Weak<dyn Any>, other: &PropertyListenerDispatcher) -> Self {
        Self {
            _owner: owner.upgrade(),
            func_fire: other.func_fire.clone(),
            func_before: other.func_before.clone(),
            func_after: other.func_after.clone(),
        }
    }

    /// Port of the `ofLazy` overload taking only a `fire_function`.
    pub fn of_lazy_fire(
        lazy: &RefCell<Weak<PropertyListenerDispatcher>>,
        owner: &Weak<dyn Any>,
        fire_function: Rc<FireFn>,
    ) -> Rc<PropertyListenerDispatcher> {
        let owner_for_alloc = owner.clone();
        with_lazy(lazy, owner, move || {
            Rc::new(PropertyListenerDispatcher::new_fire(
                &owner_for_alloc,
                fire_function,
            ))
        })
    }

    /// Port of the `ofLazy` overload taking before/after functions.
    pub fn of_lazy_before_after(
        lazy: &RefCell<Weak<PropertyListenerDispatcher>>,
        owner: &Weak<dyn Any>,
        before_fire_function: Rc<BeforeAfterFn>,
        after_fire_function: Rc<BeforeAfterFn>,
    ) -> Rc<PropertyListenerDispatcher> {
        let owner_for_alloc = owner.clone();
        with_lazy(lazy, owner, move || {
            Rc::new(PropertyListenerDispatcher::new_before_after(
                &owner_for_alloc,
                before_fire_function,
                after_fire_function,
            ))
        })
    }
}

impl PropertyListener for PropertyListenerDispatcher {
    fn fire(&self, source: SourcePtr, name: &str, old_value: *const (), new_value: *const ()) {
        if let Some(f) = &self.func_fire {
            f(source, name, old_value, new_value);
        }
    }

    fn before_change(&self, source: SourcePtr, property: &dyn Property) {
        if let Some(f) = &self.func_before {
            f(source, property);
        }
    }

    fn after_change(&self, source: SourcePtr, property: &dyn Property) {
        if let Some(f) = &self.func_after {
            f(source, property);
        }
    }
}

// The original `~property_listener_dispatcher()` body only existed to
// support the `DESTR_WITH_LOG` debug macro (an optional destructor log
// line, compiled out entirely unless `DEBUG_DESTR` was defined). Rust has
// no need for an explicit destructor here — `Rc`/`Weak` fields clean
// themselves up automatically — but if that debug trace is wanted, it can
// be reinstated with:
//
// impl Drop for PropertyListenerDispatcher {
//     fn drop(&mut self) {
//         #[cfg(feature = "destr-log")]
//         println!("~property_listener_dispatcher");
//     }
// }

#[cfg(test)]
mod tests {
    use super::*;
    use std::cell::Cell;
    use std::rc::Rc;

    #[test]
    fn fire_invokes_callback_when_present() {
        let owner: Rc<dyn Any> = Rc::new(());
        let called = Rc::new(Cell::new(false));
        let called_clone = called.clone();

        let fire_fn: Rc<FireFn> = Rc::new(move |_src, name, _old, _new| {
            assert_eq!(name, "size");
            called_clone.set(true);
        });

        let dispatcher =
            PropertyListenerDispatcher::new_fire(&Rc::downgrade(&owner), fire_fn);

        dispatcher.fire(std::ptr::null(), "size", std::ptr::null(), std::ptr::null());
        assert!(called.get());
    }

    #[test]
    fn of_lazy_fire_caches_the_dispatcher() {
        let owner: Rc<dyn Any> = Rc::new(());
        let owner_weak = Rc::downgrade(&owner);
        let lazy: RefCell<Weak<PropertyListenerDispatcher>> = RefCell::new(Weak::new());

        let fire_fn: Rc<FireFn> = Rc::new(|_src, _name, _old, _new| {});

        let a = PropertyListenerDispatcher::of_lazy_fire(&lazy, &owner_weak, fire_fn.clone());
        let b = PropertyListenerDispatcher::of_lazy_fire(&lazy, &owner_weak, fire_fn);

        assert!(Rc::ptr_eq(&a, &b));
    }
}
