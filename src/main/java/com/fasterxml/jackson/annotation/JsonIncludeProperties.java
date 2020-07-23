package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.swing.text.html.HTMLDocument;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    public static class Value implements JacksonAnnotationValue<JsonIncludeProperties>, java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        /**
         * Default instance has no explicitly included fields
         */
        protected final static JsonIncludeProperties.Value ALL = new JsonIncludeProperties.Value(null);

        /**
         * Name of the properties to include.
         * Null means that all properties are included, empty means none.
         */
        protected final Set<String> _included;

        protected Value(Set<String> included)
        {
            _included = included;
        }

        public static JsonIncludeProperties.Value from(JsonIncludeProperties src)
        {
            if (src == null) {
                return ALL;
            }

            return new Value(_asSet(src.value()));
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

        public Set<String> getIncluded()
        {
            return _included;
        }

        /**
         * Mutant factory method to override the current value with an another, merging the included fields.
         */
        public JsonIncludeProperties.Value withOverrides(JsonIncludeProperties.Value overrides) {
            if (overrides == null || overrides.getIncluded() == null) {
                return this;
            }

            if (_included == null) {
                return overrides;
            }

            HashSet<String> included = new HashSet<String>(_included);
            Iterator<String> iterator = included.iterator();
            while (iterator.hasNext()) {
                if (!overrides.getIncluded().contains(iterator.next())) {
                    iterator.remove();
                }
            }

            return new JsonIncludeProperties.Value(new HashSet<String>(included));
        }

        @Override
        public String toString() {
            return String.format("JsonIncludeProperties.Value(included=%s)",
                    _included);
        }

        @Override
        public int hashCode() {
            return (_included == null ? 0 : _included.size())
                    ;
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
            return  a._included == null ? b._included == null :
                    // keep this last just because it can be expensive
                    a._included.equals(b._included)
                    ;
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
