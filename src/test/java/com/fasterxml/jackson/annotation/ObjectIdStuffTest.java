package com.fasterxml.jackson.annotation;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Test mostly to keep code coverage decent
public class ObjectIdStuffTest
{
    @Test
    public void testObjectIdGenerator()
    {
        ObjectIdGenerator.IdKey k = new ObjectIdGenerator.IdKey(String.class, Object.class, "id1");
        int h = k.hashCode();
        if (h == 0) {
            fail("Should not produce 0 as hash");
        }
        assertTrue(k.equals(k));
        assertEquals("[ObjectId: key=id1, type=java.lang.String, scope=java.lang.Object]",
                k.toString());

        ObjectIdGenerator.IdKey k2 = new ObjectIdGenerator.IdKey(Integer.class, Object.class, "id2");
        assertFalse(k.equals(k2));
        assertFalse(k2.equals(k));
    }

    @Test
    public void testIntSequenceGenerator()
    {
        ObjectIdGenerators.IntSequenceGenerator gen = new ObjectIdGenerators.IntSequenceGenerator();
        Integer id = gen.generateId("foo");
        assertEquals(Integer.valueOf(-1), id);
        id = gen.generateId("foo");
        assertEquals(Integer.valueOf(0), id);
    }

    @Test
    public void testStringIdGenerator()
    {
        ObjectIdGenerators.StringIdGenerator gen = new ObjectIdGenerators.StringIdGenerator();
        String id = gen.generateId("foo");
        assertNotNull(id);
    }

    @Test
    public void testUUIDGenerator()
    {
        ObjectIdGenerators.UUIDGenerator gen = new ObjectIdGenerators.UUIDGenerator();
        UUID id = gen.generateId("foo");
        assertNotNull(id);
    }
}
