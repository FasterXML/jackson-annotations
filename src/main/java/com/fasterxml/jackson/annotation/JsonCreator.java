package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define constructors and factory
 * methods as one to use for instantiating new instances of the associated
 * class.
 *<p>
 * NOTE: when annotating creator methods (constructors, factory methods),
 * method must either be:
 *<ul>
 * <li>Single-argument constructor/factory method without {@link JsonProperty}
 *    annotation for the argument: if so, this is so-called "delegate creator",
 *    in which case Jackson first binds JSON into type of the argument, and
 *    then calls creator
 *   </li>
 * <li>Constructor/factory method where <b>every argument</b> is annotated with
 *   either {@link JsonProperty} or {@link JacksonInject}, to indicate name
 *   of property to bind to
 *  </li>
 * </ul>
 * Also note that all {@link JsonProperty} annotations MUST use actual name
 * (NOT empty String for "default"): this because Java bytecode does not
 * retain names of method or constructor arguments.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonCreator
{
    // no values, since there's no property
}
