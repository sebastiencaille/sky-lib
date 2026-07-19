package ch.scaille.dataflowmgr.model;

public interface DerivableCall<T extends Call> {

    public abstract T derivate(final String to);

}
