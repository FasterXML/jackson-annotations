package com.fasterxml.jackson.annotation;

import java.lang.reflect.Member;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Silly test for JsonAutoDetect.Visibility type, for code coverage
public class VisibilityTest
{
    static class Bogus {
        public String value;
    }

    @JsonAutoDetect(fieldVisibility=Visibility.NON_PRIVATE,
            getterVisibility=Visibility.PROTECTED_AND_PUBLIC,
            isGetterVisibility=Visibility.NONE,
            setterVisibility=Visibility.PUBLIC_ONLY,
            creatorVisibility=Visibility.ANY)
    private final static class Custom { }

    private final static JsonAutoDetect.Value NO_OVERRIDES = JsonAutoDetect.Value.noOverrides();
    private final static JsonAutoDetect.Value DEFAULTS = JsonAutoDetect.Value.defaultVisibility();

    @Test
    public void testAnnotationProperties() throws Exception
    {
        Member m = Bogus.class.getField("value");

        assertTrue(JsonAutoDetect.Visibility.ANY.isVisible(m));
        assertFalse(JsonAutoDetect.Visibility.NONE.isVisible(m));

        assertTrue(JsonAutoDetect.Visibility.NON_PRIVATE.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.PUBLIC_ONLY.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC.isVisible(m));
        assertTrue(JsonAutoDetect.Visibility.NON_PRIVATE.isVisible(m));

        // forget why DEFAULT would give false but
        assertFalse(JsonAutoDetect.Visibility.DEFAULT.isVisible(m));
    }

    @Test
    public void testBasicValueProperties() {
        JsonAutoDetect.Value v = JsonAutoDetect.Value.DEFAULT;
        assertEquals(JsonAutoDetect.class, v.valueFor());

        // and then standard method override basics...
        int x = v.hashCode();
        if (x == 0) { // not guaranteed in theory but...
            fail();
        }

        assertTrue(v.equals(v));
        // mostly to ensure no NPE or class cast exception:
        assertFalse(v.equals(null));
        assertFalse(v.equals("foo"));
    }

    @Test
    public void testEquality() {
        assertEquals(NO_OVERRIDES, NO_OVERRIDES);
        assertEquals(DEFAULTS, DEFAULTS);
        assertFalse(DEFAULTS.equals(NO_OVERRIDES));
        assertFalse(NO_OVERRIDES.equals(DEFAULTS));
    }

    @Test
    public void testFromAnnotation()
    {
        JsonAutoDetect ann = Custom.class.getAnnotation(JsonAutoDetect.class);
        JsonAutoDetect.Value v = JsonAutoDetect.Value.from(ann);
        JsonAutoDetect.Value v2 = JsonAutoDetect.Value.from(ann);
        assertNotSame(v, v2);
        assertEquals(v, v2);
        assertEquals(v2, v);

        assertEquals(ann.fieldVisibility(), v.getFieldVisibility());
        assertEquals(ann.getterVisibility(), v.getGetterVisibility());
        assertEquals(ann.isGetterVisibility(), v.getIsGetterVisibility());
        assertEquals(ann.setterVisibility(), v.getSetterVisibility());
        assertEquals(ann.creatorVisibility(), v.getCreatorVisibility());
    }

    @Test
    public void testToString() {
        assertEquals(
"JsonAutoDetect.Value(fields=PUBLIC_ONLY,getters=PUBLIC_ONLY,"+
"isGetters=PUBLIC_ONLY,setters=ANY,creators=PUBLIC_ONLY)",
                JsonAutoDetect.Value.defaultVisibility().toString());
        assertEquals(
"JsonAutoDetect.Value(fields=DEFAULT,getters=DEFAULT,"+
"isGetters=DEFAULT,setters=DEFAULT,creators=DEFAULT)",
                JsonAutoDetect.Value.noOverrides().toString());
    }

