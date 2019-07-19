package com.fasterxml.jackson.annotation;

import java.lang.annotation.Annotation;

/**
 * Marker interface used by value classes like {@link JsonFormat.Value} that are used
 * to contain information from one of Jackson annotations, and can be directly
 * instantiated from those annotations, as well as programmatically constructed
 * and possibly merged. The reason for such marker is to allow generic handling of
 * some of the annotations, as well as to allow easier injection of configuration
 * from sources other than annotations.
 */
public interface JacksonAnnotationValue<A extends Annotation>
{
    /**
     * Introspection method that may be used to find actual annotation that may be used
     * as the source for value instance.
     *
     * @return Annotation class for which instances of this value class are created
     */
    public Class<A> valueFor();
}
