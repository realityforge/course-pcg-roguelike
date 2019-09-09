package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.artemis.utils.ShortBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handles the association between entities and their components.
 * <p>
 * Only one component manager exists per {@link World} instance,
 * managed by the world.
 * </p>
 *
 * @author Arni Arent
 */
@SkipWire
public class ComponentManager
  extends BaseSystem
{
  /**
   * Adrian's secret rebellion.
   */
  static final int NO_COMPONENTS = 0;
  /**
   * Collects all Entites marked for deletion from this ComponentManager.
   */
  @Nonnull
  private final Bag<ComponentMapper> mappers = new Bag( ComponentMapper.class );
  private final ComponentIdentityResolver identityResolver = new ComponentIdentityResolver();
  @Nonnull
  final ShortBag entityToIdentity;
  @Nonnull
  protected final ComponentTypeFactory typeFactory;

  /**
   * Creates a new instance of {@link ComponentManager}.
   */
  protected ComponentManager( final int entityContainerSize )
  {
    entityToIdentity = new ShortBag( entityContainerSize );
    typeFactory = new ComponentTypeFactory( this, entityContainerSize );
  }

  @Override
  protected void processSystem()
  {
  }

  /**
   * Create a component of given type by class.
   *
   * @param owner          entity id
   * @param componentClass class of component to instance.
   * @return Newly created packed, pooled or basic component.
   */
  @Nullable
  protected <T extends Component> T create( final int owner, final Class<T> componentClass )
  {
    return getMapper( componentClass ).create( owner );
  }

  protected <T extends Component> ComponentMapper<T> getMapper( final Class<T> mapper )
  {
    final ComponentType type = typeFactory.getTypeFor( mapper );
    return mappers.get( type.getIndex() );
  }

  void registerComponentType( @Nonnull final ComponentType ct, final int capacity )
  {
    final int index = ct.getIndex();
    final ComponentMapper mapper = new ComponentMapper( ct.getType(), world );
    mapper.components.ensureCapacity( capacity );
    mappers.set( index, mapper );
  }

  @Nonnull
  @SuppressWarnings( "unchecked" )
  static <T extends Component> T newInstance( @Nonnull final Class<T> componentClass )
  {
    try
    {
      return ClassReflection.newInstance( componentClass );
    }
    catch ( final ReflectionException e )
    {
      throw new InvalidComponentException( componentClass, "Unable to instantiate component.", e );
    }
  }

  /**
   * Removes all components from deleted entities.
   *
   * @param pendingPurge the entities to remove components from
   */
  void clean( @Nonnull final IntBag pendingPurge )
  {
    final int[] ids = pendingPurge.getData();
    for ( int i = 0, s = pendingPurge.size(); s > i; i++ )
    {
      removeComponents( ids[ i ] );
    }
  }

  private void removeComponents( final int entityId )
  {
    final Bag<ComponentMapper> mappers = componentMappers( entityId );
    for ( int i = 0, s = mappers.size(); s > i; i++ )
    {
      mappers.get( i ).internalRemove( entityId );
    }

    setIdentity( entityId, 0 );
  }

  /**
   * Get all components from all entities for a given type.
   *
   * @param type the type of components to get
   * @return a bag containing all components of the given type
   */
  @Nonnull
  protected Bag<Component> getComponentsByType( @Nonnull final ComponentType type )
  {
    return mappers.get( type.getIndex() ).components;
  }

  /**
   * @return Bag of all generated component types, which identify components without having to use classes.
   */
  public ImmutableBag<ComponentType> getComponentTypes()
  {
    return typeFactory.types;
  }

  /**
   * Get a component of an entity.
   *
   * @param entityId the entity associated with the component
   * @param type     the type of component to get
   * @return the component of given type
   */
  protected Component getComponent( final int entityId, @Nonnull final ComponentType type )
  {
    final ComponentMapper mapper = mappers.get( type.getIndex() );
    return mapper.get( entityId );
  }

  /**
   * Get all component associated with an entity.
   *
   * @param entityId the entity to get components from
   * @param fillBag  a bag to be filled with components
   * @return the {@code fillBag}, filled with the entities components
   */
  @Nonnull
  public Bag<Component> getComponentsFor( final int entityId, @Nonnull final Bag<Component> fillBag )
  {
    final Bag<ComponentMapper> mappers = componentMappers( entityId );

    for ( int i = 0, s = mappers.size(); s > i; i++ )
    {
      fillBag.add( mappers.get( i ).get( entityId ) );
    }

    return fillBag;
  }

  /**
   * Get component composition of entity.
   */
  BitVector componentBits( final int entityId )
  {
    final int identityIndex = entityToIdentity.get( entityId );
    return identityResolver.compositionBits.get( identityIndex );
  }

  /**
   * Get component composition of entity.
   */
  private Bag<ComponentMapper> componentMappers( final int entityId )
  {
    final int identityIndex = entityToIdentity.get( entityId );
    return identityResolver.compositionMappers.get( identityIndex );
  }

  /**
   * Fetches unique identifier for composition.
   *
   * @param componentBits composition to fetch unique identifier for.
   * @return Unique identifier for passed composition.
   */
  public int compositionIdentity( @Nonnull final BitVector componentBits )
  {
    int identity = identityResolver.getIdentity( componentBits );
    if ( identity == -1 )
    {
      identity = identityResolver.allocateIdentity( componentBits, this );
      world.getAspectSubscriptionManager()
        .processComponentIdentity( identity, componentBits );
    }

    return identity;
  }

  /**
   * Fetch composition id for entity.
   *
   * A composition id is uniquely identified by a single Aspect. For performance reasons, each entity is
   * identified by its composition id. Adding or removing components from an entity will change its compositionId.
   *
   * @return composition identity.
   */
  public int getIdentity( final int entityId )
  {
    return entityToIdentity.get( entityId );
  }

  /**
   * Synchronizes new subscriptions with {@link World} state.
   *
   * @param es entity subscription to update.
   */
  void synchronize( @Nonnull final EntitySubscription es )
  {
    final Bag<BitVector> compositionBits = identityResolver.compositionBits;
    for ( int i = 1, s = compositionBits.size(); s > i; i++ )
    {
      final BitVector componentBits = compositionBits.get( i );
      es.processComponentIdentity( i, componentBits );
    }

    for ( final Entity e : world.getEntityManager().entities )
    {
      if ( e != null )
      {
        es.check( e.id, getIdentity( e.id ) );
      }
    }

    es.informEntityChanges();
    es.rebuildCompressedActives();
  }

  /**
   * Set composition id of entity.
   *
   * @param entityId      entity id
   * @param compositionId composition id
   */
  void setIdentity( final int entityId, final int compositionId )
  {
    entityToIdentity.unsafeSet( entityId, (short) compositionId );
  }

  /**
   * @return Factory responsible for tracking all component types.
   */
  @Nonnull
  public ComponentTypeFactory getTypeFactory()
  {
    return typeFactory;
  }

  public void ensureCapacity( final int newSize )
  {
    typeFactory.initialMapperCapacity = newSize;
    entityToIdentity.ensureCapacity( newSize );
    for ( final ComponentMapper mapper : mappers )
    {
      mapper.components.ensureCapacity( newSize );
    }
  }

  /**
   * Tracks all unique component compositions.
   */
  static final class ComponentIdentityResolver
  {
    @Nonnull
    final Bag<BitVector> compositionBits;
    @Nonnull
    final Bag<Bag<ComponentMapper>> compositionMappers;

    ComponentIdentityResolver()
    {
      compositionBits = new Bag( BitVector.class );
      compositionBits.add( new BitVector() );
      compositionMappers = new Bag<>();
      compositionMappers.add( new Bag( ComponentMapper.class ) );
    }

    /**
     * Fetch unique identity for passed composition.
     */
    int getIdentity( @Nonnull final BitVector components )
    {
      final int size = compositionBits.size();
      for ( int i = NO_COMPONENTS; size > i; i++ )
      { // want to start from 1 so that 0 can mean null
        if ( components.equals( compositionBits.get( i ) ) )
        {
          return i;
        }
      }

      return -1;
    }

    int allocateIdentity( @Nonnull final BitVector componentBits, @Nonnull final ComponentManager cm )
    {
      final Bag<ComponentMapper> mappers =
        new Bag<>( ComponentMapper.class, componentBits.cardinality() );

      final ComponentTypeFactory tf = cm.getTypeFactory();
      for ( int i = componentBits.nextSetBit( 0 ); i >= 0; i = componentBits.nextSetBit( i + 1 ) )
      {
        mappers.add( cm.getMapper( tf.getTypeFor( i ).getType() ) );
      }

      compositionMappers.add( mappers );
      compositionBits.add( new BitVector( componentBits ) );

      return compositionBits.size() - 1;
    }
  }
}
