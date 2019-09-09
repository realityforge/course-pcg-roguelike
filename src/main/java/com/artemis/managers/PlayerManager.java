package com.artemis.managers;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import static com.artemis.Aspect.*;

/**
 * You may sometimes want to specify to which player an entity belongs to.
 * <p>
 * An entity can only belong to a single player at a time.
 * </p>
 *
 * @author Arni Arent
 */
public class PlayerManager
  extends BaseSystem
{
  /**
   * All players mapped to entities as key.
   */
  @Nonnull
  private final Map<Entity, String> playerByEntity;
  /**
   * All entities that are mapped to a player, with the player as key.
   */
  @Nonnull
  private final Map<String, Bag<Entity>> entitiesByPlayer;

  /**
   * Creates a new PlayerManager instance.
   */
  public PlayerManager()
  {
    playerByEntity = new HashMap<>();
    entitiesByPlayer = new HashMap<>();
  }

  /**
   * Associate the entity with the specified player.
   * <p>
   * Each entity may only be assoctiated with one player at a time.
   * </p>
   *
   * @param e      the entity to associate
   * @param player the player to associtate to the entity with
   */
  public void setPlayer( final Entity e, final String player )
  {
    playerByEntity.put( e, player );
    Bag<Entity> entities = entitiesByPlayer.get( player );
    if ( entities == null )
    {
      entities = new Bag<>();
      entitiesByPlayer.put( player, entities );
    }
    entities.add( e );
  }

  /**
   * Get all entities belonging to a player.
   *
   * @param player the player
   * @return a bag containing all entities belonging to the player
   */
  public ImmutableBag<Entity> getEntitiesOfPlayer( final String player )
  {
    Bag<Entity> entities = entitiesByPlayer.get( player );
    if ( entities == null )
    {
      entities = new Bag<>();
    }
    return entities;
  }

  /**
   * Remove the association of an entity with a player.
   *
   * @param e the entity to remove
   */
  public void removeFromPlayer( @Nonnull final Entity e )
  {
    final String player = playerByEntity.get( e );
    if ( player != null )
    {
      final Bag<Entity> entities = entitiesByPlayer.get( player );
      if ( entities != null )
      {
        entities.remove( e );
      }
    }
  }

  /**
   * Get the player an entity is associated with.
   *
   * @param e the entity to get the player for
   * @return the player
   */
  public String getPlayer( final Entity e )
  {
    return playerByEntity.get( e );
  }

  /**
   * Deleted entities are removed from their player.
   *
   * @param e the deleted entity
   */
  public void deleted( @Nonnull final Entity e )
  {
    removeFromPlayer( e );
  }

  @Override
  protected void setWorld( final World world )
  {
    super.setWorld( world );
    registerManager();
  }

  /**
   * Hack to register manager to right subscription
   */
  private void registerManager()
  {
    world.getAspectSubscriptionManager()
      .get( all() )
      .addSubscriptionListener( new EntitySubscription.SubscriptionListener()
      {
        @Override
        public void inserted( @Nonnull final IntBag entities )
        {
        }

        @Override
        public void removed( @Nonnull final IntBag entities )
        {
          deleted( entities );
        }
      } );
  }

  private void deleted( @Nonnull final IntBag entities )
  {
    final int[] ids = entities.getData();
    for ( int i = 0, s = entities.size(); s > i; i++ )
    {
      deleted( world.getEntity( ids[ i ] ) );
    }
  }

  /**
   * Managers are not interested in processing.
   */
  @Override
  protected final void processSystem()
  {
  }
}
