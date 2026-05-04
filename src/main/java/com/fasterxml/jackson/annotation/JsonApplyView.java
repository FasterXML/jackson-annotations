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
 * which specifies that the property annotated (and nested values reach through it)
 * would be processed using View identified by {@code BasicView.class}.
 *<p>
 * It is also possible to disable View processing for given property by:
 *<pre>
 *  &#064;JsonApplyView(JsonApplyView.NONE.class)
 *  public MyValue fullValue;
 *</pre>
 * which similarly applies to properties reachable via {@code fullValue}.
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
    public Class<?> value();

    /**
     * Special view indicating no views should be used for processing annotated property:
     * usually implemented by setting "Active View" to {@code null} value.
     */
    static public interface NONE {}
}
