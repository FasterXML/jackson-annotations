package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Tests to verify that it is possibly to merge {@link JsonFormat.Value}
 * instances for overrides.
 */
public class FormatTest extends TestBase
{
    private final JsonFormat.Value EMPTY = JsonFormat.Value.empty();

    @JsonFormat(shape=JsonFormat.Shape.BOOLEAN, pattern="xyz", timezone="bogus")
    private final static class Bogus { }

    public void testEmptyInstanceDefaults() {
        JsonFormat.Value empty = JsonFormat.Value.empty();
        for (Feature f : Feature.values()) {
            assertNull(empty.getFeature(f));
        }
    }

    public void testEquality() {
        assertTrue(EMPTY.equals(EMPTY));
        assertTrue(new JsonFormat.Value().equals(new JsonFormat.Value()));

        JsonFormat.Value v1 = JsonFormat.Value.forShape(Shape.BOOLEAN);
        JsonFormat.Value v2 = JsonFormat.Value.forShape(Shape.BOOLEAN);
        JsonFormat.Value v3 = JsonFormat.Value.forShape(Shape.SCALAR);
        
        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        assertFalse(v1.equals(v3));
        assertFalse(v3.equals(v1));
        assertFalse(v2.equals(v3));
        assertFalse(v3.equals(v2));

        // not strictly guaranteed but...
        assertFalse(v1.hashCode() == v3.hashCode());

        // then let's converge
        assertEquals(v1, v3.withShape(Shape.BOOLEAN));
        assertFalse(v1.equals(v1.withPattern("ZBC")));
        assertFalse(v1.equals(v1.withFeature(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
        assertFalse(v1.equals(v1.withoutFeature(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
    }
    
    public void testToString() {
        assertEquals("[pattern=null,shape=STRING,locale=null,timezone=null]",
                JsonFormat.Value.forShape(JsonFormat.Shape.STRING).toString());
        assertEquals("[pattern=[.],shape=ANY,locale=null,timezone=null]",
                JsonFormat.Value.forPattern("[.]").toString());
    }

    public void testFromAnnotation()
    {
        JsonFormat ann = Bogus.class.getAnnotation(JsonFormat.class);
        JsonFormat.Value v = JsonFormat.Value.from(ann);
        assertEquals("xyz", v.getPattern());
        assertEquals(JsonFormat.Shape.BOOLEAN, v.getShape());
        // note: since it's not valid, should not try access as real thing
        assertEquals("bogus", v.timeZoneAsString());
    }
    
    public void testSimpleMerge()
    {
        // Start with an empty instance
        assertFalse(EMPTY.hasLocale());
        assertFalse(EMPTY.hasPattern());
        assertFalse(EMPTY.hasShape());
        assertFalse(EMPTY.hasTimeZone());

        assertNull(EMPTY.getLocale());
        
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

    public void testShape() {
        assertFalse(JsonFormat.Shape.STRING.isNumeric());
        assertFalse(JsonFormat.Shape.STRING.isStructured());

        assertTrue(JsonFormat.Shape.NUMBER_INT.isNumeric());
        assertTrue(JsonFormat.Shape.NUMBER_FLOAT.isNumeric());
        assertTrue(JsonFormat.Shape.NUMBER.isNumeric());

        assertTrue(JsonFormat.Shape.ARRAY.isStructured());
        assertTrue(JsonFormat.Shape.OBJECT.isStructured());
    }

    public void testFeatures() {
        JsonFormat.Features f1 = JsonFormat.Features.empty();
        JsonFormat.Features f2 = f1.with(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .without(Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertTrue(f1.equals(f1));
        assertFalse(f1.equals(f2));
        assertFalse(f1.equals(null));
        assertFalse(f1.equals("foo"));

        assertNull(f1.get(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertEquals(Boolean.TRUE, f2.get(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY));

        assertNull(f1.get(Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));
        assertEquals(Boolean.FALSE, f2.get(Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));

        JsonFormat.Features f3 = f1.withOverrides(f2);
        assertEquals(Boolean.TRUE, f3.get(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertEquals(Boolean.FALSE, f3.get(Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));

        // switch values around
        JsonFormat.Features f4 = JsonFormat.Features.construct(
                new Feature[] { // enabled:
                        Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
        }, new Feature[] { // enabled
                Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY
        });
        assertEquals(Boolean.FALSE, f4.get(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertEquals(Boolean.TRUE, f4.get(Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));
    }
}
