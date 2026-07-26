//! gtk_utils.rs
//!
//! Rust port of `gtk_utils.hh` + `gtk_utils.cpp` (originally
//! `utils.hh`/`.cpp` under the `gtk4::utils` namespace).
//!
//! Original author: scaille (Feb 27, 2021)
//!
//! ## Design notes
//!
//! C++ used `Glib::signal_idle().connect_once(lambda)` to schedule work on
//! the GTK main loop, and `std::packaged_task` + `std::future` to block a
//! *different* thread until that scheduled work finished. The direct Rust
//! equivalents are [`glib::idle_add_once`] and a one-shot
//! [`std::sync::mpsc`] channel — but two details need explicit handling
//! that C++'s looser thread-safety typing let the original gloss over:
//!
//! 1. **`Send`.** `g_idle_add` (which `Glib::signal_idle()` wraps) is
//!    genuinely safe to call from any thread — the callback always runs
//!    later, on the thread that owns the target `MainContext`. `gtk4-rs`'s
//!    `idle_add_once` encodes that by requiring `F: Send`, since the
//!    closure crosses a thread boundary conceptually. But GTK widgets
//!    (`GObject`s) aren't `Send` — their refcounting isn't atomic — so a
//!    closure capturing e.g. a `gtk4::Entry` fails that bound even though
//!    it's actually fine: the closure is *moved*, not shared, and only
//!    ever touched by the single thread that eventually runs it. This
//!    file's [`AssertSend`] wrapper asserts that explicitly, the same way
//!    the original implicitly trusted GLib's own thread-safety guarantee.
//! 2. **Borrowed data.** `abstract_gtk_component::executePolling` in the
//!    original captures local references (`[&_polling, this]`) into a
//!    lambda passed to `get_run_in_gtk`, relying on the call being
//!    *synchronous* (`future::get()` blocks until the lambda has run) to
//!    keep that borrow sound. `idle_add_once` requires `'static` because
//!    it can't see that synchronous-blocking guarantee at the type level.
//!    [`get_run_in_gtk_scoped`] restores it explicitly with one `unsafe`
//!    lifetime extension, whose soundness rests entirely on this function
//!    not returning until the closure has actually run.

use std::sync::mpsc;
use gtk4::glib;

/// Wrapper that unsafely asserts `Send` for a value that isn't provably
/// `Send` (typically because it captures/contains a GTK widget or window,
/// whose `GObject` refcounting isn't atomic) but *is* safe to move across
/// a thread boundary in this codebase's specific usage pattern: ownership
/// transfers completely, and the wrapped value is only ever actually
/// touched — dereferenced, have methods called on it, etc. — from the
/// thread that owns the relevant `MainContext`, typically by routing back
/// through [`get_run_in_gtk`]/[`get_run_in_gtk_scoped`]. This mirrors what
/// the original C++ relied on implicitly: `Glib::signal_idle()` is
/// documented callable from any thread, and GTK objects were passed to
/// background "pilot" threads (see `gtk_gui_pilot`/`gtk_entry_pilot`)
/// purely to be handed back to `get_run_in_gtk` later, never touched
/// directly off the main thread.
///
/// Reach for this only when you can point to *why* the wrapped value is
/// never actually accessed except on the right thread — it does not make
/// arbitrary cross-thread widget access sound.
pub struct AssertSend<T>(T);
unsafe impl<T> Send for AssertSend<T> {}

impl<T> AssertSend<T> {
    pub fn new(value: T) -> Self {
        Self(value)
    }

    pub fn into_inner(self) -> T {
        self.0
    }
}

/// Port of `void run_in_gtk(std::function<void()>)` — fire-and-forget,
/// runs `_lambda` once on the GTK main loop without waiting for it.
pub fn run_in_gtk<F>(f: F)
where
    F: FnOnce() + 'static,
{
    let wrapped = AssertSend::new(f);
    glib::idle_add_once(move || {
        (wrapped.into_inner())();
    });
}

/// Port of `T_return get_run_in_gtk<T_return>(std::function<T_return()>)`
/// for owned (`'static`) closures.
pub fn get_run_in_gtk<T, F>(f: F) -> T
where
    T: Send + 'static,
    F: FnOnce() -> T + 'static,
{
    let (tx, rx) = mpsc::channel::<T>();
    let wrapped = AssertSend::new((f, tx));
    glib::idle_add_once(move || {
        let (f, tx) = wrapped.into_inner();
        let _ = tx.send(f());
    });
    rx.recv()
        .expect("GTK idle callback was dropped before it produced a result")
}

/// Same contract as [`get_run_in_gtk`], but for closures that borrow data
/// with a lifetime shorter than `'static` — see the module-level notes.
///
/// # Safety / soundness
/// This function does not return until `f` has been invoked (it blocks on
/// `rx.recv()`), so nothing `f` borrows can be dropped, moved, or
/// otherwise invalidated while its erased `'static` lifetime is "live".
/// That's the same invariant the original C++ leaned on via
/// `future::get()` / `f1.wait()`.
pub fn get_run_in_gtk_scoped<T, F>(f: F) -> T
where
    T: Send + 'static,
    F: FnOnce() -> T,
{
    let (tx, rx) = mpsc::channel::<T>();

    let boxed: Box<dyn FnOnce() -> T> = Box::new(f);
    // SAFETY: `boxed` is invoked exactly once, synchronously, from the
    // idle callback below, and this function blocks on `rx.recv()` until
    // that callback has completed — so the erasure to `'static` never
    // actually outlives whatever `f` borrowed.
    let boxed: Box<dyn FnOnce() -> T + 'static> = unsafe {
        std::mem::transmute::<Box<dyn FnOnce() -> T>, Box<dyn FnOnce() -> T + 'static>>(boxed)
    };

    let wrapped = AssertSend::new((boxed, tx));
    glib::idle_add_once(move || {
        let (boxed, tx) = wrapped.into_inner();
        let _ = tx.send(boxed());
    });

    rx.recv()
        .expect("GTK idle callback was dropped before it produced a result")
}
