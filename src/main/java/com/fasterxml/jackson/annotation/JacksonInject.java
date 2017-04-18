package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * Jackson-specific annotation used for indicating that value of
 * annotated property will be "injected", i.e. set based on value
 * configured by <code>ObjectMapper</code> (usually on per-call basis).
 * Usually property is not deserialized from JSON, although it possible
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
     */
    public String value() default "";

    /**
     * Whether matching input value is used for annotated property or not;
     * if disabled (`OptBoolean.FALSE`), input value (if any) will be ignored;
     * otherwise it will override injected value.
     *<p>
     * Default is `OptBoolean.DEFAULT`, which translates to `OptBoolean.TRUE`: this is
     * for backwards compatibility (2.8 and earlier always allow binding input value).
     *
     * @since 2.9
     */
    public OptBoolean useInput() default OptBoolean.DEFAULT;

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings, and eventually
    /* decouple higher level handling from Annotation types
    /* (which can not be implemented etc)
    /**********************************************************
     */
    
    /**
     * Helper class used to contain information from a single {@link JacksonInject}
     * annotation, as well as to provide possible overrides from non-annotation sources.
     *
     * @since 2.9
     */
    public static class Value
        implements JacksonAnnotationValue<JacksonInject>,
            java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        protected final static Value EMPTY = new Value(null, null);        

        /**
         * Id to use to access injected value; if `null`, "default" name, derived
         * from accessor will be used.
         */
        protected final Object _id;

        protected final Boolean _useInput;

        protected Value(Object id, Boolean useInput) {
            _id = id;
            _useInput = useInput;
        }

        @Override
        public Class<JacksonInject> valueFor() {
            return JacksonInject.class;
        }

        /*
        /**********************************************************
        /* Factory methods
        /**********************************************************
         */

        public static Value empty() {
            return EMPTY;
        }

        public static Value construct(Object id, Boolean useInput) {
            if ("".equals(id)) {
                id = null;
            }
            if (_empty(id, useInput)) {
                return EMPTY;
            }
            return new Value(id, useInput);
        }

        public static Value from(JacksonInject src) {
            if (src == null) {
                return EMPTY;
            }
            return construct(src.value(), src.useInput().asBoolean());
        }

        public static Value forId(Object id) {
            return construct(id, null);
        }

        /*
        /**********************************************************
        /* Mutant factory methods
        /**********************************************************
         */

        public Value withId(Object id) {
            if (id == null) {
                if (_id == null) {
                    return this;
                }
            } else if (id.equals(_id)) {
                return this;
            }
            return new Value(id, _useInput);
        }

        public Value withUseInput(Boolean useInput) {
            if (useInput == null) {
                if (_useInput == null) {
                    return this;
                }
            } else if (useInput.equals(_useInput)) {
                return this;
            }
            return new Value(_id, useInput);
        }

        /*
        /**********************************************************
        /* Accessors
        /**********************************************************
         */
        
        public Object getId() { return _id; }
        public Boolean getUseInput() { return _useInput; }

        public boolean hasId() {
            return _id != null;
        }

        public boolean willUseInput(boolean defaultSetting) {
            return (_useInput == null) ? defaultSetting : _useInput.booleanValue();
        }

        /*
        /**********************************************************
        /* Std method overrides
        /**********************************************************
         */
        
        @Override
        public String toString() {
            return String.format("JacksonInject.Value(id=%s,useInput=%s)",
                    _id, _useInput);
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
            return h;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() == getClass()) {
                Value other = (Value) o;
                if (OptBoolean.equals(_useInput, other._useInput)) {
                    if (_id == null) {
                        return other._id == null;
                    }
                    return _id.equals(other._id);
                }
            }
            return false;
        }

        /*
        /**********************************************************
        /* Other
        /**********************************************************
         */

        private static boolean _empty(Object id, Boolean useInput) {
            return (id == null) && (useInput == null);
        }
    }
}
