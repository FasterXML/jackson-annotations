package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that indicates that the logical property that
 * the accessor (field, getter/setter method or Creator parameter
 * [of {@link JsonCreator}-annotated constructor or factory method])
 * is to be ignored by introspection-based
 * serialization and deserialization functionality.
 *<p>
 * Annotation only needs to be added to one of the accessors (often
 * getter method, but may be setter, field or creator parameter),
 * if the complete removal of the property is desired.
 * However: if only particular accessor is to be ignored (for example,
 * when ignoring one of potentially conflicting setter methods),
 * this can be done by annotating other not-to-be-ignored accessors
 * with {@link JsonProperty} (or its equivalents). This is considered
 * so-called "split property" case and allows definitions of
 * "read-only" (read from input into POJO) and "write-only" (write
 * in output but ignore on output)
 *<br>
 * NOTE! As Jackson 2.6, there is a new and improved way to define
 * `read-only` and `write-only` properties, using
 * {@link JsonProperty#access()} annotation: this is recommended over
 * use of separate <code>JsonIgnore</code> and {@link JsonProperty}
 * annotations.
 *<p>
 * For example, a "getter" method that would otherwise denote
 * a property (like, say, "getValue" to suggest property "value")
 * to serialize, would be ignored and no such property would
 * be output unless another annotation defines alternative method to use.
 *<p>
 * When ignoring the whole property, the default behavior if encountering
 * such property in input is to ignore it without exception; but if there
 * is a {@link JsonAnySetter} it will be called instead. Either way,
 * no exception will be thrown.
 *<p>
 * Annotation is usually used just a like a marker annotation, that
 * is, without explicitly defining 'value' argument (which defaults
 * to <code>true</code>): but argument can be explicitly defined.
 * This can be done to override an existing `JsonIgnore` by explicitly
 * defining one with 'false' argument: either in a sub-class, or by
 * using "mix-in annotations".
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnore
{
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes
     * (which is not needed often); most likely it is needed for use
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     */
    boolean value() default true;
}
