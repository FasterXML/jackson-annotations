/**
 * Public core annotations, most of which are used to configure how
 * Data Mapping/Binding works. Annotations in this package can only
 * have dependencies to non-annotation classes in Core package;
 * annotations that have dependencies to Mapper classes are included
 * in Mapper module (under <code>org.codehaus.jackson.map.annotate</code>).
 * Also contains parameter types (mostly enums) needed by annotations.
 *<p>
 * Note that prior versions (1.x) contained these annotations within
 * 'core' jar, as part of Streaming API.
 */
package com.fasterxml.jackson.annotation;
