package com.fasterxml.jackson.annotation;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that it is possibly to merge {@link JsonIncludeProperties.Value}
 * instances for overrides
 */
public class JsonIncludePropertiesTest
{
    @JsonIncludeProperties(value = {"foo", "bar"})
    private final static class Bogus
    {
    }

    private final JsonIncludeProperties.Value ALL = JsonIncludeProperties.Value.all();

    @Test
    public void testAll()
    {
        assertSame(ALL, JsonIncludeProperties.Value.from(null));
        assertNull(ALL.getIncluded());
        assertEquals(ALL, ALL);
        assertEquals("JsonIncludeProperties.Value(included=null)", ALL.toString());
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
        String tmp = v.toString();
        boolean test1 = tmp.equals("JsonIncludeProperties.Value(included=[foo, bar])");
        boolean test2 = tmp.equals("JsonIncludeProperties.Value(included=[bar, foo])");
        assertTrue(test1 || test2);
        assertEquals(v, JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class)));
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
        v = v.withOverrides(new JsonIncludeProperties.Value(Collections.<String>emptySet()));
        Set<String> included = v.getIncluded();
        assertEquals(0, included.size());
    }

    @Test
    public void testWithOverridesMerge() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(new JsonIncludeProperties.Value(_set("foo")));
        Set<String> included = v.getIncluded();
        assertEquals(1, included.size());
        assertEquals(_set("foo"), included);
    }

    private Set<String> _set(String... args)
    {
        return new LinkedHashSet<String>(Arrays.asList(args));
    }
}
