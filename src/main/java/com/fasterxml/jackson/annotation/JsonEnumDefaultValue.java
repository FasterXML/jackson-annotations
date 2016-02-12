package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a default value
 * used when trying to deserialize unknown Enum values.
 * <p>
 * This annotation is only applicable when the <code>@READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE</code>
 * deserialization feature is enabled.
 * <p>
 * If the more than one enum value is marked with this annotation,
 * the first one to be detected will be used. Which one exactly is undetermined.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonEnumDefaultValue
{

}
