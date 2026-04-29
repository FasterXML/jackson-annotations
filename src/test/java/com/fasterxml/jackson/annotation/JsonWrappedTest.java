package com.fasterxml.jackson.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWrappedTest
    extends AnnotationTestUtil
{
    private static class TestClass {
        @JsonWrapped("chr")
        public String chromosome;

        @JsonWrapped("chr")
        public int position;
    }

    private static class TestClassWithGetter {
        private String data;

        @JsonWrapped("wrapper")
        public String getData() {
            return data;
        }
    }

    private static class TestClassEmptyWrapper {
        @JsonWrapped("")
        public String field;
    }

    @Test
    public void testAnnotationRetentionAtRuntime() throws Exception {
        // Verify annotation is retained at runtime
        JsonWrapped ann = TestClass.class.getField("chromosome").getAnnotation(JsonWrapped.class);
        assertNotNull(ann, "Annotation should be retained at runtime");
        assertEquals("chr", ann.value());
    }

    @Test
    public void testAnnotationValue() throws Exception {
        JsonWrapped ann = TestClass.class.getField("chromosome").getAnnotation(JsonWrapped.class);
        assertEquals("chr", ann.value());

        JsonWrapped ann2 = TestClass.class.getField("position").getAnnotation(JsonWrapped.class);
        assertEquals("chr", ann2.value());
    }

    @Test
    public void testEmptyStringValue() throws Exception {
        JsonWrapped ann = TestClassEmptyWrapper.class.getField("field").getAnnotation(JsonWrapped.class);
        assertEquals("", ann.value());
    }

    @Test
    public void testAnnotationOnMethod() throws Exception {
        JsonWrapped ann = TestClassWithGetter.class.getMethod("getData").getAnnotation(JsonWrapped.class);
        assertNotNull(ann, "Annotation should be applicable to methods");
        assertEquals("wrapper", ann.value());
    }
}
