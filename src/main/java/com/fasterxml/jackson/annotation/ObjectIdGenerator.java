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
    /*
    /**********************************************************
    /* Accessors
    /**********************************************************
     */

    public abstract Class<?> getScope();
    
    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */
    
    /**
     * Factory method to create a blueprint instance for specified
     * scope. Generator that do not use scope may return 'this'.
     */
    public abstract ObjectIdGenerator<T> forScope(Class<?> scope);
    
    /**
     * Factory method called to create a new instance to use for
     * serialization. This includes initializing storage for keeping
     * track of serialized instances, along with id used.
     */
    public abstract ObjectIdGenerator<T> newForSerialization();

    /**
     * Factory method called to create a new instance to use for
     * serialization. This includes initializing storage for keeping
     * track of deserialized instances, along with id used.
     */
    public abstract ObjectIdGenerator<T> newForDeserialization();
    
    /**
     * Method called to check whether this generator instance can
     * be used for Object Ids of specific generator type and
     * scope.
     * 
     * @return True if this instance can be used as-is; false if not
     */
    public abstract boolean canUseFor(ObjectIdGenerator<?> gen);

    /*
    /**********************************************************
    /* Methods for serialization
    /**********************************************************
     */
    
    /**
     * Method used during serialization, to try to find an Object Id for given already serialized
     * Object: if none found, returns null.
     */
    public abstract T findId(Object forPojo);

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
    /* Methods for deserialization
    /**********************************************************
     */
    
    /**
     * Method used during deserialization, to try to find an item for which given
     * id was used.
     */
    public abstract Object findItem(T id);

    /**
     * Method called during deserialization to establishing mapping between
     * given item, and the id it was using.
     */
    public abstract void addItem(Object item, T id);

}
