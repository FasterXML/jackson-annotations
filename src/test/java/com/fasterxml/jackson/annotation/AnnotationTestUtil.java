package com.fasterxml.jackson.annotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

abstract class AnnotationTestUtil {
    /*
    /**********************************************************
    /* JDK ser/deser
    /**********************************************************
     */

    public static byte[] jdkSerialize(Object o)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2000);
        try (ObjectOutputStream obOut = new ObjectOutputStream(bytes)) {
            obOut.writeObject(o);
            obOut.close();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T jdkDeserialize(byte[] raw)
    {
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(raw))) {
            return (T) objIn.readObject();
        } catch (ClassNotFoundException e) {
            fail("Missing class: "+e.getMessage());
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
