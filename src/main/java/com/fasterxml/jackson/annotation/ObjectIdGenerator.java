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
     * Accessor called to determine whether scope of Object Identifiers
     * is global (within context of a single serialization) or not;
     * if not, scope is assumed to be per-type (using statically declared
     * type). Definition of scope is that all identifiers produced must
     * be unique within a scope: thus global scope would guarantee
     * that all produced identifiers are unique for full serialization
     * process, whereas local scopes only guarantee it for the supported
     * type (within single serialization).
     *<p>
     * One generator instance is needed per scope, and for deserialization,
     * separate Maps are kept on per-scope basis.
     *<p>
     * Standard generators (UUID, sequence-number) support global scope;
     * custom generators may support
     * 
     * @return True if global (one per serialization) scope is needed by
     *   generator; false if per-type scope is needed.
     */
    public abstract boolean usesGlobalScope();
    
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
