package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

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
 * Annotation can be applied both to classes and
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

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings.
    /**********************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JsonIgnoreProperties}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     *
     * @since 2.8
     */
    public static class Value
        implements JacksonAnnotationValue<JsonIgnoreProperties>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        /**
         * Default instance has no explicitly ignored fields, does not ignore unknowns,
         * does not explicitly allow getters/setters (that is, ignorals apply to both),
         * but does use merging for combining overrides with base settings
         */
        protected final static Value EMPTY = new Value(Collections.<String>emptySet(),
                false, false, false, true);

        /**
         * Names of properties to ignore.
         */
        protected final Set<String> _ignored;

        protected final boolean _ignoreUnknown;
        protected final boolean _allowGetters;
        protected final boolean _allowSetters;

        protected final boolean _merge;

        protected Value(Set<String> ignored, boolean ignoreUnknown,
                boolean allowGetters, boolean allowSetters,
                boolean merge)
        {
            if (ignored == null) {
                _ignored = Collections.emptySet();
            } else {
                _ignored = ignored;
            }
            _ignoreUnknown = ignoreUnknown;
            _allowGetters = allowGetters;
            _allowSetters = allowSetters;
            _merge = merge;
        }

        public static Value from(JsonIgnoreProperties src) {
            if (src == null) {
                return EMPTY; // since 2.9
            }
            return construct(_asSet(src.value()),
                    src.ignoreUnknown(), src.allowGetters(), src.allowSetters(),
                // 27-Apr-2016, tatu: No matching property in annotation because
                //   we don't know how to merge (so no point in pretending it's there)
                //   so choice is arbitrary. Probably will default to `false` fwtw:
                    false);
        }

        /**
         * Factory method that may be used (although is NOT the recommended way)
         * to construct an instance from a full set of properties. Most users would
         * be better of starting by {@link #empty()} instance and using `withXxx()`/`withoutXxx()`
         * methods, as this factory method may need to be changed if new properties
         * are added in {@link JsonIgnoreProperties} annotation.
         */
        public static Value construct(Set<String> ignored, boolean ignoreUnknown,
                boolean allowGetters, boolean allowSetters,
                boolean merge) {
            if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
                return EMPTY;
            }
            return new Value(ignored, ignoreUnknown, allowGetters, allowSetters, merge);
        }

        /**
         * Accessor for default instances which has "empty" settings; that is:
         *<ul>
         * <li>No explicitly defined fields to ignore
         *  </li>
         * <li>Does not ignore unknown fields
         *  </li>
         * <li>Does not "allow" getters if property ignored (that is, ignorals apply to both setter and getter)
         *  </li>
         * <li>Does not "allow" setters if property ignored (that is, ignorals apply to both setter and getter)
         *  </li>
         * <li>Does use merge when combining overrides to base settings, such that `true` settings
         *   for any of the properties results in `true`, and names of fields are combined (union)
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

        /**
         * @since 2.8
         */
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

        public static Value forIgnoredProperties(Set<String> propNames) {
            return EMPTY.withIgnored(propNames);
        }

        public static Value forIgnoredProperties(String... propNames) {
            if (propNames.length == 0) {
                return EMPTY;
            }
            return EMPTY.withIgnored(_asSet(propNames));
        }

        public static Value forIgnoreUnknown(boolean state) {
            return state ? EMPTY.withIgnoreUnknown()
                    : EMPTY.withoutIgnoreUnknown();
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
            // if non merging, we'll actually end up with just the overrides don't we?
            // (given there's no "use default" value for anything
            if (!overrides._merge) {
                return overrides;
            }
            if (_equals(this, overrides)) {
                return this;
            }

            // Here's where mergeability needs to be checked
            Set<String> ignored = _merge(_ignored, overrides._ignored);
            boolean ignoreUnknown = _ignoreUnknown || overrides._ignoreUnknown;
            boolean allowGetters = _allowGetters || overrides._allowGetters;
            boolean allowSetters = _allowSetters || overrides._allowSetters;

            // must have 'merge=true' to get this far
            return construct(ignored, ignoreUnknown, allowGetters, allowSetters, true);
        }

        public Value withIgnored(Set<String> ignored) {
            return construct(ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
        }

        public Value withIgnored(String... ignored) {
            return construct(_asSet(ignored), _ignoreUnknown, _allowGetters, _allowSetters, _merge);
        }
        
        public Value withoutIgnored() {
            return construct(null, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
        }
        
        public Value withIgnoreUnknown() {
            return _ignoreUnknown ? this :
                construct(_ignored, true, _allowGetters, _allowSetters, _merge);
        }
        public Value withoutIgnoreUnknown() {
            return !_ignoreUnknown ? this :
                construct(_ignored, false, _allowGetters, _allowSetters, _merge);
        }

        public Value withAllowGetters() {
            return _allowGetters ? this :
                construct(_ignored, _ignoreUnknown, true, _allowSetters, _merge);
        }
        public Value withoutAllowGetters() {
            return !_allowGetters ? this :
                construct(_ignored, _ignoreUnknown, false, _allowSetters, _merge);
        }

        public Value withAllowSetters() {
            return _allowSetters ? this :
                construct(_ignored, _ignoreUnknown, _allowGetters, true, _merge);
        }
        public Value withoutAllowSetters() {
            return !_allowSetters ? this :
                construct(_ignored, _ignoreUnknown, _allowGetters, false, _merge);
        }
        
        public Value withMerge() {
            return _merge ? this :
                construct(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, true);
        }

        public Value withoutMerge() {
            return !_merge ? this :
                construct(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, false);
        }
        
        @Override
        public Class<JsonIgnoreProperties> valueFor() {
            return JsonIgnoreProperties.class;
        }

        // for JDK serialization
        protected Object readResolve() {
            if (_empty(_ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge)) {
                return EMPTY;
            }
            return this;
        }

        public Set<String> getIgnored() {
            return _ignored;
        }

        /**
         * Method called to find names of properties to ignore when used for
         * serialization: functionally
         * same as {@link #getIgnored} if {@link #getAllowGetters()} is false
         * (that is, there is "allowGetters=false" or equivalent),
         * otherwise returns empty {@link java.util.Set}.
         */
        public Set<String> findIgnoredForSerialization() {
            if (_allowGetters) {
                return Collections.emptySet();
            }
            return _ignored;
        }

        /**
         * Method called to find names of properties to ignore when used for
         * serialization: functionally
         * same as {@link #getIgnored} if {@link #getAllowSetters()} is false
         * (that is, there is "allowSetters=false" or equivalent),
         * otherwise returns empty {@link java.util.Set}.
         */
        public Set<String> findIgnoredForDeserialization() {
            if (_allowSetters) {
                return Collections.emptySet();
            }
            return _ignored;
        }
        
        public boolean getIgnoreUnknown() {
            return _ignoreUnknown;
        }

        public boolean getAllowGetters() {
            return _allowGetters;
        }

        public boolean getAllowSetters() {
            return _allowSetters;
        }

        public boolean getMerge() {
            return _merge;
        }

        @Override
        public String toString() {
            return String.format("JsonIgnoreProperties.Value(ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s)",
                    _ignored, _ignoreUnknown, _allowGetters, _allowSetters, _merge);
        }

        @Override
        public int hashCode() {
            return (_ignored.size())
                    + (_ignoreUnknown ? 1 : -3)
                    + (_allowGetters ? 3 : -7)
                    + (_allowSetters ? 7 : -11)
                    + (_merge ? 11 : -13)
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
            return (a._ignoreUnknown == b._ignoreUnknown)
                    && (a._merge == b._merge)
                    && (a._allowGetters == b._allowGetters)
                    && (a._allowSetters == b._allowSetters)
                    // this last just because it can be expensive
                    && a._ignored.equals(b._ignored)
                    ;
        }

        private static Set<String> _asSet(String[] v) {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            Set<String> s = new HashSet<String>(v.length);
            for (String str : v) {
                s.add(str);
            }
            return s;
        }

        private static Set<String> _merge(Set<String> s1, Set<String> s2)
        {
            if (s1.isEmpty()) {
                return s2;
            } else if (s2.isEmpty()) {
                return s1;
            }
            HashSet<String> result = new HashSet<String>(s1.size() + s2.size());
            result.addAll(s1);
            result.addAll(s2);
            return result;
        }

        private static boolean _empty(Set<String> ignored, boolean ignoreUnknown,
                boolean allowGetters, boolean allowSetters, boolean merge)
        {
            if ((ignoreUnknown == EMPTY._ignoreUnknown)
                    && (allowGetters == EMPTY._allowGetters)
                    && (allowSetters == EMPTY._allowSetters)
                    && (merge == EMPTY._merge)) {
                return (ignored == null || ignored.size() == 0);
            }
            return false;
        }
    }
}
