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
    /* Shared base class for concrete implementations
    /**********************************************************
     */

    /**
     * Helper class that implements scoped storage for Object
     * references.
     */
    protected abstract static class Base<T> extends ObjectIdGenerator<T>
    {
        protected final Class<?> _scope;

        /**
         * Lazily constructed mapping of "ids-to-Objects" used by deserialization.
         */
        protected IdentityHashMap<Object, T> _ids;

        protected IdentityHashMap<T, Object> _items;

        protected Base(Class<?> scope) {
            _scope = scope;
        }

        @Override
        public boolean canUseFor(ObjectIdGenerator<?> gen, Class<?> scope) {
            return (gen.getClass() == getClass()) && (scope == _scope);
        }
        
        public Class<?> getScope() {
            return _scope;
        }
        
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

    /*
    /**********************************************************
    /* Implementation classes
    /**********************************************************
     */
    
    /**
     * Abstract place-holder class which is used to denote case
     * where Object Identifier to use comes from a POJO property
     * (getter method or field). If so, value is written directly
     * during serialization, and used as-is during deserialization.
     *<p>
     * Actual implementation class is part of <code>databind</code>
     * package.
     */
    public abstract class PropertyGenerator<T> extends Base<T> {
        protected PropertyGenerator(Class<?> scope) { super(scope); }
    }
    
    /**
     * Simple sequence-number based generator, which uses basic Java
     * <code>int</code>s (starting with value 1) as Object Identifiers.
     */
    public final static class IntSequenceGenerator extends Base<Integer>
    {
        protected int _nextValue;

        public IntSequenceGenerator() { this(Object.class, 1); }
        public IntSequenceGenerator(Class<?> scope, int fv) {
            super(scope);
            _nextValue = fv;
        }

        @Override
        public ObjectIdGenerator<Integer> newForSerialization(Class<?> scope) {
            return new IntSequenceGenerator(scope, _nextValue);
        }

        // we don't really need value for deserialization but...
        @Override
        public ObjectIdGenerator<Integer> newForDeserialization(Class<?> scope) {
            return new IntSequenceGenerator(scope, _nextValue);
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
    public final static class UUIDGenerator extends Base<UUID>
    {
        public UUIDGenerator() { this(Object.class); }
        public UUIDGenerator(Class<?> scope) {
            super(scope);
        }
        
        @Override
        public ObjectIdGenerator<UUID> newForSerialization(Class<?> scope) {
            return new UUIDGenerator(scope);
        }

        @Override
        public ObjectIdGenerator<UUID> newForDeserialization(Class<?> scope) {
            return new UUIDGenerator(scope);
        }

        /**
         * Since UUIDs are always unique, let's fully ignore scope definition
         */
        @Override
        public boolean canUseFor(ObjectIdGenerator<?> gen, Class<?> scope) {
            return (gen.getClass() == getClass());
        }
        
        @Override
        public UUID generateId(Object forPojo) {
            return UUID.randomUUID();
        }
    }
    
}
