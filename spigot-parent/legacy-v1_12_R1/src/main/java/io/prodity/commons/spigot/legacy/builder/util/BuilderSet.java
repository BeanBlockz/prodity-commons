package io.prodity.commons.spigot.legacy.builder.util;

import java.util.Collection;
import java.util.Set;

public interface BuilderSet<E, SELF extends BuilderSet<E, SELF>> extends Set<E> {

    default SELF getSelf() {
        return (SELF) this;
    }

    default SELF builderAdd(E element) {
        this.add(element);
        return this.getSelf();
    }

    default SELF builderAdd(E... elements) {
        for (int i = 0; i < elements.length; i++) {
            this.add(elements[i]);
        }
        return this.getSelf();
    }

    default SELF builderAddAll(Collection<E> elements) {
        this.addAll(elements);
        return this.getSelf();
    }

    default SELF builderRemove(E element) {
        this.remove(element);
        return this.getSelf();
    }

}