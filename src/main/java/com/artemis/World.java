package com.artemis;

import com.artemis.injection.CachedInjector;
import com.artemis.injection.Injector;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import static com.artemis.WorldConfiguration.*;

/**
 * The primary instance for the framework.
 * <p>
 * It contains all the systems. You must use this to create, delete and
 * retrieve entities. It is also important to set the delta each game loop
 * iteration, and initialize before game loop.
 * </p>
 *
 * @author Arni Arent
 * @author junkdog
 */
public class World
{
  /**
   * Manages all entities for the world.
   */
  @Nonnull
  private final EntityManager em;
  /**
   * Manages all component-entity associations for the world.
   */
  @Nonnull
  private final ComponentManager cm;
  /**
   * Pool of entity edits.
   */
  @Nonnull
  final BatchChangeProcessor batchProcessor;
  /**
   * Contains all systems unordered.
   */
  final Bag<BaseSystem> systemsBag;
  /**
   * Manages all aspect based entity subscriptions for the world.
   */
  @Nonnull
  final AspectSubscriptionManager asm;
  /**
   * Contains strategy for invoking systems upon process.
   */
  SystemInvocationStrategy invocationStrategy;
  @Nonnull
  final WorldSegment partition;
  /**
   * The time passed since the last update.
   */
  public float delta;
  final boolean alwaysDelayComponentRemoval;

  /**
   * Creates a world without custom systems.
   * <p>
   * {@link EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
   * available by default.
   * </p>
   * Why are you using this? Use {@link #World(WorldConfiguration)} to create a world with your own systems.
   */
  public World()
  {
    this( new WorldConfiguration() );
  }

  /**
   * Creates a new world.
   * <p>
   * {@link EntityManager}, {@link ComponentManager} and {@link AspectSubscriptionManager} are
   * available by default, on top of your own systems.
   * </p>
   *
   * @see WorldConfigurationBuilder
   * @see WorldConfiguration
   */
  public World( @Nonnull final WorldConfiguration configuration )
  {
    partition = new WorldSegment( configuration );
    systemsBag = configuration.systems;

    final ComponentManager lcm =
      (ComponentManager) systemsBag.get( COMPONENT_MANAGER_IDX );
    final EntityManager lem =
      (EntityManager) systemsBag.get( ENTITY_MANAGER_IDX );
    final AspectSubscriptionManager lasm =
      (AspectSubscriptionManager) systemsBag.get( ASPECT_SUBSCRIPTION_MANAGER_IDX );

    cm = lcm == null ? new ComponentManager( configuration.expectedEntityCount() ) : lcm;
    em = lem == null ? new EntityManager( configuration.expectedEntityCount() ) : lem;
    asm = lasm == null ? new AspectSubscriptionManager() : lasm;
    batchProcessor = new BatchChangeProcessor( this );
    alwaysDelayComponentRemoval = configuration.isAlwaysDelayComponentRemoval();

    configuration.initialize( this, partition.injector, asm );
  }

  /**
   * Inject dependencies on object.
   * <p/>
   * Immediately perform dependency injection on the target, even if the target isn't of an Artemis class.
   * <p/>
   * If you want to specify nonstandard dependencies to inject, use
   * {@link WorldConfiguration#register(String, Object)} instead, or
   * configure an {@link Injector}
   * <p/>
   * If you want a non-throwing alternative, use {@link #inject(Object, boolean)}
   *
   * @param target Object to inject into.
   *               throws {@link MundaneWireException} if {@code target} is annotated with {@link com.artemis.annotations.SkipWire}
   * @see com.artemis.annotations.Wire for more details about dependency injection.
   * @see #inject(Object, boolean)
   */
  public void inject( @Nonnull final Object target )
  {
    inject( target, true );
  }

  /**
   * Inject dependencies on object.
   * <p/>
   * Will not if it is annotated with {@link com.artemis.annotations.Wire}.
   * <p/>
   * If you want to specify nonstandard dependencies to inject, use
   * {@link WorldConfiguration#register(String, Object)} instead, or
   * configure an {@link Injector}.
   *
   * @param target              Object to inject into.
   * @param failIfNotInjectable if true, this method will
   *                            throws {@link MundaneWireException} if {@code target} is annotated with
   *                            {@link com.artemis.annotations.SkipWire} and {@code failIfNotInjectable} is true
   * @see com.artemis.annotations.Wire for more details about dependency injection.
   * @see #inject(Object)
   */
  public void inject( @Nonnull final Object target, final boolean failIfNotInjectable )
  {
    final boolean injectable = partition.injector.isInjectable( target );
    if ( !injectable && failIfNotInjectable )
    {
      throw new MundaneWireException( "Attempted injection on " + target.getClass()
        .getName() + ", which is annotated with @SkipWire" );
    }

    if ( injectable )
    {
      partition.injector.inject( target );
    }
  }

