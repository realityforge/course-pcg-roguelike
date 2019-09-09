package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import javax.annotation.Nonnull;
import static com.artemis.utils.reflect.ReflectionUtil.*;

/**
 * Tracks a subset of entities, but does not implement any sorting or iteration.
 *
 * Like {@link BaseEntitySystem}, but uses Entity references instead of int.
 *
 * This system exists as a convenience for users migrating from other Artemis
 * clones or older versions of odb. We recommend using the int systems over
 * the Entity variants.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class EntitySystem
  extends BaseEntitySystem
  implements EntitySubscription.SubscriptionListener
{
  static final int FLAG_INSERTED = 1;
  static final int FLAG_REMOVED = 1 << 1;
  private boolean shouldSyncEntities;
  @Nonnull
  private final WildBag<Entity> entities = new WildBag<>();
  private int methodFlags;

  /**
   * Creates an entity system that uses the specified aspect as a matcher
   * against entities.
   *
   * @param aspect to match against entities
   */
  public EntitySystem( final Aspect.Builder aspect )
  {
    super( aspect );
  }

  public EntitySystem()
  {
  }

  /**
   * Set the world this system works on.
   *
   * @param world the world to set
   */
  @Override
  protected void setWorld( final World world )
  {
    super.setWorld( world );
    if ( implementsObserver( this, "inserted" ) )
    {
      methodFlags |= FLAG_INSERTED;
    }
    if ( implementsObserver( this, "removed" ) )
    {
      methodFlags |= FLAG_REMOVED;
    }
  }

  @Override
  public final void inserted( @Nonnull final IntBag entities )
  {
    shouldSyncEntities = true;
    // performance hack, skip calls to entities if system lacks implementation of added.
    if ( ( methodFlags & FLAG_INSERTED ) > 0 )
    {
      super.inserted( entities );
    }
  }

  @Override
  protected final void inserted( final int entityId )
  {
    inserted( world.getEntity( entityId ) );
  }

  @Override
  public final void removed( @Nonnull final IntBag entities )
  {
    shouldSyncEntities = true;
    // performance hack, skip calls to entities if system lacks implementation of deleted.
    if ( ( methodFlags & FLAG_REMOVED ) > 0 )
    {
      super.removed( entities );
    }
  }

  @Override
  protected final void removed( final int entityId )
  {
    removed( world.getEntity( entityId ) );
  }

  /**
   * Called if entity has come into scope for this system, e.g
   * created or a component was added to it.
   *
   * @param e the entity that was added to this system
   */
  public void inserted( final Entity e )
  {
    throw new RuntimeException( "everything changes" );
  }

  /**
   * <p>Called if entity has gone out of scope of this system, e.g deleted
   * or had one of it's components removed.</p>
   *
   * <p>Explicitly removed components are only retrievable at this point
   * if annotated with {@link DelayedComponentRemoval}.</p>
   *
   * <p>Deleted entities retain all their components - until all listeners
   * have been informed.</p>
   *
   * @param e the entity that was removed from this system
   */
  public void removed( final Entity e )
  {
    throw new RuntimeException( "everything breaks" );
  }

  /**
   * Gets the entities processed by this system. Do not delete entities from
   * this bag - it is the live thing.
   *
   * @return System's entity bag, as matched by aspect.
   */
  @Nonnull
  public Bag<Entity> getEntities()
  {
    if ( shouldSyncEntities )
    {
      final int oldSize = entities.size();
      entities.setSize( 0 );
      final IntBag entityIds = subscription.getEntities();
      final int[] ids = entityIds.getData();
      for ( int i = 0; i < entityIds.size(); i++ )
      {
        entities.add( world.getEntity( ids[ i ] ) );
      }

      if ( oldSize > entities.size() )
      {
        entities.clearTail( oldSize );
      }

      shouldSyncEntities = false;
    }

    return entities;
  }
}
