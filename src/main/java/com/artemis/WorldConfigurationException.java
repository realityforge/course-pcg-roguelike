package com.artemis;

/**
 * World configuration failed.
 *
 * @author Daan van Yperen
 */
public class WorldConfigurationException
  extends RuntimeException
{
  public WorldConfigurationException( final String msg )
  {
    super( msg );
  }

  public WorldConfigurationException( final String msg, final Throwable e )
  {
    super( msg, e );
  }
}
