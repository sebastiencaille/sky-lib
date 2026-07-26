//! binding_chain.rs
//!
//! Rust port of `binding_chain.hh`.
//!
//! Original author: scaille (Sep 18, 2017)
//!
//! ## Design notes — read this first
//!
//! This is the one file in the set that needed real architectural
//! adaptation rather than a mechanical translation, because it leans on
//! three C++ features Rust doesn't have:
//!
//! 1. **`dynamic_cast<binding_backward<T>*>` / `dynamic_cast<binding_forward<T>*>`**
//!    — at each hop in the chain, the C++ code recovers a specific typed
//!    interface from a `shared_ptr<binding_link>` (the common base) at
//!    runtime. Rust's `Any::downcast_ref` only downcasts to a *concrete*
//!    type, not to "whichever of several types implements trait X" the
//!    way `dynamic_cast` does. Rather than fight that, each [`Link`] here
//!    stores its `to_property` / `to_component` behavior as **type-erased
//!    closures**, built at `bind()` time when the concrete `Ps`/`Cs`
//!    types are still known. The value being passed through is boxed as
//!    `Box<dyn Any>` and downcast back to its concrete type *inside* the
//!    closure — which is exactly the runtime type-check `dynamic_cast`
//!    was doing, just performed at the point the value is unboxed instead
//!    of the point the link is looked up.
//! 2. **Multiple inheritance** (`property_link : binding_link,
//!    binding_forward<_Pt>, binding_backward<_Pt>`, etc.) — replaced by
//!    plain structs that build a [`Link`] (a pair of closures) rather
//!    than trying to implement multiple marker traits at once.
//! 3. **`weak_ptr<Self> m_myself`, set right after construction** — this
//!    two-step "construct, then patch in a weak self-reference" pattern
//!    appears throughout the original (`binding_chain`,
//!    `chain_component_link`, `action_dependency`). Rust has a
//!    first-class way to do this safely in one step: [`Rc::new_cyclic`].
//!    It's used everywhere a `weak_ptr<Self>` was being stashed.
//!
//! Generic parameters keep the original names: `Pt` = property-side type,
//! `Ct`/`Cst` = component-side type, `Ps`/`PsT` = the "current" type at a
//! given point in the chain (which starts as `Pt` and changes after each
//! converter).

use std::any::Any;
use std::cell::{Cell, RefCell};
use std::marker::PhantomData;
use std::rc::{Rc, Weak};

use crate::lib_properties::{BindingConverter, ErrorNotifier, BindingChainController, BindingChainDependency, ComponentBinding, ComponentLink, Property, GuiException, SourcePtr, PropertyListener};
use super::property_listener::{FireFn, PropertyListenerDispatcher};

type ToPropertyFn = dyn Fn(usize, SourcePtr, Box<dyn Any>) -> Result<(), GuiException>;
type ToComponentFn = dyn Fn(usize, Box<dyn Any>) -> Result<(), GuiException>;


/// A whole binding chain, attached to a property, attaching to a
/// component. Port of `binding_chain<_Pt>`.
///
/// `Pt` = the property side's value type of the *whole* chain.
pub struct BindingChain<Pt: 'static> {
    links: RefCell<Vec<crate::lib_properties::binding_chain::Link>>,
    property: Rc<dyn Property>,
    error_notifier: Option<Rc<dyn ErrorNotifier>>,
    value_update_listener: RefCell<Weak<PropertyListenerDispatcher>>,
    transmit: Cell<bool>,
    dependencies: RefCell<Vec<Rc<dyn BindingChainDependency>>>,
    myself: RefCell<Weak<BindingChain<Pt>>>,
    _marker: PhantomData<Pt>,
}

/// A single stage of a bound chain, returned by `bind()` calls so the
/// next `.bind(...)` in the chain can be type-checked against it. Port of
/// `end_of_chain<_Pt, _PsT>`.
pub struct EndOfChain<Pt: 'static, PsT: 'static> {
    chain: Rc<BindingChain<Pt>>,
    _marker: PhantomData<PsT>,
}

