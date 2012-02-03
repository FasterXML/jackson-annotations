package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static,
 * no-argument value-returning (non-void) method to be used as a "getter"
 * for a logical property.
 * It can be used as an alternative to more general
 * {@link JsonProperty} annotation (which is the recommended choice in
 * general case).
 *<p>
 * Getter means that when serializing Object instance of class that has
 * this method (possibly inherited from a super class), a call is made
 * through the method, and return value will be serialized as value of
 * the property.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonGetter
{
    /**
     * Defines name of the logical property this
     * method is used to access ("get"); empty String means that
     * name should be derived from the underlying method (using
     * standard Bean name detection rules)
     */
    String value() default "";
}
