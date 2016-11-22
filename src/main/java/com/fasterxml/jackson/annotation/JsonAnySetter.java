package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a logical "any setter" mutator --
 * either using non-static
 * two-argument method (first argument name of property, second value
 * to set) or a field (of type {@link java.util.Map} or POJO) -
 * to be used as a "fallback" handler
 * for all otherwise unrecognized properties found from JSON content.
 * It is similar to {@link javax.xml.bind.annotation.XmlAnyElement}
 * in behavior; and can only be used to denote a single property
 * per type.
 *<p>
 * If used, all otherwise unmapped key-value pairs from JSON Object values
 * are added using mutator.
 *<p>
 * NOTE: ability to annotated fields was added in version 2.8; earlier only
 * methods could be annotated.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAnySetter
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
