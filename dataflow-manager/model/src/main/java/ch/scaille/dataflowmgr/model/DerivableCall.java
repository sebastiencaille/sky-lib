package ch.scaille.dataflowmgr.model;

public interface DerivableCall<T extends Call> {

    T derivate(final String to);

}
