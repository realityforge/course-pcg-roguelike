package com.artemis;

import com.artemis.utils.Bag;

public class ImmediateComponentRemover<A extends Component>
  extends ComponentRemover<A>
{
  public ImmediateComponentRemover( final Bag<A> components, final ComponentPool pool )
  {
    super( components, pool );
  }

  @Override
  void mark( final int entityId )
  {
    if ( pool != null )
    {
      final PooledComponent c = (PooledComponent) components.get( entityId );
			if ( c != null )
			{
				pool.free( c );
			}
    }
    components.unsafeSet( entityId, null );
  }

  @Override
  boolean unmark( final int entityId )
  {
    return false;
  }

  @Override
  void purge()
  {
  }

  @Override
  boolean has( final int entityId )
  {
    return false;
  }
}
