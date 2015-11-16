package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation similar to {@link javax.xml.bind.annotation.XmlRootElement},
 * used to indicate name to use for root-level wrapping, if wrapping is
 * enabled. Annotation itself does not indicate that wrapping should
 * be used; but if it is, name used for serialization should be name
 * specified here, and deserializer will expect the name as well.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@com.fasterxml.jackson.annotation.JacksonAnnotation
public @interface JsonRootName
{
    /**
     * Root name to use if root-level wrapping is enabled. For data formats
     * that use composite names (XML), this is the "local part" of the name
     * to use.
     */
    public String value();

    /**
     * Optional namespace to use with data formats that support such
     * concept (specifically XML); if so, used with {@link #value} to
     * construct fully-qualified name.
     *
     * @since 2.4
     */
    public String namespace() default "";
    
    /*
     * Optional marker property that can be defined as <code>true</code> to force
     * wrapping of root element, regardless of whether globally
     * "root wrapping" is enabled or not.
     *<p>
     * Note that value of <code>false</code> is taken to mean "use defaults",
     * and will not block use of wrapper if use is indicated by global features.
     *
     * @since 2.4
    public boolean alwaysWrap() default false;
     */
}
