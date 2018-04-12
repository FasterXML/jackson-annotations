package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Tests to verify `shape`-related aspects of {@link JsonFormat.Value} handling.
 */
public class FormatShapeTest extends TestBase
{
    private final JsonFormat.Value EMPTY = JsonFormat.Value.empty();

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
    
        assertEquals(v1, v3.withShape(Shape.BOOLEAN));
    }

    public void testShape()
    {
        assertFalse(Shape.STRING.isNumeric());
        assertFalse(Shape.isNumeric(Shape.STRING));
        assertFalse(Shape.STRING.isStructured());
        assertFalse(Shape.isStructured(Shape.STRING));

        assertFalse(Shape.BOOLEAN.isNumeric());
        assertFalse(Shape.isNumeric(Shape.BOOLEAN));
        assertFalse(Shape.BOOLEAN.isStructured());
        assertFalse(Shape.isStructured(Shape.BOOLEAN));

        assertTrue(Shape.NUMBER_INT.isNumeric());
        assertTrue(Shape.isNumeric(Shape.NUMBER_INT));
        assertTrue(Shape.NUMBER_FLOAT.isNumeric());
        assertTrue(Shape.isNumeric(Shape.NUMBER_FLOAT));
        assertTrue(Shape.NUMBER.isNumeric());
        assertTrue(Shape.isNumeric(Shape.NUMBER));

        assertFalse(Shape.ARRAY.isNumeric());
        assertFalse(Shape.isNumeric(Shape.ARRAY));
        assertTrue(Shape.ARRAY.isStructured());
        assertTrue(Shape.isStructured(Shape.ARRAY));

        assertFalse(Shape.OBJECT.isNumeric());
        assertFalse(Shape.isNumeric(Shape.OBJECT));
        assertTrue(Shape.OBJECT.isStructured());
        assertTrue(Shape.isStructured(Shape.OBJECT));

        assertFalse(Shape.POJO.isNumeric());
        assertFalse(Shape.isNumeric(Shape.POJO));
        assertTrue(Shape.POJO.isStructured());
        assertTrue(Shape.isStructured(Shape.POJO));
        
        assertFalse(Shape.ANY.isNumeric());
        assertFalse(Shape.isNumeric(Shape.ANY));
        assertFalse(Shape.ANY.isStructured());
        assertFalse(Shape.isStructured(Shape.ANY));

        assertFalse(Shape.NATURAL.isNumeric());
        assertFalse(Shape.isNumeric(Shape.NATURAL));
        assertFalse(Shape.NATURAL.isStructured());
        assertFalse(Shape.isStructured(Shape.NATURAL));
    }
}
