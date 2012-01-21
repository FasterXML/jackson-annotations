package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate when value of the annotated property (when
 * used for a field, method or constructor parameter), or all 
 * properties of the annotated class, is to be serialized.
 * Without annotation property values are always included, but by using
 * this annotation one can specify simple exclusion rules to reduce
 * amount of properties to write out.
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD,
    ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@com.fasterxml.jackson.annotation.JacksonAnnotation
public @interface JsonInclude
{
    /**
     * Inclusion rule to use.
     */
    public Include value() default Include.ALWAYS;
    
    /*
    /**********************************************************
    /* Value enumerations needed
    /**********************************************************
     */

    /**
     * Enumeration used with {@link JsonInclude}
     * to define which properties
     * of Java Beans are to be included in serialization.
     *<p>
     * Note: Jackson 1.x had similarly named ("Inclusion") enumeration included
     * in <code>JsonSerialize</code> annotation: it is not deprecated
     * and this value used instead.
     */
    public enum Include
    {
        /**
         * Value that indicates that property is to be always included,
         * independent of value of the property.
         */
        ALWAYS,

        /**
         * Value that indicates that only properties with non-null
         * values are to be included.
         */
        NON_NULL,

        /**
         * Value that indicates that only properties that have values
         * that differ from default settings (meaning values they have
         * when Bean is constructed with its no-arguments constructor)
         * are to be included. Value is generally not useful with
         * {@link java.util.Map}s, since they have no default values;
         * and if used, works same as {@link #ALWAYS}.
         */
        NON_DEFAULT,

        /**
         * Value that indicates that only properties that have values
         * that values that are null or what is considered empty are
         * not to be included.
         *<p>
         * Default emptiness is defined for following type:
         *<ul>
         * <li>For {@link java.util.Collection}s and {@link java.util.Map}s,
         *    method <code>isEmpty()</code> is called;
         *   </li>
         * <li>For Java arrays, empty arrays are ones with length of 0
         *   </li>
         * <li>For Java {@link java.lang.String}s, <code>length()</code> is called,
         *   and return value of 0 indicates empty String (note that <code>String.isEmpty()</code>
         *   was added in Java 1.6 and as such can not be used by Jackson
         *   </li>
         * <ul>
         *  and for other types, non-null values are to be included.
         *<p>
         * Note that this default handling can be overridden by custom
         * <code>JsonSerializer</code> implementation: if method <code>isEmpty()</code>
         * is overridden, it will be called to see if non-null values are
         * considered empty (null is always considered empty).
         */
        NON_EMPTY
        ;
    }

}
