package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

public class JsonTypeInfoTest extends TestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible=true)
    private final static class Anno1 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY,
            property = "ext")
    private final static class Anno2 { }

    private final static JsonTypeInfo.Value EMPTY = JsonTypeInfo.Value.EMPTY;

    public void testEmpty() {
        assertSame(EMPTY, JsonTypeInfo.Value.from(null));

        assertEquals(EMPTY, EMPTY);
    }

    public void testFromAnnotation() throws Exception
    {
        JsonTypeInfo.Value v1 = JsonTypeInfo.Value.from(Anno1.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.CLASS, v1.getIdType());
        // default from annotation definition
        assertEquals(JsonTypeInfo.As.PROPERTY, v1.getInclusionType());
        // default from annotation definition
        assertEquals("@class", v1.getPropertyName());
        assertTrue(v1.getIdVisible());

        JsonTypeInfo.Value v2 = JsonTypeInfo.Value.from(Anno2.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.NAME, v2.getIdType());
        assertEquals(JsonTypeInfo.As.EXTERNAL_PROPERTY, v2.getInclusionType());
        assertEquals("ext", v2.getPropertyName());
        assertFalse(v2.getIdVisible());

        assertTrue(v1.equals(v1));
        assertTrue(v2.equals(v2));

        assertFalse(v1.equals(v2));
        assertFalse(v2.equals(v1));

        assertEquals("JsonTypeInfo.Value(idType=CLASS,includeAs=PROPERTY,propertyName=@class,defaultImpl=null,idVisible=true)", v1.toString());
        assertEquals("JsonTypeInfo.Value(idType=NAME,includeAs=EXTERNAL_PROPERTY,propertyName=ext,defaultImpl=null,idVisible=false)", v2.toString());
    }
}
