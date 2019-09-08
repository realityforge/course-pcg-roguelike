package com.artemis;

import com.artemis.utils.reflect.ClassReflection;
import javax.annotation.Nonnull;

@SuppressWarnings( "serial" )
public class InvalidComponentException
  extends RuntimeException
{
  private final Class<?> componentClass;

  public InvalidComponentException( @Nonnull final Class<?> componentClass, final String string )
  {
    super( message( componentClass, string ) );
    this.componentClass = componentClass;
  }

  public InvalidComponentException( @Nonnull final Class<?> componentClass, final String string, final Exception e )
  {
    super( message( componentClass, string ), e );
    this.componentClass = componentClass;
  }

  @Nonnull
  private static String message( @Nonnull final Class<?> componentClass, final String string )
  {
    return ClassReflection.getSimpleName( componentClass ) + ": " + string;
  }

  public Class<?> getComponentClass()
  {
    return componentClass;
  }
}
