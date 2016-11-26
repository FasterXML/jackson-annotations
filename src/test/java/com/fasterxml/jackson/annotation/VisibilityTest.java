package com.fasterxml.jackson.annotation;

import java.lang.reflect.Member;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

// Silly test for JsonAutoDetect.Visibility type, for code coverage
public class VisibilityTest extends TestBase
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
    
    public void testProperties() throws Exception
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
    
    public void testEquality() {
        assertEquals(NO_OVERRIDES, NO_OVERRIDES);
        assertEquals(DEFAULTS, DEFAULTS);
        assertFalse(DEFAULTS.equals(NO_OVERRIDES));
        assertFalse(NO_OVERRIDES.equals(DEFAULTS));
    }

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

    public void testSimpleMerge() {
        
    }

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
