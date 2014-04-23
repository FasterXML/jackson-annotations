package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotaion used to define a human readable description for a logical
 * property.
 * Currently used to populate the description field in generated JSON
 * Schemas.
 *
 * @since 2.3
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonPropertyDescription
{
    /**
     * Defines a human readable description of the logical property.
     * Currently used to populate the description field in generated JSON
     * Schemas.
     */
    String value() default "";
}
