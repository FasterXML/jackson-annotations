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
 * Note that the main inclusion criteria (one annotated with {@link #value})
 * is checked on <b>Java object level</b>, for the annotated type,
 * and <b>NOT</b> on JSON output -- so even with {@link Include#NON_NULL}
 * it is possible that JSON null values are output, if object reference
 * in question is not `null`. An example is {@link java.util.concurrent.atomic.AtomicReference}
 * instance constructed to reference <code>null</code> value: such a value
 * would be serialized as JSON null, and not filtered out.
 *<p>
 * To base inclusion on value of contained value(s), you will typically also need
 * to specify {@link #content()} annotation; for example, specifying only
 * {@link #value} as {@link Include#NON_EMPTY} for a {link java.util.Map} would
 * exclude <code>Map</code>s with no values, but would include <code>Map</code>s
 * with `null` values. To exclude Map with only `null` value, you would use both
 * annotations like so:
 *<pre>
 *public class Bean {
 *   {@literal @JsonInclude}(value=Include.NON_EMPTY, content=Include.NON_NULL)
 *   public Map&lt;String,String&gt; entries;
 *}
 *</pre>
 * Similarly you could Maps that only contain
 * "empty" elements, or "non-default" values (see {@link Include#NON_EMPTY} and
 * {@link Include#NON_DEFAULT} for more details).
 *<p>
 * In addition to `Map`s, `content` concept is also supported for referential
 * types (like {@link java.util.concurrent.atomic.AtomicReference}).
 * Note that `content` is NOT currently (as of Jackson 2.9) supported for
 * arrays or {@link java.util.Collection}s, but supported may be added in
 * future versions.
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
     * properties annotated; defaults to {@link Include#ALWAYS}.
     */
    public Include value() default Include.ALWAYS;

    /**
     * Inclusion rule to use for entries ("content") of annotated
     * {@link java.util.Map}s and referential types (like
     * {@link java.util.concurrent.atomic.AtomicReference});
     * defaults to {@link Include#ALWAYS}.
     * 
     * @since 2.5
     */
    public Include content() default Include.ALWAYS;

    /**
     * Specifies type of "Filter Object" to use in case
     * {@link #value} is {@link JsonInclude.Include#CUSTOM}:
     * if so, an instance is created by calling <code>HandlerInstantiator</code>
     * (of <code>ObjectMapper</code>), which by default simply calls
     * zero-argument constructor of the Filter Class.
     *
     * @since 2.9
     */
    public Class<?> valueFilter() default Void.class;

    /**
     * Specifies type of "Filter Object" to use in case
     * {@link #content} is {@link JsonInclude.Include#CUSTOM}:
     * if so, an instance is created by calling <code>HandlerInstantiator</code>
     * (of <code>ObjectMapper</code>), which by default simply calls
     * zero-argument constructor of the Filter Class.
     *
     * @since 2.9
     */
    public Class<?> contentFilter() default Void.class;
    
    /*
    /**********************************************************
    /* Value enumerations
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
         * Value that indicates that only properties with null value,
         * or what is considered empty, are not to be included.
         * Definition of emptiness is data type specific; see below
         * for details on actual handling.
         *<p>
         * Default emptiness for all types includes:
         *<ul>
         * <li><code>Null</code> values.</li>
         * <li>"Absent" values (see {@link #NON_ABSENT})</li>
         *</ul>
         * so that as baseline, "empty" set includes values that would be
         * excluded by both {@link #NON_NULL} and {@link #NON_ABSENT}.
         *<br>
         * Beyond this base, following types have additional empty values:
         *<ul>
         * <li>For {@link java.util.Collection}s and {@link java.util.Map}s,
         *    method <code>isEmpty()</code> is called;
         *   </li>
         * <li>For Java arrays, empty arrays are ones with length of 0
         *   </li>
         * <li>For Java {@link java.lang.String}s, <code>length()</code> is called,
         *   and return value of 0 indicates empty String
         *   </li>
         * </ul>
         *  and for other types, null values are excluded but other exclusions (if any).
         *<p>
         * Note that this default handling can be overridden by custom
         * <code>JsonSerializer</code> implementation: if method <code>isEmpty()</code>
         * is overridden, it will be called to see if non-null values are
         * considered empty (null is always considered empty).
         *<p>
         * Compatibility note: Jackson 2.6 included a wider range of "empty" values than
         * either earlier (up to 2.5) or later (2.7 and beyond) types; specifically:
         *<ul>
         * <li>Default values of primitive types (like <code>0</code> for `int`/`java.lang.Integer`
         *  and `false` for `bool`/`Boolean`)
         *  </li>
         * <li>Timestamp 0 for date/time types
         *  </li>
         *</ul>
         * With 2.7, definition has been tightened back to only containing types explained
         * above (null, absent, empty String, empty containers), and now
         * extended definition may be specified using {@link #NON_DEFAULT}.
         */
        NON_EMPTY,

        /**
         * Meaning of this setting depends on context: whether annotation is
         * specified for POJO type (class), or not. In latter case annotation
         * is either used as the global default, or as property override.
         *<p>
         * When used for a POJO, definition is that only values that differ from
         * the default values of POJO properties are included. This is done
         * by creating an instance of POJO using zero-argument constructor,
         * and accessing property values: value is used as the default value
         * by using <code>equals()</code> method, except for the case where property
         * has `null` value in which case straight null check is used.
         *<p>
         * When NOT used for a POJO (that is, as a global default, or as property
         * override), definition is such that:
         *<ul>
         * <li>All values considered "empty" (as per {@link #NON_EMPTY}) are excluded</li>
         * <li>Primitive/wrapper default values are excluded</li>
         * <li>Date/time values that have timestamp (`long` value of milliseconds since
         *   epoch, see {@link java.util.Date}) of `0L` are excluded</li>
         * </ul>
         */
        NON_DEFAULT,

        /**
         * Value that indicates that separate `filter` Object (specified by
         * {@link JsonInclude#valueFilter} for value itself, and/or
         * {@link JsonInclude#contentFilter} for contents of structured types)
         * is to be used for determining inclusion criteria.
         * Filter object's <code>equals()</code> method is called with value
         * to serialize; if it returns <code>true</code> value is <b>excluded</b>
         * (that is, filtered out); if <code>false</code> value is <b>included</b>.
         *
         * @since 2.9
         */
        CUSTOM,
        
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

        protected final static Value EMPTY = new Value(Include.USE_DEFAULTS,
                Include.USE_DEFAULTS, null, null);

        protected final Include _valueInclusion;
        protected final Include _contentInclusion;

        /**
         * @since 2.9
         */
        protected final Class<?> _valueFilter;

        /**
         * @since 2.9
         */
        protected final Class<?> _contentFilter;
        
        public Value(JsonInclude src) {
            this(src.value(), src.content(),
                    src.valueFilter(), src.contentFilter());
        }

        protected Value(Include vi, Include ci,
                Class<?> valueFilter, Class<?> contentFilter) {
            _valueInclusion = (vi == null) ? Include.USE_DEFAULTS : vi;
            _contentInclusion = (ci == null) ? Include.USE_DEFAULTS : ci;
            _valueFilter = (valueFilter == Void.class) ? null : valueFilter;
            _contentFilter = (contentFilter == Void.class) ? null : contentFilter;
        }

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
         *
         * @since 2.8
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

        // for JDK serialization
        protected Object readResolve() {
            if ((_valueInclusion == Include.USE_DEFAULTS)
                    && (_contentInclusion == Include.USE_DEFAULTS)
                    && (_valueFilter == null)
                    && (_contentFilter == null)
                    ) {
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
            Class<?> vf = overrides._valueFilter;
            Class<?> cf = overrides._contentFilter;

            boolean viDiff = (vi != _valueInclusion) && (vi != Include.USE_DEFAULTS);
            boolean ciDiff = (ci != _contentInclusion) && (ci != Include.USE_DEFAULTS);
            boolean filterDiff = (vf != _valueFilter) || (cf != _valueFilter);

            if (viDiff) {
                if (ciDiff) {
                    return new Value(vi, ci, vf, cf);
                }
                return new Value(vi, _contentInclusion, vf, cf);
            } else if (ciDiff) {
                return new Value(_valueInclusion, ci, vf, cf);
            } else if (filterDiff) {
                return new Value(_valueInclusion, _contentInclusion, vf, cf);
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
            return new Value(valueIncl, contentIncl, null, null);
        }

        /**
         * Factory method to use for constructing an instance for components
         *
         * @since 2.9
         */
        public static Value construct(Include valueIncl, Include contentIncl,
                Class<?> valueFilter, Class<?> contentFilter)
        {
            if (valueFilter == Void.class) {
                valueFilter = null;
            }
            if (contentFilter == Void.class) {
                contentFilter = null;
            }
            if (((valueIncl == Include.USE_DEFAULTS) || (valueIncl == null))
                    && ((contentIncl == Include.USE_DEFAULTS) || (contentIncl == null))
                    && (valueFilter == null)
                    && (contentFilter == null)
                    ) {
                return EMPTY;
            }
            return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
        }
        
        /**
         * Factory method to use for constructing an instance from instance of
         * {@link JsonInclude}
         */
        public static Value from(JsonInclude src) {
            if (src == null) {
                return EMPTY;
            }
            Include vi = src.value();
            Include ci = src.content();

            if ((vi == Include.USE_DEFAULTS) && (ci == Include.USE_DEFAULTS)) {
                return EMPTY;
            }
            Class<?> vf = src.valueFilter();
            if (vf == Void.class) {
                vf = null;
            }
            Class<?> cf = src.contentFilter();
            if (cf == Void.class) {
                cf = null;
            }
            return new Value(vi, ci, vf, cf);
        }

        public Value withValueInclusion(Include incl) {
            return (incl == _valueInclusion) ? this
                    : new Value(incl, _contentInclusion, _valueFilter, _contentFilter);
        }

        /**
         * Mutant factory that will either
         *<ul>
         * <li>Set <code>value</code> as <code>USE_DEFAULTS</code>
         * and <code>valueFilter</code> to <code>filter</code> (if filter not null);
         * or</li>
         * <li>Set <code>value</code> as <code>ALWAYS</code> (if filter null)
         *  </li>
         *  </ul>
         *
         * @since 2.9
         */
        public Value withValueFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) { // clear filter
                incl = Include.USE_DEFAULTS;
                filter = null;
            } else {
                incl = Include.CUSTOM;
            }
            return construct(incl, _contentInclusion, filter, _contentFilter);
        }

        /**
         * Mutant factory that will either
         *<ul>
         * <li>Set <code>content</code> as <code>USE_DEFAULTS</code>
         * and <code>contentFilter</code> to <code>filter</code> (if filter not null);
         * or</li>
         * <li>Set <code>content</code> as <code>ALWAYS</code> (if filter null)
         *  </li>
         *  </ul>
         *
         * @since 2.9
         */
        public Value withContentFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) { // clear filter
                incl = Include.USE_DEFAULTS;
                filter = null;
            } else {
                incl = Include.CUSTOM;
            }
            return construct(_valueInclusion, incl, _valueFilter, filter);
        }

        public Value withContentInclusion(Include incl) {
            return (incl == _contentInclusion) ? this
                    : new Value(_valueInclusion, incl, _valueFilter, _contentFilter);
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

        public Class<?> getValueFilter() {
            return _valueFilter;
        }

        public Class<?> getContentFilter() {
            return _contentFilter;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(80);
            sb.append("JsonInclude.Value(value=")
                .append(_valueInclusion)
                .append(",content=")
                .append(_contentInclusion);
            if (_valueFilter != null) {
                sb.append(",valueFilter=").append(_valueFilter.getName()).append(".class");
            }
            if (_contentFilter != null) {
                sb.append(",contentFilter=").append(_contentFilter.getName()).append(".class");
            }
            return sb.append(')').toString();
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
                    && (other._contentInclusion == _contentInclusion)
                    && (other._valueFilter == _valueFilter)
                    && (other._contentFilter == _contentFilter)
                    ;
        }
    }
}
