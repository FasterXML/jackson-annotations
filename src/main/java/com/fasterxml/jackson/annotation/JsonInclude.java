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
 *<p>
 * Note that inclusion criteria is checked on <b>Java object level</b>
 * and <b>NOT</b> on JSON output -- so even with {@link Include#NON_NULL}
 * it is possible that JSON null values are output, if object reference
 * in question is not `null`. An example is {@link java.util.concurrent.atomic.AtomicReference}
 * instance constructed to reference <code>null</code> value: such a value
 * would be serialized as JSON null, and not filtered out.
 * In such cases {@link Include#NON_EMPTY} should be used instead, since missing
 * reference (that is, reference to Java null) is considered "empty" (it is also
 * considered "default", so match {@link Include#NON_DEFAULT}).
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD,
    ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonInclude
{
    /**
     * Inclusion rule to use for instances (values) of types (Classes) or
     * properties annotated.
     */
    public Include value() default Include.ALWAYS;

    /**
     * Inclusion rule to use for entries ("content") of annotated
     * {@link java.util.Map}s; defaults to {@link Include#ALWAYS}.
     * 
     * @since 2.5
     */
    public Include content() default Include.ALWAYS;

    /*
    /**********************************************************
    /* Value enumerations needed
    /**********************************************************
     */

    /**
     * Enumeration used with {@link JsonInclude}
     * to define which properties
     * of Java Beans are to be included in serialization.
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
         * Value that indicates that properties are included unless their value
         * is:
         *<ul>
         *  <li>null</li>
         *  <li>"absent" value of a referential type (like Java 8 `Optional`, or
         *     {link java.utl.concurrent.atomic.AtomicReference}); that is, something
         *     that would not deference to a non-null value.
         * </ul>
         * This option is mostly used to work with "Optional"s (Java 8, Guava).
         *
         * @since 2.6
         */
        NON_ABSENT,

        /**
         * Value that indicates that only properties that have values
         * that values that are null or what is considered empty are
         * not to be included.
         * Definition of emptiness is data type specific; see below
         * for details on actual handling.
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
         * <li>For date/time types, if timestamp from Epoch is zero (January 1st, 1970, UTC),
         *    value is considered empty.
         *   </li>
         * </ul>
         *  and for other types, null values are excluded but other exclusions (if any).
         *<p>
         * Note that this default handling can be overridden by custom
         * <code>JsonSerializer</code> implementation: if method <code>isEmpty()</code>
         * is overridden, it will be called to see if non-null values are
         * considered empty (null is always considered empty).
         */
        NON_EMPTY,

        /**
         * Value that indicates that only properties that have values
         * that differ from default settings (meaning values they have
         * when Bean is constructed with its no-arguments constructor)
         * are to be included.
         *<p>
         * Note that value does not make sense for
         * {@link java.util.Map} types, since they have no default values;
         * and if used, works same as {@link #ALWAYS}.
         */
        NON_DEFAULT,
        
        /**
         * Pseudo-value used to indicate that the higher-level defaults make
         * sense, to avoid overriding inclusion value. For example, if returned
         * for a property this would use defaults for the class that contains
         * property, if any defined; and if none defined for that, then 
         * global serialization inclusion details.
         *
         * @since 2.6
         */
        USE_DEFAULTS
        
        ;
    }

    /*
    /**********************************************************
    /* Value class used to enclose information
    /**********************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JsonInclude}
     * annotation.
     *
     * @since 2.6
     */
    public static class Value
        implements JacksonAnnotationValue<JsonInclude>, // since 2.6
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        protected final static Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS);

        protected final Include _valueInclusion;
        protected final Include _contentInclusion;

        public Value(JsonInclude src) {
            this(src.value(), src.content());
        }

        protected Value(Include vi, Include ci) {
            _valueInclusion = (vi == null) ? Include.USE_DEFAULTS : vi;
            _contentInclusion = (ci == null) ? Include.USE_DEFAULTS : ci;
        }

        public static Value empty() {
            return EMPTY;
        }

        // for JDK serialization
        protected Object readResolve() {
            if ((_valueInclusion == Include.USE_DEFAULTS)
                    && (_contentInclusion == Include.USE_DEFAULTS)) {
                return EMPTY;
            }
            return this;
        }
        
        /**
         * Mutant factory method that merges values of this value with given override
         * values, so that any explicitly defined inclusion in overrides has precedence over
         * settings of this value instance. If no overrides exist will return <code>this</code>
         * instance; otherwise new {@link Value} with changed inclusion values.
         */
        public Value withOverrides(Value overrides) {
            if ((overrides == null) || (overrides == EMPTY)) {
                return this;
            }
            Include vi = overrides._valueInclusion;
            Include ci = overrides._contentInclusion;

            boolean viDiff = (vi != _valueInclusion) && (vi != Include.USE_DEFAULTS);
            boolean ciDiff = (ci != _valueInclusion) && (ci != Include.USE_DEFAULTS);

            if (viDiff) {
                if (ciDiff) {
                    return new Value(vi, ci);
                }
                return new Value(vi, _contentInclusion);
            }
            return this;
        }

        /**
         * Factory method to use for constructing an instance for components
         */
        public static Value construct(Include valueIncl, Include contentIncl) {
            if (((valueIncl == Include.USE_DEFAULTS) || (valueIncl == null))
                    && ((contentIncl == Include.USE_DEFAULTS) || (contentIncl == null))) {
                return EMPTY;
            }
            return new Value(valueIncl, contentIncl);
        }

        /**
         * Factory method to use for constructing an instance from instance of
         * {@link JsonInclude}
         */
        public static Value from(JsonInclude src) {
            if (src == null) { // should this return EMPTY?
                return null;
            }
            Include vi = src.value();
            Include ci = src.content();

            if ((vi == Include.USE_DEFAULTS) && (ci == Include.USE_DEFAULTS)) {
                return EMPTY;
            }
            return new Value(vi, ci);
        }

        public Value withValueInclusion(Include incl) {
            return (incl == _valueInclusion) ? this : new Value(incl, _contentInclusion);
        }

        public Value withContentInclusion(Include incl) {
            return (incl == _contentInclusion) ? this : new Value(_valueInclusion, incl);
        }

        @Override
        public Class<JsonInclude> valueFor() {
            return JsonInclude.class;
        }

        public Include getValueInclusion() {
            return _valueInclusion;
        }

        public Include getContentInclusion() {
            return _contentInclusion;
        }

        @Override
        public String toString() {
            return String.format("[value=%s,content=%s]", _valueInclusion, _contentInclusion);
        }

        @Override
        public int hashCode() {
            return (_valueInclusion.hashCode() << 2)
                    + _contentInclusion.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            Value other = (Value) o;
            
            return (other._valueInclusion == _valueInclusion)
                    && (other._contentInclusion == _contentInclusion);
        }
    }
}
