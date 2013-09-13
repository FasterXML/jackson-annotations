package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate which logical filter is to be used
 * for filtering out properties of type (class) annotated;
 * association made by this annotation declaring ids of filters,
 * and  <code>com.fasterxml.jackson.databind.ObjectMapper</code> (or objects
 * it delegates to) providing matching filters by id.
 *<p>
 * Filters to use are usually of type
 * <code>com.fasterxml.jackson.databind.ser.BeanPropertyFilter</code> and
 * are registered through <code>com.fasterxml.jackson.databind.ObjectMapper</code>
 *<p>
 * Since 2.3, this annotation can also be used on properties (fields, methods,
 * constructor parameters).
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
    ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER // new in 2.3
})
@Retention(RetentionPolicy.RUNTIME)
@com.fasterxml.jackson.annotation.JacksonAnnotation
public @interface JsonFilter
{
    /**
     * Id of filter to use; if empty String (""), no filter is to be used.
     */
    public String value();
}
