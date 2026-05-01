package com.fasterxml.jackson.annotation;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonWrappedTest
    extends AnnotationTestUtil
{
    private static class BeanWithField {
        @JsonWrapped("wrapper")
        public String field;
    }

    private static class BeanWithMethod {
        @JsonWrapped("wrapper")
        public String getField() { return null; }
    }

    private static class BeanWithParam {
        public BeanWithParam(@JsonWrapped("wrapper") String field) { }
    }

    @JsonWrapped("wrapper")
    @JacksonAnnotationsInside
    @interface BundleAnnotation { }

    @Test
    public void testRuntimeRetentionOnField() throws Exception {
        JsonWrapped ann = BeanWithField.class.getField("field").getAnnotation(JsonWrapped.class);
        assertNotNull(ann);
        assertTrue(ann.enabled());
    }

    @Test
    public void testRuntimeRetentionOnMethod() throws Exception {
        JsonWrapped ann = BeanWithMethod.class.getMethod("getField").getAnnotation(JsonWrapped.class);
        assertNotNull(ann);
    }

    @Test
    public void testApplicableOnConstructorParameter() throws Exception {
        Constructor<?> ctor = BeanWithParam.class.getDeclaredConstructor(String.class);
        JsonWrapped ann = ctor.getParameters()[0].getAnnotation(JsonWrapped.class);
        assertNotNull(ann);
    }

    @Test
    public void testApplicableOnAnnotationType() {
        JsonWrapped ann = BundleAnnotation.class.getAnnotation(JsonWrapped.class);
        assertNotNull(ann);
    }
}
