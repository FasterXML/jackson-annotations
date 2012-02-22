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
}
