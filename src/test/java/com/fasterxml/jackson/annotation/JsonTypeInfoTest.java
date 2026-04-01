package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTypeInfoTest
    extends AnnotationTestUtil
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible=true,
            defaultImpl = JsonTypeInfo.class, requireTypeIdForSubtypes = OptBoolean.TRUE)
    private final static class Anno1 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY,
            property = "ext",
            defaultImpl = Void.class, requireTypeIdForSubtypes = OptBoolean.FALSE)
    private final static class Anno2 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY,
            property = "ext",
            defaultImpl = Void.class)
    private final static class Anno3 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible = true,
            defaultImpl = Void.class,
            writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    private final static class Anno4 { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
            writeTypeIdForDefaultImpl = OptBoolean.TRUE)
    private final static class Anno5 { }

    @Test
    public void testEmpty() {
        // 07-Mar-2017, tatu: Important to distinguish "none" from 'empty' value...
        assertNull(JsonTypeInfo.Value.from(null));
    }

    @Test
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
        assertTrue(v1.getRequireTypeIdForSubtypes());

        JsonTypeInfo.Value v2 = JsonTypeInfo.Value.from(Anno2.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.NAME, v2.getIdType());
        assertEquals(JsonTypeInfo.As.EXTERNAL_PROPERTY, v2.getInclusionType());
        assertEquals("ext", v2.getPropertyName());
        assertFalse(v2.getIdVisible());
        assertEquals(Void.class, v2.getDefaultImpl());
        assertFalse(v2.getRequireTypeIdForSubtypes());

        assertTrue(v1.equals(v1));
        assertTrue(v2.equals(v2));

        assertFalse(v1.equals(v2));
        assertFalse(v2.equals(v1));

        assertEquals("JsonTypeInfo.Value(idType=CLASS,includeAs=PROPERTY,propertyName=@class,defaultImpl=NULL,idVisible=true,requireTypeIdForSubtypes=true,writeTypeIdForDefaultImpl=null)", v1.toString());
        assertEquals("JsonTypeInfo.Value(idType=NAME,includeAs=EXTERNAL_PROPERTY,propertyName=ext,defaultImpl=java.lang.Void,idVisible=false,requireTypeIdForSubtypes=false,writeTypeIdForDefaultImpl=null)", v2.toString());

        // Let's also verify JDK serializability
        byte[] b = jdkSerialize(v1);
        JsonTypeInfo.Value deser = jdkDeserialize(b);

        assertEquals(v1, deser);
    }

    @Test
    public void testMutators() throws Exception
    {
        JsonTypeInfo.Value v = JsonTypeInfo.Value.from(Anno1.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(JsonTypeInfo.Id.CLASS, v.getIdType());

        assertSame(v, v.withIdType(JsonTypeInfo.Id.CLASS));
        JsonTypeInfo.Value v2 = v.withIdType(JsonTypeInfo.Id.MINIMAL_CLASS);
        assertEquals(JsonTypeInfo.Id.MINIMAL_CLASS, v2.getIdType());
        JsonTypeInfo.Value v3 = v.withIdType(JsonTypeInfo.Id.SIMPLE_NAME);
        assertEquals(JsonTypeInfo.Id.SIMPLE_NAME, v3.getIdType());

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

    @Test
    public void testWithRequireTypeIdForSubtypes() {
        JsonTypeInfo.Value empty = JsonTypeInfo.Value.EMPTY;
        assertNull(empty.getRequireTypeIdForSubtypes());

        JsonTypeInfo.Value requireTypeIdTrue = empty.withRequireTypeIdForSubtypes(Boolean.TRUE);
        assertEquals(Boolean.TRUE, requireTypeIdTrue.getRequireTypeIdForSubtypes());

        JsonTypeInfo.Value requireTypeIdFalse = empty.withRequireTypeIdForSubtypes(Boolean.FALSE);
        assertEquals(Boolean.FALSE, requireTypeIdFalse.getRequireTypeIdForSubtypes());

        JsonTypeInfo.Value requireTypeIdDefault = empty.withRequireTypeIdForSubtypes(null);
        assertNull(requireTypeIdDefault.getRequireTypeIdForSubtypes());
    }

    @Test
    public void testDefaultValueForRequireTypeIdForSubtypes() {
        // default value
        JsonTypeInfo.Value v3 = JsonTypeInfo.Value.from(Anno3.class.getAnnotation(JsonTypeInfo.class));
        assertNull(v3.getRequireTypeIdForSubtypes());
        
        // toString()
        assertEquals("JsonTypeInfo.Value(idType=NAME,includeAs=EXTERNAL_PROPERTY,propertyName=ext,"
                + "defaultImpl=java.lang.Void,idVisible=false,requireTypeIdForSubtypes=null,writeTypeIdForDefaultImpl=null)", v3.toString());
    }

    // [annotations#342]
    @Test
    public void testWriteTypeIdForDefaultImplFromAnnotation() {
        // Anno4: writeTypeIdForDefaultImpl = FALSE
        JsonTypeInfo.Value v4 = JsonTypeInfo.Value.from(Anno4.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(Boolean.FALSE, v4.getWriteTypeIdForDefaultImpl());
        assertFalse(v4.shouldWriteTypeIdForDefaultImpl());

        // Anno5: writeTypeIdForDefaultImpl = TRUE
        JsonTypeInfo.Value v5 = JsonTypeInfo.Value.from(Anno5.class.getAnnotation(JsonTypeInfo.class));
        assertEquals(Boolean.TRUE, v5.getWriteTypeIdForDefaultImpl());
        assertTrue(v5.shouldWriteTypeIdForDefaultImpl());

        // Anno3: writeTypeIdForDefaultImpl not set (DEFAULT -> null)
        JsonTypeInfo.Value v3 = JsonTypeInfo.Value.from(Anno3.class.getAnnotation(JsonTypeInfo.class));
        assertNull(v3.getWriteTypeIdForDefaultImpl());
        // default should be treated as "write" (true)
        assertTrue(v3.shouldWriteTypeIdForDefaultImpl());
    }

    // [annotations#342]
    @Test
    public void testWithWriteTypeIdForDefaultImpl() {
        JsonTypeInfo.Value empty = JsonTypeInfo.Value.EMPTY;
        assertNull(empty.getWriteTypeIdForDefaultImpl());
        assertTrue(empty.shouldWriteTypeIdForDefaultImpl());

        // Mutate to FALSE
        JsonTypeInfo.Value vFalse = empty.withWriteTypeIdForDefaultImpl(Boolean.FALSE);
        assertEquals(Boolean.FALSE, vFalse.getWriteTypeIdForDefaultImpl());
        assertFalse(vFalse.shouldWriteTypeIdForDefaultImpl());

        // Mutate to TRUE
        JsonTypeInfo.Value vTrue = empty.withWriteTypeIdForDefaultImpl(Boolean.TRUE);
        assertEquals(Boolean.TRUE, vTrue.getWriteTypeIdForDefaultImpl());
        assertTrue(vTrue.shouldWriteTypeIdForDefaultImpl());

        // Mutate back to null
        JsonTypeInfo.Value vNull = vFalse.withWriteTypeIdForDefaultImpl(null);
        assertNull(vNull.getWriteTypeIdForDefaultImpl());
        assertTrue(vNull.shouldWriteTypeIdForDefaultImpl());

        // Same value returns same instance
        assertSame(vFalse, vFalse.withWriteTypeIdForDefaultImpl(Boolean.FALSE));
        assertSame(vTrue, vTrue.withWriteTypeIdForDefaultImpl(Boolean.TRUE));
    }

    // [annotations#342]
    @Test
    public void testWriteTypeIdForDefaultImplEqualsAndHashCode() {
        JsonTypeInfo.Value v1 = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.TRUE);
        JsonTypeInfo.Value v2 = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.TRUE);
        JsonTypeInfo.Value v3 = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.FALSE);
        JsonTypeInfo.Value vNull = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(null);

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());

        assertNotEquals(v1, v3);
        assertNotEquals(v1, vNull);
        assertNotEquals(v3, vNull);
    }

    // [annotations#342]
    @Test
    public void testWriteTypeIdForDefaultImplToString() {
        JsonTypeInfo.Value vFalse = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.FALSE);
        assertTrue(vFalse.toString().contains("writeTypeIdForDefaultImpl=false"));

        JsonTypeInfo.Value vTrue = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.TRUE);
        assertTrue(vTrue.toString().contains("writeTypeIdForDefaultImpl=true"));
    }

    // [annotations#342]
    @Test
    public void testWriteTypeIdForDefaultImplConstruct() {
        JsonTypeInfo.Value v = JsonTypeInfo.Value.construct(
                JsonTypeInfo.Id.CLASS, JsonTypeInfo.As.PROPERTY,
                null, Void.class, false, null, Boolean.FALSE);
        assertEquals(Boolean.FALSE, v.getWriteTypeIdForDefaultImpl());
        assertFalse(v.shouldWriteTypeIdForDefaultImpl());
    }

    // [annotations#342]
    @Test
    public void testWriteTypeIdForDefaultImplSerialization() throws Exception {
        JsonTypeInfo.Value v = JsonTypeInfo.Value.EMPTY.withWriteTypeIdForDefaultImpl(Boolean.FALSE);
        byte[] b = jdkSerialize(v);
        JsonTypeInfo.Value deser = jdkDeserialize(b);
        assertEquals(v, deser);
        assertEquals(Boolean.FALSE, deser.getWriteTypeIdForDefaultImpl());
    }
}
