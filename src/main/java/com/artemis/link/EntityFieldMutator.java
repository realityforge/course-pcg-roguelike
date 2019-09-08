package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;

class EntityFieldMutator
  implements UniFieldMutator
{
  private World world;

  @Override
  public int read( final Component c, @Nonnull final Field f )
  {
    try
    {
      final Entity e = (Entity) f.get( c );
      return ( e != null ) ? e.getId() : -1;
    }
    catch ( final ReflectionException exc )
    {
      throw new RuntimeException( exc );
    }
  }

  @Override
  public void write( final int value, final Component c, @Nonnull final Field f )
  {
    try
    {
      final Entity e = ( value != -1 ) ? world.getEntity( value ) : null;
      f.set( c, e );
    }
    catch ( final ReflectionException exc )
    {
      throw new RuntimeException( exc );
    }
  }

  @Override
  public void setWorld( final World world )
  {
    this.world = world;
  }
}
