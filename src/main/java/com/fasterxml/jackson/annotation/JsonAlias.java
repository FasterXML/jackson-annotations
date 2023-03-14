package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to define one or more alternative names for
 * a property, accepted during deserialization as alternative to the official
 * name. Alias information is also exposed during POJO introspection, but has
 * no effect during serialization where primary name is always used.
 *<p>
 * Examples:
 *<pre>
 *public class Info {
 *  &#64;JsonAlias({ "n", "Name" })
 *  public String name;
 *}
 *</pre>
 * <p>
 * NOTE: Order of alias declaration has no effect. All properties are assigned
 * in the order they come from incoming JSON document. If same property is
 * assigned more than once with different value, later will remain.
 * For example, deserializing
 * <pre>
 * public class Person {
 *    &#64;JsonAlias({ "name", "fullName" })
 *    public String name;
 * }
 * </pre>
 * from
 * <pre>
 * { "fullName": "Faster Jackson", "name": "Jackson" }
 * </pre>
 * will have value "Jackson".
 * <p>
 * Also, can be used with enums where incoming JSON properties may not match the defined
 * enum values. For instance, if you have an enum called {@code Size} with values
 * {@code SMALL}, {@code MEDIUM}, and {@code LARGE}, you can use this annotation
 * to define alternate values for each enum value. This way, the deserialization
 * process can map the incoming JSON values to the correct enum values.
 * <p>Sample implementation:
 * <pre>public enum Size {
 *     &#64;JsonAlias({ "small", "s", "S" })
 *     SMALL,
 *
 *     &#64;JsonAlias({ "medium", "m", "M" })
 *     MEDIUM,
 *
 *     &#64;JsonAlias({ "large", "l", "L" })
 *     LARGE
 * }</pre>
 * <p>
 * During deserialization, any of these JSON structures will be valid
 * and correctly mapped to the MEDIUM enum value: {"size": "m"}, {"size": "medium"}, or {"size": "M"}.
 * @since 2.9
 */
@Target({ElementType.ANNOTATION_TYPE, // for combo-annotations
    ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER// for properties (field, setter, ctor param)
})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAlias
{
    /**
     * One or more secondary names to accept as aliases to the official name.
     *
     * @return Zero or more aliases to associate with property annotated
     */
    public String[] value() default { };
}
