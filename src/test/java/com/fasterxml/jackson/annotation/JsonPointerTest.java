package com.fasterxml.jackson.annotation;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonPointerTest
{
    private static class BeanWithField {
        @JsonPointer("/nested/field")
        public String field;
    }

    private static class BeanWithMethod {
        @JsonPointer("/nested/field")
        public void setField(String field) { }
    }

    private static class BeanWithParameter {
        public BeanWithParameter(@JsonPointer("/nested/field") String field) { }
    }

    @JsonPointer("/nested/field")
    @JacksonAnnotationsInside
    @interface BundleAnnotation { }

    @Test
    public void testRuntimeRetentionOnField() throws Exception {
        JsonPointer ann = BeanWithField.class.getField("field").getAnnotation(JsonPointer.class);
        assertNotNull(ann);
        assertEquals("/nested/field", ann.value());
    }

    @Test
    public void testRuntimeRetentionOnMethod() throws Exception {
        JsonPointer ann = BeanWithMethod.class.getMethod("setField", String.class)
                .getAnnotation(JsonPointer.class);
        assertNotNull(ann);
    }

    @Test
    public void testApplicableOnConstructorParameter() throws Exception {
        Constructor<?> ctor = BeanWithParameter.class.getDeclaredConstructor(String.class);
        JsonPointer ann = ctor.getParameters()[0].getAnnotation(JsonPointer.class);
        assertNotNull(ann);
    }

    @Test
    public void testApplicableOnAnnotationType() {
        JsonPointer ann = BundleAnnotation.class.getAnnotation(JsonPointer.class);
        assertNotNull(ann);
    }
}
