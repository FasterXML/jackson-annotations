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

    /*
    /**********************************************************
    /* Value class used to enclose information, allow for
    /* merging of layered configuration settings.
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

        protected final static Value EMPTY = new Value(null);        

        /**
         * Id to use to access injected value; if `null`, "default" name, derived
         * from accessor will be used.
         */
        protected final Object _id;

        protected Value(Object id) {
            _id = id;
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

        public static Value construct(Object id) {
            if (id == null) {
                return EMPTY;
            }
            return new Value(id);
        }

        public static Value from(JacksonInject src) {
            if (src == null) {
                return EMPTY;
            }
            String id = src.value();
            if ("".equals(id)) {
                id = null;
            }
            return construct(id);
        }

        public static Value forId(Object id) {
            if (id == null) {
                return EMPTY;
            }
            return new Value(id);
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
            return new Value(id);
        }

        /*
        /**********************************************************
        /* Accessors
        /**********************************************************
         */
        
        public boolean hasId() {
            return _id != null;
        }

        public Object getId() { return _id; }
    }
}
