package com.artemis.link;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.Aspect.*;

class IntBagFieldMutator
  implements MultiFieldMutator<IntBag, Component>
{
  private final IntBag empty = new IntBag();
  private EntitySubscription all;

  @Override
  public void validate( int sourceId, @Nonnull IntBag ids, @Nullable LinkListener listener )
  {
    for ( int i = 0; ids.size() > i; i++ )
    {
      int id = ids.get( i );
      if ( !all.getActiveEntityIds().unsafeGet( id ) )
      {
        ids.remove( i-- );
				if ( listener != null )
				{
					listener.onTargetDead( sourceId, id );
				}
      }
    }
  }

  @Nonnull
  @Override
  public IntBag read( Component c, @Nonnull Field f )
  {
    try
    {
      final boolean isNotAccessible = !f.isAccessible();
      if ( isNotAccessible )
      {
        f.setAccessible( true );
      }
      IntBag e = (IntBag) f.get( c );
      if ( isNotAccessible )
      {
        f.setAccessible( false );
      }
      return ( e != null ) ? e : empty;
    }
    catch ( ReflectionException exc )
    {
      throw new RuntimeException( exc );
    }
  }

  @Override
  public void setWorld( @Nonnull World world )
  {
    all = world.getAspectSubscriptionManager().get( all() );
  }
}
