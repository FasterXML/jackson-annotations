package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a property should be serialized
 * "unwrapped" -- that is, if it would be serialized as JSON Object, its
 * properties are instead included as properties of its containing
 * Object -- and deserialized reproducing "missing" structure.
 * For example, consider case of POJO like:
 * 
 *<pre>
 *  public class Parent {
 *    public int age;
 *    public Name name;
 *  }
 *  public class Name {
 *    public String first, last;
 *  }
 *</pre>  
 * which would normally be serialized as follows (assuming {@code @JsonUnwrapped}
 * had no effect):
 *<pre>
 *  {
 *    "age" : 18,
 *    "name" : {
 *      "first" : "Joey",
 *      "last" : "Sixpack"
 *    }
 *  }
 *</pre>
 * can be changed to this:
 *<pre>
 *  {
 *    "age" : 18,
 *    "first" : "Joey",
 *    "last" : "Sixpack"
 *  }
 *</pre>
 * by changing Parent class to:
 *<pre>
 *  public class Parent {
 *    public int age;
 *    &#064;JsonUnwrapped
 *    public Name name;
 *  }
 *</pre>
 * Annotation can only be added to properties, and not classes, as it is contextual.
 * When values are deserialized "wrapping" is applied so that serialized output can
 * be read back in.
 *<p>
 * Also note that annotation only applies if:
 *<ul>
 * <li>Value is serialized as an Object valie (can not unwrap Array values using this
 *   mechanism)
 *   </li>
 * <li>Reading/writing is done using Jackson standard {@code BeanDeserializer} /
 *  {@code BeanSerializer}; or custom deserializer/serializer MUST explicitly support similar
 *  operation.
 *   </li>
 * <li>Will not work with polymorphic type handling ("polymorphic deserialization")
 *   </li>
 * </ul>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonUnwrapped
{
    /**
     * Property that is usually only used when overriding (masking) annotations,
     * using mix-in annotations. Otherwise default value of 'true' is fine, and
     * value need not be explicitly included.
     */
    boolean enabled() default true;

    /**
     * Optional property that can be used to add prefix String to use in front
     * of names of properties that are unwrapped: this can be done for example to prevent
     * name collisions.
     */
    String prefix() default "";

    /**
     * Optional property that can be used to add suffix String to append at the end
     * of names of properties that are unwrapped: this can be done for example to prevent
     * name collisions.
     */
    String suffix() default "";
}
