package com.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;

/**
 * Simple implementation of {@link ObjectIdResolver}
 *
 * @author Pascal Gélinas
 */
public class SimpleObjectIdResolver implements ObjectIdResolver {
    protected Map<IdKey, Object> _items;

    public SimpleObjectIdResolver() { }

    @Override
    public void bindItem(IdKey id, Object ob)
    {
        if (_items == null) {
            _items = new HashMap<>();
        } else {
            Object old = _items.get(id);
            if (old != null) {
                // 11-Nov-2020, tatu: As per [annotations#180] allow duplicate calls:
                if (old == ob) {
                    return;
                }
                throw new IllegalStateException(String.format(
"Object Id conflict: Id %s already bound to an Object %s: attempt to re-bind to a different Object %s",
                id.toString(), _desc(old), _desc(ob)));
            }
        }
        _items.put(id, ob);
    }

    private String _desc(Object ob) {
        if (ob == null) { 
            return "(null)";
        }
        String desc;
        if (ob instanceof String) {
            desc = "\""+ob+"\"";
        } else {
            desc = ob.toString();
            if (desc.length() > 100) {
                desc = desc.substring(0, 100) + "[... truncated]";
            }
        }
        return  ("(type: `"+ob.getClass().getName()+"`, value: "+desc+")");
    }

    @Override
    public Object resolveId(IdKey id) {
        return (_items == null) ? null : _items.get(id);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == getClass();
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        // 19-Dec-2014, tatu: Important: must re-create without existing mapping; otherwise bindings leak
        //    (and worse, cause unnecessary memory retention)
        return new SimpleObjectIdResolver();
    }
}