  public <T> T getRegistered( final String name )
  {
    return partition.injector.getRegistered( name );
  }

  public <T> T getRegistered( final Class<T> type )
  {
    return partition.injector.getRegistered( type );
  }

  /**
   * Disposes all systems. Only necessary if either need to free
   * managed resources upon bringing the world to an end.
   *
   * @throws ArtemisMultiException if any system throws an exception.
   */
  public void dispose()
  {
    final List<Throwable> exceptions = new ArrayList<>();

    for ( final BaseSystem system : systemsBag )
    {
      try
      {
        system.dispose();
      }
      catch ( final Exception e )
      {
        exceptions.add( e );
      }
    }

    if ( exceptions.size() > 0 )
    {
      throw new ArtemisMultiException( exceptions );
    }
  }

  /**
   * Get entity editor for entity.
   *
   * @param entityId entity to fetch editor for.
   * @return a fast albeit verbose editor to perform batch changes to entities.
   */
  @Nonnull
  public EntityEdit edit( final int entityId )
  {
    if ( !em.isActive( entityId ) )
    {
      throw new RuntimeException( "Issued edit on deleted " + entityId );
    }

    return batchProcessor.obtainEditor( entityId );
  }

  /**
   * Gets the <code>composition id</code> uniquely identifying the
   * component composition of an entity. Each composition identity maps
   * to one unique <code>BitVector</code>.
   *
   * @param entityId Entity for which to get the composition id
   * @return composition identity of entity
   */
  public int compositionId( final int entityId )
  {
    return cm.getIdentity( entityId );
  }

  /**
   * Returns a manager that takes care of all the entities in the world.
   *
   * @return entity manager
   */
  @Nonnull
  public EntityManager getEntityManager()
  {
    return em;
  }

  /**
   * Returns a manager that takes care of all the components in the world.
   *
   * @return component manager
   */
  @Nonnull
  public ComponentManager getComponentManager()
  {
    return cm;
  }

  /**
   * Returns the manager responsible for creating and maintaining
   * {@link EntitySubscription subscriptions} in the world.
   *
   * @return aspect subscription manager
   */
  @Nonnull
  public AspectSubscriptionManager getAspectSubscriptionManager()
  {
    return asm;
  }

  /**
   * Time since last game loop.
   *
   * @return delta time since last game loop
   */
  public float getDelta()
  {
    return delta;
  }

  /**
   * You must specify the delta for the game here.
   *
   * @param delta time since last game loop
   */
  public void setDelta( final float delta )
  {
    this.delta = delta;
  }

  /**
   * Delete the entity from the world.
   *
   * @param e the entity to delete
   * @see #delete(int) recommended alternative.
   */
  public void deleteEntity( @Nonnull final Entity e )
  {
    delete( e.id );
  }

  /**
   * Delete the entity from the world.
   *
   * The entity is considered to be in a final state once invoked;
   * adding or removing components from an entity scheduled for
   * deletion will likely throw exceptions.
   *
   * @param entityId the entity to delete
   */
  public void delete( final int entityId )
  {
    batchProcessor.delete( entityId );
  }

  /**
   * Create and return a new or reused entity instance. Entity is
   * automatically added to the world.
   *
   * @return entity
   * @see #create() recommended alternative.
   */
  public Entity createEntity()
  {
    final Entity e = em.createEntityInstance();
    batchProcessor.changed.unsafeSet( e.getId() );
    return e;
  }

  /**
   * Create and return a new or reused entity id. Entity is
   * automatically added to the world.
   *
   * @return assigned entity id, where id >= 0.
   */
  public int create()
  {
    final int entityId = em.create();
    batchProcessor.changed.unsafeSet( entityId );
    return entityId;
  }

  /**
   * Create and return an {@link Entity} wrapping a new or reused entity instance.
   * Entity is automatically added to the world.
   *
   * Use {@link Entity#edit()} to set up your newly created entity.
   *
   * You can also create entities using:
   * <ul>
   *   <li>{@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.</li>
   *   <li>{@link Archetype} Fastest, low level, no parameterized components.</li>
   *   <li><a href="https://github.com/junkdog/artemis-odb/wiki/Serialization">Serialization</a>,
   *        with a simple prefab-like class to parameterize the entities.</li>
   * </ul>
   *
   * @return entity
   * @see #create() recommended alternative.
   */
  public Entity createEntity( @Nonnull final Archetype archetype )
  {
    final Entity e = em.createEntityInstance();

    final int id = e.getId();
    archetype.transmuter.perform( id );
    cm.setIdentity( e.id, archetype.compositionId );

    batchProcessor.changed.unsafeSet( id );

    return e;
  }

