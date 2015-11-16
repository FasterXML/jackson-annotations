package com.fasterxml.jackson.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a human readable description for annotated
 * type (class).
 * Currently used to populate the description field in generated JSON
 * Schemas.
 *
 * @since 2.7
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotation
public @interface JsonClassDescription
{
    /**
     * Defines a human readable description of the class.
     */
    String value() default "";
}
