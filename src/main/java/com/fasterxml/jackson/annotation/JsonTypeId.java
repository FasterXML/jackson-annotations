package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used on a property accessor
 * (field, getter or setter, constructor parameter) to indicate that
 * the property is to contain type id to use when including
 * polymorphic type information.
 * Annotation should <b>only be used</b> if the intent is to override
 * generation of standard type id: if so, value of the property will be
 * accessed during serialization and used as the type id.
 *<p>
 * On deserialization annotation has no effect, as visibility of type id
 * is governed by value of {@link JsonTypeInfo#visible}; properties with
 * this annotation get no special handling.
 *<p>
 * On serialization, this annotation will exclude property from being
 * serialized along other properties; instead, its value is serialized
 * as the type identifier. Since type identifier may be included in
 * various places, it may still appear like 'normal' property (when using
 * {@link JsonTypeInfo.As#PROPERTY}), but is more commonly embedded
 * in a different place, as per inclusion rules (see {@link JsonTypeInfo}
 * for details).
 * 
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonTypeId
{

}
