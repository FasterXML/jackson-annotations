package com.fasterxml.jackson.annotation;

import java.lang.annotation.*;

/**
 * Annotation that can be used to define a non-static,
 * single-argument method to be used as a "setter" for a logical property
 * as an alternative to recommended
 * {@link JsonProperty} annotation;
 * or (as of 2.9 and later), specify additional aspects of the
 * assigning property a value during serialization.
 *<p>
 *
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSetter
{
    /**
     * Optional default argument that defines logical property this
     * method is used to modify ("set"); this is the property
     * name used in JSON content.
     */
    String value() default "";

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings.
    /**********************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JsonSetter}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     *
     * @since 2.8
     */
    public static class Value
        implements JacksonAnnotationValue<JsonSetter>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        /**
         * Default instance used in place of "default settings".
         */
        protected final static Value EMPTY = new Value();

        protected Value()
        {
        }

        public static Value from(JsonIgnoreProperties src) {
            return construct();
        }

        /**
         * Factory method that may be used (although is NOT the recommended way)
         * to construct an instance from a full set of properties. Most users would
         * be better of starting by {@link #empty()} instance and using `withXxx`/`withoutXxx`
         * methods, as this factory method may need to be changed if new properties
         * are added in {@link JsonIgnoreProperties} annotation.
         */
        public static Value construct() {
            /*
            if (...) {
                return EMPTY;
            }
            */
            return new Value();
        }

        /**
         * Accessor for default instances which has "empty" settings; that is:
         *<ul>
         * <li>
         *  </li>
         * </ul>
         */
        public static Value empty() {
            return EMPTY;
        }

        /**
         * Helper method that will try to combine values from two {@link Value}
         * instances, using one as base settings, and the other as overrides
         * to use instead of base values when defined; base values are only
         * use if override does not specify a value (matching value is null
         * or logically missing).
         * Note that one or both of value instances may be `null`, directly;
         * if both are `null`, result will also be `null`; otherwise never null.
         */
        public static Value merge(Value base, Value overrides)
        {
            return (base == null) ? overrides
                    : base.withOverrides(overrides);
        }

        public static Value mergeAll(Value... values)
        {
            Value result = null;
            for (Value curr : values) {
                if (curr != null) {
                    result = (result == null)  ? curr : result.withOverrides(curr);
                }
            }
            return result;
        }

        /*
        public static Value forIgnoredProperties(Set<String> propNames) {
            return EMPTY.withIgnored(propNames);
        }
        */

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
            // must have 'merge=true' to get this far
            return construct();
        }

        @Override
        public Class<JsonSetter> valueFor() {
            return JsonSetter.class;
        }

        // for JDK serialization
        protected Object readResolve() {
            if (_empty()) {
                return EMPTY;
            }
            return this;
        }

        @Override
        public String toString() {
            /*
            return String.format("[ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s]",
                    _ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
                    */
            return "[]";
        }

        @Override
        public int hashCode() {
            return -1;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            return (o.getClass() == getClass())
                    && _equals(this, (Value) o);
        }

        private static boolean _equals(Value a, Value b)
        {
            return true;
            /*
            return (a._ignoreUnknown == b._ignoreUnknown)
                    && (a._merge == b._merge)
                    && (a._allowGetters == b._allowGetters)
                    && (a._allowSetters == b._allowSetters)
                    // this last just because it can be expensive
                    && a._ignored.equals(b._ignored)
                    ;
                    */
        }

        private static boolean _empty()
        {
            return true;
        }
    }
}
