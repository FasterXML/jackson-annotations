package com.fasterxml.jackson.annotation;

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
     * Helper class for implementations contained.
     */
    @SuppressWarnings("serial")
    private abstract static class Base<T> extends ObjectIdGenerator<T>
    {
        protected final Class<?> _scope;

        protected Base(Class<?> scope) {
            _scope = scope;
        }

        @Override
        public final Class<?> getScope() {
            return _scope;
        }
        
        @Override
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return (gen.getClass() == getClass()) && (gen.getScope() == _scope);
        }
        
        @Override
        public abstract T generateId(Object forPojo);
    }

    /*
    /**********************************************************
    /* Implementation classes
    /**********************************************************
     */
    
    /**
     * Abstract marker class used to allow explicitly specifying
     * that no generator is used; which also implies that no
     * Object Id is to be included or used.
     */
    @SuppressWarnings("serial")
    public abstract static class None extends ObjectIdGenerator<Object> { }
    
    /**
     * Abstract place-holder class which is used to denote case
     * where Object Identifier to use comes from a POJO property
     * (getter method or field). If so, value is written directly
     * during serialization, and used as-is during deserialization.
     *<p>
     * Actual implementation class is part of <code>databind</code>
     * package.
     */
    public abstract static class PropertyGenerator extends Base<Object> {
        private static final long serialVersionUID = 1L;

        protected PropertyGenerator(Class<?> scope) { super(scope); }
    }
    
    /**
     * Simple sequence-number based generator, which uses basic Java
     * <code>int</code>s (starting with value 1) as Object Identifiers.
     */
    public final static class IntSequenceGenerator extends Base<Integer>
    {
        private static final long serialVersionUID = 1L;

        protected transient int _nextValue;

        public IntSequenceGenerator() { this(Object.class, -1); }
        public IntSequenceGenerator(Class<?> scope, int fv) {
            super(scope);
            _nextValue = fv;
        }

        protected int initialValue() { return 1; }
        
        @Override
        public ObjectIdGenerator<Integer> forScope(Class<?> scope) {
            return (_scope == scope) ? this : new IntSequenceGenerator(scope, _nextValue);
        }
        
        @Override
        public ObjectIdGenerator<Integer> newForSerialization(Object context) {
            return new IntSequenceGenerator(_scope, initialValue());
        }

        @Override
        public IdKey key(Object key) {
            return new IdKey(getClass(), _scope, key);
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
     *<p>
     * One difference to other generators is that scope is always
     * set as <code>Object.class</code> (regardless of arguments): this
     * because UUIDs are globally unique, and scope has no meaning.
     */
    public final static class UUIDGenerator extends Base<UUID>
    {
        private static final long serialVersionUID = 1L;

        public UUIDGenerator() { this(Object.class); }
        private UUIDGenerator(Class<?> scope) {
            super(Object.class);
        }

        /**
         * Can just return base instance since this is essentially scopeless
         */
        @Override
        public ObjectIdGenerator<UUID> forScope(Class<?> scope) {
            return this;
        }
        
        /**
         * Can just return base instance since this is essentially scopeless
         */
        @Override
        public ObjectIdGenerator<UUID> newForSerialization(Object context) {
            return this;
        }

        @Override
        public UUID generateId(Object forPojo) {
            return UUID.randomUUID();
        }

        @Override
        public IdKey key(Object key) {
            return new IdKey(getClass(), null, key);
        }

        /**
         * Since UUIDs are always unique, let's fully ignore scope definition
         */
        @Override
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return (gen.getClass() == getClass());
        }
    }
}
