package com.fasterxml.jackson.annotation;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that it is possibly to merge {@link JsonIncludeProperties.Value}
 * instances for overrides
 */
public class JsonIncludePropertiesTest
    extends AnnotationTestUtil
{
    @JsonIncludeProperties(value = {"foo", "bar"})
    private final static class Bogus
    {
    }

    @JsonIncludeProperties(order = OptBoolean.TRUE, value = {"id", "code", "name"})
    private final static class Ordered
    {
    }

    private final JsonIncludeProperties.Value ALL = JsonIncludeProperties.Value.all();

    @Test
    public void testAll()
    {
        assertSame(ALL, JsonIncludeProperties.Value.from(null));
        assertNull(ALL.getIncluded());
        assertNull(ALL.getOrdered());
        assertEquals(ALL, ALL);
        assertEquals("JsonIncludeProperties.Value(included=null,ordered=null)", ALL.toString());
        assertEquals(0, ALL.hashCode());
    }

    @Test
    public void testFromAnnotation()
    {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        assertNotNull(v);
        Set<String> included = v.getIncluded();
        assertEquals(2, v.getIncluded().size());
        assertEquals(_set("foo", "bar"), included);
        assertNull(v.getOrdered());
        String tmp = v.toString();
        boolean test1 = tmp.equals("JsonIncludeProperties.Value(included=[foo, bar],ordered=null)");
        boolean test2 = tmp.equals("JsonIncludeProperties.Value(included=[bar, foo],ordered=null)");
        assertTrue(test1 || test2);
        assertEquals(v, JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class)));

        // Let's also verify JDK serializability
        byte[] b = jdkSerialize(v);
        JsonIncludeProperties.Value deser = jdkDeserialize(b);

        assertEquals(v, deser);
    }

    @Test
    public void testWithOverridesAll() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(ALL);
        Set<String> included = v.getIncluded();
        assertEquals(2, included.size());
        assertEquals(_set("foo", "bar"), included);
    }

    @Test
    public void testWithOverridesEmpty() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(new JsonIncludeProperties.Value(Collections.<String>emptySet(), false));
        Set<String> included = v.getIncluded();
        assertEquals(0, included.size());
    }

    @Test
    public void testWithOverridesMerge() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(new JsonIncludeProperties.Value(_set("foo"), false));
        Set<String> included = v.getIncluded();
        assertEquals(1, included.size());
        assertEquals(_set("foo"), included);
    }

    @Test
    public void testFromAnnotationOrdered()
    {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(
                Ordered.class.getAnnotation(JsonIncludeProperties.class));
        assertNotNull(v);
        assertEquals(3, v.getIncluded().size());
        assertEquals(Boolean.TRUE, v.getOrdered());
    }

    @Test
    public void testHashCodeIncludesContents() {
        JsonIncludeProperties.Value v1 = new JsonIncludeProperties.Value(_set("a", "b"), null);
        JsonIncludeProperties.Value v2 = new JsonIncludeProperties.Value(_set("c", "d"), null);
        assertNotEquals(v1, v2);
        assertNotEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    public void testOrderedEquality()
    {
        JsonIncludeProperties.Value v1 = new JsonIncludeProperties.Value(_set("a", "b"), Boolean.TRUE);
        JsonIncludeProperties.Value v2 = new JsonIncludeProperties.Value(_set("a", "b"), Boolean.FALSE);
        JsonIncludeProperties.Value v3 = new JsonIncludeProperties.Value(_set("a", "b"), Boolean.TRUE);
        JsonIncludeProperties.Value v4 = new JsonIncludeProperties.Value(_set("a", "b"), null);
        assertNotEquals(v1, v2);
        assertNotEquals(v1, v4);
        assertNotEquals(v2, v4);
        assertEquals(v1, v3);
    }

    private Set<String> _set(String... args)
    {
        return new LinkedHashSet<String>(Arrays.asList(args));
    }
}
