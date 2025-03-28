package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static
 * method as a "setter" or "getter" for a logical property
 * accessor (depending on its signature),
 * or a non-static Object field to be used (serialized, deserialized)
 * as a logical property (to assign value or get value from)
 *<p>
 * Value ("") indicates that the name of field (or, derived name
 * of an accessor method (setter / getter)) is to be used
 * as the property name without any modifications; a non-empty value
 * can be used to specify a different name.
 * Property name refers to the name used externally, as the property
 * name in JSON objects (as opposed to internal name of field in
 * Java Object).
 *<p>
 * NOTE: annotation with non-empty Value can NOT be used if declaring multiple
 * Java fields in a single declaration like:
 *<pre>
 * public class POJO {
 *    \@JsonProperty("a")
 *    public int a, b, c;
 *</pre>
 * since it would associate same annotation for all fields, leading to name
 * collision.
 *<p>
 * Starting with Jackson 2.6 this annotation may also be
 * used to change serialization of {@code Enum} like so:
 *<pre>
public enum MyEnum {
    {@literal @JsonProperty}("theFirstValue") THE_FIRST_VALUE,
    {@literal @JsonProperty}("another_value") ANOTHER_VALUE;
}
</pre>
 * as an alternative to using {@link JsonValue} annotation.
 *<br>
 * NOTE: for {@code Enum}s, empty String is a valid value (and
 * missing {@code value} is taken as empty String), unlike for regular
 * properties, and does NOT mean "use default Enum name".
 * (handling fixed in Jackson 2.19)
 *<p>
 * Starting with Jackson 2.12 it is also possible to specify {@code namespace}
 * of property: this property is only used by certain format backends (most
 * notably XML).
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
     * Optional namespace to use with data formats that support such
     * concept (specifically XML); if so, used with {@link #value} to
     * construct fully-qualified name.
     *
     * @since 2.12
     */
    String namespace() default "";

    /**
     * Property similar to {@link #isRequired}, but one that only
     * allows two values ({@code true} and {@code false}), defaulting
     * to {@code false}. It also has lower precedence than {@link #isRequired}
     * in cases where both are specified.
     *
     * @since 2.0
     */
    boolean required() default false;

    /**
     * Property that MAY indicate whether a value (which may be explicit
     * null) is required for a property during deserialization or not.
     * If specified as required ({code OptBoolean.TRUE}), deserialization
     * should indicate a validity problem if no
     * value is present in incoming content(usually by throwing an exception,
     * but this may be sent via problem handlers that can try to
     * rectify the problem, for example, by supplying a default value).
     * If marked as not required ({code OptBoolean.FALSE}), no checking
     * for value existence should be done.
     * If not specified ({code OptBoolean.DEFAULT}), checking depends on higher
     * level settings (some modules may specify default "required-ness" for certain
     * kinds of properties).
     *<p>
     * Note that as of 2.19, possible validation is only done for Creator
     * Properties, to ensure existence of property value in JSON:
     * for other properties (ones injected using a setter or mutable
     * field), no validation is performed. Support for those cases
     * may be added in the future.
     * State of this property is exposed via introspection, and its
     * value is typically used by Schema generators, such as one for
     * JSON Schema.
     *<p>
     * Also note that the required value must come <b>directly</b> from the
     * input source (e.g., JSON) and not from secondary sources, such as
     * defaulting logic or absent value providers.
     * If secondary sources are expected to supply the value,
     * this property should be set to {@code false}. This is important because
     * validation of {@code required} properties occurs before the application of
     * secondary sources.
     *<p>
     * NOTE: as of Jackson 2.19, the older property, {@link #required()},
     * may still be used, but will have lower precedence than this annotation --
     * basically it is only considered if this property has value {@code OptBoolean.DEFAULT}.
     *
     * @since 2.19
     */
    OptBoolean isRequired() default OptBoolean.DEFAULT;
    
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
     * It may also be used by Jackson extension modules; core `jackson-databind`
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
     * accessors (getter, field-as-getter) and mutators (constructor parameter,
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
         * Access setting that means that the property may only be read for serialization
         * (value accessed via "getter" Method, or read from Field)
         * but not written (set) during deserialization.
         * Put another way, this would reflect "read-only POJO", in which value contained
         * may be read but not written/set.
         */
        READ_ONLY,

        /**
         * Access setting that means that the property may only be written (set)
         * as part of deserialization (using "setter" method, or assigning to Field,
         * or passed as Creator argument)
         * but will not be read (get) for serialization, that is, the value of the property
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
