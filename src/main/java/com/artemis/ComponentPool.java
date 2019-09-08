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
  private Class<T> type;

  ComponentPool( Class<T> type )
  {
    this.type = type;
    cache = new Bag<>( type );
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
    catch ( ReflectionException e )
    {
      throw new InvalidComponentException( type, e.getMessage(), e );
    }
  }

  void free( @Nonnull T component )
  {
    component.reset();
    cache.add( component );
  }
}
