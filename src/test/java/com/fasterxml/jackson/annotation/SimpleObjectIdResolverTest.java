package com.fasterxml.jackson.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SimpleObjectIdResolverTest
{
    @Test
    public void testSimpleHandling() 
    {
        // Let's create a simple ObjectIdResolver with 2 non-duplicate items
        SimpleObjectIdResolver resolver = new SimpleObjectIdResolver();
        ObjectIdGenerator.IdKey key1 = new ObjectIdGenerator.IdKey(String.class, null, "key1");
        ObjectIdGenerator.IdKey key2 = new ObjectIdGenerator.IdKey(String.class, null, "key2");

        // Bind 2 non-duplicate items, check they can be resolved
        resolver.bindItem(key1, "value1");
        resolver.bindItem(key2, "value2");

        assertEquals("value1", resolver.resolveId(key1));
        assertEquals("value2", resolver.resolveId(key2));
        assertEquals(2, resolver._items.size());

        // And then verify that multiple bindings of the same key/bound value is ok
        resolver.bindItem(key1, "value1");
        resolver.bindItem(key2, "value2");
        assertEquals(2, resolver._items.size());

        // But that overriding is not
        try {
            resolver.bindItem(key1, "value3");
            fail("Should have thrown an exception for re-binding");
        } catch (IllegalStateException e) {
            assertEquals(
"Object Id conflict: Id [ObjectId: key=key1, type=java.lang.String, scope=NONE]"
+" already bound to an Object (type: `java.lang.String`, value: \"value1\"):"
+" attempt to re-bind to a different Object (type: `java.lang.String`, value: \"value3\")",
                    e.getMessage());
        }
    }

}