/// Port of `binding_link` plus the per-link `binding_forward<T>` /
/// `binding_backward<T>` behavior, collapsed into one type-erased struct.
/// See the module-level notes for why.
struct Link {
    to_property: Box<ToPropertyFn>,
    to_component: Box<ToComponentFn>,
    /// Some links (`chain_component_link`) need to run cleanup when
    /// dropped (unregistering from the component binding) — this mirrors
    /// that link's `~chain_component_link()` destructor. Storing the
    /// keep-alive handle here and giving it a `Drop` impl reproduces that.
    _keep_alive: Option<Box<dyn Any>>,
}

impl<Pt: 'static + Clone, PsT: 'static> EndOfChain<Pt, PsT> {
    fn new(chain: Rc<BindingChain<Pt>>) -> Rc<Self> {
        Rc::new(Self {
            chain,
            _marker: PhantomData,
        })
    }

    /// Chains another converter onto the binding.
    pub fn bind_converter<Cst: 'static>(
        &self,
        converter: Rc<dyn BindingConverter<PsT, Cst>>,
    ) -> Rc<EndOfChain<Pt, Cst>> {
        self.chain.bind_converter(converter)
    }

    /// Terminates the chain at a component binding.
    pub fn bind_component<Cst: 'static>(
        &self,
        component_binding: Rc<dyn ComponentBinding<Cst>>,
    ) -> Rc<dyn BindingChainController> {
        self.chain.bind_component(component_binding)
    }
}



