package io.prodity.commons.plugin.annotate.process;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import io.prodity.commons.except.tryto.Try;
import io.prodity.commons.plugin.annotate.Plugin;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.yaml.snakeyaml.Yaml;

public abstract class PluginProcessor extends AbstractProcessor {

    private final PluginSerializerMap serializers;
    private final String propertiesFileName;

    protected PluginProcessor(PluginSerializerMap serializers) {
        this(serializers, null);
    }

    protected PluginProcessor(PluginSerializerMap serializers, @Nullable String propertiesFileName) {
        Preconditions.checkNotNull(serializers, "serializers");
        this.serializers = serializers;
        this.propertiesFileName = propertiesFileName;
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        final Set<? extends Element> annotatedElements = environment.getElementsAnnotatedWith(Plugin.class);
        if (annotatedElements.isEmpty()) {
            return false;
        }

        final Messager messager = this.processingEnv.getMessager();
        if (annotatedElements.size() > 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "multiple @Plugin elements found");
            return false;
        }

        final Element element = Iterables.get(annotatedElements, 0);

        if (!(element instanceof TypeElement)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@Plugin element is not an instance of TypeElement");
            return false;
        }

        final TypeElement typeElement = (TypeElement) element;
        final PluginDescription descriptionData = new PluginDescription();

        final String mainClass = typeElement.getQualifiedName().toString();
        descriptionData.set("main", mainClass);

        this.serializers.forEach((annotationClass, serializer) -> {
            this.applyToDescription(typeElement, annotationClass, serializer, descriptionData);
        });

        final Map<String, Object> values = descriptionData.getValuesCopy();
        final Map<String, String> replacements = Try.to(this::loadReplacements).get();
        this.setReplacements(values, replacements);
        this.writeValuesToFile(values);

        return true;
    }

    private Map<String, String> loadReplacements() {
        if (this.propertiesFileName == null) {
            return Maps.newHashMap();
        }

        final Properties properties = this.loadProperties();

        final Map<String, String> replacements = Maps.newHashMap();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            final String key = this.surroundReplacement(entry.getKey().toString());
            final String value = entry.getValue().toString();
            replacements.put(key, value);
        }

        return replacements;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            final FileObject fileObject = this.processingEnv.getFiler()
                .getResource(StandardLocation.CLASS_OUTPUT, "", this.propertiesFileName);
            try (InputStream inputStream = fileObject.openInputStream()) {
                properties.load(inputStream);
            }
        } catch (FileNotFoundException exception) {
            // They don't have a file, that's fine.
        } catch (IOException exception) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FAILED TO READ " + this.propertiesFileName);
        }
        return properties;
    }

    private String surroundReplacement(String text) {
        Preconditions.checkNotNull(text, "text");
        return "%" + text + "%";
    }

    private void setReplacements(Map<String, Object> values, Map<String, String> replacements) {
        Preconditions.checkNotNull(values, "values");
        Preconditions.checkNotNull(replacements, "replacements");

        if (values.size() == 0 || replacements.size() == 0) {
            return;
        }

        for (Map.Entry<String, Object> valueEntry : values.entrySet()) {
            Object value = valueEntry.getValue();
            if (!(value instanceof String)) {
                continue;
            }
            final String stringValue = (String) value;
            for (Map.Entry<String, String> replacementEntry : replacements.entrySet()) {
                value = stringValue.replace(replacementEntry.getKey(), replacementEntry.getValue());
            }
            valueEntry.setValue(value);
        }
    }

    private void writeValuesToFile(Map<String, Object> values) {
        Preconditions.checkNotNull(values, "values");
        Try.to(() -> {
            final Yaml yaml = new Yaml();
            final FileObject fileObject = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            try (
                Writer writer = fileObject.openWriter();
                BufferedWriter bufferedWriter = new BufferedWriter(writer)
            ) {
                yaml.dump(values, bufferedWriter);
                bufferedWriter.flush();
            }
        }).run();
    }

    private <T extends Annotation> void applyToDescription(TypeElement typeElement, Class<? extends Annotation> annotationClass,
        PluginSerializer<T> serializer, PluginDescription descriptionData) {
        final T annotation = (T) typeElement.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        serializer.serializeIntoDescription(annotation, descriptionData);
    }

}