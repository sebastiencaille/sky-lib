//! utils.rs
//!
//! Rust port of `utils.hh`.
//!
//! Original author: scaille (Jan 24, 2020)

use std::cell::RefCell;
use std::rc::{Rc, Weak};

/// Lazily fetches or allocates a value cached behind a `Weak` pointer.
pub fn with_lazy<T, O: ?Sized>(
    lazy: &RefCell<Weak<T>>,
    _owner: &Weak<O>,
    allocator: impl FnOnce() -> Rc<T>,
) -> Rc<T> {
    if let Some(existing) = lazy.borrow().upgrade() {
        return existing;
    }

    let listener = allocator();
    *lazy.borrow_mut() = Rc::downgrade(&listener);
    listener
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn with_lazy_caches_across_calls() {
        let lazy: RefCell<Weak<i32>> = RefCell::new(Weak::new());
        let owner: Weak<()> = Weak::new();

        let mut allocations = 0;
        let a = with_lazy(&lazy, &owner, || {
            allocations += 1;
            Rc::new(42)
        });
        let b = with_lazy(&lazy, &owner, || {
            allocations += 1;
            Rc::new(42)
        });

        assert!(Rc::ptr_eq(&a, &b));
        assert_eq!(allocations, 1);
    }

    #[test]
    fn with_lazy_reallocates_once_dropped() {
        let lazy: RefCell<Weak<i32>> = RefCell::new(Weak::new());
        let owner: Weak<()> = Weak::new();

        let a = with_lazy(&lazy, &owner, || Rc::new(1));
        drop(a);
        let b = with_lazy(&lazy, &owner, || Rc::new(2));
        assert_eq!(*b, 2);
    }
}
