package com.fasterxml.jackson.annotation;

// Silly test for PropertyAccessor, for code coverage
public class PropertyAccessorTest extends TestBase
{
    public void testProperties()
    {
        assertTrue(PropertyAccessor.ALL.creatorEnabled());
        assertTrue(PropertyAccessor.CREATOR.creatorEnabled());
        assertTrue(PropertyAccessor.CREATOR.creatorEnabled());

        assertTrue(PropertyAccessor.ALL.getterEnabled());
        assertTrue(PropertyAccessor.GETTER.getterEnabled());
        assertFalse(PropertyAccessor.CREATOR.getterEnabled());

        assertTrue(PropertyAccessor.ALL.isGetterEnabled());
        assertTrue(PropertyAccessor.IS_GETTER.isGetterEnabled());
        assertFalse(PropertyAccessor.CREATOR.isGetterEnabled());

        assertTrue(PropertyAccessor.ALL.setterEnabled());
        assertTrue(PropertyAccessor.SETTER.setterEnabled());
        assertFalse(PropertyAccessor.CREATOR.setterEnabled());

        assertTrue(PropertyAccessor.ALL.fieldEnabled());
        assertTrue(PropertyAccessor.FIELD.fieldEnabled());
        assertFalse(PropertyAccessor.CREATOR.fieldEnabled());
    }
}
