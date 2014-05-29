package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
 * As of Jackson 2.1, known special handling include:
 *<ul>
 * <li>{@link java.util.Date}: Shape can  be {@link Shape#STRING} or {@link Shape#NUMBER};
 *    pattern may contain {@link java.text.SimpleDateFormat}-compatible pattern definition.
 *   </li>
 *</ul>
 * Jackson 2.1 added following new features:
 *<ul>
 * <li>Can now be used on Classes (types) as well, for modified default behavior, possibly
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
 *</ul>
 * In Jackson 2.4:
 * <ul>
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
     * defaults ({@link java.util.TimeZone#getDefault()}) unless explicitly
     * set to another locale.
     */
    public String timezone() default DEFAULT_TIMEZONE;
    
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
         * Marker enum value that indicates "default" (or "whatever") choice; needed
         * since Annotations can not have null values for enums.
         */
        ANY,

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
     * Helper class used to contain information from a single {@link JsonFormat}
     * annotation.
     */
    public static class Value
    {
        private final String pattern;
        private final Shape shape;
        private final Locale locale;

        private final String timezoneStr;

        // lazily constructed when created from annotations
        private TimeZone _timezone;

        public Value() {
            this("", Shape.ANY, "", "");
        }
        
        public Value(JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone());
        }

        public Value(String p, Shape sh, String localeStr, String tzStr)
        {
            this(p, sh,
                    (localeStr == null || localeStr.length() == 0 || DEFAULT_LOCALE.equals(localeStr)) ?
                            null : new Locale(localeStr),
                    (tzStr == null || tzStr.length() == 0 || DEFAULT_TIMEZONE.equals(tzStr)) ?
                            null : tzStr,
                    null
            );
        }

        /**
         * @since 2.1
         */
        public Value(String p, Shape sh, Locale l, TimeZone tz)
        {
            pattern = p;
            shape = sh;
            locale = l;
            _timezone = tz;
            timezoneStr = null;
        }

        /**
         * @since 2.4
         */
        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz)
        {
            pattern = p;
            shape = sh;
            locale = l;
            _timezone = tz;
            timezoneStr = tzStr;
        }

        /**
         * @since 2.1
         */
        public Value withPattern(String p) {
            return new Value(p, shape, locale, timezoneStr, _timezone);
        }

        /**
         * @since 2.1
         */
        public Value withShape(Shape s) {
            return new Value(pattern, s, locale, timezoneStr, _timezone);
        }

        /**
         * @since 2.1
         */
        public Value withLocale(Locale l) {
            return new Value(pattern, shape, l, timezoneStr, _timezone);
        }

        /**
         * @since 2.1
         */
        public Value withTimeZone(TimeZone tz) {
            return new Value(pattern, shape, locale, null, tz);
        }
        
        public String getPattern() { return pattern; }
        public Shape getShape() { return shape; }
        public Locale getLocale() { return locale; }

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
            return timezoneStr;
        }
        
        public TimeZone getTimeZone() {
            TimeZone tz = _timezone;
            if (tz == null) {
                if (timezoneStr == null) {
                    return null;
                }
                _timezone = tz = TimeZone.getTimeZone(timezoneStr);
            }
            return tz;
        }

        /**
         * @since 2.4
         */
        public boolean hasShape() { return shape != Shape.ANY; }
        
        /**
         * @since 2.4
         */
        public boolean hasPattern() {
            return (pattern != null) && (pattern.length() > 0);
        }
        
        /**
         * @since 2.4
         */
        public boolean hasLocale() { return locale != null; }

        /**
         * @since 2.4
         */
        public boolean hasTimeZone() {
            return (_timezone != null) || (timezoneStr != null && !timezoneStr.isEmpty());
        }
    }
}
