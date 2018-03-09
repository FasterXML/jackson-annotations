package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

public class JsonTypeInfoTest extends TestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible=true,
            defaultImpl = JsonTypeInfo.class)
    private final static class Anno1 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY,
            property = "ext",
            defaultImpl = Void.class)
    private final static class Anno2 { }

    public void testEmpty() {
        // 07-Mar-2017, tatu: Important to distinguish "none" from 'empty' value...
        assertNull(JsonTypeInfo.Value.from(null));
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
        assertNull(v1.getDefaultImpl());

        JsonTypeInfo.Value v2 = JsonTypeInfo.Value.from(Anno2.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.NAME, v2.getIdType());
        assertEquals(JsonTypeInfo.As.EXTERNAL_PROPERTY, v2.getInclusionType());
        assertEquals("ext", v2.getPropertyName());
        assertFalse(v2.getIdVisible());
        assertEquals(Void.class, v2.getDefaultImpl());

        assertTrue(v1.equals(v1));
        assertTrue(v2.equals(v2));

        assertFalse(v1.equals(v2));
        assertFalse(v2.equals(v1));

        assertEquals("JsonTypeInfo.Value(idType=CLASS,includeAs=PROPERTY,propertyName=@class,defaultImpl=NULL,idVisible=true)", v1.toString());
        assertEquals("JsonTypeInfo.Value(idType=NAME,includeAs=EXTERNAL_PROPERTY,propertyName=ext,defaultImpl=java.lang.Void,idVisible=false)", v2.toString());
    }

    public void testMutators() throws Exception
    {
        JsonTypeInfo.Value v = JsonTypeInfo.Value.from(Anno1.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.CLASS, v.getIdType());

        assertSame(v, v.withIdType(JsonTypeInfo.Id.CLASS));
        JsonTypeInfo.Value v2 = v.withIdType(JsonTypeInfo.Id.MINIMAL_CLASS);
        assertEquals(JsonTypeInfo.Id.MINIMAL_CLASS, v2.getIdType());

        assertEquals(JsonTypeInfo.As.PROPERTY, v.getInclusionType());
        assertSame(v, v.withInclusionType(JsonTypeInfo.As.PROPERTY));
        v2 = v.withInclusionType(JsonTypeInfo.As.EXTERNAL_PROPERTY);
        assertEquals(JsonTypeInfo.As.EXTERNAL_PROPERTY, v2.getInclusionType());

        assertSame(v, v.withDefaultImpl(null));
        v2 = v.withDefaultImpl(String.class);
        assertEquals(String.class, v2.getDefaultImpl());

        assertSame(v, v.withIdVisible(true));
        assertFalse(v.withIdVisible(false).getIdVisible());

        assertEquals("foobar", v.withPropertyName("foobar").getPropertyName());
    }
}
