package com.fasterxml.jackson.annotation;

import java.util.IdentityHashMap;
import java.util.UUID;

/**
 * Container class for standard {@link ObjectIdGenerator} implementations.
 */
public class ObjectIdGenerators
{
    /*
    /**********************************************************
    /* Implementation classes
    /**********************************************************
     */

    /**
     * Helper class that implements scoped storage for Object
     * references.
     */
    private abstract static class Base<T> extends ObjectIdGenerator<T>
    {
        /**
         * Lazily constructed mapping of "ids-to-Objects" used by deserialization.
         */
        protected IdentityHashMap<Object, T> _ids;

        protected IdentityHashMap<T, Object> _items;
        
        protected T findId(Object item) {
            if (_ids == null) {
                return null;
            }
            return _ids.get(item);
        }

        protected Object findItem(T id) {
            if (_items == null) {
                return null;
            }
            return _items.get(id);
        }

        /**
         * Method called during serialization to keep track of ids we have
         * used.
         */
        protected void addId(Object item, T id) {
            if (_ids == null) {
                _ids = new IdentityHashMap<Object, T>(16);
            }
            _ids.put(item, id);
        }

        /**
         * Method called during deserialization to keep track of items we have
         * deserialized, along with ids they had.
         */
        protected void addItem(Object item, T id) {
            if (_items == null) {
                _items = new IdentityHashMap<T, Object>(16);
            }
            _ids.put(item, id);
        }
    }
    
    /**
     * Abstract place-holder class which is used to denote case
     * where Object Identifier to use comes from a POJO property
     * (getter method or field). If so, value is written directly
     * during serialization, and used as-is during deserialization.
     *<p>
     * Actual implementation class is part of <code>databind</code>
     * package.
     */
    public abstract class PropertyGenerator<T> extends Base<T> { }
    
    /**
     * Simple sequence-number based generator, which uses basic Java
     * <code>int</code>s (starting with value 1) as Object Identifiers.
     */
    public static class IntSequenceGenerator extends Base<Integer>
    {
        protected int _nextValue;

        public IntSequenceGenerator() { this(1); }
        public IntSequenceGenerator(int fv) {
            super();
            _nextValue = fv;
        }

        @Override
        public ObjectIdGenerator<Integer> newForSerialization(Class<?> scope) {
            return new IntSequenceGenerator(_nextValue);
        }

        // we don't really need value for deserialization but...
        @Override
        public ObjectIdGenerator<Integer> newForDeserialization(Class<?> scope) {
            return new IntSequenceGenerator(_nextValue);
        }
        
        /**
         * We can easily support global scope with simple sequences, so return true
         */
        @Override
        public boolean usesGlobalScope() {
            return true;
        }

        @Override
        public Integer generateId(Object forPojo) {
            int id = _nextValue;
            ++_nextValue;
            return id;
        }
    }

    /**
     * Implementation that just uses {@link java.util.UUID}s as reliably
     * unique identifiers: downside is that resulting String is
     * 36 characters long.
     */
    public static class UUIDGenerator extends Base<UUID>
    {
        @Override
        public ObjectIdGenerator<UUID> newForSerialization(Class<?> scope) {
            return new UUIDGenerator();
        }

        @Override
        public ObjectIdGenerator<UUID> newForDeserialization(Class<?> scope) {
            return new UUIDGenerator();
        }
        
        /**
         * UUIDs are globally unique, so yes we can support global scope
         */
        @Override
        public boolean usesGlobalScope() {
            return true;
        }

        @Override
        public UUID generateId(Object forPojo) {
            return UUID.randomUUID();
        }
    }
    
}
