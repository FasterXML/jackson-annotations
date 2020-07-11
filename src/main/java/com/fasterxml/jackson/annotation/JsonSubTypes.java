package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used with {@link JsonTypeInfo} to indicate sub-types of serializable
 * polymorphic types, and to associate logical names used within JSON content
 * (which is more portable than using physical Java class names).
 *<p>
 * Note that just annotating a property or base type with this annotation does
 * NOT enable polymorphic type handling: in addition, {@link JsonTypeInfo}
 * or equivalent (such as enabling of so-called "default typing") annotation
 * is needed, and only in such case is subtype information used.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
    ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSubTypes {
    /**
     * Subtypes of the annotated type (annotated class, or property value type
     * associated with the annotated method). These will be checked recursively
     * so that types can be defined by only including direct subtypes.
     */
    public Type[] value();

    /**
     * Definition of a subtype, along with optional name(s). If no name is defined
     * (empty Strings are ignored), class of the type will be checked for {@link JsonTypeName}
     * annotation; and if that is also missing or empty, a default
     * name will be constructed by type id mechanism.
     * Default name is usually based on class name.
     */
    public @interface Type {
        /**
         * Class of the subtype
         */
        public Class<?> value();

        /**
         * Logical type name used as the type identifier for the class, if defined; empty
         * String means "not defined". Used unless {@link #names} is defined as non-empty.
         */
        public String name() default "";

        /**
         * (optional) Logical type names used as the type identifier for the class: used if
         * more than one type name should be associated with the same type.
         *
         * @since 2.12
         */
        public String[] names() default {};
    }
}
