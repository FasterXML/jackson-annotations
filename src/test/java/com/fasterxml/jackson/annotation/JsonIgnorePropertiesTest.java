package com.fasterxml.jackson.annotation;

import java.util.*;

/**
 * Tests to verify that it is possibly to merge {@link JsonIgnoreProperties.Value}
 * instances for overrides
 */
public class JsonIgnorePropertiesTest extends TestBase
{
    @JsonIgnoreProperties(value={ "foo", "bar" }, ignoreUnknown=true)
    private final static class Bogus {
    }

    private final JsonIgnoreProperties.Value EMPTY = JsonIgnoreProperties.Value.empty();

    public void testEmpty() {
        // ok to try to create from null; gives empty
        assertSame(EMPTY, JsonIgnoreProperties.Value.from(null));

        assertEquals(0, EMPTY.getIgnored().size());
        assertFalse(EMPTY.getAllowGetters());
        assertFalse(EMPTY.getAllowSetters());
    }

    public void testEquality() {
        assertEquals(EMPTY, EMPTY);

        // empty has "merge" set to 'true' so:
        assertSame(EMPTY, EMPTY.withMerge());

        JsonIgnoreProperties.Value v = EMPTY.withoutMerge();
        assertEquals(v, v);
        assertFalse(EMPTY.equals(v));
        assertFalse(v.equals(EMPTY));
    }

    public void testFromAnnotation() throws Exception
    {
        JsonIgnoreProperties.Value v = JsonIgnoreProperties.Value.from(
                Bogus.class.getAnnotation(JsonIgnoreProperties.class));
        assertNotNull(v);
        assertFalse(v.getMerge());
        assertFalse(v.getAllowGetters());
        assertFalse(v.getAllowSetters());
        Set<String> ign = v.getIgnored();
        assertEquals(2, v.getIgnored().size());
        assertEquals(_set("foo", "bar"), ign);
    }

    public void testFactories() {
        assertSame(EMPTY, JsonIgnoreProperties.Value.forIgnoreUnknown(false));
        assertSame(EMPTY, JsonIgnoreProperties.Value.forIgnoredProperties());
        assertSame(EMPTY, JsonIgnoreProperties.Value.forIgnoredProperties(Collections.<String>emptySet()));

        JsonIgnoreProperties.Value v = JsonIgnoreProperties.Value.forIgnoredProperties("a", "b");
        assertEquals(_set("a", "b"), v.getIgnored());

        JsonIgnoreProperties.Value vser = v.withAllowGetters();
        assertTrue(vser.getAllowGetters());
        assertFalse(vser.getAllowSetters());
        assertEquals(_set("a", "b"), vser.getIgnored());
        assertEquals(_set("a", "b"), vser.findIgnoredForDeserialization());
        assertEquals(_set(), vser.findIgnoredForSerialization());

        JsonIgnoreProperties.Value vdeser = v.withAllowSetters();
        assertFalse(vdeser.getAllowGetters());
        assertTrue(vdeser.getAllowSetters());
        assertEquals(_set("a", "b"), vdeser.getIgnored());
        assertEquals(_set(), vdeser.findIgnoredForDeserialization());
        assertEquals(_set("a", "b"), vdeser.findIgnoredForSerialization());
    }

    public void testMutantFactories()
    {
        assertEquals(2, EMPTY.withIgnored("a", "b").getIgnored().size());
        assertEquals(1, EMPTY.withIgnored(Collections.singleton("x")).getIgnored().size());
        assertEquals(0, EMPTY.withIgnored((Set<String>) null).getIgnored().size());

        assertTrue(EMPTY.withIgnoreUnknown().getIgnoreUnknown());
        assertFalse(EMPTY.withoutIgnoreUnknown().getIgnoreUnknown());

        assertTrue(EMPTY.withAllowGetters().getAllowGetters());
        assertFalse(EMPTY.withoutAllowGetters().getAllowGetters());
        assertTrue(EMPTY.withAllowSetters().getAllowSetters());
        assertFalse(EMPTY.withoutAllowSetters().getAllowSetters());

        assertTrue(EMPTY.withMerge().getMerge());
        assertFalse(EMPTY.withoutMerge().getMerge());
    }

    public void testSimpleMerge()
    {
        JsonIgnoreProperties.Value v1 = EMPTY.withIgnoreUnknown().withAllowGetters();
        JsonIgnoreProperties.Value v2a = EMPTY.withMerge()
                .withIgnored("a");
        JsonIgnoreProperties.Value v2b = EMPTY.withoutMerge();

        // when merging, should just have union of things
        JsonIgnoreProperties.Value v3a = v1.withOverrides(v2a);
        assertEquals(Collections.singleton("a"), v3a.getIgnored());
        assertTrue(v3a.getIgnoreUnknown());
        assertTrue(v3a.getAllowGetters());
        assertFalse(v3a.getAllowSetters());

        // when NOT merging, simply replacing values
        JsonIgnoreProperties.Value v3b = JsonIgnoreProperties.Value.merge(v1, v2b);
        assertEquals(Collections.emptySet(), v3b.getIgnored());
        assertFalse(v3b.getIgnoreUnknown());
        assertFalse(v3b.getAllowGetters());
        assertFalse(v3b.getAllowSetters());

        // and effectively really just uses overrides as is
        assertEquals(v2b, v3b);

        assertSame(v2b, v2b.withOverrides(null));
        assertSame(v2b, v2b.withOverrides(EMPTY));
    }

    public void testMergeIgnoreProperties()
    {
        JsonIgnoreProperties.Value v1 = EMPTY.withIgnored("a");
        JsonIgnoreProperties.Value v2 = EMPTY.withIgnored("b");
        JsonIgnoreProperties.Value v3 = EMPTY.withIgnored("c");

        JsonIgnoreProperties.Value merged = JsonIgnoreProperties.Value.mergeAll(v1, v2, v3);
        Set<String> all = merged.getIgnored();
        assertEquals(3, all.size());
        assertTrue(all.contains("a"));
        assertTrue(all.contains("b"));
        assertTrue(all.contains("c"));
    }

    public void testToString() {
        assertEquals(
                "JsonIgnoreProperties.Value(ignored=[],ignoreUnknown=false,allowGetters=false,allowSetters=true,merge=true)",
                EMPTY.withAllowSetters()
                    .withMerge()
                    .toString());
        int hash = EMPTY.hashCode();
        // no real good way to test but...
        if (hash == 0) {
            fail("Should not get 0 for hash");
        }
    }

    private Set<String> _set(String... args) {
        return new LinkedHashSet<String>(Arrays.asList(args));
    }
}
