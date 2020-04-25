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
        assertFalse(empty.hasLocale());
        assertFalse(empty.hasPattern());
        assertFalse(empty.hasShape());
        assertFalse(empty.hasTimeZone());
        assertFalse(empty.hasLenient());

        assertFalse(empty.isLenient());
    }

    public void testEquality() {
        JsonFormat.Value v1 = JsonFormat.Value.forShape(Shape.BOOLEAN);
        
        // then let's converge
        assertFalse(v1.equals(v1.withPattern("ZBC")));
        assertFalse(v1.equals(v1.withFeature(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
        assertFalse(v1.equals(v1.withoutFeature(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
    }
    
    public void testToString() {
        assertEquals("JsonFormat.Value(pattern=,shape=STRING,lenient=null,locale=null,timezone=null,features=EMPTY)",
                JsonFormat.Value.forShape(JsonFormat.Shape.STRING).toString());
        assertEquals("JsonFormat.Value(pattern=[.],shape=ANY,lenient=null,locale=null,timezone=null,features=EMPTY)",
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

        // also:
        assertSame(EMPTY, JsonFormat.Value.from(null));
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

        // minor optimization: overriding with itself has no effect
        assertSame(merged, merged.withOverrides(merged));

        // but that empty is overridden
        merged = JsonFormat.Value.merge(EMPTY, v);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertFalse(merged.hasLocale());
        assertFalse(merged.hasShape());
        assertFalse(merged.hasTimeZone());

        // also some shortcuts:
        assertSame(merged, merged.withOverrides(null));

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

    public void testMultiMerge()
    {
        final String TEST_PATTERN = "format-string"; // not parsed, usage varies
        JsonFormat.Value format2 = JsonFormat.Value.forPattern(TEST_PATTERN);
        JsonFormat.Value format3 = JsonFormat.Value.forLeniency(Boolean.FALSE);

        JsonFormat.Value merged = JsonFormat.Value.mergeAll(EMPTY, format2, format3);
        assertEquals(TEST_PATTERN, merged.getPattern());
        assertEquals(Boolean.FALSE, merged.getLenient());
    }

    /*
    /**********************************************************
    /* Test specific value properties
    /**********************************************************
     */

    public void testLeniency() {
        JsonFormat.Value empty = JsonFormat.Value.empty();
        assertFalse(empty.hasLenient());
        assertFalse(empty.isLenient());
        assertNull(empty.getLenient());

        JsonFormat.Value lenient = empty.withLenient(Boolean.TRUE);
        assertTrue(lenient.hasLenient());
        assertTrue(lenient.isLenient());
        assertEquals(Boolean.TRUE, lenient.getLenient());
        assertTrue(lenient.equals(lenient));
        assertFalse(empty.equals(lenient));
        assertFalse(lenient.equals(empty));

        // should NOT create now one if no change:
        assertSame(lenient, lenient.withLenient(Boolean.TRUE));

        JsonFormat.Value strict = lenient.withLenient(Boolean.FALSE);
        assertTrue(strict.hasLenient());
        assertFalse(strict.isLenient());
        assertEquals(Boolean.FALSE, strict.getLenient());
        assertTrue(strict.equals(strict));
        assertFalse(empty.equals(strict));
        assertFalse(strict.equals(empty));
        assertFalse(lenient.equals(strict));
        assertFalse(strict.equals(lenient));

        // and finally, can also clear up setting
        JsonFormat.Value dunno = lenient.withLenient(null);
        assertFalse(dunno.hasLenient());
        assertFalse(dunno.isLenient());
        assertNull(dunno.getLenient());
        assertTrue(empty.equals(dunno));
        assertTrue(dunno.equals(empty));
        assertFalse(lenient.equals(dunno));
        assertFalse(dunno.equals(lenient));
    }

    public void testCaseInsensitiveValues() {
        JsonFormat.Value empty = JsonFormat.Value.empty();
        assertNull(empty.getFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES));

        JsonFormat.Value insensitive = empty.withFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        assertTrue(insensitive.getFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES));

        JsonFormat.Value sensitive = empty.withoutFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        assertFalse(sensitive.getFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES));
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
        Object str = "foo";
        assertFalse(f1.equals(str));

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
