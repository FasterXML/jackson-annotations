package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;

/**
 * Definition of API used for constructing POJO from Object Identifiers (as
 * annotated using {@link JsonIdentityInfo}).
 * 
 * @since 2.4
 */
public interface ObjectIdResolver {
    /**
     * Method called when a POJO is deserialized and has an Object Identifier.
     * Method exists so that implementation can keep track of existing object in
     * json stream that could be useful for further resolution.
     * 
     * @param id
     *            The Object Identifer
     * @param ob
     *            The POJO associated to that Identifier
     */
    void bindItem(IdKey id, Object pojo);

    /**
     * Method called when deserialization encounters the given Object Identifier
     * and requires the POJO associated with it.
     * 
     * @param id
     *            The Object Identifier
     * @return The POJO, or null if unable to resolve.
     */
    Object resolveId(IdKey id);

    /**
     * Factory method called to create a new instance to use for
     * deserialization: needed since resolvers may have state (a pool of
     * objects).
     * <p>
     * Note that actual type of 'context' is
     * <code>com.fasterxml.jackson.databind.DeserializationContext</code>, but
     * can not be declared here as type itself (as well as call to this object)
     * comes from databind package.
     * 
     * @param context
     *            Deserialization context object used (of type
     *            <code>com.fasterxml.jackson.databind.DeserializationContext</code>
     *            ; may be needed by more complex resolvers to access contextual
     *            information such as configuration.
     */
    ObjectIdResolver newForDeserialization(Object context);

    /**
     * Method called to check whether this resolver instance can be used for
     * Object Ids of specific resolver type; determination is based by passing a
     * configured "blueprint" (prototype) instance; from which the actual
     * instances are created (using {@link #newForDeserialization}).
     * 
     * @return True if this instance can be used as-is; false if not
     */
    boolean canUseFor(ObjectIdResolver resolverType);
}
