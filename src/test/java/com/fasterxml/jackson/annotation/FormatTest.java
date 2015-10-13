package com.fasterxml.jackson.annotation;

/**
 * Tests to verify that it is possibly to merge {@link JsonFormat.Value}
 * instances for overrides.
 */
public class FormatTest extends TestBase
{
    private final JsonFormat.Value EMPTY = JsonFormat.Value.empty();

    public void testEmptyInstanceDefaults() {
        JsonFormat.Value empty = JsonFormat.Value.empty();
        for (JsonFormat.Feature f : JsonFormat.Feature.values()) {
            assertNull(empty.getFeature(f));
        }
    }

    public void testEquality() {
        assertTrue(EMPTY.equals(EMPTY));
        assertTrue(new JsonFormat.Value().equals(new JsonFormat.Value()));

        JsonFormat.Value v1 = JsonFormat.Value.forShape(JsonFormat.Shape.BOOLEAN);
        JsonFormat.Value v2 = JsonFormat.Value.forShape(JsonFormat.Shape.BOOLEAN);
        JsonFormat.Value v3 = JsonFormat.Value.forShape(JsonFormat.Shape.SCALAR);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        assertFalse(v1.equals(v3));
        assertFalse(v3.equals(v1));
        assertFalse(v2.equals(v3));
        assertFalse(v3.equals(v2));
    }
    
    public void testToString() {
        assertEquals("[pattern=null,shape=STRING,locale=null,timezone=null]",
                JsonFormat.Value.forShape(JsonFormat.Shape.STRING).toString());
        assertEquals("[pattern=[.],shape=ANY,locale=null,timezone=null]",
                JsonFormat.Value.forPattern("[.]").toString());
    }

    public void testSimpleMerge()
    {
        // Start with an empty instance
        assertFalse(EMPTY.hasLocale());
        assertFalse(EMPTY.hasPattern());
        assertFalse(EMPTY.hasShape());
        assertFalse(EMPTY.hasTimeZone());

        // then with a non-empty one
        final String TEST_PATTERN = "format-string"; // not parsed, usage varies

        JsonFormat.Value v = JsonFormat.Value.forPattern(TEST_PATTERN);
        assertTrue(v.hasPattern());
        assertEquals(TEST_PATTERN, v.getPattern());
        assertFalse(v.hasLocale());
        assertFalse(v.hasShape());
        assertFalse(v.hasTimeZone());

        // and ensure nothing overridden with empty
        JsonFormat.Value merged = v.withOverrides(EMPTY);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertFalse(merged.hasLocale());
        assertFalse(merged.hasShape());
        assertFalse(merged.hasTimeZone());

        // but that empty is overridden
        merged = EMPTY.withOverrides(v);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertFalse(merged.hasLocale());
        assertFalse(merged.hasShape());
        assertFalse(merged.hasTimeZone());

        // then with some other combinations
        final JsonFormat.Shape TEST_SHAPE = JsonFormat.Shape.NUMBER;
        JsonFormat.Value v2 = JsonFormat.Value.forShape(TEST_SHAPE);

        merged = v.withOverrides(v2);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertFalse(merged.hasLocale());
        assertEquals(TEST_SHAPE, merged.getShape());
        assertFalse(merged.hasTimeZone());

        merged = v2.withOverrides(v);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertFalse(merged.hasLocale());
        assertEquals(TEST_SHAPE, merged.getShape());
        assertFalse(merged.hasTimeZone());
    }
}
