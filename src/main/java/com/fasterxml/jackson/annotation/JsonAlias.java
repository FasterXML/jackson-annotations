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
 * </p>
 * <p>
 * NOTE: When there is more than one <b>alias-to-key match</b>, "Last one wins"
 * rule is applied to the key of deserialization target object. But the same
 * rule does NOT apply to the order of {@link JsonAlias#value()} field.
 * </p>
 * <p>
 * For example,
 * <pre>
 * public class Person {
 *    &#64;JsonAlias({ "name", "fullName" })
 *    public String description;
 * }
 * </pre>
 * below JSON object will be deserialized with <code>description</code> field value of "Jackson".
 * <pre>
 *  { "fullName": "Faster Jackson", "name": "Jackson"}
 * </pre>
 * And below JSON object will be deserialized with <code>description</code>
 * field value of "Faster Jackson".
 * <pre>
 *  { "name": "Jackson", "fullName": "Faster Jackson"}
 * </pre>
 * </p>
 *
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
