package com.artemis;

/**
 * Injection failed.
 *
 * @author Daan van Yperen
 */
public class InjectionException
  extends RuntimeException
{
  public InjectionException( final String msg )
  {
    super( msg );
  }

  public InjectionException( final String msg, final Throwable e )
  {
    super( msg, e );
  }
}
