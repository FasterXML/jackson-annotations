package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static,
 * no-argument method to be an "any getter"; accessor for getting
 * a set of key/value pairs, to be serialized as part of containing POJO
 * (similar to unwrapping) along with regular property values it has.
 * This typically serves as a counterpart
 * to "any setter" mutators (see {@link JsonAnySetter}).
 * Note that the return type of annotated methods <b>must</b> be
 * {@link java.util.Map}).
 *<p>
 * As with {@link JsonAnySetter}, only one property should be annotated
 * with this annotation; if multiple methods are annotated, an exception
 * may be thrown.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAnyGetter
{
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes.
     * Overriding may be necessary when used
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     *
     * @since 2.9
     */
    boolean enabled() default true;
}
