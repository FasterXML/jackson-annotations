package com.fasterxml.jackson.annotation;

import java.util.UUID;

/**
 * Container class for standard {@link ObjectIdGenerator} implementations:
 *<ul>
 *  <li>{@link IntSequenceGenerator}
 *  <li>{@link PropertyGenerator}
 *  <li>{@link StringIdGenerator} (since 2.7)
 *  <li>{@link UUIDGenerator}
 *</ul>
 *<p>
 * NOTE: {@link PropertyGenerator} applicability is limited in one case: it can only
 * be used on polymorphic base types (ones indicated using {@link JsonTypeInfo} or
 * default typing) via class annotations: property annotation will fail due to lack
 * of access to property, needed to determine type of Object Id for deserialization.
 * This limitation may be lifted in future versions but it is the limitation at least
 * up to and including Jackson 2.9.
 */
public class ObjectIdGenerators
{
    /**
     * Shared base class for concrete implementations.
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
            // 02-Apr-2015, tatu: As per [annotations#56], should check for null
            if (key == null) {
                return null;
            }
            return new IdKey(getClass(), _scope, key);
        }
        
        @Override
        public Integer generateId(Object forPojo) {
            // 02-Apr-2015, tatu: As per [annotations#56], should check for null
            if (forPojo == null)  {
                return null;
            }
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
            // 02-Apr-2015, tatu: As per [annotations#56], should check for null
            if (key == null) {
                return null;
            }
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

    /**
     * Implementation that will accept arbitrary (but unique) String Ids on
     * deserialization, and (by default) use random UUID generation similar
     * to {@link UUIDGenerator} for generation ids.
     *<p>
     * This generator is most useful for cases where another system creates
     * String Ids (of arbitrary structure, if any), and Jackson only needs to
     * keep track of id-to-Object mapping. Generation also works, although if
     * UUIDs are always used, {@link UUIDGenerator} is a better match as it
     * will also validate ids being used.
     *
     * @since 2.7
     */
    public final static class StringIdGenerator extends Base<String>
    {
        private static final long serialVersionUID = 1L;

        public StringIdGenerator() { this(Object.class); }
        private StringIdGenerator(Class<?> scope) {
            super(Object.class);
        }

        // Can just return base instance since this is essentially scopeless
        @Override
        public ObjectIdGenerator<String> forScope(Class<?> scope) {
            return this;
        }

        // Can just return base instance since this is essentially scopeless
        @Override
        public ObjectIdGenerator<String> newForSerialization(Object context) {
            return this;
        }

        @Override
        public String generateId(Object forPojo) {
            return UUID.randomUUID().toString();
        }

        @Override
        public IdKey key(Object key) {
            if (key == null) {
                return null;
            }
            return new IdKey(getClass(), null, key);
        }

        // Should be usable for generic Opaque String ids?
        @Override
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return (gen instanceof StringIdGenerator);
        }
    }
}
