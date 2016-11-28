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
     * no matter what).
     */
    Visibility creatorVisibility() default Visibility.DEFAULT;

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
     * Helper class used to contain information from a single {@link JsonIgnoreProperties}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     *
     * @since 2.9
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
         * <li>Only public Creators visible</li>
         *</ul>
         */
        protected final static Value DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY,
                Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.ANY,
                Visibility.PUBLIC_ONLY);

        /**
         * Empty instance that specifies no overrides, that is, all visibility
         * levels set as {@link Visibility#DEFAULT}.
         */
        protected final static Value NO_OVERRIDES = new Value(Visibility.DEFAULT,
                Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT,
                Visibility.DEFAULT);

        protected final Visibility _fieldVisibility;
        protected final Visibility _getterVisibility;
        protected final Visibility _isGetterVisibility;
        protected final Visibility _setterVisibility;
        protected final Visibility _creatorVisibility;

        private Value(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators) {
            _fieldVisibility = fields;
            _getterVisibility = getters;
            _isGetterVisibility = isGetters;
            _setterVisibility = setters;
            _creatorVisibility = creators;
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
                    src.creatorVisibility());
                    
        }

        /**
         * Factory method for cnstructing instance with visibility of specified accessor
         * (or, in case of <code>ALL</code>, all of them) set as specified; and the
         * rest (if any) set as {@link Visibility#DEFAULT}).
         */
        public static Value construct(PropertyAccessor acc, Visibility visibility) {
            Visibility fields = Visibility.DEFAULT;
            Visibility getters = Visibility.DEFAULT;
            Visibility isGetters = Visibility.DEFAULT;
            Visibility setters = Visibility.DEFAULT;
            Visibility creators = Visibility.DEFAULT;
            switch (acc) {
            case CREATOR:
                creators = visibility;
                break;
            case FIELD:
                fields = visibility;
                break;
            case GETTER:
                getters = visibility;
                break;
            case IS_GETTER:
                isGetters = visibility;
                break;
            case NONE:
                break;
            case SETTER:
                setters = visibility;
                break;
            case ALL: // default
                fields = getters = isGetters = setters = creators = visibility;
                break;
            }
            return construct(fields, getters, isGetters, setters, creators);
        }

        public static Value construct(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators)
        {
            Value v = _predefined(fields, getters, isGetters, setters, creators);
            if (v == null) {
                v = new Value(fields, getters, isGetters, setters, creators);
            }
            return v;
        }

        public Value withFieldVisibility(Visibility v) {
            return construct(v, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility);
        }

        public Value withGetterVisibility(Visibility v) {
            return construct(_fieldVisibility, v, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility);
        }

        public Value withIsGetterVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, v,
                    _setterVisibility, _creatorVisibility);
        }

        public Value withSetterVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    v, _creatorVisibility);
        }

        public Value withCreatorVisibility(Visibility v) {
            return construct(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, v);
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
            if (_equals(this, overrides)) {
                return this;
            }
            Visibility fields = overrides._fieldVisibility;
            if (fields == Visibility.DEFAULT) {
                fields = _fieldVisibility;
            }
            Visibility getters = overrides._getterVisibility;
            if (getters == Visibility.DEFAULT) {
                getters = _getterVisibility;
            }
            Visibility isGetters = overrides._isGetterVisibility;
            if (isGetters == Visibility.DEFAULT) {
                isGetters = _isGetterVisibility;
            }
            Visibility setters = overrides._setterVisibility;
            if (setters == Visibility.DEFAULT) {
                setters = _setterVisibility;
            }
            Visibility creators = overrides._creatorVisibility;
            if (creators == Visibility.DEFAULT) {
                creators = _creatorVisibility;
            }
            return construct(fields, getters, isGetters, setters, creators);
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

        // for JDK serialization
        protected Object readResolve() {
            Value v = _predefined(_fieldVisibility, _getterVisibility, _isGetterVisibility,
                    _setterVisibility, _creatorVisibility);
            return (v == null) ? this : v;
        }

        @Override
        public String toString() {
            return String.format(
"JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s)",
_fieldVisibility, _getterVisibility, _isGetterVisibility, _setterVisibility, _creatorVisibility
                    );
        }

        @Override
        public int hashCode() {
            return 1 + _fieldVisibility.ordinal()
                ^ (3 * _getterVisibility.ordinal())
                - (7 * _isGetterVisibility.ordinal())
                + (11 * _setterVisibility.ordinal())
                ^ (13 * _creatorVisibility.ordinal())
                    ;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            return (o.getClass() == getClass()) && _equals(this, (Value) o);
        }

        private static Value _predefined(Visibility fields,
                Visibility getters, Visibility isGetters, Visibility setters,
                Visibility creators)
        {
            if (fields == DEFAULT_FIELD_VISIBILITY) {
                if ((getters == DEFAULT._getterVisibility)
                    && (isGetters == DEFAULT._isGetterVisibility)
                    && (setters == DEFAULT._setterVisibility)
                    && (creators == DEFAULT._creatorVisibility)) {
                    return DEFAULT;
                }
            } else if (fields == Visibility.DEFAULT) {
                if ((getters == Visibility.DEFAULT)
                        && (isGetters == Visibility.DEFAULT)
                        && (setters == Visibility.DEFAULT)
                        && (creators == Visibility.DEFAULT)) {
                    return NO_OVERRIDES;
                }
            }
            return null;
        }

        private static boolean _equals(Value a, Value b)
        {
            return (a._fieldVisibility == b._fieldVisibility)
                    && (a._getterVisibility == b._getterVisibility)
                    && (a._isGetterVisibility == b._isGetterVisibility)
                    && (a._setterVisibility == b._setterVisibility)
                    && (a._creatorVisibility == b._creatorVisibility)
                    ;
        }
    }
}
