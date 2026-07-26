//! binding_interface.rs
//!
//! Rust port of `binding_interface.hh` (originally `Converter.hh`).
//!
//! Original author: scaille (Feb 19, 2012)
//!
//! ## Design notes
//! - Every abstract C++ class here (`error_notifier`, `binding_converter`,
//!   `component_link`, `component_binding`, `binding_chain_controller`,
//!   `binding_chain_dependency`, `action`) becomes a Rust trait.
//! - `_Pt = gui_exception_ptr` in `gui_error_to_string` was implicitly
//!   nullable (`shared_ptr`, default-constructed to null in
//!   `convert_component_value_to_property_value`, and null-checked in
//!   `convert_property_value_to_component_value`). `Rc<T>` can't be null,
//!   so that instantiation becomes `Option<GuiExceptionPtr>` — see
//!   [`GuiErrorToString`] below, and note `controller_property.rs` /
//!   `input_error_property_impl.rs` use the same `Option<GuiExceptionPtr>`
//!   alias for consistency.
//! - `action_dependency<_T>`'s template parameter `_T` is unused in the
//!   original class body; it's kept here as a `PhantomData<T>` purely for
//!   API parity.
//! - C++'s `weak_ptr<binding_chain_dependency> _myself`, passed straight
//!   through to `property_listener_dispatcher::ofLazy` as an opaque
//!   `weak_ptr<void>` owner, relies on `weak_ptr`'s aliasing constructor
//!   to type-erase while preserving the control block. Rust's `Weak<dyn
//!   Trait>` can't be re-coerced to `Weak<dyn Any>` after the fact, so
//!   [`BindingChainDependency`] exposes an explicit `as_any_weak` method
//!   that concrete implementors provide (backed by a `Weak<Self>`
//!   captured via `Rc::new_cyclic` at construction time).

use std::any::Any;
use std::cell::{RefCell};
use std::rc::{Rc, Weak};

use crate::lib_properties::{Property, BindingConverter, BindingChainController, BindingChainDependency, GuiException, GuiExceptionPtr, GuiErrorToString, ActionDependency, PropertyGroupActions, PropertyListener};
use super::property_listener::{BeforeAfterFn, PropertyListenerDispatcher};

/// Port of `gui_error_to_string`. Instantiated over `Option<GuiExceptionPtr>`
/// rather than `GuiExceptionPtr` directly — see module notes.

impl BindingConverter<Option<GuiExceptionPtr>, String> for GuiErrorToString {
    fn convert_component_value_to_property_value(
        &self,
        _component_value: &String,
    ) -> Result<Option<GuiExceptionPtr>, GuiException> {
        // "nonsense" per the original comment: this direction is never
        // meaningful for an error converter. Mirrors the C++ `return nullptr;`.
        Ok(None)
    }

    fn convert_property_value_to_component_value(
        &self,
        property_value: &Option<GuiExceptionPtr>,
    ) -> String {
        match property_value {
            None => String::new(),
            Some(error) => error.what().to_string(),
        }
    }
}

impl GuiErrorToString {
    pub fn of() -> Rc<dyn BindingConverter<Option<GuiExceptionPtr>, String>> {
        Rc::new(GuiErrorToString)
    }
}



impl<T: 'static> ActionDependency<T> {
    pub fn new(action: Rc<dyn Fn(PropertyGroupActions, &dyn Property)>) -> Rc<Self> {
        Rc::new_cyclic(|weak_self| Self {
            myself: RefCell::new(weak_self.clone()),
            chain: RefCell::new(Weak::<super::binding_chain::BindingChain<()>>::new()),
            listener: RefCell::new(Weak::new()),
            action,
            _marker: std::marker::PhantomData,
        })
    }

    /// Port of `action_dependency::unbind`.
    pub fn unbind(&self) {
        if let Some(chain) = self.chain.borrow().upgrade()
            && let Some(listener) = self.listener.borrow().upgrade() {
            let property_listener: Rc<dyn PropertyListener> = listener;
            chain.get_property().remove_listener(property_listener);
        }
    }

}

impl<T: 'static> BindingChainDependency for ActionDependency<T> {
    fn as_any_weak(&self) -> Weak<dyn Any> {
        self.myself.borrow().clone()
    }

    fn register_dep(&self, chain: Weak<dyn BindingChainController>, myself: Weak<dyn Any>) {
        *self.chain.borrow_mut() = chain.clone();
        let Some(controller) = chain.upgrade() else {
            return;
        };
        let property = controller.get_property();

        let action_before = Rc::clone(&self.action);
        let action_after = Rc::clone(&self.action);
        let before: Rc<BeforeAfterFn> = Rc::new(move |_source, property| {
            action_before(PropertyGroupActions::BeforeFire, property);
        });
        let after: Rc<BeforeAfterFn> = Rc::new(move |_source, property| {
            action_after(PropertyGroupActions::AfterFire, property);
        });

        let dispatcher = PropertyListenerDispatcher::of_lazy_before_after(
            &self.listener,
            &myself,
            before,
            after,
        );
        property.add_listener(dispatcher);
    }
}
