package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Customize how the fluid generator processes your class.
 *
 * Is ignored when your fluid generator is not used in your project.
 *
 * @author Daan van Yperen
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@Documented
public @interface Fluid
{
  /**
   * Name to use as a prefix for all methods relating to this
   * component.
   *
   * If left empty artemis will use the default: classname starting
   * with lowercase character. MyPos.class: "myPos"
   */
  @Nonnull String name() default "";

  /**
   * If you are using getters with parameters, but you prefer the fluid
   * interface to just swallow the return value and return {@code E} instead,
   * set this to true.
   */
  boolean swallowGettersWithParameters() default false;

  /**
   * Exclude class from fluid interface.
   */
  boolean exclude() default false;
}