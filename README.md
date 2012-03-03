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

* [Mix-in annotations](wiki/MixinAnnotations) allow associating annotations on third-party classes ''without modifying classes''.
* Jackson annotations support full inheritance: meaning that you can ''override annotation definitions'', and not just class annotations but also method/field annotations!

### Renaming properties

One of most common tasks is to change JSON name used for a property: for example:

    public class POJO {
      @JsonProperty("firstName")
      public String _first_name;
    }

would result in JSON like:

    { "firstName" : "Bob" }

instead of

    { "_first_name" : "Bob"

### Ignoring properties

### Handling polymorphic types

# Further reading

* [Jackson Project Home](http://wiki.fasterxml.com/JacksonHome)
* [Documentation](http://wiki.fasterxml.com/JacksonDocumentation)
 * [JavaDocs](http://wiki.fasterxml.com/JacksonJavaDocs)
* [Downloads](http://wiki.fasterxml.com/JacksonDownload)

Check out [Wiki].
