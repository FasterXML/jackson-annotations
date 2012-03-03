# Overview

This project contains general purpose annotations for
Jackson Data Processor, used on value and handler types.
The only annotations not included are ones that require dependency
to the [Databind package](/FasterXML/jackson-databind).

Project contains versions 2.0 and above: source code for earlier (1.x) versions is available from [Codehaus](http://jackson.codehaus.org) SVN repository.
Note that with version 1.x these annotations were part of the 'core jar'.

## Basic Usage

### Improvements over typical Java annotations

In addition to regular usage (see below), there are couple of noteworthy improvements Jackson does:

* [Mix-in annotations](jackson-annotations/wiki/MixinAnnotations) allow associating annotations on third-party classes ''without modifying classes''.
* Jackson annotations support full inheritance: meaning that you can ''override annotation definitions'', and not just class annotations but also method/field annotations!

### Maven, Java package

All annotations are in Java package `com.fasterxml.core.annotation`.
To use annotations, you need to use Maven dependency:

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.0.0</version>
    </dependency>


or download jars from Maven repository or [Download page](wiki.fasterxml.com/JacksonDownload)

### Usage: renaming properties

One of most common tasks is to change JSON name used for a property: for example:

    public class Name {
      @JsonProperty("firstName")
      public String _first_name;
    }

would result in JSON like:

    { "firstName" : "Bob" }

instead of

    { "_first_name" : "Bob"

### Usage: Ignoring properties

Sometimes POJOs contain properties that you do not want to write out, so you can do:

    public class Value {
      public int value;
      @JsonIgnore public int internalValue;
    }

and get JSON like:

    { "value" : 42 }

or, you may get properties in JSON that you just want to skip: if so, you can use:

    @JsonIgnoreProperties({ "extra", "uselessValue" })
    public class Value {
      public int value;
    }

which would be able to handle JSON like:

    { "value" : 42, "extra" : "fluffy", "uselessValue" : -13 }

### Usage: Changing property auto-detection

The default Jackson property detection rules will find:

* All ''public'' fields
* All ''public'' getters ('getXxx()' methods)
* All setters ('setXxx(value)' methods), ''regardless of visibility'')

But if this does not work, you can change visibility levels by using annotation `@JsonAutoDetect`.
If you wanted, for example, to auto-detect ALL fields (similar to how packages like GSON work), you could do:

    @JsonAutoDetect(fieldVisiblity=JsonAutoDetect.Visibility.ANY)
    public class POJOWithFields {
      private int value;
    }

or, to disable auto-detection of fields altogether:

    @JsonAutoDetect(fieldVisiblity=JsonAutoDetect.Visibility.NONE)
    public class POJOWithNoFields {
      // will NOT be included, unless there is access 'getValue()'
      public int value;
    }


### Handling polymorphic types

If you need to read and write values of Objects where there are multiple possible subtypes (i.e. ones that exhibit polymorphism), you may need to enable inclusion of type information. This is needed so that Jackson can read back correct Object type when deserializing (reading JSON into Objects).
This can be done by adding `@JsonTypeInfo` annotation on ''base class'':

    // Include Java class name ("com.myempl.ImplClass") as JSON property "class"
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
    public abstract class BaseClass {
    }

    public class Impl1 extends BaseClass {
      public int x;
    }
    public class Impl2 extends BaseClass {
      public String name;
    }

    public class PojoWithTypedObjects {
      public List<BaseClass> items;
    }

and this could result in serialized JSON like:

    { "items" : [
      { "class":"Impl2", "name":"Bob" },
      { "class":"Impl1", "x":13 }
    ])


Note that this annotation has lots of configuration possibilities: for more information check out:

* [JavaDocs](http://wiki.fasterxml.com/JacksonJavaDocs)
* [Intro to polymorphic type handling](http://www.cowtowncoder.com/blog/archives/2010/03/entry_372.html)


# Further reading

* [Jackson Project Home](http://wiki.fasterxml.com/JacksonHome)
* [Documentation](http://wiki.fasterxml.com/JacksonDocumentation)
 * [JavaDocs](http://wiki.fasterxml.com/JacksonJavaDocs)
* [Downloads](http://wiki.fasterxml.com/JacksonDownload)

Check out [Wiki].
