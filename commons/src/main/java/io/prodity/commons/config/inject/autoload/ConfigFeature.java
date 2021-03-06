package io.prodity.commons.config.inject.autoload;

import io.prodity.commons.config.inject.ConfigInjectionContext;
import io.prodity.commons.config.inject.ConfigInjector;
import io.prodity.commons.config.inject.SimpleConfigInjector;
import io.prodity.commons.config.inject.deserialize.ElementColorizer;
import io.prodity.commons.config.inject.deserialize.ElementResolver;
import io.prodity.commons.config.inject.deserialize.repository.ElementRepositoryResolver;
import io.prodity.commons.inject.InjectionFeature;
import io.prodity.commons.plugin.ProdityPlugin;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;

public class ConfigFeature implements InjectionFeature {

    @Override
    public void preLoad(ProdityPlugin plugin) {
        this.bind(plugin, (binder) -> {
            binder.bind(SimpleConfigInjector.class).to(ConfigInjector.class);
            binder.bind(ElementResolver.class);
            binder.bind(ElementColorizer.class);
            binder.bind(ElementRepositoryResolver.class);
            binder.bind(ConfigInjectionContext.class);
            binder.bind(ConfigResolver.class).to(JustInTimeInjectionResolver.class);
        });
    }

}