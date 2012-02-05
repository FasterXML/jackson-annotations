package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used on a property value accessor
 * (field, getter) to indicate that
 * the property will be used to find id value that indicates identity
 * of the object, for purposes of handling object references (needed
 * to handled cyclic dependencies, or avoid value duplications).
 * Any objects should only have at most one accessor defined as
 * id property; using it for multiple may result in an exception
 * (behavior is unspecified).
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonObjectId
{

}
