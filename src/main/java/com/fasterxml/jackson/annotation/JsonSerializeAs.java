package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate override of intended serialization type;
 * a more generic type (super-type) than declared one.
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
 *   // ValueImpl extends GenericValue
 *   &#64;JsonSerializeAs(GenericValue.class)
 *   public ValueImpl value;
 *
 *   &#64;JsonSerializeAs(keys = GenericKey.class, content = GenericValue.class)
 *   public Map&lt;KeyImpl, ValueImpl> props;
 * }
 *</pre>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD,
    ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerializeAs
{
    /**
     * Type to serialize values as, instead of type declared.
     * Must be a super-type of declared type (or type itself); otherwise an
     * exception may be thrown by serializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> value() default Void.class;

    /**
     * Type to serialize content entries (array and {@link java.util.Collection}
     * elements, {@link java.util.Map} values) as, instead of type declared.
     * Must be a super-type of declared type (or type itself); otherwise an
     * exception may be thrown by serializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> content() default Void.class;

    /**
     * Type to serialize {@link java.util.Map} keys as, instead of type declared.
     * Must be a super-type of declared type (or type itself); otherwise an
     * exception may be thrown by serializer.
     *<p>
     * Bogus type {@link java.lang.Void} is used to indicate that declared
     * type is used as-is (i.e. this annotation property has no setting).
     */
    public Class<?> key() default Void.class;
}
