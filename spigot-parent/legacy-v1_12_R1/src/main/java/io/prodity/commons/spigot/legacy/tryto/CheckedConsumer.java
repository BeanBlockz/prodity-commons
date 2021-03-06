package io.prodity.commons.spigot.legacy.tryto;

@FunctionalInterface
public interface CheckedConsumer<T> {

    void accept(T t) throws Throwable;

}