package com.artemis;

import com.artemis.EntityTransmuter.TransmuteOperation;
import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Builder for basic Archetype instances. To reap the maximum benefit of Archetypes,
 * it's recommended to stash them away inside an manager or similar. Archetypes
 * main advantage come from the improved insertion into systems performance.
 * Calling {@link Entity#edit() edit()} on the Entity returned by {@link World#createEntity(Archetype)}
 * nullifies this optimization.
 * <p>
 * Generated archetypes provide a blueprint for quick entity creation.
 * Instance generated entities using {@link World#createEntity(Archetype)}
 *
 * @since 0.7
 */
public class ArchetypeBuilder
{
  @Nonnull
  private final Bag<Class<? extends Component>> classes;

  /**
   * Constructs an archetype builder containing the composition of the specified parent.
   *
   * @param parent archetype composition to copy.
   */
  public ArchetypeBuilder( @Nullable final Archetype parent )
  {
    classes = new Bag<>();
    if ( parent == null )
    {
      return;
    }

    parent.transmuter.getAdditions( classes );
  }

  /**
   * Constructs an empty archetype builder.
   */
  public ArchetypeBuilder()
  {
    this( null );
  }

  /**
   * Ensure this builder includes the specified component type.
   *
   * @return This instance for chaining.
   */
  @Nonnull
  public ArchetypeBuilder add( @Nonnull final Class<? extends Component> type )
  {
    if ( !classes.contains( type ) )
    {
      classes.add( type );
    }

    return this;
  }

  /**
   * Ensure this builder includes the specified component types.
   *
   * @return This instance for chaining.
   */
  @Nonnull
  public ArchetypeBuilder add( @Nonnull final Class<? extends Component>... types )
  {
    for ( final Class<? extends Component> type : types )
    {
      if ( !classes.contains( type ) )
      {
        classes.add( type );
      }
    }

    return this;
  }

  /**
   * Remove the specified component from this builder, if it is present (optional operation).
   *
   * @return This instance for chaining.
   */
  @Nonnull
  public ArchetypeBuilder remove( @Nonnull final Class<? extends Component> type )
  {
    classes.remove( type );
    return this;
  }

  /**
   * Remove the specified component from this builder, if it is present (optional operation).
   *
   * @return This instance for chaining.
   */
  @Nonnull
  public ArchetypeBuilder remove( @Nonnull final Class<? extends Component>... types )
  {
    for ( final Class<? extends Component> type : types )
    {
      classes.remove( type );
    }

    return this;
  }

  /**
   * Create a new world specific instance of Archetype based on the current state.
   *
   * @param world applicable domain of the Archetype.
   * @return new Archetype based on current state
   */
  @Nonnull
  public Archetype build( @Nonnull final World world )
  {
    final ComponentType[] types = resolveTypes( world );

    final ComponentManager cm = world.getComponentManager();
    final ComponentMapper[] mappers = new ComponentMapper[ types.length ];
    for ( int i = 0, s = mappers.length; s > i; i++ )
    {
      mappers[ i ] = cm.getMapper( types[ i ].getType() );
    }

    final int compositionId = cm.compositionIdentity( bitset( types ) );
    final TransmuteOperation operation =
      new TransmuteOperation( compositionId, mappers, new ComponentMapper[ 0 ] );

    return new Archetype( operation, compositionId );
  }

  /**
   * generate bitset mask of types.
   */
  @Nonnull
  private static BitVector bitset( @Nonnull final ComponentType[] types )
  {
    final BitVector bs = new BitVector();
    for ( final ComponentType type : types )
    {
      bs.set( type.getIndex() );
    }

    return bs;
  }

  /**
   * Converts java classes to component types.
   */
  @Nonnull
  private ComponentType[] resolveTypes( @Nonnull final World world )
  {
    final ComponentTypeFactory tf = world.getComponentManager().typeFactory;
    final ComponentType[] types = new ComponentType[ classes.size() ];
    for ( int i = 0, s = classes.size(); s > i; i++ )
    {
      types[ i ] = tf.getTypeFor( classes.get( i ) );
    }

    return types;
  }
}