impl<Pt: 'static + Clone> BindingChain<Pt> {
    /// Port of the static `of()` factory. Builds the chain and its weak
    /// self-reference in one step via `Rc::new_cyclic` — see module notes.
    pub fn of(property: Rc<dyn Property>, error_notifier: Option<Rc<dyn ErrorNotifier>>) -> Rc<Self> {
        Rc::new_cyclic(|weak_self| Self {
            links: RefCell::new(Vec::new()),
            property,
            error_notifier,
            value_update_listener: RefCell::new(Weak::new()),
            transmit: Cell::new(true),
            dependencies: RefCell::new(Vec::new()),
            myself: RefCell::new(weak_self.clone()),
            _marker: PhantomData,
        })
    }

    fn myself(&self) -> Weak<BindingChain<Pt>> {
        self.myself.borrow().clone()
    }

    /// Port of `to_property<_PsT>`.
    ///
    /// Held only for the duration of the call — recursive calls further
    /// down the chain (a link's closure calling back into
    /// `chain.to_property(index - 1, ...)`) take their own fresh shared
    /// borrow, which `RefCell` permits alongside this one since neither
    /// needs mutable access to `links` while firing.
    fn to_property<PsT: 'static>(
        &self,
        index: usize,
        component: SourcePtr,
        value: PsT,
    ) -> Result<(), GuiException> {
        let links = self.links.borrow();
        (links[index].to_property)(index, component, Box::new(value))
    }

    /// Port of `to_component<_CsT>`.
    fn to_component<CsT: 'static>(&self, index: usize, value: CsT) -> Result<(), GuiException> {
        let links = self.links.borrow();
        if index >= links.len() {
            return Ok(());
        }
        (links[index].to_component)(index, Box::new(value))
    }

    /// Port of `propagate_property_change`.
    fn propagate_property_change(
        &self,
        source: SourcePtr,
        _name: &str,
        _old_value: *const (),
        new_value: *const (),
    ) {
        if !self.transmit.get() {
            return;
        }
        // SAFETY: the property system guarantees `new_value` points to a
        // live `Pt` for the duration of this call — mirrors the C++
        // `*(const _Pt*) _new_value` cast.
        let value = unsafe { (*new_value.cast::<Pt>()).clone() };
        if let Err(e) = self.to_component(0, value) {
            if let Some(notifier) = &self.error_notifier {
                notifier.set_error(source, &e);
            }
        }
    }

    /// Port of `bind<_PsT, _CsT>(shared_ptr<binding_converter<...>>)`.
    pub fn bind_converter<PsT: 'static, Cst: 'static>(
        self: &Rc<Self>,
        converter: Rc<dyn BindingConverter<PsT, Cst>>,
    ) -> Rc<EndOfChain<Pt, Cst>> {
        let chain = Rc::clone(self);
        let chain_for_component = Rc::clone(self);

        let to_property: Box<ToPropertyFn> = {
            let chain = Rc::clone(&chain);
            let converter = Rc::clone(&converter);
            Box::new(move |index, component, value| {
                let value = *value
                    .downcast::<Cst>()
                    .expect("binding_chain: type mismatch in converter_link::to_property");
                match converter.convert_component_value_to_property_value(&value) {
                    Ok(converted) => chain.to_property(index - 1, component, converted),
                    Err(e) => {
                        if let Some(notifier) = &chain.error_notifier {
                            notifier.set_error(component, &e);
                        }
                        Ok(())
                    }
                }
            })
        };

        let to_component: Box<ToComponentFn> = Box::new(move |index, value| {
            let value = *value
                .downcast::<PsT>()
                .expect("binding_chain: type mismatch in converter_link::to_component");
            let converted = converter.convert_property_value_to_component_value(&value);
            chain_for_component.to_component(index + 1, converted)
        });

        self.links.borrow_mut().push(Link {
            to_property,
            to_component,
            _keep_alive: None,
        });

        self.new_end_of_chain::<Cst>()
    }

    /// Port of `bind<_Ct>(shared_ptr<component_binding<_Ct>>)`.
    pub fn bind_component<Ct: 'static>(
        self: &Rc<Self>,
        component_binding: Rc<dyn ComponentBinding<Ct>>,
    ) -> Rc<dyn BindingChainController> {
        let index = self.links.borrow().len();
        let link = ChainComponentLink::of(Rc::clone(self), Rc::clone(&component_binding), index);
        // `add_component_value_change_listener` expects `Weak<dyn
        // ComponentLink<Ct>>`; passing `Weak<ChainComponentLink<Pt, Ct>>`
        // here coerces implicitly at the call site (unsized coercion),
        // unlike `as`, which doesn't support this conversion for `Weak`.
        let link_weak: Weak<dyn ComponentLink<Ct>> =
            Rc::<ChainComponentLink<Pt, Ct>>::downgrade(&link);
        component_binding.add_component_value_change_listener(link_weak);

        let chain_for_property = Rc::clone(self);
        let component_binding_for_to_component = Rc::clone(&component_binding);
        let chain_for_error = Rc::clone(self);

        let to_property: Box<ToPropertyFn> = Box::new(move |index, component, value| {
            let value = *value
                .downcast::<Ct>()
                .expect("binding_chain: type mismatch in chain_component_link::to_property");
            chain_for_property.to_property(index - 1, component, value)
        });

        let to_component: Box<ToComponentFn> = Box::new(move |_index, value| {
            let value = *value
                .downcast::<Ct>()
                .expect("binding_chain: type mismatch in chain_component_link::to_component");
            component_binding_for_to_component.set_component_value(
                chain_for_error.property.as_ref(),
                value,
            );
            if let Some(notifier) = &chain_for_error.error_notifier {
                notifier.clear_error(chain_for_error.property.as_source_ptr());
            }
            Ok(())
        });

        self.links.borrow_mut().push(Link {
            to_property,
            to_component,
            _keep_alive: Some(Box::new(link)),
        });

        self.myself().upgrade().expect("binding_chain dropped") as Rc<dyn BindingChainController>
    }

    fn new_end_of_chain<Et: 'static>(self: &Rc<Self>) -> Rc<EndOfChain<Pt, Et>> {
        EndOfChain::new(Rc::clone(self))
    }

    /// Port of `bind_property`.
    pub fn bind_property(
        self: &Rc<Self>,
        setter: Rc<dyn Fn(SourcePtr, Pt)>,
    ) -> Rc<EndOfChain<Pt, Pt>> {
        let chain_weak = self.myself();
        let fire_fn: Rc<FireFn> = Rc::new(move |source, name, old_value, new_value| {
            if let Some(chain) = chain_weak.upgrade() {
                chain.propagate_property_change(source, name, old_value, new_value);
            }
        });

        let owner: Weak<dyn Any> = self.myself();
        let dispatcher = PropertyListenerDispatcher::of_lazy_fire(
            &self.value_update_listener,
            &owner,
            fire_fn,
        );
        self.property.add_listener(dispatcher);

        let chain_for_property: Weak<BindingChain<Pt>> = self.myself();
        let to_property: Box<ToPropertyFn> = Box::new(move |_index, component, value| {
            let value = *value
                .downcast::<Pt>()
                .expect("binding_chain: type mismatch in property_link::to_property");
            setter(component, value);
            if let Some(chain) = chain_for_property.upgrade() {
                if let Some(notifier) = &chain.error_notifier {
                    notifier.clear_error(component);
                }
            }
            Ok(())
        });

        let chain_for_component = self.myself();
        let to_component: Box<ToComponentFn> = Box::new(move |index, value| {
            if let Some(chain) = chain_for_component.upgrade() {
                chain.to_component(index + 1, *value.downcast::<Pt>().expect(
                    "binding_chain: type mismatch in property_link::to_component",
                ))
            } else {
                Ok(())
            }
        });

        self.links.borrow_mut().push(Link {
            to_property,
            to_component,
            _keep_alive: None,
        });

        self.new_end_of_chain::<Pt>()
    }
}

