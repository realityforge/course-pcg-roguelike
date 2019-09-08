package com.artemis;

import com.artemis.injection.Injector;
import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.ComponentManager.*;

/**
 * Contains configuration for your world.
 *
 * Can be used for:
 * - Adding Systems.
 * - Adding Managers.
 * - Registering Pojo to inject.
 * - Registering custom dependency injector.
 *
 * @see WorldConfigurationBuilder allows convenient creation.
 */
public final class WorldConfiguration
{
  public static final int COMPONENT_MANAGER_IDX = 0;
  public static final int ENTITY_MANAGER_IDX = 1;
  public static final int ASPECT_SUBSCRIPTION_MANAGER_IDX = 2;
  final Bag<BaseSystem> systems = new Bag<BaseSystem>( BaseSystem.class );
  protected int expectedEntityCount = 128;
  @Nonnull
  protected Map<String, Object> injectables = new HashMap<String, Object>();
  @Nullable
  protected Injector injector;
  @Nullable
  protected SystemInvocationStrategy invocationStrategy;
  private boolean alwaysDelayComponentRemoval = false;
  @Nonnull
  private Set<Class<? extends BaseSystem>> registered = new HashSet<Class<? extends BaseSystem>>();

  public WorldConfiguration()
  {
    // reserving space for core managers
    systems.add( null ); // ComponentManager
    systems.add( null ); // EntityManager
    systems.add( null ); // AspectSubscriptionManager
  }

  public int expectedEntityCount()
  {
    return expectedEntityCount;
  }

  /**
   * Initializes array type containers with the value supplied.
   *
   * @param expectedEntityCount count of expected entities.
   * @return This instance for chaining.
   */
  @Nonnull
  public WorldConfiguration expectedEntityCount( int expectedEntityCount )
  {
    this.expectedEntityCount = expectedEntityCount;
    return this;
  }

  /**
   * Set Injector to handle all dependency injections.
   *
   * @param injector Injector to handle dependency injections.
   * @return This instance for chaining.
   */
  @Nonnull
  public WorldConfiguration setInjector( @Nullable Injector injector )
  {
		if ( injector == null )
		{
			throw new NullPointerException( "Injector must not be null" );
		}

    this.injector = injector;
    return this;
  }

  /**
   * Set strategy for invoking systems on {@link World#process()}.
   *
   * @param invocationStrategy Strategy that will invoke systems.
   * @return This instance for chaining.
   */
  @Nonnull
  public WorldConfiguration setInvocationStrategy( @Nullable SystemInvocationStrategy invocationStrategy )
  {
		if ( invocationStrategy == null )
		{
			throw new NullPointerException();
		}
    this.invocationStrategy = invocationStrategy;
    return this;
  }

  /**
   * Manually register object for injection by type.
   *
   * Explicitly annotate to be injected fields with <code>@Wire</code>. A class level
   * <code>@Wire</code> annotation is not enough.
   *
   * Since objects are injected by type, this method is limited to one object per type.
   * Use {@link #register(String, Object)} to register multiple objects of the same type.
   *
   * Not required for systems.
   *
   * @param o object to inject.
   * @return This instance for chaining.
   */
  @Nonnull
  public WorldConfiguration register( @Nonnull Object o )
  {
    return register( o.getClass().getName(), o );
  }

  /**
   * Manually register object for injection by name.
   *
   * Explicitly annotate to be injected fields with <code>@Wire(name="myName")</code>. A class
   * level <code>@Wire</code> annotation is not enough.
   *
   * Not required for systems.
   *
   * @param name unique identifier matching injection site name.
   * @param o    object to inject.
   * @return This instance for chaining.
   */
  @Nonnull
  public WorldConfiguration register( String name, Object o )
  {
    injectables.put( name, o );
    return this;
  }

  /**
   * Adds a system to this world that will be processed by
   * {@link World#process()}.
   *
   * @param system the system to add
   * @return the added system
   */
  @Nonnull
  public WorldConfiguration setSystem( @Nonnull Class<? extends BaseSystem> system )
  {
    try
    {
      return setSystem( ClassReflection.newInstance( system ) );
    }
    catch ( ReflectionException e )
    {
      throw new RuntimeException( e );
    }
  }

  /**
   * Will add a system to this world.
   *
   * @param <T>    the system class type
   * @param system the system to add
   * @return the added system
   */
  @Nonnull
  public <T extends BaseSystem> WorldConfiguration setSystem( @Nonnull T system )
  {
    systems.add( system );

    if ( !registered.add( system.getClass() ) )
    {
      String name = system.getClass().getSimpleName();
      throw new RuntimeException( name + " already added to " + getClass().getSimpleName() );
    }

    return this;
  }

  void initialize( @Nonnull World world, @Nonnull Injector injector, @Nonnull AspectSubscriptionManager asm )
  {
		if ( invocationStrategy == null )
		{
			invocationStrategy = new InvocationStrategy();
		}

    invocationStrategy.setWorld( world );

    world.invocationStrategy = invocationStrategy;

    systems.set( COMPONENT_MANAGER_IDX, world.getComponentManager() );
    systems.set( ENTITY_MANAGER_IDX, world.getEntityManager() );
    systems.set( ASPECT_SUBSCRIPTION_MANAGER_IDX, asm );

    for ( BaseSystem system : systems )
    {
      world.partition.systems.put( system.getClass(), system );
      system.setWorld( world );
      if ( ClassReflection.isInstance( Manager.class, system ) )
      {
        ( (Manager) system ).registerManager();
      }
    }

    injector.initialize( world, injectables );

    initializeSystems( injector );

    asm.processComponentIdentity( NO_COMPONENTS, new BitVector() );

    invocationStrategy.setSystems( systems );
    invocationStrategy.initialize();
  }

  private void initializeSystems( @Nonnull Injector injector )
  {
    for ( int i = 0, s = systems.size(); i < s; i++ )
    {
      BaseSystem system = systems.get( i );
      injector.inject( system );
    }

    for ( int i = 0, s = systems.size(); i < s; i++ )
    {
      BaseSystem system = systems.get( i );
      system.initialize();
    }
  }

  /**
   * Delay component removal until all subscriptions have been notified.
   *
   * Extends the lifecycle of ALL component types, ensuring removed instances are retrievable until
   * all {@link EntitySubscription.SubscriptionListener#removed(IntBag) listeners} have been notified - regardless
   * of removal method.
   */
  public boolean isAlwaysDelayComponentRemoval()
  {
    return alwaysDelayComponentRemoval;
  }

  /**
   * Delay component removal until all subscriptions have been notified.
   *
   * Extends the lifecycle of ALL component types, ensuring removed instances are retrievable until
   * all {@link EntitySubscription.SubscriptionListener#removed(IntBag) listeners} have been notified - regardless
   * of removal method.
   *
   * Has a slight performance cost.
   *
   * @param value When {@code true}, component removal for all components will be delayed.
   */
  public void setAlwaysDelayComponentRemoval( boolean value )
  {
    this.alwaysDelayComponentRemoval = value;
  }
}
