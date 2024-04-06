package com.fasterxml.jackson.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Silly test for OptBoolean, for code coverage
public class OptBooleanTest {
    @Test
    public void testProperties()
    {
        assertTrue(OptBoolean.TRUE.asPrimitive());
        assertFalse(OptBoolean.FALSE.asPrimitive());
        assertFalse(OptBoolean.DEFAULT.asPrimitive());

        assertSame(OptBoolean.FALSE, OptBoolean.fromBoolean(false));
        assertSame(OptBoolean.TRUE, OptBoolean.fromBoolean(true));
    }
}