impl<Pt: 'static + Clone> BindingChainController for BindingChain<Pt> {
    fn attach(&self) {
        self.transmit.set(true);
        self.property.attach();
    }

    fn detach(&self) {
        self.transmit.set(false);
    }

    fn get_property(&self) -> Rc<dyn Property> {
        Rc::clone(&self.property)
    }

    fn add_dependency(
        &self,
        dependency: Rc<dyn BindingChainDependency>,
    ) -> Rc<dyn BindingChainController> {
        let weak_chain = self.myself();
        let owner = dependency.as_any_weak();
        self.dependencies.borrow_mut().push(Rc::clone(&dependency));
        dependency.register_dep(weak_chain.clone(), owner);
        weak_chain.upgrade().expect("binding_chain dropped") as Rc<dyn BindingChainController>
    }
}

impl<Pt: 'static> Drop for BindingChain<Pt> {
    fn drop(&mut self) {
        if let Some(listener) = self.value_update_listener.borrow().upgrade() {
            let property_listener: Rc<dyn PropertyListener> = listener;
            self.property.remove_listener(property_listener);
        }
    }
}

/// Link that connects the last stage of a chain to a component. Port of
/// `chain_component_link<_Ct>`. Implements [`ComponentLink<Ct>`] so a
/// `component_binding` can call back into it, and cleans up (unregisters
/// itself) on drop, mirroring `~chain_component_link()`.
struct ChainComponentLink<Pt: 'static, Ct: 'static> {
    chain: Rc<BindingChain<Pt>>,
    component_binding: Rc<dyn ComponentBinding<Ct>>,
    index: usize,
}

impl<Pt: 'static + Clone, Ct: 'static> ChainComponentLink<Pt, Ct> {
    fn of(
        chain: Rc<BindingChain<Pt>>,
        component_binding: Rc<dyn ComponentBinding<Ct>>,
        index: usize,
    ) -> Rc<Self> {
        Rc::new(Self {
            chain,
            component_binding,
            index,
        })
    }
}

impl<Pt: 'static + Clone, Ct: 'static> ComponentLink<Ct> for ChainComponentLink<Pt, Ct> {
    fn set_value_from_component(&self, component: SourcePtr, component_value: Ct) {
        if !self.chain.transmit.get() {
            return;
        }
        let last = self.chain.links.borrow().len().saturating_sub(1);
        if let Err(e) = self.chain.to_property(last, component, component_value) {
            if let Some(notifier) = &self.chain.error_notifier {
                notifier.set_error(component, &e);
            }
        }
        let _ = self.index; // kept for parity with the original's stored index
    }

    fn reload_component_value(&self) {
        // Port of the comment "should trigger the listeners".
        self.chain.property.attach();
    }
}

impl<Pt: 'static, Ct: 'static> Drop for ChainComponentLink<Pt, Ct> {
    fn drop(&mut self) {
        self.component_binding.remove_component_value_change_listener();
    }
}
