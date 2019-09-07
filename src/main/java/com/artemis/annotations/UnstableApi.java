package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes marked with this annotation may undergo extensive refactoring between
 * releases.
 */
@Retention( RetentionPolicy.SOURCE )
@Target( ElementType.TYPE )
@Documented
public @interface UnstableApi
{
}
