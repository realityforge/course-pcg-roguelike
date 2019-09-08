package com.artemis.link;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;

class IntFieldMutator
  implements UniFieldMutator
{
  @Override
  public int read( final Component c, @Nonnull final Field f )
  {
    try
    {
      return (Integer) f.get( c );
    }
    catch ( final ReflectionException e )
    {
      throw new RuntimeException( e );
    }
  }

  @Override
  public void write( final int value, final Component c, @Nonnull final Field f )
  {
    try
    {
      f.set( c, value );
    }
    catch ( final ReflectionException e )
    {
      throw new RuntimeException( e );
    }
  }

  @Override
  public void setWorld( final World world )
  {
  }
}
