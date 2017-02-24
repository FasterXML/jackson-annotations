package com.fasterxml.jackson.annotation;

import java.lang.annotation.*;
import java.util.Locale;
import java.util.TimeZone;

/**
 * General-purpose annotation used for configuring details of how
 * values of properties are to be serialized.
 * Unlike most other Jackson annotations, annotation does not
 * have specific universal interpretation: instead, effect depends on datatype
 * of property being annotated (or more specifically, deserializer
 * and serializer being used).
 *<p>
 * Common uses include choosing between alternate representations -- for example,
 * whether {@link java.util.Date} is to be serialized as number (Java timestamp)
 * or String (such as ISO-8601 compatible time value) -- as well as configuring
 * exact details with {@link #pattern} property.
 *<p>
 * As of Jackson 2.6, known special handling includes:
 *<ul>
 * <li>{@link java.util.Date}: Shape can  be {@link Shape#STRING} or {@link Shape#NUMBER};
 *    pattern may contain {@link java.text.SimpleDateFormat}-compatible pattern definition.
 *   </li>
 * <li>Can be used on Classes (types) as well, for modified default behavior, possibly
 *   overridden by per-property annotation
 *   </li>
 * <li>{@link java.lang.Enum}s: Shapes {@link Shape#STRING} and {@link Shape#NUMBER} can be
 *    used to change between numeric (index) and textual (name or <code>toString()</code>);
 *    but it is also possible to use {@link Shape#OBJECT} to serialize (but not deserialize)
 *    {@link java.lang.Enum}s as JSON Objects (as if they were POJOs). NOTE: serialization
 *     as JSON Object only works with class annotation; 
 *    will not work as per-property annotation.
 *   </li>
 * <li>{@link java.util.Collection}s can be serialized as (and deserialized from) JSON Objects,
 *    if {@link Shape#OBJECT} is used. NOTE: can ONLY be used as class annotation;
 *    will not work as per-property annotation.
 *   </li>
 * <li>{@link java.lang.Number} subclasses can be serialized as full objects if
 *    {@link Shape#OBJECT} is used. Otherwise the default behavior of serializing to a
 *    scalar number value will be preferred. NOTE: can ONLY be used as class annotation;
 *    will not work as per-property annotation.
 *   </li>
 *</ul>
 *
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
    ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat
{
    /**
     * Value that indicates that default {@link java.util.Locale}
     * (from deserialization or serialization context) should be used:
     * annotation does not define value to use.
     */
    public final static String DEFAULT_LOCALE = "##default";

    /**
     * Value that indicates that default {@link java.util.TimeZone}
     * (from deserialization or serialization context) should be used:
     * annotation does not define value to use.
     *<p>
     * NOTE: default here does NOT mean JVM defaults but Jackson databindings
     * default, usually UTC, but may be changed on <code>ObjectMapper</code>.
     */
    public final static String DEFAULT_TIMEZONE = "##default";
    
    /**
     * Datatype-specific additional piece of configuration that may be used
     * to further refine formatting aspects. This may, for example, determine
     * low-level format String used for {@link java.util.Date} serialization;
     * however, exact use is determined by specific <code>JsonSerializer</code>
     */
    public String pattern() default "";

    /**
     * Structure to use for serialization: definition of mapping depends on datatype,
     * but usually has straight-forward counterpart in data format (JSON).
     * Note that commonly only a subset of shapes is available; and if 'invalid' value
     * is chosen, defaults are usually used.
     */
    public Shape shape() default Shape.ANY;

    /**
     * {@link java.util.Locale} to use for serialization (if needed).
     * Special value of {@link #DEFAULT_LOCALE}
     * can be used to mean "just use the default", where default is specified
     * by the serialization context, which in turn defaults to system
     * defaults ({@link java.util.Locale#getDefault()}) unless explicitly
     * set to another locale.
     */
    public String locale() default DEFAULT_LOCALE;
    
    /**
     * {@link java.util.TimeZone} to use for serialization (if needed).
     * Special value of {@link #DEFAULT_TIMEZONE}
     * can be used to mean "just use the default", where default is specified
     * by the serialization context, which in turn defaults to system
     * default (UTC) unless explicitly set to another timezone.
     */
    public String timezone() default DEFAULT_TIMEZONE;

    /**
     * Property that indicates whether "lenient" handling should be enabled or
     * disabled. This is relevant mostly for deserialization of some textual
     * datatypes, especially date/time types.
     *<p>
     * Note that underlying default setting depends on datatype (or more precisely
     * deserializer for it): for most date/time types, default is for leniency
     * to be enabled.
     * 
     * @since 2.9
     */
    public OptBoolean lenient() default OptBoolean.DEFAULT;

    /**
     * Set of {@link JsonFormat.Feature}s to explicitly enable with respect
     * to handling of annotated property. This will have precedence over possible
     * global configuration.
     *
     * @since 2.6
     */
    public JsonFormat.Feature[] with() default { };

    /**
     * Set of {@link JsonFormat.Feature}s to explicitly disable with respect
     * to handling of annotated property. This will have precedence over possible
     * global configuration.
     *
     * @since 2.6
     */
    public JsonFormat.Feature[] without() default { };

    /*
    /**********************************************************
    /* Value enumeration(s), value class(es)
    /**********************************************************
     */

    /**
     * Value enumeration used for indicating preferred Shape; translates
     * loosely to JSON types, with some extra values to indicate less precise
     * choices (i.e. allowing one of multiple actual shapes)
     */
    public enum Shape
    {
        /**
         * Marker enum value that indicates "whatever" choice, meaning that annotation
         * does NOT specify shape to use.
         * Note that this is different from {@link Shape#NATURAL}, which
         * specifically instructs use of the "natural" shape for datatype.
         */
        ANY,

        /**
         * Marker enum value that indicates the "default" choice for given datatype;
         * for example, JSON String for {@link java.lang.String}, or JSON Number
         * for Java numbers.
         * Note that this is different from {@link Shape#ANY} in that this is actual
         * explicit choice that overrides possible default settings.
         *
         * @since 2.8
         */
        NATURAL,
        
        /**
         * Value that indicates shape should not be structural (that is, not
         * {@link #ARRAY} or {@link #OBJECT}, but can be any other shape.
         */
        SCALAR,

        /**
         * Value that indicates that (JSON) Array type should be used.
         */
        ARRAY,
        
        /**
         * Value that indicates that (JSON) Object type should be used.
         */
        OBJECT,

        /**
         * Value that indicates that a numeric (JSON) type should be used
         * (but does not specify whether integer or floating-point representation
         * should be used)
         */
        NUMBER,

        /**
         * Value that indicates that floating-point numeric type should be used
         */
        NUMBER_FLOAT,

        /**
         * Value that indicates that integer number type should be used
         * (and not {@link #NUMBER_FLOAT}).
         */
        NUMBER_INT,

        /**
         * Value that indicates that (JSON) String type should be used.
         */
        STRING,
        
        /**
         * Value that indicates that (JSON) boolean type
         * (true, false) should be used.
         */
        BOOLEAN
        ;

        public boolean isNumeric() {
            return (this == NUMBER) || (this == NUMBER_INT) || (this == NUMBER_FLOAT);
        }

        public boolean isStructured() {
            return (this == OBJECT) || (this == ARRAY);
        }
    }

    /**
     * Set of features that can be enabled/disabled for property annotated.
     * These often relate to specific <code>SerializationFeature</code>
     * or <code>DeserializationFeature</code>, as noted by entries.
     *<p>
     * Note that whether specific setting has an effect depends on whether
     * <code>JsonSerializer</code> / <code>JsonDeserializer</code> being used
     * takes the format setting into account. If not, please file an issue
     * for adding support via issue tracker for package that has handlers
     * (if you know which one; if not, just use `jackson-databind`).
     *
     * @since 2.6
     */
    public enum Feature {
        /**
         * Override for <code>DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY</code>
         * which will allow deserialization of JSON non-array values into single-element
         * Java arrays and {@link java.util.Collection}s.
         */
        ACCEPT_SINGLE_VALUE_AS_ARRAY,

        /**
         * Override for <code>MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES</code>.
         * Only affects deserialization, has no effect on serialization.
         *<p>
         * NOTE: starting with 2.9 can also effect Enum handling (and potentially other
         * places where case-insensitive property values are accepted).
         * 
         * @since 2.8
         */
        ACCEPT_CASE_INSENSITIVE_PROPERTIES,

        /**
         * Override for <code>SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS</code>,
         * similar constraints apply.
         */
        WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,

        /**
         * Override for <code>SerializationFeature.WRITE_DATES_WITH_ZONE_ID</code>,
         * similar constraints apply.
         */
        WRITE_DATES_WITH_ZONE_ID,

        /**
         * Override for <code>SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED</code>
         * which will force serialization of single-element arrays and {@link java.util.Collection}s
         * as that single element and excluding array wrapper.
         */
        WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,

        /**
         * Override for <code>SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS</code>,
         * enabling of which will force sorting of {@link java.util.Map} keys before
         * serialization.
         */
        WRITE_SORTED_MAP_ENTRIES,

        /**
         * Override for <code>DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIMEZONE</code>
         * that specifies whether context provided timezone
         * <code>DeserializationContext.getTimeZone()</code> should be used to adjust Date/Time
         * values on deserialization, even if value itself contains timezone informatio
         *<p>
         * NOTE: due to limitations of "old" JDK date/time types (that is,
         * {@link java.util.Date} and {@link java.util.Calendar}), this setting is only
         * applicable to <code>Joda</code> and <code>Java 8 date/time</code> values,
         * but not to <code>java.util.Date</code> or <code>java.util.Calendar</code>.
         *
         * @since 2.8
         */
        ADJUST_DATES_TO_CONTEXT_TIME_ZONE
    }

    /**
     * Helper class that encapsulates information equivalent to {@link java.lang.Boolean}
     * valued {@link java.util.EnumMap}.
     *
     * @since 2.6
     */
    public static class Features
    {
        private final int _enabled, _disabled;

        private final static Features EMPTY = new Features(0, 0);
        
        private Features(int e, int d) {
            _enabled = e;
            _disabled = d;
        }

        public static Features empty() {
            return EMPTY;
        }
        
        public static Features construct(JsonFormat f) {
            return construct(f.with(), f.without());
        }
        
        public static Features construct(Feature[] enabled, Feature[] disabled)
        {
            int e = 0;
            for (Feature f : enabled) {
                e |= (1 << f.ordinal());
            }
            int d = 0;
            for (Feature f : disabled) {
                d |= (1 << f.ordinal());
            }
            return new Features(e, d);
        }

        public Features withOverrides(Features overrides) {
            // Cheap checks first: maybe one is empty?
            if (overrides == null) {
                return this;
            }
            int overrideD = overrides._disabled;
            int overrideE = overrides._enabled;
            if ((overrideD == 0) && (overrideE == 0)) {
                return this;
            }
            if ((_enabled == 0) && (_disabled == 0)) {
                return overrides;
            }
            // If not, calculate combination with overrides
            int newE = (_enabled & ~overrideD) | overrideE;
            int newD = (_disabled & ~overrideE) | overrideD;
            
            // one more thing; no point in creating new instance if there's no change
            if ((newE == _enabled) && (newD == _disabled)) {
                return this;
            }
            
            return new Features(newE, newD);
        }

        public Features with(Feature...features) {
            int e = _enabled;
            for (Feature f : features) {
                e |= (1 << f.ordinal());
            }
            return (e == _enabled) ? this : new Features(e, _disabled);
        }

        public Features without(Feature...features) {
            int d = _disabled;
            for (Feature f : features) {
                d |= (1 << f.ordinal());
            }
            return (d == _disabled) ? this : new Features(_enabled, d);
        }
        
        public Boolean get(Feature f) {
            int mask = (1 << f.ordinal());
            if ((_disabled & mask) != 0) {
                return Boolean.FALSE;
            }
            if ((_enabled & mask) != 0) {
                return Boolean.TRUE;
            }
            return null;
        }

        @Override
        public int hashCode() {
            return _disabled + _enabled;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            Features other = (Features) o;
            return (other._enabled == _enabled) && (other._disabled == _disabled);
        }
    }
    
    /**
     * Helper class used to contain information from a single {@link JsonFormat}
     * annotation.
     */
    public static class Value
        implements JacksonAnnotationValue<JsonFormat>, // since 2.6
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        private final static Value EMPTY = new Value();

        private final String _pattern;
        private final Shape _shape;
        private final Locale _locale;

        private final String _timezoneStr;

        /**
         * @since 2.9
         */
        private final Boolean _lenient;

        /**
         * @since 2.6
         */
        private final Features _features;

        // lazily constructed when created from annotations
        private transient TimeZone _timezone;
        
        public Value() {
            this("", Shape.ANY, "", "", Features.empty(), null);
        }
        
        public Value(JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(),
                    Features.construct(ann), ann.lenient().asBoolean());
        }

        /**
         * @since 2.9
         */
        public Value(String p, Shape sh, String localeStr, String tzStr, Features f,
                Boolean lenient)
        {
            this(p, sh,
                    (localeStr == null || localeStr.length() == 0 || DEFAULT_LOCALE.equals(localeStr)) ?
                            null : new Locale(localeStr),
                    (tzStr == null || tzStr.length() == 0 || DEFAULT_TIMEZONE.equals(tzStr)) ?
                            null : tzStr,
                    null, f, lenient);
        }

        /**
         * @since 2.9
         */
        public Value(String p, Shape sh, Locale l, TimeZone tz, Features f,
                Boolean lenient)
        {
            _pattern = p;
            _shape = (sh == null) ? Shape.ANY : sh;
            _locale = l;
            _timezone = tz;
            _timezoneStr = null;
            _features = (f == null) ? Features.empty() : f;
            _lenient = lenient;
        }

        /**
         * @since 2.9
         */
        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f,
                Boolean lenient)
        {
            _pattern = p;
            _shape = (sh == null) ? Shape.ANY : sh;
            _locale = l;
            _timezone = tz;
            _timezoneStr = tzStr;
            _features = (f == null) ? Features.empty() : f;
            _lenient = lenient;
        }

        @Deprecated // since 2.9
        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f) {
            this(p, sh, l, tzStr, tz, f, null);
        }
        
        @Deprecated // since 2.9
        public Value(String p, Shape sh, String localeStr, String tzStr, Features f) {
            this(p, sh, localeStr, tzStr, f, null);
        }
        @Deprecated // since 2.9
        public Value(String p, Shape sh, Locale l, TimeZone tz, Features f) {
            this(p, sh, l, tz, f, null);
        }
        
        /**
         * @since 2.7
         */
        public final static Value empty() {
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
        
        /**
         * @since 2.7
         */
        public final static Value from(JsonFormat ann) {
            return (ann == null) ? EMPTY : new Value(ann);
        }

        /**
         * @since 2.7
         */
        public final Value withOverrides(Value overrides) {
            if ((overrides == null) || (overrides == EMPTY) || (overrides == this)) {
                return this;
            }
            if (this == EMPTY) { // cheesy, but probably common enough
                return overrides;
            }
            String p = overrides._pattern;
            if ((p == null) || p.isEmpty()) {
                p = _pattern;
            }
            Shape sh = overrides._shape;
            if (sh == Shape.ANY) {
                sh = _shape;
            }
            Locale l = overrides._locale;
            if (l == null) {
                l = _locale;
            }
            Features f = _features;
            if (f == null) {
                f = overrides._features;
            } else {
                f = f.withOverrides(overrides._features);
            }
            Boolean lenient = overrides._lenient;
            if (lenient == null) {
                lenient = _lenient;
            }

            // timezone not merged, just choose one
            String tzStr = overrides._timezoneStr;
            TimeZone tz;
            
            if ((tzStr == null) || tzStr.isEmpty()) { // no overrides, use space
                tzStr = _timezoneStr;
                tz = _timezone;
            } else {
                tz = overrides._timezone;
            }
            return new Value(p, sh, l, tzStr, tz, f, lenient);
        }

        /**
         * @since 2.6
         */
        public static Value forPattern(String p) {
            return new Value(p, null, null, null, null, Features.empty(), null);
        }

        /**
         * @since 2.7
         */
        public static Value forShape(Shape sh) {
            return new Value(null, sh, null, null, null, Features.empty(), null);
        }

        /**
         * @since 2.9
         */
        public static Value forLeniency(boolean lenient) {
            return new Value(null, null, null, null, null, Features.empty(),
                    Boolean.valueOf(lenient));
        }
        
        /**
         * @since 2.1
         */
        public Value withPattern(String p) {
            return new Value(p, _shape, _locale, _timezoneStr, _timezone,
                    _features, _lenient);
        }

        /**
         * @since 2.1
         */
        public Value withShape(Shape s) {
            if (s == _shape) {
                return this;
            }
            return new Value(_pattern, s, _locale, _timezoneStr, _timezone,
                    _features, _lenient);
        }

        /**
         * @since 2.1
         */
        public Value withLocale(Locale l) {
            return new Value(_pattern, _shape, l, _timezoneStr, _timezone,
                    _features, _lenient);
        }

        /**
         * @since 2.1
         */
        public Value withTimeZone(TimeZone tz) {
            return new Value(_pattern, _shape, _locale, null, tz,
                    _features, _lenient);
        }

        /**
         * @since 2.9
         */
        public Value withLenient(Boolean lenient) {
            if (lenient == _lenient) {
                return this;
            }
            return new Value(_pattern, _shape, _locale, _timezoneStr, _timezone,
                    _features, lenient);
        }

        /**
         * @since 2.6
         */
        public Value withFeature(JsonFormat.Feature f) {
            Features newFeats = _features.with(f);
            return (newFeats == _features) ? this :
                new Value(_pattern, _shape, _locale, _timezoneStr, _timezone,
                        newFeats, _lenient);
        }

        /**
         * @since 2.6
         */
        public Value withoutFeature(JsonFormat.Feature f) {
            Features newFeats = _features.without(f);
            return (newFeats == _features) ? this :
                new Value(_pattern, _shape, _locale, _timezoneStr, _timezone,
                        newFeats, _lenient);
        }

        @Override
        public Class<JsonFormat> valueFor() {
            return JsonFormat.class;
        }
        
        public String getPattern() { return _pattern; }
        public Shape getShape() { return _shape; }
        public Locale getLocale() { return _locale; }

        /**
         * @since 2.9
         */
        public Boolean getLenient() {
            return _lenient;
        }
        /**
         * @since 2.9
         */
        public boolean isLenient() {
            return Boolean.TRUE.equals(_lenient);
        }

        /**
         * Alternate access (compared to {@link #getTimeZone()}) which is useful
         * when caller just wants time zone id to convert, but not as JDK
         * provided {@link TimeZone}
         * 
         * @since 2.4
         */
        public String timeZoneAsString() {
            if (_timezone != null) {
                return _timezone.getID();
            }
            return _timezoneStr;
        }
        
        public TimeZone getTimeZone() {
            TimeZone tz = _timezone;
            if (tz == null) {
                if (_timezoneStr == null) {
                    return null;
                }
                tz = TimeZone.getTimeZone(_timezoneStr);
                _timezone = tz;
            }
            return tz;
        }

        /**
         * @since 2.4
         */
        public boolean hasShape() { return _shape != Shape.ANY; }
        
        /**
         * @since 2.4
         */
        public boolean hasPattern() {
            return (_pattern != null) && (_pattern.length() > 0);
        }
        
        /**
         * @since 2.4
         */
        public boolean hasLocale() { return _locale != null; }

        /**
         * @since 2.4
         */
        public boolean hasTimeZone() {
            return (_timezone != null) || (_timezoneStr != null && !_timezoneStr.isEmpty());
        }

        /**
         * Accessor for checking whether there is a setting for leniency.
         * NOTE: does NOT mean that `lenient` is `true` necessarily; just that
         * it has been set.
         *
         * @since 2.9
         */
        public boolean hasLenient() {
            return _lenient != null;
        }

        /**
         * Accessor for checking whether this format value has specific setting for
         * given feature. Result is 3-valued with either `null`, {@link Boolean#TRUE} or
         * {@link Boolean#FALSE}, indicating 'yes/no/dunno' choices, where `null` ("dunno")
         * indicates that the default handling should be used based on global defaults,
         * and there is no format override.
         *
         * @since 2.6
         */
        public Boolean getFeature(JsonFormat.Feature f) {
            return _features.get(f);
        }

        /**
         * Accessor for getting full set of features enabled/disabled.
         *
         * @since 2.8
         */
        public Features getFeatures() {
            return _features;
        }

        @Override
        public String toString() {
            // !!! TODO: Features?
            return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s)",
                    _pattern, _shape, _lenient, _locale, _timezoneStr);
        }

        @Override
        public int hashCode() {
             int hash = (_timezoneStr == null) ? 1 : _timezoneStr.hashCode();
             if (_pattern != null) {
                 hash ^= _pattern.hashCode();
             }
             hash += _shape.hashCode();
             if (_lenient != null) {
                 hash ^= _lenient.hashCode();
             }
             if (_locale != null) {
                 hash += _locale.hashCode();
             }
             hash ^= _features.hashCode();
             return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            Value other = (Value) o;

            if ((_shape != other._shape) 
                    || !_features.equals(other._features)) {
                return false;
            }
            return _equal(_lenient, other._lenient)
                    && _equal(_timezoneStr, other._timezoneStr)
                    && _equal(_pattern, other._pattern)
                    && _equal(_timezone, other._timezone)
                    && _equal(_locale, other._locale);
        }

        private static <T> boolean _equal(T value1, T value2)
        {
            if (value1 == null) {
                return (value2 == null);
            }
            if (value2 == null) {
                return false;
            }
            return value1.equals(value2);
        }
    }
}
