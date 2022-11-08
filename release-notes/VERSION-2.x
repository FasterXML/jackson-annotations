Project: jackson-annotations

NOTE: Annotations module will never contain changes in patch versions,
 only .0 releases can have changes. We may still release patch versions, but
 they will be identical to .0 versions, and only released for convenience
 (developers can line up all Jackson components with same patch version number).
 Main components will typically depend on .0 versions: please do NOT file
 issues against this being a bug; it is intentional.

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.15.0 (not yet released)

No changes since 2.14

2.14.0 (05-Nov-2022)

#204: Allow explicit `JsonSubTypes` repeated names check
 (contributed by Igor S)

2.13.0 (30-Sep-2021)

- Add `mvnw` wrapper

2.12.0 (29-Nov-2020)

#171: `JsonSubType.Type` should accept array of names
 (contributed by Swayam R)
#173: Jackson version alignment with Gradle 6
#174: Add `@JsonIncludeProperties`
 (contributed by Baptiste P)
#175: Add `@JsonTypeInfo(use=DEDUCTION)`
 (contributed by drekbour@github)
#177: Ability to use `@JsonAnyGetter` on fields
 (contributed by dominikrebhan@github)
#179: Add `@JsonKey` annotation
 (contributed by Kevin B)
#180: Allow repeated calls to `SimpleObjectIdResolver.bindItem()` for same mapping
#181: Add `namespace` property for `@JsonProperty` (for XML module)
- Add target `ElementType.ANNOTATION_TYPE` for `@JsonEnumDefaultValue` (was
  missing for some reason)

2.11.0 (26-Apr-2020)

- `JsonPattern.Value.pattern` retained as "", never (accidentally) exposed
  as `null`

2.10.0 (26-Sep-2019)

#138: Add basic Java 9+ module info
#141: Add `JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES`
 (suggested by Craig P)
#159: Add `JsonFormat.Shape.BINARY`

2.9.1 (07-Sep-2017)

#123: Add Automatic-Module-Name (`com.fasterxml.jackson.annotation`) for
  JDK9 interoperability
#124: Add new `PropertyAccessor` `SCALAR_CONSTRUCTOR`; `JsonAutoDetect.scalarConstructorVisibility
#129: Remove Target of `ElementType.CONSTRUCTOR` from `@JsonIgnoreProperties`,
  `@JsonPropertyOrder`

2.9.0 (30-Jul-2017)

#103: Add `JsonInclude.Include.CUSTOM`, properties for specifying filter(s) to use
#104: Add `JsonSetter.nulls`, `JsonSetter.contentNulls` for configurable null handling
#105: Add `JsonFormat.lenient` to allow configuring lenience of date/time deserializers
#108: Allow `@JsonValue` on fields
#109: Add `enabled` for `@JsonAnyGetter`, `@JsonAnySetter`, to allow disabling via mix-ins
#113: Add `@JsonMerge` to support (deep) merging of properties
#116: Add `@JsonAlias` annotation to allow specifying alternate names for a property
#120: Add new properties for `@JacksonInject`
- Allow use of `@JsonView` on classes, to specify Default View to use on non-annotated
  properties.

2.8.0 (04-Jul-2016)

#65: Add new choice for `JsonFormat.Shape`, `NATURAL`
#79: Change `@JsonTypeInfo.defaultImpl` default value to deprecate `JsonTypeInfo.None.class`
#83: Add `@JsonEnumDefaultValue` for indicating default enum choice if no real match found
 (suggested by Alejandro R)
#87: Add `@JsonIgnoreProperties.Value` to support merging of settings
#89: Add `JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES`
#95: Add `JsonFormat.Feature#ADJUST_DATES_TO_CONTEXT_TIME_ZONE`
 (suggested by Alexey B)

2.7.0 (10-Jan-2016)

#73: Add `@JsonClassDescription`
 (suggested by ufoscout@github)
#77: Add a new `ObjectIdGenerator`, `StringIdGenerator`, to allow arbitrary
  `String` Object Id usage
- Major rewrite of merging of `JsonFormat.Value` and `JsonInclude.Value`, to allow
  for better multi-level defaults (global, per-type, property)

2.6.0 (17-Jul-2015)

#43: Add `@JsonFormat(with=Feature.xxx)` to support things like
 `DeserializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED` on per-property basis.
