package com.fasterxml.jackson.annotation;

/**
 * Definition of API used for constructing Object Identifiers
 * (as annotated using {@link JsonIdentityInfo}).
 * Also defines factory methods used for creating instances
 * for serialization, deserialization.
 *
 * @param <T> Type of Object Identifiers produced.
 */
public abstract class ObjectIdGenerator<T>
{
    /**
     * Factory method called to create a new instance to use for
     * serialization. This includes initializing storage for keeping
     * track of serialized instances, along with id used.
     * Caller has to make sure to create proper number of instances
     * to ensure scoping (as implied by {@link #usesGlobalScope()}).
     */
    public abstract ObjectIdGenerator<T> newForSerialization(Class<?> scope);

    /**
     * Factory method called to create a new instance to use for
     * serialization. This includes initializing storage for keeping
     * track of deserialized instances, along with id used.
     * Caller has to make sure to create proper number of instances
     * to ensure scoping (as implied by {@link #usesGlobalScope()}).
     */
    public abstract ObjectIdGenerator<T> newForDeserialization(Class<?> scope);
    
    /**
     * Method called to check whether this generator instance can
     * be used for Object Ids of specific generator type and
     * scope.
     * 
     * @return True if this instance can be used as-is; false if not
     */
    public abstract boolean canUseFor(ObjectIdGenerator<?> gen, Class<?> scope);
    
    /**
     * Method used for generating an Object Identifier to serialize
     * for given POJO.
     * 
     * @param forPojo POJO for which identifier is needed
     * 
     * @return Object Identifier to use.
     */
    public abstract T generateId(Object forPojo);
}
