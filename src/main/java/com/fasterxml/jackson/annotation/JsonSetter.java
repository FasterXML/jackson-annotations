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
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
// ^^^ allowed on Fields, (constructor) parameters since 2.9
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

    /**
     * Specifies action to take when input contains explicit `null` value
     * (if format has one).
     * Default action, in absence of any explicit configuration,
     * is usually {@link Nulls#SET}, meaning that the `null` is set as
     * value using setter.
     *<p>
     * NOTE: is not usually used in case property value is missing, unless
     * data format specifies that there is defaulting which would result
     * in an explicit null assignment.
     */
    Nulls nulls() default Nulls.DEFAULT;

    /**
     * Specifies action to take when input to match into content value
     * (of a {@link java.util.Collection}, {@link java.util.Map}, array,
     * or referential value) contains explicit `null` value
     * (if format has one) to bind.
     * Default action, in absence of any explicit configuration,
     * is usually {@link Nulls#SET}, meaning that the `null` is included as usual.
     */
    Nulls contentNulls() default Nulls.DEFAULT;
    
    /**
     * Specifies whether property value should use "merging" approach, in which
     * current value is first accessed (with a getter), or not; if not,
     * assignment happens without considering current state.
     * Merging is not an option if there is no way to introspect
     * current state: for example, if there is no
     * accessor to use, or if assignment occurs using a Creator setter (constructor
     * or factory method), since there is no instance with state to introspect.
     * Merging also only has actual effect for structured types where there is an
     * obvious way to update a state (for example, POJOs have default values for properties,
     * and {@link java.util.Collection}s and {@link java.util.Map}s may have existing
     * elements; whereas scalar types do not such state: an <code>int</code> has a value,
     * but no obvious and non-ambiguous way to merge state.
     *<p>
     * Merging is applied by using a deserialization method that accepts existing state
     * as an argument: it is then up to <code>JsonDeserializer</code> implementation
     * to use that base state in a way that makes sense without further configuration.
     * For structured types this is usually obvious; and for scalar types not -- if
     * no obvious method exists, merging is not allowed; deserializer may choose to
     * either quietly ignore it, or throw an exception.
     *<p>
     * Note also that use of merging usually adds some processing overhead since it adds
     * an extra step of accessing the current state before assignment.
     *<p>
     * Default value is to "use defaults"; in absence of any explicit configuration
     * this would mean {@link OptBoolean#FALSE}, that is, merging is <b>not</b> enabled.
     */
    OptBoolean merge() default OptBoolean.DEFAULT;

    /*
    /**********************************************************
    /* Value enumeration(s)
    /**********************************************************
     */

    /**
     * Enumeration used with {@link JsonSetter#nulls()}
     * to define how explicit `null` values from input (if input format
     * has the concept; JSON, for example does) are handled.
     */
    public enum Nulls
    {
        /**
         * Value that indicates that an input null should result in assignment
         * of Java `null` value of matching property (except where deserializer
         * indicates other "null value" by overriding <code>getNullValue(...)</code>
         * method)
         */
        SET,

        /**
         * Value that indicates that an input null value should be skipped and
         * no assignment is to be made; this usually means that the property
         * will have its default value.
         */
        SKIP,

        /**
         * Value that indicates that an exception (of type that indicates input mismatch
         * problem) is to be thrown, to indicate that null values are not accepted.
         */
        FAIL,

        /**
         * Value that indicates that value to assign should come from the value
         * deserializer of the type, using method <code>getEmptyValue()</code>.
         */
        DESERIALIZER_EMPTY,

        /**
         * Pseudo-value used to indicate that defaults are to be used for handling,
         * that is, this value specifies no explicit handling override.
         */
        DEFAULT
        ;
    }

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
     * @since 2.9
     */
    public static class Value
        implements JacksonAnnotationValue<JsonSetter>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        private final Boolean _merge;

        private final Nulls _nulls;

        private final Nulls _contentNulls;
        
        /**
         * Default instance used in place of "default settings".
         */
        protected final static Value EMPTY = new Value(null, Nulls.DEFAULT, Nulls.DEFAULT);

        protected Value(Boolean merge, Nulls nulls, Nulls contentNulls) {
            _merge = merge;
            _nulls = nulls;
            _contentNulls = contentNulls;
        }

        // for JDK serialization
        protected Object readResolve() {
            if (_empty(_merge, _nulls, _contentNulls)) {
                return EMPTY;
            }
            return this;
        }

        public static Value from(JsonSetter src) {
            if (src == null) {
                return EMPTY;
            }
            return construct(src.merge().asBoolean(),
                    src.nulls(), src.contentNulls());
        }

        /**
         * Factory method that may be used (although is NOT the recommended way)
         * to construct an instance from a full set of properties. Most users would
         * be better of starting by {@link #empty()} instance and using `withXxx`/`withoutXxx`
         * methods, as this factory method may need to be changed if new properties
         * are added in {@link JsonIgnoreProperties} annotation.
         */
        public static Value construct(Boolean merge, Nulls nulls, Nulls contentNulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if (_empty(merge, nulls, contentNulls)) {
                return EMPTY;
            }
            return new Value(merge, nulls, contentNulls);
        }

        /**
         * Accessor for default instances which has "empty" settings; that is:
         *<ul>
         * <li>No definition for `merge` (that is, `null`, from {@link OptBoolean#DEFAULT})
         *  </li>
         * <li>Null handling using global defaults, {@link Nulls#DEFAULT}.
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

        public static Value forValueNulls(Nulls nulls) {
            return construct(null, nulls, Nulls.DEFAULT);
        }

        public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
            return construct(null, nulls, contentNulls);
        }
        
        public static Value forContentNulls(Nulls nulls) {
            return construct(null, Nulls.DEFAULT, nulls);
        }
        
        public static Value forMerging(Boolean merge) {
            return construct(merge, Nulls.DEFAULT, Nulls.DEFAULT);
        }

        public static Value forMerging() {
            return construct(Boolean.TRUE, Nulls.DEFAULT, Nulls.DEFAULT);
        }

        public static Value forNonMerging() {
            return construct(Boolean.FALSE, Nulls.DEFAULT, Nulls.DEFAULT);
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
            Boolean merge = overrides._merge;
            Nulls nulls = overrides._nulls;
            Nulls contentNulls = overrides._contentNulls;

            if (merge == null) {
                merge = _merge;
            }
            if (nulls == Nulls.DEFAULT) {
                nulls = _nulls;
            }
            if (contentNulls == Nulls.DEFAULT) {
                contentNulls = _contentNulls;
            }

            if ((merge == _merge) && (nulls == _nulls) && (contentNulls == _contentNulls)) {
                return this;
            }
            return construct(merge, nulls, contentNulls);
        }

        public Value withValueNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == _nulls) {
                return this;
            }
            return construct(_merge, nulls, _contentNulls);
        }

        public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
            if (valueNulls == null) {
                valueNulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if ((valueNulls == _nulls) && (contentNulls == _contentNulls)) {
                return this;
            }
            return construct(_merge, valueNulls, contentNulls);
        }
        
        public Value withContentNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == _contentNulls) {
                return this;
            }
            return construct(_merge, _nulls, nulls);
        }
        
        public Value withMerge(Boolean merge) {
            return (merge == _merge) ? this : construct(merge, _nulls, _contentNulls);
        }

        public Nulls getValueNulls() { return _nulls; }
        public Nulls getContentNulls() { return _contentNulls; }
        public Boolean getMerge() { return _merge; }

        public boolean shouldMerge() { return (_merge != null) && _merge.booleanValue(); }

        /**
         * Returns same as {@link #getValueNulls()} unless value would be
         * {@link JsonSetter.Nulls#DEFAULT} in which case `null` is returned.
         */
        public Nulls nonDefaultValueNulls() {
            return (_nulls == Nulls.DEFAULT) ? null : _nulls;
        }

        /**
         * Returns same as {@link #getContentNulls()} unless value would be
         * {@link JsonSetter.Nulls#DEFAULT} in which case `null` is returned.
         */
        public Nulls nonDefaultContentNulls() {
            return (_contentNulls == Nulls.DEFAULT) ? null : _contentNulls;
        }

        @Override
        public Class<JsonSetter> valueFor() {
            return JsonSetter.class;
        }

        @Override
        public String toString() {
            return String.format("JsonSetter.Value(merge=%s,valueNulls=%s,contentNulls=%s)",
                    _merge, _nulls, _contentNulls);
        }

        @Override
        public int hashCode() {
            return (_merge == null) ? 1 : (_merge.booleanValue() ? 30 : -30)
                    + _nulls.ordinal()
                    + (_contentNulls.ordinal() << 2);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() == getClass()) {
                Value other = (Value) o;
                return (other._merge == _merge)
                        && (other._nulls == _nulls)
                        && (other._contentNulls == _contentNulls);
            }
            return false;
        }

        private static boolean _empty(Boolean merge, Nulls nulls, Nulls contentNulls) {
            return (merge == null)
                    && (nulls == Nulls.DEFAULT)
                    && (contentNulls == Nulls.DEFAULT);
        }
    }
}
