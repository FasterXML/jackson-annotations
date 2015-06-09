package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Class annotation that can be used to define which kinds of Methods
 * are to be detected by auto-detection, and with what minimum access level.
 * Auto-detection means using name conventions
 * and/or signature templates to find methods to use for data binding.
 * For example, so-called "getters" can be auto-detected by looking for
 * public member methods that return a value, do not take argument,
 * and have prefix "get" in their name.
 *<p>
 * Default setting for all accessors is {@link Visibility#DEFAULT}, which 
 * in turn means that the global defaults are used. Defaults
 * are different for different accessor types (getters need to be public;
 * setters can have any access modifier, for example).
 * If you assign different {@link Visibility} type then it will override
 * global defaults: for example, to require that all setters must be public,
 * you would use:
 *<pre>
 *   @JsonAutoDetect(setterVisibility=Visibility.PUBLIC_ONLY)
 *</pre>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect
{
    /**
     * Enumeration for possible visibility thresholds (minimum visibility)
     * that can be used to limit which methods (and fields) are
     * auto-detected.
     */
    public enum Visibility {
        /**
         * Value that means that all kinds of access modifiers are acceptable,
         * from private to public.
         */
        ANY,
        /**
         * Value that means that any other access modifier other than 'private'
         * is considered auto-detectable.
         */
        NON_PRIVATE,
        /**
         * Value that means access modifiers 'protected' and 'public' are
         * auto-detectable (and 'private' and "package access" == no modifiers
         * are not)
         */
        PROTECTED_AND_PUBLIC,
        /**
         * Value to indicate that only 'public' access modifier is considered
         * auto-detectable.
         */
        PUBLIC_ONLY,
        /**
         * Value that indicates that no access modifiers are auto-detectable:
         * this can be used to explicitly disable auto-detection for specified
         * types.
         */
        NONE,
        
        /**
         * Value that indicates that default visibility level (whatever it is,
         * depends on context) is to be used. This usually means that inherited
         * value (from parent visibility settings) is to be used.
         */
        DEFAULT;

        public boolean isVisible(Member m) {
            switch (this) {
            case ANY:
                return true;
            case NONE:
                return false;
            case NON_PRIVATE:
                return !Modifier.isPrivate(m.getModifiers());
            case PROTECTED_AND_PUBLIC:
                if (Modifier.isProtected(m.getModifiers())) {
                    return true;
                }
                // fall through to public case:
            case PUBLIC_ONLY:
                return Modifier.isPublic(m.getModifiers());
            default:
                return false;
            }
        }
    }
    
    /**
     * Minimum visibility required for auto-detecting regular getter methods.
     */
    Visibility getterVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting is-getter methods.
     */
    Visibility isGetterVisibility() default Visibility.DEFAULT;
    
    /**
     * Minimum visibility required for auto-detecting setter methods.
     */    
    Visibility setterVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting Creator methods,
     * except for no-argument constructors (which are always detected
     * no matter what).
     */
    Visibility creatorVisibility() default Visibility.DEFAULT;

    /**
     * Minimum visibility required for auto-detecting member fields.
     */ 
    Visibility fieldVisibility() default Visibility.DEFAULT;
}
