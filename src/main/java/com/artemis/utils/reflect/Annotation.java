package com.artemis.utils.reflect;

import javax.annotation.Nullable;

/**
 * Provides information about, and access to, an annotation of a field, class or interface.
 *
 * @author dludwig
 */
public final class Annotation
{
  private final java.lang.annotation.Annotation annotation;

  Annotation( final java.lang.annotation.Annotation annotation )
  {
    this.annotation = annotation;
  }

  @Nullable
  @SuppressWarnings( "unchecked" )
  public <T extends java.lang.annotation.Annotation> T getAnnotation( final Class<T> annotationType )
  {
    if ( annotation.annotationType().equals( annotationType ) )
    {
      return (T) annotation;
    }
    return null;
  }

  public Class<? extends java.lang.annotation.Annotation> getAnnotationType()
  {
    return annotation.annotationType();
  }
}
