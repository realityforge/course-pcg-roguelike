package com.artemis.backends.gwt.emu.java.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daan van Yperen
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.CONSTRUCTOR, ElementType.METHOD } )
public @interface SafeVarargs
{
}
