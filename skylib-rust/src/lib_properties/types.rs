//! types.rs
//!
//! Rust port of `types.hh` (originally `binding_chain.hh`).
//!
//! Original author: scaille (Sep 18, 2017)

use std::fmt;

use crate::lib_properties::GuiException;

impl GuiException {
    pub fn new(message: impl Into<String>) -> Self {
        Self {
            what: message.into(),
        }
    }

    /// Equivalent to the C++ `what()` accessor.
    pub fn what(&self) -> &str {
        &self.what
    }
}

impl fmt::Display for GuiException {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(&self.what)
    }
}

impl std::error::Error for GuiException {}

#[cfg(test)]
mod tests {
    use std::rc::Rc;
    use crate::lib_properties::GuiExceptionPtr;
    use super::*;

    #[test]
    fn what_roundtrips_the_message() {
        let e = GuiException::new("boom");
        assert_eq!(e.what(), "boom");
        assert_eq!(e.to_string(), "boom");
    }

    #[test]
    fn shared_via_rc() {
        let e: GuiExceptionPtr = Rc::new(GuiException::new("shared"));
        let e2 = Rc::clone(&e);
        assert_eq!(e2.what(), "shared");
        assert_eq!(Rc::strong_count(&e), 2);
    }
}
