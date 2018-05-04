package io.prodity.commons.plugin.annotate;

import com.google.common.base.Preconditions;
import io.prodity.commons.plugin.annotate.process.PluginSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(PluginDependencyComposite.class)
public @interface PluginDependency {

    String value();

    boolean soft() default false;

    class Serializer {

        @Nonnull
        public static PluginSerializer<PluginDependency> create(String dependsKey, String softDependsKey) {
            Preconditions.checkNotNull(dependsKey, "dependsKey");
            Preconditions.checkNotNull(softDependsKey, "softDependsKey");

            return (annotation, data) -> {
                final String key = annotation.soft() ? softDependsKey : dependsKey;
                data.set(key, "[" + annotation.value() + "]");
            };
        }

    }

}