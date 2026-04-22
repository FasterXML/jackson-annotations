package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for specifying view that should be used to process
 * the property that is defined by method or field annotated.
 *<p>
 * An example annotation would be:
 *<pre>
 *  &#064;JsonApplyView(BasicView.class)
 *  public MyValue value;
 *</pre>
 * which would specify that property annotated would be processed
 * using View identified by {@code BasicView.class}.
 *<p>
 * Note: initially processing only covers serialization.
 *
 * @since 2.22
 */
@Target({ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonApplyView
{
    /**
     * View that should be used to process annotated property, if any;
     * special value {@link JsonApplyView.NONE} indicates that no View
     * should used.
     */
    public Class<?> value() default NONE.class;

    /**
     * Special view indicating no views should be used for processing annotated property.
     */
    static public interface NONE {}
}
