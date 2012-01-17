package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation (annotations used on other annotations)
 * used for indicating that instead of using target annotation
 * (annotation annotated with this annotation),
 * Jackson should use meta-annotations it has.
 * This can be useful in creating "combo-annotations" by having
 * a container annotation, which needs to be annotated with this
 * annotation as well as all annotations it 'contains'.
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonAnnotationsInside
{

}