    @Test
    public void testSimpleMerge() {
        JsonAutoDetect.Value base = JsonAutoDetect.Value.construct(
                Visibility.ANY,
                Visibility.PUBLIC_ONLY,
                Visibility.ANY,
                Visibility.NONE,
                Visibility.ANY);
        JsonAutoDetect.Value overrides = JsonAutoDetect.Value.construct(
                Visibility.NON_PRIVATE,
                Visibility.DEFAULT,
                Visibility.PUBLIC_ONLY,
                Visibility.DEFAULT,
                Visibility.DEFAULT);
        JsonAutoDetect.Value merged = JsonAutoDetect.Value.merge(base, overrides);
        assertFalse(merged.equals(base));
        assertFalse(merged.equals(overrides));
        assertEquals(merged, merged);

        assertEquals(Visibility.NON_PRIVATE, merged.getFieldVisibility());
        assertEquals(Visibility.PUBLIC_ONLY, merged.getGetterVisibility());
        assertEquals(Visibility.PUBLIC_ONLY, merged.getIsGetterVisibility());
        assertEquals(Visibility.NONE, merged.getSetterVisibility());
        assertEquals(Visibility.ANY, merged.getCreatorVisibility());

        // try the other way around too
        merged = JsonAutoDetect.Value.merge(overrides, base);
        assertEquals(Visibility.ANY, merged.getFieldVisibility());
        assertEquals(Visibility.PUBLIC_ONLY, merged.getGetterVisibility());
        assertEquals(Visibility.ANY, merged.getIsGetterVisibility());
        assertEquals(Visibility.NONE, merged.getSetterVisibility());
        assertEquals(Visibility.ANY, merged.getCreatorVisibility());

        // plus, special cases
        assertSame(overrides, JsonAutoDetect.Value.merge(null, overrides));
        assertSame(overrides, JsonAutoDetect.Value.merge(overrides, null));
    }

    @Test
    public void testFactoryMethods() {
        JsonAutoDetect.Value v = JsonAutoDetect.Value.construct(PropertyAccessor.FIELD,
                Visibility.ANY);
        assertEquals(Visibility.ANY, v.getFieldVisibility());
        assertEquals(Visibility.DEFAULT, v.getGetterVisibility());
        assertEquals(Visibility.DEFAULT, v.getIsGetterVisibility());
        assertEquals(Visibility.DEFAULT, v.getSetterVisibility());
        assertEquals(Visibility.DEFAULT, v.getCreatorVisibility());

        JsonAutoDetect.Value all = JsonAutoDetect.Value.construct(PropertyAccessor.ALL,
                Visibility.NONE);
        assertEquals(Visibility.NONE, all.getFieldVisibility());
        assertEquals(Visibility.NONE, all.getGetterVisibility());
        assertEquals(Visibility.NONE, all.getIsGetterVisibility());
        assertEquals(Visibility.NONE, all.getSetterVisibility());
        assertEquals(Visibility.NONE, all.getCreatorVisibility());
    }

    @Test
    public void testSimpleChanges() {
        assertSame(NO_OVERRIDES, NO_OVERRIDES.withFieldVisibility(Visibility.DEFAULT));
        JsonAutoDetect.Value v = NO_OVERRIDES.withCreatorVisibility(Visibility.PUBLIC_ONLY);
        assertNotSame(NO_OVERRIDES, v);
        assertEquals(Visibility.PUBLIC_ONLY, v.getCreatorVisibility());

        v = NO_OVERRIDES.withFieldVisibility(Visibility.ANY);
        assertEquals(Visibility.ANY, v.getFieldVisibility());

        v = NO_OVERRIDES.withGetterVisibility(Visibility.NON_PRIVATE);
        assertEquals(Visibility.NON_PRIVATE, v.getGetterVisibility());

        v = NO_OVERRIDES.withIsGetterVisibility(Visibility.PROTECTED_AND_PUBLIC);
        assertEquals(Visibility.PROTECTED_AND_PUBLIC, v.getIsGetterVisibility());

        v = NO_OVERRIDES.withSetterVisibility(Visibility.PUBLIC_ONLY);
        assertEquals(Visibility.PUBLIC_ONLY, v.getSetterVisibility());
    }
}
