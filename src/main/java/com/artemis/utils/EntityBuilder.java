package com.artemis.utils;

import com.artemis.Archetype;
import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.reflect.ClassReflection;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Non-reusable entity creation helper for rapid prototyping.
 *
 * Discouraged for use other than rapid prototyping and simple games.
 * Use {@link ComponentMapper} instead, or check out the Fluid Entity
 * interface.
 *
 * Example: new Builder(world)
 * .with(Pos.class, Anim.class)
 * .tag("boss")
 * .player("player1")
 * .group("enemies")
 * .build();
 *
 * @author Daan van Yperen
 * @author Junkdog
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public class EntityBuilder
{
  @Nonnull
  protected final World world;
  protected final EntityEdit edit;

  /**
   * Begin building new entity.
   */
  public EntityBuilder( @Nonnull final World world )
  {
    this.world = world;
    edit = world.createEntity().edit();
  }

  /**
   * Begin building new entity based on archetype.
   */
  public EntityBuilder( @Nonnull final World world, @Nonnull final Archetype archetype )
  {
    this.world = world;
    edit = world.createEntity( archetype ).edit();
  }

  /**
   * Add component to entity.
   */
  @Nonnull
  public EntityBuilder with( final Component component )
  {
    edit.add( component );
    return this;
  }

  /**
   * Add components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Component component1, final Component component2 )
  {
    edit.add( component1 );
    edit.add( component2 );
    return this;
  }

  /**
   * Add components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Component component1, final Component component2, final Component component3 )
  {
    edit.add( component1 );
    edit.add( component2 );
    edit.add( component3 );
    return this;
  }

  /**
   * Add components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Component component1, final Component component2, final Component component3, final Component component4 )
  {
    edit.add( component1 );
    edit.add( component2 );
    edit.add( component3 );
    edit.add( component4 );
    return this;
  }

  /**
   * Add components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Component component1,
                             final Component component2,
                             final Component component3,
                             final Component component4,
                             final Component component5 )
  {
    edit.add( component1 );
    edit.add( component2 );
    edit.add( component3 );
    edit.add( component4 );
    edit.add( component5 );
    return this;
  }

  /**
   * Add components to entity.
   */
  @Nonnull
  public EntityBuilder with( @Nonnull final Component... components )
  {
    for ( final Component component : components )
    {
      edit.add( component );
    }
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Class<? extends Component> component )
  {
    edit.create( component );
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Class<? extends Component> component1, final Class<? extends Component> component2 )
  {
    edit.create( component1 );
    edit.create( component2 );
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Class<? extends Component> component1,
                             final Class<? extends Component> component2,
                             final Class<? extends Component> component3 )
  {
    edit.create( component1 );
    edit.create( component2 );
    edit.create( component3 );
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Class<? extends Component> component1,
                             final Class<? extends Component> component2,
                             final Class<? extends Component> component3,
                             final Class<? extends Component> component4 )
  {
    edit.create( component1 );
    edit.create( component2 );
    edit.create( component3 );
    edit.create( component4 );
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( final Class<? extends Component> component1,
                             final Class<? extends Component> component2,
                             final Class<? extends Component> component3,
                             final Class<? extends Component> component4,
                             final Class<? extends Component> component5 )
  {
    edit.create( component1 );
    edit.create( component2 );
    edit.create( component3 );
    edit.create( component4 );
    edit.create( component5 );
    return this;
  }

  /**
   * Add artemis managed components to entity.
   */
  @Nonnull
  public EntityBuilder with( @Nonnull final Class<? extends Component>... components )
  {
    for ( final Class<? extends Component> component : components )
    {
      edit.create( component );
    }
    return this;
  }

  /**
   * Set UUID of entity
   */
  @Nonnull
  public EntityBuilder UUID( final UUID uuid )
  {
    resolveManager( UuidEntityManager.class ).setUuid( edit.getEntity(), uuid );
    return this;
  }

  /**
   * Register entity with owning player.
   * An entity can only belong to a single player at a time.
   * Requires registered PlayerManager.
   */
  @Nonnull
  public EntityBuilder player( final String player )
  {
    resolveManager( PlayerManager.class ).setPlayer( edit.getEntity(), player );
    return this;
  }

  /**
   * Register entity with tag. Requires registered TagManager
   */
  @Nonnull
  public EntityBuilder tag( final String tag )
  {
    resolveManager( TagManager.class ).register( tag, edit.getEntity() );
    return this;
  }

  /**
   * Register entity with group. Requires registered TagManager
   */
  @Nonnull
  public EntityBuilder group( final String group )
  {
    resolveManager( GroupManager.class ).add( edit.getEntity(), group );
    return this;
  }

  /**
   * Register entity with multiple groups. Requires registered TagManager
   */
  @Nonnull
  public EntityBuilder groups( @Nonnull final String... groups )
  {
    for ( final String group : groups )
    {
      group( group );
    }
    return this;
  }

  /**
   * Assemble, add to world
   */
  public Entity build()
  {
    return edit.getEntity();
  }

  /**
   * Fetch manager or throw RuntimeException if not registered.
   */
  protected <T extends BaseSystem> T resolveManager( @Nonnull final Class<T> type )
  {
    final T teamManager = world.getSystem( type );
    if ( teamManager == null )
    {
      throw new RuntimeException( "Register " + ClassReflection.getSimpleName( type ) + " with your artemis world." );
    }
    return teamManager;
  }
}
