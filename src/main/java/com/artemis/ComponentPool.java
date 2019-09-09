package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentPool<T extends PooledComponent>
{
  @Nonnull
  private final Bag<T> cache;
  private final Class<T> type;

  ComponentPool( final Class<T> type )
  {
    this.type = type;
    cache = new Bag<>();
  }

  @Nullable
  @SuppressWarnings( "unchecked" )
  <T extends PooledComponent> T obtain()
  {
    try
    {
      return (T) ( ( cache.size() > 0 )
                   ? cache.removeLast()
                   : ClassReflection.newInstance( type ) );
    }
    catch ( final ReflectionException e )
    {
      throw new InvalidComponentException( type, e.getMessage(), e );
    }
  }

  void free( @Nonnull final T component )
  {
    component.reset();
    cache.add( component );
  }
}
