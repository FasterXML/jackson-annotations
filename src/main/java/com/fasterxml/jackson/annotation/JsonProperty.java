package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static
 * method as a "setter" or "getter" for a logical property
 * (depending on its signature),
 * or non-static object field to be used (serialized, deserialized) as
 * a logical property.
 *<p>
 * Default value ("") indicates that the field name is used
 * as the property name without any modifications, but it
 * can be specified to non-empty value to specify different
 * name. Property name refers to name used externally, as
 * the field name in JSON objects.
 *<p>
 * Starting with Jackson 2.6 this annotation may also be
 * used to change serialization of <code>Enum</code> like so:
 *<pre>
public enum MyEnum {
    {@literal @JsonProperty}("theFirstValue") THE_FIRST_VALUE,
    {@literal @JsonProperty}("another_value") ANOTHER_VALUE;
}
</pre>
 * as an alternative to using {@link JsonValue} annotation.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonProperty
{
    /**
     * Special value that indicates that handlers should use the default
     * name (derived from method or field name) for property.
     * 
     * @since 2.1
     */
    public final static String USE_DEFAULT_NAME = "";

    /**
     * Marker value used to indicate that no index has been specified.
     * Used as the default value as annotations do not allow "missing"
     * values.
     * 
     * @since 2.4
     */
    public final static int INDEX_UNKNOWN = -1;
    
    /**
     * Defines name of the logical property, i.e. JSON object field
     * name to use for the property. If value is empty String (which is the
     * default), will try to use name of the field that is annotated.
     * Note that there is
     * <b>no default name available for constructor arguments</b>,
     * meaning that
     * <b>Empty String is not a valid value for constructor arguments</b>.
     */
    String value() default USE_DEFAULT_NAME;

    /**
     * Property that indicates whether a value (which may be explicit
     * null) is expected for property during deserialization or not.
     * If expected, <code>BeanDeserialized</code> should indicate
     * this as a validity problem (usually by throwing an exception,
     * but this may be sent via problem handlers that can try to
     * rectify the problem, for example, by supplying a default
     * value).
     *<p>
     * Note that as of 2.6, this property is only used for Creator
     * Properties, to ensure existence of property value in JSON:
     * for other properties (ones injected using a setter or mutable
     * field), no validation is performed. Support for those cases
     * may be added in future.
     * State of this property is exposed via introspection, and its
     * value is typically used by Schema generators, such as one for
     * JSON Schema.
     *
     * @since 2.0
     */
    boolean required() default false;

    /**
     * Property that indicates numerical index of this property (relative
     * to other properties specified for the Object). This index
     * is typically used by binary formats, but may also be useful
     * for schema languages and other tools.
     * 
     * @since 2.4
     */
    int index() default INDEX_UNKNOWN;

    /**
     * Property that may be used to <b>document</b> expected default value
     * for the property: most often used as source information for generating
     * schemas (like JSON Schema or protobuf/thrift schema), or documentation.
     * It may also be used by Jackson extension modules; core jackson databind
     * does not have any automated handling beyond simply exposing this
     * value through bean property introspection.
     *<p>
     * It is possible that in future this annotation could be used for value
     * defaulting, and especially for default values of Creator properties,
     * since they support {@link #required()} in 2.6 and above.
     *
     * @since 2.5
     */
    String defaultValue() default "";

    /**
     * Optional property that may be used to change the way visibility of
     * accessors (getter, field-as-getter) and mutators (contructor parameter,
     * setter, field-as-setter) is determined, either so that otherwise
     * non-visible accessors (like private getters) may be used; or that
     * otherwise visible accessors are ignored.
     *<p>
     * Default value os {@link Access#AUTO} which means that access is determined
     * solely based on visibility and other annotations.
     *
     * @since 2.6
     */
    Access access() default Access.AUTO;
    
    /**
     * Various options for {@link #access} property, specifying how property
     * may be accessed during serialization ("read") and deserialization ("write")
     * (note that the direction of read and write is from perspective of the property,
     * not from external data format: this may be confusing in some contexts).
     *<p>
     * Note that while this annotation modifies access to annotated property,
     * its effects may be further overridden by {@link JsonIgnore} property:
     * if both annotations are present on an accessors, {@link JsonIgnore}
     * has precedence over this property.
     * This annotation property is, however, preferred over use of "split"
     * {@link JsonIgnore}/<code>JsonProperty</code> combination.
     *
     * @since 2.6
     */
    public enum Access
    {
        /**
         * Access setting which means that visibility rules are to be used
         * to automatically determine read- and/or write-access of this property.
         */
        AUTO,

        /**
         * Access setting that means that the property may only be read for serialization,
         * but not written (set) during deserialization.
         */
        READ_ONLY,

        /**
         * Access setting that means that the property may only be written (set)
         * for deserialization,
         * but will not be read (get) on serialization, that is, the value of the property
         * is not included in serialization.
         */
        WRITE_ONLY,

        /**
         * Access setting that means that the property will be accessed for both
         * serialization (writing out values as external representation)
         * and deserialization (reading values from external representation),
         * regardless of visibility rules.
         */
        READ_WRITE
        ;
    }
}
