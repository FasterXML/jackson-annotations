package com.fasterxml.jackson.annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tests to verify that it is possibly to merge {@link JsonIncludeProperties.Value}
 * instances for overrides
 */
public class JsonIncludePropertiesTest extends TestBase
{
    @JsonIncludeProperties(value = {"foo", "bar"})
    private final static class Bogus
    {
    }

    private final JsonIncludeProperties.Value ALL = JsonIncludeProperties.Value.all();

    public void testAll()
    {
        assertSame(ALL, JsonIncludeProperties.Value.from(null));
        assertNull(ALL.getIncluded());
        assertEquals(ALL, ALL);
        assertEquals("JsonIncludeProperties.Value(included=null)", ALL.toString());
        assertEquals(0, ALL.hashCode());
    }

    public void testFromAnnotation()
    {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        assertNotNull(v);
        Set<String> included = v.getIncluded();
        assertEquals(2, v.getIncluded().size());
        assertEquals(_set("foo", "bar"), included);
        assertEquals("JsonIncludeProperties.Value(included=[bar, foo])", v.toString());
        assertEquals(v, JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class)));
    }

    public void testWithOverridesAll() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(ALL);
        Set<String> included = v.getIncluded();
        assertEquals(2, included.size());
        assertEquals(_set("foo", "bar"), included);
    }

    public void testWithOverridesEmpty() {
        JsonIncludeProperties.Value v = JsonIncludeProperties.Value.from(Bogus.class.getAnnotation(JsonIncludeProperties.class));
        v = v.withOverrides(new JsonIncludeProperties.Value(Collections.<String>emptySet()));
        Set<String> included = v.getIncluded();
        assertEquals(0, included.size());
    }

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