#56: Improve `ObjectIdGenerators.key()` to handle `null` appropriately by returning `null`
#58: Add new properties for `@JsonIgnoreProperties`, "allowGetters", "allowSetters" 
#60: Add new value type, `OptBoolean`, for "optional booleans", to support proper handling
  and usage of default values, not just explicit true/false.
#61: Add new property, `@JsonProperty.access` (and matching enum) to support read-only/write-only properties
#64: Add `@Documented` for `@JsonPropertyDescription`
 (suggested by Zoltan S)
- Add `JsonInclude.Include.NON_ABSENT` value, for excluding "absent" Optional values.
- Add tag interface `JacksonAnnotationValue` for helper types used for encapsulating information
  for "complex" annotations (multi-property ones)

2.5.0 (01-Jan-2015)

#47: Add `@JsonCreator.mode` property to explicitly choose between delegating-
 and property-based creators, or to disable specific creator (Mode.DISABLED)
#48: Allow `@JsonView` for (method) parameters too
#49: Add `@JsonTypeInfo.skipWritingDefault`
#50: Add `ObjectIdGenerator.maySerializeAsObject()`,
  `ObjectIdGenerator.ObjectIdGenerator.maySerializeAsObject()` to support JSOG
- Added `@JsonInclude.content` to allow specifying inclusion criteria
  for `java.util.Map` entries separate from inclusion of `Map` values
  themselves
- Finalize fix for [databind#490], by ensuring new mapping initialized for new context
- Added `@JsonProperty.defaultValue()` (related to [databind#596])

2.4.0 (29-May-2014)

#31: Allow use of `@JsonPropertyOrder` for properties (not just classes)
#32: Add `@JsonProperty.index`
- Add `JsonFormat.Value#timeZoneAsString` (needed by Joda module)
- Add `@JsonRootName.namespace` to allow specifying of namespace with
  standard Jackson annotations (not just XML-specific ones that dataformat-xml
  provides)

2.3.0 (13-Nov-2013)

#13: Add `@JsonPropertyDescription`
 (suggested by Net-A-Porter@github)
#20: Allow use of `@JsonFilter` for properties (via fields, methods,
  constructor parameters)
(note: although #15 -- Add `JsonTypeInfo.As.EXISTING` property to support new
 variation for including Type Id was included, jackson-databind does not
 yet support it as of 2.3.0)

2.2.0 (22-Apr-2013)

No changes since 2.1.1

2.1.1 (11-Nov-2012)

Fixes:

* Make ObjectIdGenerator java.io.Serializable (needed when serializing
  ObjectMappers/-Writers/-Readers)

2.1.0 (08-Oct-2012)

New features:

* [Issue#4]: Add '@JsonIdentityReference', to support use case where values of
  a specific reference property are always serialized as ids, never as full POJO

Improvements:

* Added '@JsonIdentityInfo.firstAsID' property, to allow forcing all references
  to an Object to be serialized as id, including first one.
* Fix OSGi artifact name to be fully-qualified


2.0.2 (14-May-2012)

Fixes:

* OSGi bundle name was accidentally changed in 2.0.1; reverted back to one
  used in 2.0.0, earlier
 (reported Pascal G)

2.0.1 (22-Apr-2012)

Fixes:

* [JACKSON-827] Fix incompatibilities with JDK 1.5 (2.0.0 accidentally
  required 1.6)
 (reported Pascal G)

2.0.0 (25-Mar-2012)

Improvements:

* [JACKSON-437]: Allow injecting of type id as POJO property, by setting
  new '@JsonTypeInfo.visible' property to true.
* [JACKSON-669]: Allow prefix/suffix for @JsonUnwrapped properties
  (requested by Aner P)
* [JACKSON-787]: @JsonIgnoredProperties can be used on properties too

New features:

* [JACKSON-107]: Add support for Object Identity (to handled cycles, shared refs),
  with @JsonIdentityInfo
* [JACKSON-714] Add general-purpose '@JsonFormat' annotation
* [JACKSON-752]: Add @JsonInclude (replacement of @JsonSerialize.include)
* [JACKSON-754]: Add @JacksonAnnotationsInside for creating "annotation
  bundles" (also: AnnotationIntrospector.isAnnotationBundle())

Other:

* Lots of miscellaneous refactoring; moving most annotations from
  databind into this package; only leaving ones that depend on
  databind package types

------------------------------------------------------------------------
=== History: ===
------------------------------------------------------------------------

[entries for versions 1.x and earlier not retained; refer to earlier releases)
