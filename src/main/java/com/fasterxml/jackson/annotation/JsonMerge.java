package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify whether annotated property value should use "merging" approach,
 * in which current value is first accessed (with a getter or field) and then modified
 * with incoming data, or not: if not, assignment happens without considering current state.
 *<p>
 * Merging is only option if there is a way to introspect current state:
 * if there is accessor (getter, field) to use.
 * Merging can not be enabled if no accessor exists
 * or if assignment occurs using a Creator setter (constructor
 * or factory method), since there is no instance with state to introspect.
 * Merging also only has actual effect for structured types where there is an
 * obvious way to update a state (for example, POJOs have default values for properties,
 * and {@link java.util.Collection}s and {@link java.util.Map}s may have existing
 * elements; whereas scalar types do not such state: an <code>int</code> has a value,
 * but no obvious and non-ambiguous way to merge state.
 *<p>
 * Merging is applied by using a deserialization method that accepts existing state
 * as an argument: it is then up to <code>JsonDeserializer</code> implementation
 * to use that base state in a way that makes sense without further configuration.
 * For structured types this is usually obvious; and for scalar types not -- if
 * no obvious method exists, merging is not allowed; deserializer may choose to
 * either quietly ignore it, or throw an exception.
 *<p>
 * Note that use of merging usually adds some processing overhead since it adds
 * an extra step of accessing the current state before assignment.
 *<p>
 * Note also that "root values" (values directly deserialized and not reached
 * via POJO properties) can not use this annotation; instead, <code>ObjectMapper</code>
 * and <code>Object</code> have "updating reader" operations.
 *<p>
 * Default value is {@link OptBoolean#TRUE}, that is, merging <b>is enabled</b>.
 *
 * @since 2.9
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonMerge
{
    /**
     * Whether merging should or should not be enabled for the annotated property.
     */
    OptBoolean value() default OptBoolean.TRUE;
}
