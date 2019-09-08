package com.artemis;

import com.artemis.utils.reflect.ClassReflection;
import javax.annotation.Nonnull;

@SuppressWarnings( "serial" )
public class MundaneWireException
  extends RuntimeException
{
  public MundaneWireException( @Nonnull final Class<? extends BaseSystem> klazz )
  {
    super( "Not added to world: " + ClassReflection.getSimpleName( klazz ) );
  }

  public MundaneWireException( final String message, final Throwable cause )
  {
    super( message, cause );
  }

  public MundaneWireException( final String message )
  {
    super( message );
  }
}
