package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Jackson-specific annotation used for indicating that value of
 * annotated property will be "injected", i.e. set based on value
 * configured by <code>ObjectMapper</code> (usually on per-call basis).
 * Usually property is not deserialized from JSON, although it is possible
 * to have injected value as default and still allow optional override
 * from JSON.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonInject
{
    /**
     * Logical id of the value to inject; if not specified (or specified
     * as empty String), will use id based on declared type of property.
     *
     * @return Logical id of the value to inject
     */
    public String value() default "";

    /**
     * Whether matching value from input (if any) is used for annotated property or not;
     * if disabled (`OptBoolean.FALSE`), input value (if any) will be ignored;
     * otherwise it will override injected value.
     *<p>
     * Default is `OptBoolean.DEFAULT`, which translates to `OptBoolean.TRUE`.
     *
     * @return {@link OptBoolean#TRUE} to enable use of value from input instead of
     *    injected value, if available; {@link OptBoolean#FALSE} if injected value will
     *    always be used regardless of input.
     */
    public OptBoolean useInput() default OptBoolean.DEFAULT;

    /**
     * Whether to throw an exception when the {@code ObjectMapper} does not find
     * the value to inject.
     *<p>
     * Default is {@code OptBoolean.DEFAULT} for backwards-compatibility: in this
     * case {@code ObjectMapper} defaults are used (which in turn are same
     * as {code OptBoolean.FALSE}).
     *
     * @return {@link OptBoolean#FALSE} to throw an exception; {@link OptBoolean#TRUE}
     * to avoid throwing it; or {@link OptBoolean#DEFAULT} to use configure defaults
     * (which are same as {@link OptBoolean#FALSE} for Jackson 2.x)
     */
    public OptBoolean optional() default OptBoolean.DEFAULT;

    /*
    /**********************************************************************
    /* Value class used to enclose information, allow for  merging of layered
    /* configuration settings, and eventually decouple higher level handling
    /* from Annotation types (which can not be implemented etc.)
    /**********************************************************************
     */

    /**
     * Helper class used to contain information from a single {@link JacksonInject}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     */
    public static class Value
        implements JacksonAnnotationValue<JacksonInject>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        protected final static Value EMPTY = new Value(null, null, null);

        /**
         * Id to use to access injected value; if `null`, "default" name, derived
         * from accessor will be used.
         */
        protected final Object _id;

        protected final Boolean _useInput;

        protected final Boolean _optional;

        protected Value(Object id, Boolean useInput, Boolean optional) {
            _id = id;
            _useInput = useInput;
            _optional = optional;
        }

        @Override
        public Class<JacksonInject> valueFor() {
            return JacksonInject.class;
        }

        /*
        /******************************************************************
        /* Factory methods
        /******************************************************************
         */

        public static Value empty() {
            return EMPTY;
        }

        public static Value construct(Object id, Boolean useInput, Boolean optional) {
            if ("".equals(id)) {
                id = null;
            }
            if (_empty(id, useInput, optional)) {
                return EMPTY;
            }
            return new Value(id, useInput, optional);
        }

        public static Value from(JacksonInject src) {
            if (src == null) {
                return EMPTY;
            }
            return construct(src.value(), src.useInput().asBoolean(), src.optional().asBoolean());
        }

        public static Value forId(Object id) {
            return construct(id, null, null);
        }

        /*
        /******************************************************************
        /* Mutant factory methods
        /******************************************************************
         */

        public Value withId(Object id) {
            if (id == null) {
                if (_id == null) {
                    return this;
                }
            } else if (id.equals(_id)) {
                return this;
            }
            return new Value(id, _useInput, _optional);
        }

        public Value withUseInput(Boolean useInput) {
            if (useInput == null) {
                if (_useInput == null) {
                    return this;
                }
            } else if (useInput.equals(_useInput)) {
                return this;
            }
            return new Value(_id, useInput, _optional);
        }

        public Value withOptional(Boolean optional) {
            if (optional == null) {
                if (_optional == null) {
                    return this;
                }
            } else if (optional.equals(_optional)) {
                return this;
            }
            return new Value(_id, _useInput, optional);
        }

        /*
        /******************************************************************
        /* Accessors
        /******************************************************************
         */

        public Object getId() { return _id; }
        public Boolean getUseInput() { return _useInput; }
        public Boolean getOptional() { return _optional; }

        public boolean hasId() {
            return _id != null;
        }

        public boolean willUseInput(boolean defaultSetting) {
            return (_useInput == null) ? defaultSetting : _useInput.booleanValue();
        }

        /*
        /******************************************************************
        /* Standard method overrides
        /******************************************************************
         */

        @Override
        public String toString() {
            return String.format("JacksonInject.Value(id=%s,useInput=%s,optional=%s)",
                    _id, _useInput, _optional);
        }

        @Override
        public int hashCode() {
            int h = 1;
            if (_id != null) {
                h += _id.hashCode();
            }
            if (_useInput != null) {
                h += _useInput.hashCode();
            }
            if (_optional != null) {
                h += _optional.hashCode();
            }
            return h;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() == getClass()) {
                Value other = (Value) o;

                return (_id == null && other._id == null
                        || _id != null && _id.equals(other._id))
                        && (_useInput == null && other._useInput == null
                        || _useInput != null && _useInput.equals(other._useInput))
                        && (_optional == null && other._optional == null
                        || _optional != null && _optional.equals(other._optional));
            }
            return false;
        }

        /*
        /******************************************************************
        /* Other
        /******************************************************************
         */

        private static boolean _empty(Object id, Boolean useInput, Boolean optional) {
            return (id == null) && (useInput == null) && optional == null;
        }
    }
}
