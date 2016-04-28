package com.fasterxml.jackson.annotation;

// Silly test for OptBoolean, for code coverage
public class OptBooleanTest extends TestBase
{
    public void testProperties()
    {
        assertTrue(OptBoolean.TRUE.asPrimitive());
        assertFalse(OptBoolean.FALSE.asPrimitive());
        assertFalse(OptBoolean.DEFAULT.asPrimitive());

        assertSame(OptBoolean.FALSE, OptBoolean.fromBoolean(false));
        assertSame(OptBoolean.TRUE, OptBoolean.fromBoolean(true));
    }
}
