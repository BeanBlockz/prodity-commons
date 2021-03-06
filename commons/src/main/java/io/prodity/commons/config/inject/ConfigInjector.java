package io.prodity.commons.config.inject;

import io.prodity.commons.config.inject.except.ConfigInjectException;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ConfigInjector {

    /**
     * Injects the specified class with values loaded from its defined configuration file.
     *
     * @param configClass the {@link Class} to inject that must be annotated with {@link io.prodity.commons.config.annotate.inject.Config}
     * @return the newly created & injected config object
     * @throws ConfigInjectException If the config injection failed.
     */
    <T> T inject(Class<T> configClass) throws ConfigInjectException;

}