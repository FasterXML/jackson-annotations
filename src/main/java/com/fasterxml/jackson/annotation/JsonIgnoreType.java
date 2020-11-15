package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that indicates that all properties that have
 * type annotated with this annotation
 * are to be ignored during serialization and deserialization.
 *<p>
 * Note that this does NOT mean that properties included by annotated
 * type are ignored. Given hypothetical types:
 *<pre>
 *  {@code @JsonIgnoreType}
 *  class Credentials {
 *     public String password;
 *  }
 *
 *  class Settings {
 *     public int userId;
 *     public String name;
 *     public Credentials pwd;
 *  }
 *</pre>
 * serialization of {@code Settings} would only include properties "userId"
 * and "name" but NOT "pwd", since it is of type annotated with {@code @JsonIgnoreType}.
 *<p>
 * Note: annotation does have boolean 'value' property (which defaults
 * to 'true'), so that it is actually possible to override value
 * using mix-in annotations. Usually value is not specified as it defaults
 * to {@code true} meaning annotation should take effect.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnoreType
{
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes
     * (which is not needed often); most likely it is needed for use
     * with "mix-in annotations" ("annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     */
    boolean value() default true;
}
