package com.artemis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Never persisted when serializing.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@Inherited
public @interface Transient
{
}
