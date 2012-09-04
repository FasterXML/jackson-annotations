package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static,
 * two-argument method (first argument name of property, second value
 * to set), to be used as a "fallback" handler
 * for all otherwise unrecognized properties found from JSON content.
 * It is similar to {@link javax.xml.bind.annotation.XmlAnyElement}
 * in behavior; and can only be used to denote a single property
 * per type.
 *<p>
 * If used, all otherwise unmapped key-value pairs from JSON Object values
 * are added to the property (of type Map or bean).
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAnySetter
{
}
