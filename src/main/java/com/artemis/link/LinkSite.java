package com.artemis.link;

import com.artemis.AspectSubscriptionManager;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.ComponentType;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.BitVector;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.Aspect.*;
import static com.artemis.annotations.LinkPolicy.Policy.*;

abstract class LinkSite
  implements EntitySubscription.SubscriptionListener
{
  @Nonnull
  protected final ComponentType type;
  @Nonnull
  protected final Field field;
  protected final ComponentMapper<? extends Component> mapper;
  protected final EntitySubscription subscription;
  @Nullable
  protected final LinkPolicy.Policy policy;
  protected final BitVector activeEntityIds;
  protected LinkListener listener;

  protected LinkSite( @Nonnull final World world,
                      @Nonnull final ComponentType type,
                      @Nonnull final Field field,
                      final LinkPolicy.Policy defaultPolicy )
  {

    this.type = type;
    this.field = field;
    final LinkPolicy.Policy policyOverride = LinkFactory.getPolicy( field );
    this.policy = ( policyOverride != null ) ? policyOverride : defaultPolicy;

    mapper = world.getMapper( type.getType() );

    activeEntityIds = world.getAspectSubscriptionManager().get( all() ).getActiveEntityIds();

    final AspectSubscriptionManager subscriptions = world.getAspectSubscriptionManager();
    subscription = subscriptions.get( all( type.getType() ) );
    subscription.addSubscriptionListener( this );
  }

  @Override
  public boolean equals( @Nullable final Object o )
  {
		if ( this == o )
		{
			return true;
		}
		if ( o == null || getClass() != o.getClass() )
		{
			return false;
		}

    final LinkSite that = (LinkSite) o;

    return type.equals( that.type ) && field.equals( that.field );
  }

  @Override
  public int hashCode()
  {
    return type.hashCode() ^ field.hashCode();
  }

  @Override
  public void inserted( @Nonnull final IntBag entities )
  {
    final int[] ids = entities.getData();
    for ( int i = 0, s = entities.size(); s > i; i++ )
    {
      insert( ids[ i ] );
    }
  }

  protected abstract void insert( int id );

  @Override
  public void removed( @Nonnull final IntBag entities )
  {
    final int[] ids = entities.getData();
    for ( int i = 0, s = entities.size(); s > i; i++ )
    {
      removed( ids[ i ] );
    }
  }

  protected abstract void removed( int id );

  protected abstract void check( int id );

  protected void process()
  {
		if ( CHECK_SOURCE_AND_TARGETS != policy )
		{
			return;
		}

    final IntBag entities = subscription.getEntities();
    final int[] ids = entities.getData();
    for ( int i = 0, s = entities.size(); s > i; i++ )
    {
      check( ids[ i ] );
    }
  }
}
