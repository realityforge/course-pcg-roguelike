package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import com.artemis.utils.IntBag;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class BatchChangeProcessor
{
  @Nonnull
  private final World world;
  @Nonnull
  private final AspectSubscriptionManager asm;
  final BitVector changed = new BitVector();
  final WildBag<ComponentRemover> purgatories = new WildBag<>();
  // marked for deletion, will be removed for entity subscriptions asap
  private final BitVector deleted = new BitVector();
  // collected deleted entities during this {@link World#process()} round;
  // cleaned at end of round.
  private final BitVector pendingPurge = new BitVector();
  private final IntBag toPurge = new IntBag();
  private final Bag<EntityEdit> pool = new Bag<>();
  private final WildBag<EntityEdit> edited = new WildBag<>();

  BatchChangeProcessor( @Nonnull final World world )
  {
    this.world = world;
    asm = world.getAspectSubscriptionManager();

    final EntityManager em = world.getEntityManager();
    em.registerEntityStore( changed );
    em.registerEntityStore( deleted );
    em.registerEntityStore( pendingPurge );
  }

  boolean isDeleted( final int entityId )
  {
    return pendingPurge.unsafeGet( entityId );
  }

  void delete( final int entityId )
  {
    deleted.unsafeSet( entityId );
    pendingPurge.unsafeSet( entityId );

    // guarding against previous transmutations
    changed.unsafeClear( entityId );
  }

  /**
   * Get entity editor.
   *
   * @param entityId entity to fetch editor for.
   * @return a fast albeit verbose editor to perform batch changes to entities.
   */
  @Nonnull
  EntityEdit obtainEditor( final int entityId )
  {
    final int size = edited.size();
    if ( 0 != size )
    {
      final EntityEdit edit = edited.get( size - 1 );
      if ( entityId == edit.getEntityId() )
      {
        return edit;
      }
    }

    final EntityEdit edit = entityEdit();
    assert null != edit;
    edited.add( edit );

    edit.entityId = entityId;

    return edit;
  }

  @Nullable
  private EntityEdit entityEdit()
  {
    if ( pool.isEmpty() )
    {
      return new EntityEdit( world );
    }
    else
    {
      return pool.removeLast();
    }
  }

  void update()
  {
    while ( !changed.isEmpty() || !deleted.isEmpty() )
    {
      asm.process( changed, deleted );
      purgeComponents();
    }

    clean();
  }

  void purgeComponents()
  {
    for ( int i = 0, s = purgatories.size(); s > i; i++ )
    {
      purgatories.get( i ).purge();
    }

    purgatories.setSize( 0 );
  }

  @Nonnull
  IntBag getPendingPurge()
  {
    pendingPurge.toIntBag( toPurge );
    pendingPurge.clear();
    return toPurge;
  }

  private boolean clean()
  {
    if ( edited.isEmpty() )
    {
      return false;
    }

    for ( int i = 0, s = edited.size(); s > i; i++ )
    {
      pool.add( edited.get( i ) );
    }
    edited.setSize( 0 );

    return true;
  }
}
