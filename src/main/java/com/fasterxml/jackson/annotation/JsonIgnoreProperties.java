package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to either suppress serialization of
 * properties (during serialization), or ignore processing of
 * JSON properties read (during deserialization).
 *<p>
 * Example:
 *<pre>
 * // to prevent specified fields from being serialized or deserialized
 * // (i.e. not include in JSON output; or being set even if they were included)
 * &#064;JsonIgnoreProperties({ "internalId", "secretKey" })
 * // To ignore any unknown properties in JSON input without exception:
 * &#064;JsonIgnoreProperties(ignoreUnknown=true)
 *</pre>
 *<p>
 * Starting with 2.0, this annotation can be applied both to classes and
 * to properties. If used for both, actual set will be union of all
 * ignorals: that is, you can only add properties to ignore, not remove
 * or override. So you can not remove properties to ignore using
 * per-property annotation.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
    ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnoreProperties
{
    /**
     * Names of properties to ignore.
     */
    public String[] value() default { };

    /**
     * Property that defines whether it is ok to just ignore any
     * unrecognized properties during deserialization.
     * If true, all properties that are unrecognized -- that is,
     * there are no setters or creators that accept them -- are
     * ignored without warnings (although handlers for unknown
     * properties, if any, will still be called) without
     * exception.
     *<p>
     * Does not have any effect on serialization.
     */
    public boolean ignoreUnknown() default false;

    /**
     * Property that can be enabled to allow "getters" to be used (that is,
     * prevent ignoral of getters for properties listed in {@link #value()}).
     * This is commonly set to support defining "read-only" properties; ones
     * for which there is a getter, but no matching setter: in this case,
     * properties should be ignored for deserialization but NOT serialization.
     * Another way to think about this setting is that setting it to `true`
     * will "disable" ignoring of getters.
     *<p>
     * Default value is `false`, which means that getters with matching names
     * will be ignored.
     * 
     * @since 2.6
     */
    public boolean allowGetters() default false;

    /**
     * Property that can be enabled to allow "setters" to be used (that is,
     * prevent ignoral of setters for properties listed in {@link #value()}).
     * This could be used to specify "write-only" properties; ones
     * that should not be serialized out, but that may be provided in for
     * deserialization.
     * Another way to think about this setting is that setting it to `true`
     * will "disable" ignoring of setters.
     *<p>
     * Default value is `false`, which means that setters with matching names
     * will be ignored.
     * 
     * @since 2.6
     */
    public boolean allowSetters() default false;
}
