# Overview

This project contains general purpose annotations for
Jackson Data Processor, used on value and handler types.
The only annotations not included are ones that require dependency
to the [Databind package](/FasterXML/jackson-databind).

Project contains versions 2.0 and above: source code for earlier (1.x) versions is available from [Codehaus](http://jackson.codehaus.org) SVN repository.
Note that with version 1.x these annotations were part of the 'core jar'.

[Full Listing of Jackson Annotations](jackson-annotations/wiki/JacksonAnnotations) details all available annotations; [Javadocs](http://fasterxml.github.com/jackson-annotations/javadoc/2.0.2/) gives more details.

-----

## Usage, general

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

## Usage, simple

Let's start with simple use cases: renaming or ignoring properties, and modifying types that are used.

Note: while examples only show field properties, same annotations would work with method (getter/setter) properties.

### Annotations for renaming properties

One of most common tasks is to change JSON name used for a property: for example:

    public class Name {
      @JsonProperty("firstName")
      public String _first_name;
    }

would result in JSON like:

    { "firstName" : "Bob" }

instead of

    { "_first_name" : "Bob"

### Annotations for Ignoring properties

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

Finally, you may even want to just ignore any "extra" properties from JSON (ones for which there is no counterpart in POJO). This can be done by adding:

    @JsonIgnoreProperties(ignoreUnknown=true)
    public class PojoWithAny {
      public int value;
    }

### Annotations for choosing more/less specific types

Sometimes the type Jackson uses when reading or writing a property is not quite what you want:

* When reading (deserializing), declared type may be a general type, but you know which exact implementation type to use
* When writing (serializing), Jackson will by default use the specific runtime type; but you may not want to include all information from that type but rather just contents of its supertype.

These cases can be handled by following annotations:

    public class ValueContainer {
      // although nominal type is 'Value', we want to read JSON as 'ValueImpl'
      @JsonDeserialize(as=ValueImpl.class)
      public Value value;

      // although runtime type may be 'AdvancedType', we really want to serialize
      // as 'BasicType'; two ways to do this:
      @JsonSerialize(as=BasicType.class)
      // or could also use: @JsonSerialize(typing=Typing.STATIC)
      public BasicType another;
    }

-----

## Usage, intermediate

### Using constructors or factory methods

By default, Jackson tries to use the "default" constructor (one that takes no arguments), when creating value instances. But you can also choose to use another constructor, or a static factory method to create instance. To do this, you will need to use annotation `@JsonCreator`, and possibly `@JsonProperty` annotations to bind names to arguments:

    public class CtorPOJO {
       private final int _x, _y;

       @JsonCreator
       public CtorPOJO(@JsonProperty("x") int x, @JsonProperty("y") int y) {
          _x = x;
          _y = y;
       }
    }

`@JsonCreator` can be used similarly for static factory methods.
But there is also an alternative usage, which is so-called "delegating" creator:

    public class DelegatingPOJO {
       private final int _x, _y;

       @JsonCreator
       public DelegatingPOJO(Map<String,Object> delegate) {
          _x = (Integer) delegate.get("x");
          _y = (Integer) delegate.get("y");
       }
      
the difference being that the creator method can only take one argument, and that argument must NOT have `@JsonProperty` annotation.

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

* [Javadocs](http://fasterxml.github.com/jackson-annotations/javadoc/2.0.2/)
* [Intro to polymorphic type handling](http://www.cowtowncoder.com/blog/archives/2010/03/entry_372.html)

### Changing property auto-detection

The default Jackson property detection rules will find:

* All ''public'' fields
* All ''public'' getters ('getXxx()' methods)
* All setters ('setXxx(value)' methods), ''regardless of visibility'')

But if this does not work, you can change visibility levels by using annotation `@JsonAutoDetect`.
If you wanted, for example, to auto-detect ALL fields (similar to how packages like GSON work), you could do:

    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
    public class POJOWithFields {
      private int value;
    }

or, to disable auto-detection of fields altogether:

    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
    public class POJOWithNoFields {
      // will NOT be included, unless there is access 'getValue()'
      public int value;
    }

-----

# Further reading

Project-specific documentation:

* [Full Listing of Jackson Annotations](jackson-annotations/wiki/JacksonAnnotations) details all available annotations.
* [Documentation](jackson-annotations/wiki/Documentation)

Related:

* [Databinding](https://github.com/FasterXML/jackson-databind) module has more documentation, since it is the main user of annotations.
In addition, here are other useful links:
* [Jackson Project Home](http://wiki.fasterxml.com/JacksonHome)
 * [Annotation documentation at FasterXML Wiki](http://wiki.fasterxml.com/JacksonAnnotations) covers 1.x annotations as well as 2.0