  /**
   * Create and return an {@link Entity} wrapping a new or reused entity instance.
   * Entity is automatically added to the world.
   *
   * Use {@link Entity#edit()} to set up your newly created entity.
   *
   * You can also create entities using:
   * - {@link com.artemis.utils.EntityBuilder} Convenient entity creation. Not useful when pooling.
   * - {@link Archetype} Fastest, low level, no parameterized components.
   *
   * @return assigned entity id
   */
  public int create( @Nonnull final Archetype archetype )
  {
    final int entityId = em.create();

    archetype.transmuter.perform( entityId );
    cm.setIdentity( entityId, archetype.compositionId );

    batchProcessor.changed.unsafeSet( entityId );

    return entityId;
  }

  /**
   * Get entity with the specified id.
   *
   * Resolves entity id to the unique entity instance. <em>This method may
   * return an entity even if it isn't active in the world.</em> Make sure to
   * not retain id's of deleted entities.
   *
   * @param entityId the entities id
   * @return the specific entity
   */
  public Entity getEntity( final int entityId )
  {
    return em.getEntity( entityId );
  }

  /**
   * Gives you all the systems in this world for possible iteration.
   *
   * @return all entity systems in world
   */
  public ImmutableBag<BaseSystem> getSystems()
  {
    return systemsBag;
  }

  /**
   * Retrieve a system for specified system type.
   *
   * @param <T>  the class type of system
   * @param type type of system
   * @return instance of the system in this world
   */
  @Nonnull
  @SuppressWarnings( "unchecked" )
  public <T extends BaseSystem> T getSystem( final Class<T> type )
  {
    return (T) partition.systems.get( type );
  }

  /**
   * Set strategy for invoking systems on {@link #process()}.
   */
  protected void setInvocationStrategy( @Nonnull final SystemInvocationStrategy invocationStrategy )
  {
    this.invocationStrategy = invocationStrategy;
    invocationStrategy.setWorld( this );
    invocationStrategy.setSystems( systemsBag );
    invocationStrategy.initialize();
  }

  /**
   * Process all non-passive systems.
   *
   * @see InvocationStrategy to control and extend how systems are invoked.
   */
  public void process()
  {
    invocationStrategy.process();

    final IntBag pendingPurge = batchProcessor.getPendingPurge();
    if ( !pendingPurge.isEmpty() )
    {
      cm.clean( pendingPurge );
      em.clean( pendingPurge );

      batchProcessor.purgeComponents();
    }
  }

  /**
   * Retrieves a ComponentMapper instance for fast retrieval of components
   * from entities.
   *
   * Odb automatically injects component mappers into systems, calling this
   * method is usually not required.,
   *
   * @param <T>  class type of the component
   * @param type type of component to get mapper for
   * @return mapper for specified component type
   */
  public <T extends Component> ComponentMapper<T> getMapper( final Class<T> type )
  {
    return cm.getMapper( type );
  }

  /**
   * @return Injector responsible for dependency injection.
   */
  @Nonnull
  public Injector getInjector()
  {
    return partition.injector;
  }

  /**
   * @return Strategy used for invoking systems during {@link World#process()}.
   */
  @Nonnull
  public <T extends SystemInvocationStrategy> T getInvocationStrategy()
  {
    return (T) invocationStrategy;
  }

  static class WorldSegment
  {
    /**
     * Contains all systems and systems classes mapped.
     */
    @Nonnull
    final Map<Class<?>, BaseSystem> systems;
    /**
     * Responsible for dependency injection.
     */
    @Nonnull
    final Injector injector;

    WorldSegment( @Nonnull final WorldConfiguration configuration )
    {
      systems = new IdentityHashMap<>();
      injector = ( configuration.injector != null )
                 ? configuration.injector
                 : new CachedInjector();
    }
  }

  /**
   * When true, component removal is delayed for all components until all subscriptions have been notified.
   *
   * @see WorldConfiguration#setAlwaysDelayComponentRemoval(boolean)
   * @see WorldConfigurationBuilder#alwaysDelayComponentRemoval(boolean)
   */
  public boolean isAlwaysDelayComponentRemoval()
  {
    return alwaysDelayComponentRemoval;
  }
}
