package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for indicating that values of annotated type
 * or property should be serializing so that instances either
 * contain additional object identifier (in addition actual object
 * properties), or as a reference that consists of an object id
 * that refers to a full serialization. In practice this is done
 * by serializing the first instance as full object and object
 * identity, and other references to the object as reference values.
 *<p>
 * There are two main approaches to generating object identifier:
 * either using a generator (either one of standard ones, or a custom
 * generator), or using a value of a property. The latter case is
 * indicated by using a placeholder generator marker
 * {@link ObjectIdGenerators#ObjectIdGenerator}; former by using explicit generator.
 * Object id has to be serialized as a property in case of POJOs;
 * object identity is currently NOT support for JSON Array types
 * (Java arrays or Lists) or Java Map types.
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
    ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIdentityInfo
{
    /**
     * Name of JSON property in which Object Id will reside: also,
     * if "from property" marker generator is used, identifies
     * property that will be accessed to get type id.
     * If a property is used, name must match its external
     * name (one defined by annotation, or derived from accessor
     * name as per Java Bean Introspection rules).
     *<p>
     * Default value is <code>@id</code>.
     */
    public String property() default "@id";

    /**
     * Generator to use for producing Object Identifier for objects:
     * either one of pre-defined generators from
     * {@link IdGenerator}, or a custom generator.
     * Defined as class to instantiate.
     */
    public Class<? extends ObjectIdGenerator<?>> generator();

    /**
     * Scope is a concept used by {@link ObjectIdGenerator} created based
     * on {@link #property}, iff {@link ObjectIdGenerator#usesGlobalScope()}
     * returns false. If so, separate generator instances are created for
     * each distinct scope. If not defined (i.e. left at default value of
     * {@link JsonIdentityInfo}), will just use type of the annotated
     * class or property as scope.
     *<p>
     * If {@link ObjectIdGenerator#usesGlobalScope()} returns true,
     * value of this property is ignored.
     */
    public Class<?> scope() default JsonIdentityInfo.class;
}
