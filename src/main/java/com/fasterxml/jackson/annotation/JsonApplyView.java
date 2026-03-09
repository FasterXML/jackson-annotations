package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for specifying view that should be used to
 * process (serialize) the property that is defined by method
 * or field annotated.
 *<p>
 * An example annotation would be:
 *<pre>
 *  &#064;JsonApplyView(BasicView.class)
 *</pre>
 * which would specify that property annotated would be processed
 * (serialized) using View identified by <code>BasicView.class</code>.
 */
@Target({ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonApplyView {
    /**
     * View that should be used to serialize annotated property.
     */
    public Class<?> value() default NONE.class;

    /**
     * Special view indicating no views should be used to serialize annotated property.
     */
    static public interface NONE {}
}
