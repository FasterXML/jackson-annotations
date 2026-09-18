package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a property is to be deserialized from the
 * value selected by a JSON Pointer expression, instead of from a direct child
 * property of the containing JSON Object.
 *<p>
 * For example, given input such as:
 *<pre>
 * {
 *   "employee" : {
 *     "details" : {
 *       "departmentId" : 123
 *     }
 *   }
 * }
 *</pre>
 * property can be bound directly with:
 *<pre>
 * public class Employee {
 *   &#64;JsonPointer("/employee/details/departmentId")
 *   public int departmentId;
 * }
 *</pre>
 * Pointer syntax follows JSON Pointer (RFC 6901), including escaping of
 * {@code '~'} and {@code '/'} characters as {@code "~0"} and {@code "~1"},
 * respectively.
 *<p>
 * This annotation only defines logical property access during deserialization;
 * it has no effect on serialization. If the pointer does not resolve to a value,
 * the property is considered absent. An explicitly resolved JSON {@code null}
 * is handled as a regular null property value.
 *<p>
 * Actual support for this annotation is provided by data-binding modules such
 * as {@code jackson-databind}; annotation introspection by itself does not enable
 * JSON Pointer traversal.
 *
 * @since 2.23
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonPointer
{
    /**
     * JSON Pointer expression used to locate the value in the input document.
     *
     * @return JSON Pointer expression for the annotated property
     */
    String value();
}
