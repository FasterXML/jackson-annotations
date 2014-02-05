package com.fasterxml.jackson.annotation;

/**
 * Definition of API used for constructing Object Identifiers
 * (as annotated using {@link JsonIdentityInfo}).
 * Also defines factory methods used for creating instances
 * for serialization, deserialization.
 *
 * @param <T> Type of Object Identifiers produced.
 */
@SuppressWarnings("serial")
public abstract class ObjectIdGenerator<T>
    implements java.io.Serializable
{
    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */

    public abstract Class<?> getScope();

    /**
     * Method called to check whether this generator instance can
     * be used for Object Ids of specific generator type and
     * scope; determination is based by passing a configured
     * "blueprint" (prototype) instance; from which the actual
     * instances are created (using {@link #newForSerialization}).
     * 
     * @return True if this instance can be used as-is; false if not
     */
    public abstract boolean canUseFor(ObjectIdGenerator<?> gen);
    
    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */
    
    /**
     * Factory method to create a blueprint instance for specified
     * scope. Generators that do not use scope may return 'this'.
     */
    public abstract ObjectIdGenerator<T> forScope(Class<?> scope);
    
    /**
     * Factory method called to create a new instance to use for
     * serialization: needed since generators may have state
     * (next id to produce).
     *<p>
     * Note that actual type of 'context' is
     * <code>com.fasterxml.jackson.databind.SerializerProvider</code>,
     * but can not be declared here as type itself (as well as call
     * to this object) comes from databind package.
     * 
     * @param context Serialization context object used (of type
     *    <code>com.fasterxml.jackson.databind.SerializerProvider</code>;
     *    may be needed by more complex generators to access contextual
     *    information such as configuration.
     */
    public abstract ObjectIdGenerator<T> newForSerialization(Object context);

    /**
     * Method for constructing key to use for ObjectId-to-POJO maps.
     */
    public abstract IdKey key(Object key);
    
    /*
    /**********************************************************
    /* Methods for serialization
    /**********************************************************
     */
    
    /**
     * Method used for generating a new Object Identifier to serialize
     * for given POJO.
     * 
     * @param forPojo POJO for which identifier is needed
     * 
     * @return Object Identifier to use.
     */
    public abstract T generateId(Object forPojo);

    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    /**
     * Simple key class that can be used as a key for
     * ObjectId-to-POJO mappings, when multiple ObjectId types
     * and scopes are used.
     */
    public final static class IdKey
        implements java.io.Serializable
    {
        private static final long serialVersionUID = 1L;

        /**
         * Type of {@link ObjectIdGenerator} used for generating Object Id
         */
        public final Class<?> type;

        /**
         * Scope of the Object Id (may be null, to denote global)
         */
        public final Class<?> scope;

        /**
         * Object for which Object Id was generated: can NOT be null.
         */
        public final Object key;

        /**
         * Hash code
         */
        private final int hashCode;
        
        public IdKey(Class<?> type, Class<?> scope, Object key) {
            this.type = type;
            this.scope = scope;
            this.key = key;
            
            int h = key.hashCode() + type.getName().hashCode();
            if (scope != null) {
                h ^= scope.getName().hashCode();
            }
            hashCode = h;
        }

        @Override
        public int hashCode() { return hashCode; }

        @Override
        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;
            IdKey other = (IdKey) o;
            return (other.key.equals(key)) && (other.type == type) && (other.scope == scope);
        }
    }
}
