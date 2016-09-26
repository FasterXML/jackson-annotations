package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static,
 * single-argument method to be used as a "setter" for a logical property
 * as an alternative to recommended
 * {@link JsonProperty} annotation (which was introduced in version 1.1).
 *<p>
 * Setter means that when a property with matching name is encountered in
 * JSON content, this method will be used to set value of the property.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
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
}
