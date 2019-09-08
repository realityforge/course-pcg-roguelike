package com.artemis;

import com.artemis.utils.reflect.ClassReflection;
import javax.annotation.Nonnull;

@SuppressWarnings( "serial" )
public class MundaneWireException
  extends RuntimeException
{
  public MundaneWireException( @Nonnull Class<? extends BaseSystem> klazz )
  {
    super( "Not added to world: " + ClassReflection.getSimpleName( klazz ) );
  }

  public MundaneWireException( String message, Throwable cause )
  {
    super( message, cause );
  }

  public MundaneWireException( String message )
  {
    super( message );
  }
}
