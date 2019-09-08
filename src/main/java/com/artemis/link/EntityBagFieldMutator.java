package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.Aspect.*;

class EntityBagFieldMutator
  implements MultiFieldMutator<Bag<Entity>, Component>
{
  private final Bag<Entity> empty = new Bag<>();
  private EntitySubscription all;

  @Override
  public void validate( final int sourceId, @Nonnull final Bag<Entity> entities, @Nullable final LinkListener listener )
  {
    for ( int i = 0; entities.size() > i; i++ )
    {
      final Entity e = entities.get( i );
      if ( !all.getActiveEntityIds().unsafeGet( e.getId() ) )
      {
        entities.remove( i-- );
				if ( listener != null )
				{
					listener.onTargetDead( sourceId, e.getId() );
				}
      }
    }
  }

  @Nonnull
  @Override
  public Bag<Entity> read( final Component c, @Nonnull final Field f )
  {
    try
    {
      final Bag<Entity> e = (Bag<Entity>) f.get( c );
      return ( e != null ) ? e : empty;
    }
    catch ( final ReflectionException exc )
    {
      throw new RuntimeException( exc );
    }
  }

  @Override
  public void setWorld( @Nonnull final World world )
  {
    all = world.getAspectSubscriptionManager().get( all() );
  }
}
