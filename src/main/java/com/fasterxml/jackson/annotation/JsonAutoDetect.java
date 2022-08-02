package com.fasterxml.jackson.annotation;

import java.lang.annotation.*;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Class annotation that can be used to define which kinds of Methods
 * are to be detected by auto-detection, and with what minimum access level.
 * Auto-detection means using name conventions
 * and/or signature templates to find methods to use for data binding.
 * For example, so-called "getters" can be auto-detected by looking for
 * public member methods that return a value, do not take argument,
 * and have prefix "get" in their name.
 *<p>
 * Default setting for all accessors is {@link Visibility#DEFAULT}, which 
 * in turn means that the global defaults are used. Defaults
 * are different for different accessor types (getters need to be public;
 * setters can have any access modifier, for example).
 * If you assign different {@link Visibility} type then it will override
 * global defaults: for example, to require that all setters must be public,
 * you would use:
 *<pre>
 *   &#64;JsonAutoDetect(setterVisibility=Visibility.PUBLIC_ONLY)
 *</pre>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect
{
    /**
     * Enumeration for possible visibility thresholds (minimum visibility)
     * that can be used to limit which methods (and fields) are
     * auto-detected.
     */
    public enum Visibility {
        /**
         * Value that means that all kinds of access modifiers are acceptable,
         * from private to public.
         */
        ANY,
        /**
         * Value that means that any other access modifier other than 'private'
         * is considered auto-detectable.
         */
        NON_PRIVATE,
        /**
         * Value that means access modifiers 'protected' and 'public' are
         * auto-detectable (and 'private' and "package access" == no modifiers
         * are not)
         */
        PROTECTED_AND_PUBLIC,
        /**
         * Value to indicate that only 'public' access modifier is considered
         * auto-detectable.
         */
        PUBLIC_ONLY,
        /**
         * Value that indicates that no access modifiers are auto-detectable:
         * this can be used to explicitly disable auto-detection for specified
         * types.
         */
        NONE,
        
        /**
         * Value that indicates that default visibility level (whatever it is,
         * depends on context) is to be used. This usually means that inherited
         * value (from parent visibility settings) is to be used.
         */
        DEFAULT;

        public boolean isVisible(Member m) {
            switch (this) {
            case ANY:
                return true;
            case NONE:
                return false;
            case NON_PRIVATE:
                return !Modifier.isPrivate(m.getModifiers());
            case PROTECTED_AND_PUBLIC:
                if (Modifier.isProtected(m.getModifiers())) {
                    return true;
                }
                // fall through to public case:
            case PUBLIC_ONLY:
                return Modifier.isPublic(m.getModifiers());
            default:
                return false;
            }
        }
    }
    
    /**
     * Minimum visibility required for auto-detecting regular getter methods.
     */
    Visibility getterVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting is-getter methods.
     */
    Visibility isGetterVisibility() default Visibility.DEFAULT;
    
    /**
     * Minimum visibility required for auto-detecting setter methods.
     */    
    Visibility setterVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting Creator methods,
     * except for no-argument constructors (which are always detected
     * no matter what), and (since 3.0) single-scalar-argument
     * Creators for which there is separate setting.
     */
    Visibility creatorVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting single-scalar-argument
     * constructors, as distinct from "regular" creators
     * (see {@link #creatorVisibility}).
     * Specifically a small set of scalar types is allowed; see
     * {@link PropertyAccessor#SCALAR_CONSTRUCTOR} for list.
     *<p>
     * Default value is more permissive than that of general Creators:
     * all non-private scalar-constructors are detected by default.
     *
     * @since 3.0
     */
    Visibility scalarConstructorVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting member fields.
     */ 
    Visibility fieldVisibility() default Visibility.DEFAULT;

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings.
    /**********************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JsonAutoDetect}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     */
    public static class Value
        implements JacksonAnnotationValue<JsonAutoDetect>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        private final static Visibility DEFAULT_FIELD_VISIBILITY = Visibility.PUBLIC_ONLY;

        /**
         * Default instance with baseline visibility checking:
         *<ul>
         * <li>Only public fields visible</li>
         * <li>Only public getters, is-getters visible</li>
         * <li>All setters (regardless of access) visible</li>
         * <li>Only public Creators visible (except see below)</li>
         * <li>All non-private single-scalar constructors are visible</li>
         *</ul>
         */
        protected final static Value DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY,
                Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.ANY,
                Visibility.PUBLIC_ONLY, Visibility.NON_PRIVATE);

        /**
         * Empty instance that specifies no overrides, that is, all visibility
         * levels set as {@link Visibility#DEFAULT}.
         */
        protected final static Value NO_OVERRIDES = new Value(Visibility.DEFAULT,
                Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT,
                Visibility.DEFAULT, Visibility.DEFAULT);

        protected final Visibility _fieldVisibility;
        protected final Visibility _getterVisibility;
        protected final Visibility _isGetterVisibility;
        protected final Visibility _setterVisibility;
        protected final Visibility _creatorVisibility;

        /**
         * @since 3.0
         */
        protected final Visibility _scalarConstructorVisibility;

        private Value(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators, Visibility scalarCtors) {
            _fieldVisibility = fields;
            _getterVisibility = getters;
            _isGetterVisibility = isGetters;
            _setterVisibility = setters;
            _creatorVisibility = creators;
            _scalarConstructorVisibility = scalarCtors;
        }

        public static Value defaultVisibility() {
            return DEFAULT;
        }

        public static Value noOverrides() {
            return NO_OVERRIDES;
        }

        public static Value from(JsonAutoDetect src) {
            return construct(src.fieldVisibility(),
                    src.getterVisibility(), src.isGetterVisibility(), src.setterVisibility(),
                    src.creatorVisibility(), src.scalarConstructorVisibility());
                    
        }

        /**
         * Factory method for constructing instance with visibility of specified accessor
         * (or, in case of <code>ALL</code>, all of them) set as specified; and the
         * rest (if any) set as {@link Visibility#DEFAULT}).
         */
        public static Value construct(PropertyAccessor acc, Visibility visibility) {
            Visibility fields = Visibility.DEFAULT;
            Visibility getters = Visibility.DEFAULT;
            Visibility isGetters = Visibility.DEFAULT;
            Visibility setters = Visibility.DEFAULT;
            Visibility creators = Visibility.DEFAULT;
            Visibility scalarCtors = Visibility.DEFAULT;
            switch (acc) {
            case FIELD:
                fields = visibility;
                break;
            case GETTER:
                getters = visibility;
                break;
            case IS_GETTER:
                isGetters = visibility;
                break;
            case SETTER:
                setters = visibility;
                break;
            case CREATOR:
                creators = visibility;
                break;
            case SCALAR_CONSTRUCTOR:
                scalarCtors = visibility;
                break;
            case NONE:
                break;
            case ALL: // default
                fields = getters = isGetters = setters = creators = scalarCtors = visibility;
                break;
            }
            return construct(fields, getters, isGetters, setters, creators, scalarCtors);
        }

        public static Value construct(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators, Visibility scalarCtors)
        {
            Value v = _predefined(fields, getters, isGetters, setters, creators, scalarCtors);
            if (v == null) {
                v = new Value(fields, getters, isGetters, setters, creators, scalarCtors);
            }
            return v;
        }

        public Value withFieldVisibility(Visibility v) {
            return construct(v, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility, _scalarConstructorVisibility);
        }

        public Value withGetterVisibility(Visibility v) {
            return construct(_fieldVisibility, v, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility, _scalarConstructorVisibility);
        }

        public Value withIsGetterVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, v,
                    _setterVisibility, _creatorVisibility, _scalarConstructorVisibility);
        }

        public Value withSetterVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    v, _creatorVisibility, _scalarConstructorVisibility);
        }

        public Value withCreatorVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, v, _scalarConstructorVisibility);
        }

        public Value withScalarConstructorVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility, v);
        }
        
        public static Value merge(Value base, Value overrides)
        {
            return (base == null) ? overrides
                    : base.withOverrides(overrides);
        }

        public Value withOverrides(Value overrides) {
            if ((overrides == null) || (overrides == NO_OVERRIDES) || (overrides == this)) {
                return this;
            }
            Visibility fields = _override(_fieldVisibility, overrides._fieldVisibility);
            Visibility getters = _override(_getterVisibility, overrides._getterVisibility);
            Visibility isGetters = _override(_isGetterVisibility, overrides._isGetterVisibility);
            Visibility setters = _override(_setterVisibility, overrides._setterVisibility);
            Visibility creators = _override(_creatorVisibility, overrides._creatorVisibility);
            Visibility scalarCtors = _override(_scalarConstructorVisibility, overrides._scalarConstructorVisibility);
            if (_equals(this, fields, getters, isGetters, setters, creators, scalarCtors)) {
                return this;
            }
            return construct(fields, getters, isGetters, setters, creators, scalarCtors);
        }

        private static Visibility _override(Visibility base, Visibility override) {
            return (override == Visibility.DEFAULT) ? base : override;
        }
        
        @Override
        public Class<JsonAutoDetect> valueFor() {
            return JsonAutoDetect.class;
        }

        public Visibility getFieldVisibility() { return _fieldVisibility; }
        public Visibility getGetterVisibility() { return _getterVisibility; }
        public Visibility getIsGetterVisibility() { return _isGetterVisibility; }
        public Visibility getSetterVisibility() { return _setterVisibility; }
        public Visibility getCreatorVisibility() { return _creatorVisibility; }
        public Visibility getScalarConstructorVisibility() { return _scalarConstructorVisibility; }

        // for JDK serialization
        protected Object readResolve() {
            Value v = _predefined(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility, _scalarConstructorVisibility);
            return (v == null) ? this : v;
        }

        @Override
        public String toString() {
            return String.format(
"JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s,scalarConstructors=%s)",
_fieldVisibility, _getterVisibility, _isGetterVisibility, _setterVisibility,
_creatorVisibility, _scalarConstructorVisibility);
        }

        @Override
        public int hashCode() {
            return 1 + _fieldVisibility.ordinal()
                ^ (3 * _getterVisibility.ordinal())
                - (7 * _isGetterVisibility.ordinal())
                + (11 * _setterVisibility.ordinal())
                ^ (13 * _creatorVisibility.ordinal())
                + (17 * _scalarConstructorVisibility.ordinal())
                    ;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            Value v = (Value) o;
            return _equals(this, v._fieldVisibility,
                    v._getterVisibility, v._isGetterVisibility, v._setterVisibility,
                    v._creatorVisibility, v._scalarConstructorVisibility)
            ;
        }

        private static Value _predefined(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators, Visibility scalarCtors)
        {
            if (fields == DEFAULT_FIELD_VISIBILITY) {
                if ((getters == DEFAULT._getterVisibility)
                    && (isGetters == DEFAULT._isGetterVisibility)
                    && (setters == DEFAULT._setterVisibility)
                    && (creators == DEFAULT._creatorVisibility)
                    && (scalarCtors == DEFAULT._scalarConstructorVisibility)) {
                    return DEFAULT;
                }
            } else if (fields == Visibility.DEFAULT) {
                if ((getters == Visibility.DEFAULT)
                        && (isGetters == Visibility.DEFAULT)
                        && (setters == Visibility.DEFAULT)
                        && (creators == Visibility.DEFAULT)
                        && (scalarCtors == Visibility.DEFAULT)) {
                    return NO_OVERRIDES;
                }
            }
            return null;
        }

        private static boolean _equals(Value v,
                Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators, Visibility scalarCtors)
        {
            return (v._fieldVisibility == fields)
                    && (v._getterVisibility == getters)
                    && (v._isGetterVisibility == isGetters)
                    && (v._setterVisibility == setters)
                    && (v._creatorVisibility == creators)
                    && (v._scalarConstructorVisibility == scalarCtors)
                ;
        }
    }
}
