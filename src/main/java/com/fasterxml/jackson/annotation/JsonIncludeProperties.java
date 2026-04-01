package com.fasterxml.jackson.annotation;

import java.lang.annotation.*;
import java.util.*;

/**
 * Annotation that can be used to either only include serialization of
 * properties (during serialization), or only include processing of
 * JSON properties read (during deserialization).
 * <p>
 * Example:
 * <pre>
 * // to only include specified fields from being serialized or deserialized
 * // (i.e. only include in JSON output; or being set even if they were included)
 * &#064;JsonIncludeProperties({ "internalId", "secretKey" })
 * </pre>
 * <p>
 * Annotation can be applied both to classes and
 * to properties. If used for both, actual set will be union of all
 * includes: that is, you can only add properties to include, not remove
 * or override. So you can not remove properties to include using
 * per-property annotation.
 *
 * @since 2.12
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIncludeProperties
{
    /**
     * Names of properties to include.
     */
    public String[] value() default {};

    /**
     * Property that can be enabled to indicate that the order of properties
     * in {@link #value()} is the order in which properties should be serialized;
     * that is, {@link #value()} should be used as if it was
     * {@link JsonPropertyOrder#value()} (unless that annotation already
     * exists. This is useful in reducing amount of annotation duplication.
     *<p>
     * Property defaults to {@link OptBoolean#DEFAULT}, meaning "undefined"
     * (which effectively translates into {@code OptBoolean.FALSE}).
     *
     * @since 2.22
     */
    public OptBoolean order() default OptBoolean.DEFAULT;

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings.
    /**********************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JsonIncludeProperties}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     *
     * @since 2.12
     */
    public static class Value implements JacksonAnnotationValue<JsonIncludeProperties>,
        java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        /**
         * Default instance has no explicitly included fields
         */
        protected final static JsonIncludeProperties.Value ALL = new JsonIncludeProperties.Value(null, null);

        /**
         * Name of the properties to include.
         * Null means that all properties are included, empty means none.
         */
        protected final Set<String> _included;

        /**
         * Whether the order of properties in {@link #_included} defines
         * the serialization order. {@code null} indicates "not defined"
         * (and generally is used as {@code Boolean.FALSE}).
         *
         * @since 2.22
         */
        protected final Boolean _ordered;

        /**
         * @deprecated Since 2.22, use {@link #Value(Set, Boolean)} instead.
         */
        @Deprecated
        protected Value(Set<String> included)
        {
            this(included, null);
        }

        /**
         * @since 2.22
         */
        protected Value(Set<String> included, Boolean ordered)
        {
            _included = included;
            _ordered = ordered;
        }

        public static JsonIncludeProperties.Value from(JsonIncludeProperties src)
        {
            if (src == null) {
                return ALL;
            }
            return new Value(_asSet(src.value()), src.order().asBoolean());
        }

        public static JsonIncludeProperties.Value all()
        {
            return ALL;
        }

        @Override
        public Class<JsonIncludeProperties> valueFor()
        {
            return JsonIncludeProperties.class;
        }

        /**
         * @return Names included, if any, possibly empty; {@code null} for "not defined"
         */
        public Set<String> getIncluded()
        {
            return _included;
        }

        /**
         * @return Whether the order of properties in {@link #getIncluded()} defines
         *   the serialization order; {@code null} if not set.
         *
         * @since 2.22
         */
        public Boolean getOrdered()
        {
            return _ordered;
        }

        /**
         * Mutant factory method to override the current value with an another,
         * merging the included fields so that only entries that exist in both original
         * and override set are included, taking into account that "undefined" {@link Value}s
         * do not count ("undefined" meaning that {@code getIncluded()} returns {@code null}).
         * So: overriding with "undefined" returns original {@code Value} as-is; overriding an
         * "undefined" {@code Value} returns override {@code Value} as-is.
         */
        public JsonIncludeProperties.Value withOverrides(JsonIncludeProperties.Value overrides)
        {
            final Set<String> otherIncluded;

            if (overrides == null || (otherIncluded = overrides.getIncluded()) == null) {
                return this;
            }

            if (_included == null) {
                return overrides;
            }

            HashSet<String> toInclude = new HashSet<String>();
            for (String incl : otherIncluded) {
                if (_included.contains(incl)) {
                    toInclude.add(incl);
                }
            }

            Boolean ordered = (overrides._ordered != null) ? overrides._ordered : _ordered;
            return new JsonIncludeProperties.Value(toInclude, ordered);
        }

        @Override
        public String toString() {
            return String.format("JsonIncludeProperties.Value(included=%s,ordered=%s)",
                    _included, _ordered);
        }

        @Override
        public int hashCode() {
            return ((_included == null) ? 0 : _included.hashCode())
                    + (Boolean.TRUE.equals(_ordered) ? 1 : 0);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            return (o.getClass() == getClass())
                    && Objects.equals(_ordered, ((Value) o)._ordered)
                    && Objects.equals(_included, ((Value) o)._included);
        }

        private static Set<String> _asSet(String[] v)
        {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            Set<String> s = new HashSet<String>(v.length);
            for (String str : v) {
                s.add(str);
            }
            return s;
        }
    }
}
