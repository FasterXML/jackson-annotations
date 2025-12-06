package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate override of deserialization target type;
 * a more specialized type (sub-type) than declared one.
 * Can be used on types ({@code Class}es) or on properties (property accessors
 * like "getters", "setters" and constructor parameters).
 * Overrides can apply to:
 *<ul>
 * <li>{@code value} itself (for all types and properties)
 *  </li>
 * <li>{@code content} of structured types (array and {@link java.util.Collection}
 *   elements; {@link java.util.Map} values)
 *  </li>
 * <li>{@code key}s of {@link java.util.Map}s
 *  </li>
 *<p>
 * To indicate that no override is to be used, {@code Void.class} is used
 * as the marker (will use declared type) -- this is necessary as Annotation
 * properties cannot have {@code null} values.
 *<p>
 * Example usage:
 *<pre>
 * public class POJO {
 *   &#64;JsonDeserializeAs(ValueImpl.class)
 *   public Value value;
 *
 *   &#64;JsonDeserializeAs(keys = KeyEnum.class, content = ValueImpl.class)
 *   public Map&lt;Object, Object> props;
 * }
 *</pre>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD,
    ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonDeserializeAs
{
    /**
     * Type to deserialize values as, instead of type declared.
     * Must be a sub-type of declared type; otherwise an
     * exception may be thrown by deserializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> value() default Void.class;

    /**
     * Type to deserialize content entries (array and {@link java.util.Collection}
     * elements, {@link java.util.Map} values) as, instead of type declared.
     * Must be a sub-type of declared type; otherwise an
     * exception may be thrown by deserializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> content() default Void.class;

    /**
     * Type to deserialize {@link java.util.Map} keys as, instead of type declared.
     * Must be a sub-type of declared type; otherwise an
     * exception may be thrown by deserializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> keys() default Void.class;
}
