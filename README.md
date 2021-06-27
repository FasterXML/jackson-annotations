# Overview

This project contains general purpose annotations for
Jackson Data Processor, used on value and handler types.
The only annotations not included are ones that require dependency
to the [Databind package](../../../jackson-databind).
Note that only annotations themselves (and related value classes) are included, but no functionality
that uses annotations.

Project contains versions 2.0 and above: source code for earlier (1.x) versions is available from [Jackson-1](../../../jackson-1) repository.

[Full Listing of Jackson Annotations](../../wiki/Jackson-Annotations) details all available annotations;
[Project Wiki](../../wiki) gives more details.

Project is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

[![Build (github)](https://github.com/FasterXML/jackson-annotations/actions/workflows/main.yml/badge.svg)](https://github.com/FasterXML/jackson-annotations/actions/workflows/main.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.core/jackson-annotations/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.jackson.core/jackson-annotations)
[![Javadoc](https://javadoc.io/badge/com.fasterxml.jackson.core/jackson-annotations.svg)](http://www.javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations)
[![Tidelift](https://tidelift.com/badges/package/maven/com.fasterxml.jackson.core:jackson-annotations)](https://tidelift.com/subscription/pkg/maven-com-fasterxml-jackson-core-jackson-annotations?utm_source=maven-com-fasterxml-jackson-core-jackson-annotations&utm_medium=referral&utm_campaign=readme)

-----

## Usage, general

### Improvements over typical Java annotations

In addition to regular usage (see below), there are couple of noteworthy improvements Jackson does:

* [Mix-in annotations](../../wiki/Mixin-Annotations) allow associating annotations on third-party classes ''without modifying classes''.
* Jackson annotations support full inheritance: meaning that you can ''override annotation definitions'', and not just class annotations but also method/field annotations!

### Maven, Java package

All annotations are in Java package `com.fasterxml.jackson.annotation`.
To use annotations, you need to use Maven dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-annotations</artifactId>
  <version>${jackson-annotations-version}</version>
</dependency>
```

or download jars from Maven repository (or via quick links on [Wiki](../../wiki))

## Usage, simple

Let's start with simple use cases: renaming or ignoring properties, and modifying types that are used.

Note: while examples only show field properties, same annotations would work with method (getter/setter) properties.

### Annotations for renaming properties

One of most common tasks is to change JSON name used for a property: for example:

```java
public class Name {
  @JsonProperty("firstName")
  public String _first_name;
}
```

would result in JSON like:

```json
{ "firstName" : "Bob" }
```

instead of

```json
{ "_first_name" : "Bob" }
```

### Annotations for Ignoring properties

Sometimes POJOs contain properties that you do not want to write out, so you can do:

```java
public class Value {
  public int value;
  @JsonIgnore public int internalValue;
}
```

and get JSON like:

```json
{ "value" : 42 }
```

or, you may get properties in JSON that you just want to skip: if so, you can use:

```java
@JsonIgnoreProperties({ "extra", "uselessValue" })
public class Value {
  public int value;
}
```

which would be able to handle JSON like:

```json
{ "value" : 42, "extra" : "fluffy", "uselessValue" : -13 }
```

Finally, you may even want to just ignore any "extra" properties from JSON (ones for which there is no counterpart in POJO). This can be done by adding:

```java
@JsonIgnoreProperties(ignoreUnknown=true)
public class PojoWithAny {
  public int value;
}
```

### Annotations for choosing more/less specific types

Sometimes the type Jackson uses when reading or writing a property is not quite what you want:

* When reading (deserializing), declared type may be a general type, but you know which exact implementation type to use
* When writing (serializing), Jackson will by default use the specific runtime type; but you may not want to include all information from that type but rather just contents of its supertype.

These cases can be handled by following annotations:

```java
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
```

-----

## Usage, intermediate

### Using constructors or factory methods

By default, Jackson tries to use the "default" constructor (one that takes no arguments), when creating value instances. But you can also choose to use another constructor, or a static factory method to create instance. To do this, you will need to use annotation `@JsonCreator`, and possibly `@JsonProperty` annotations to bind names to arguments:

```java
public class CtorPOJO {
   private final int _x, _y;

   @JsonCreator
   public CtorPOJO(@JsonProperty("x") int x, @JsonProperty("y") int y) {
      _x = x;
      _y = y;
   }
}
```

`@JsonCreator` can be used similarly for static factory methods.
But there is also an alternative usage, which is so-called "delegating" creator:

```java
public class DelegatingPOJO {
   private final int _x, _y;

   @JsonCreator
   public DelegatingPOJO(Map<String,Object> delegate) {
      _x = (Integer) delegate.get("x");
      _y = (Integer) delegate.get("y");
   }
}
```
      
the difference being that the creator method can only take one argument, and that argument must NOT have `@JsonProperty` annotation.

### Handling polymorphic types

If you need to read and write values of Objects where there are multiple possible subtypes (i.e. ones that exhibit polymorphism), you may need to enable inclusion of type information. This is needed so that Jackson can read back correct Object type when deserializing (reading JSON into Objects).
This can be done by adding `@JsonTypeInfo` annotation on ''base class'':

```java
@JsonTypeInfo(use=Id.MINIMAL_CLASS, include=As.PROPERTY, property="type") // Include Java class simple-name as JSON property "type"
@JsonSubTypes({@Type(Car.class), @Type(Aeroplane.class)}) // Required for deserialization only  
public abstract class Vehicle {
}
public class Car extends Vehicle {
  public String licensePlate;
}
public class Aeroplane extends Vehicle {
  public int wingSpan;
}

public class PojoWithTypedObjects {
  public List<Vehicle> items;
}
```

which gives serialized JSON like:

```json
{ "items": [
  { "type": "Car", "licensePlate": "X12345" },
  { "type": "Aeroplane", "wingSpan": 13 }
]}
```

Alternatively, `@JsonTypeInfo(use=DEDUCTION)` can be used to avoid requiring the 'type' field. For deserialization, types are _deduced_ based
on the fields available. Exceptions will be raised if subtypes do not have a distinct signature of fieldnames or JSON does
not resolve to single known signature.

Note that `@JsonTypeInfo` has lots of configuration possibilities: for more information check out
[Intro to polymorphic type handling](http://www.cowtowncoder.com/blog/archives/2010/03/entry_372.html)

### Changing property auto-detection

The default Jackson property detection rules will find:

* All ''public'' fields
* All ''public'' getters ('getXxx()' methods)
* All setters ('setXxx(value)' methods), ''regardless of visibility'')

But if this does not work, you can change visibility levels by using annotation `@JsonAutoDetect`.
If you wanted, for example, to auto-detect ALL fields (similar to how packages like GSON work), you could do:

```java
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class POJOWithFields {
  private int value;
}
```

or, to disable auto-detection of fields altogether:

```java
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
public class POJOWithNoFields {
  // will NOT be included, unless there is access 'getValue()'
  public int value;
}
```

-----

## Support

### Community support

Jackson components are supported by the Jackson community through mailing lists, Gitter forum, Github issues. See [Participation, Contributing](../../../jackson#participation-contributing) for full details.

### Enterprise support

Available as part of the Tidelift Subscription.

The maintainers of `jackson-annotations` and thousands of other packages are working with Tidelift to deliver commercial support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk, and improve code health, while paying the maintainers of the exact dependencies you use. [Learn more.](https://tidelift.com/subscription/pkg/maven-com-fasterxml-jackson-core-jackson-annotations?utm_source=maven-com-fasterxml-jackson-core-jackson-annotations&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)

-----

## Further reading

Project-specific documentation:

* [Full Listing of Jackson Annotations](../../wiki/Jackson-Annotations) details all available annotations.
* [jackson-annotations wiki](../../wiki)

Backwards compatibility:

* You can make Jackson 2 use Jackson 1 annotations with [jackson-legacy-introspector](https://github.com/Laures/jackson-legacy-introspector)

Related:

* [Databinding](https://github.com/FasterXML/jackson-databind) module has more documentation, since it is the main user of annotations.


