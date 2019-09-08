package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.BitVector;
import com.artemis.utils.ShortBag;
import javax.annotation.Nonnull;

/**
 * Fastest way of changing entity component compositions. Primarily useful when
 * bootstrapping entities over several different systems or when
 * dealing with many entities at the same time (light particle systems etc).
 * <p>
 * Given a set of component additions/removals: for each encountered
 * compositionId, cache the calculated new compositionId. This extends
 * the performance benefits introduced with
 * {@link Archetype Archetypes} in 0.7.0 to carry over to existing entities.
 * </p>
 *
 * @see EntityTransmuterFactory
 */
public final class EntityTransmuter
{
  @Nonnull
  private final Factory factory;
  @Nonnull
  private final EntityManager em;
  @Nonnull
  private final BatchChangeProcessor batchProcessor;
  @Nonnull
  private final Bag<TransmuteOperation> operations;
  @Nonnull
  private final ShortBag entityToIdentity;

  public EntityTransmuter( @Nonnull World world, Aspect.Builder aspect )
  {
    this( world, world.getAspectSubscriptionManager().get( aspect ).getAspect() );
  }

  EntityTransmuter( @Nonnull World world, @Nonnull Aspect aspect )
  {
    this( world, new BitVector( aspect.allSet ), new BitVector( aspect.exclusionSet ) );
  }

  EntityTransmuter( @Nonnull World world, BitVector additions, BitVector removals )
  {
    em = world.getEntityManager();
    entityToIdentity = world.getComponentManager().entityToIdentity;
    batchProcessor = world.batchProcessor;
    operations = new Bag<>( TransmuteOperation.class );

    factory = new Factory( world, additions, removals );
  }

  /**
   * <p>Apply on target entity. Does nothing if entity has been scheduled for
   * deletion.</p>
   *
   * <p>Transmuter will add components by replacing and retire pre-existing components.</p>
   *
   * @param entityId target entity id
   */
  public void transmute( int entityId )
  {
		if ( !isValid( entityId ) )
		{
			return;
		}

    TransmuteOperation operation = getOperation( entityId );
    operation.perform( entityId );
    entityToIdentity.unsafeSet( entityId, operation.compositionId );
  }

  void transmuteNoOperation( int entityId )
  {
		if ( !isValid( entityId ) )
		{
			return;
		}

    TransmuteOperation operation = getOperation( entityId );
    entityToIdentity.unsafeSet( entityId, operation.compositionId );
  }

  private boolean isValid( int entityId )
  {
		if ( !em.isActive( entityId ) )
		{
			throw new RuntimeException( "Issued transmute on deleted " + entityId );
		}

		if ( batchProcessor.isDeleted( entityId ) )
		{
			return false;
		}

    batchProcessor.changed.unsafeSet( entityId );

    return true;
  }

  /**
   * Apply on target entity.
   *
   * Transmuter will add components by replacing and retire pre-existing components.
   *
   * @param e target entity.
   */
  public void transmute( @Nonnull Entity e )
  {
    transmute( e.id );
  }

  TransmuteOperation getOperation( int entityId )
  {
    return operation( entityId, entityToIdentity.get( entityId ) );
  }

  private TransmuteOperation operation( int entityId, int compositionId )
  {
    TransmuteOperation operation = operations.safeGet( compositionId );
    if ( operation == null )
    {
      operation = factory.createOperation( entityId );
      operations.set( compositionId, operation );
    }
    return operation;
  }

  @Nonnull
  @Override
  public String toString()
  {
    return "EntityTransmuter(add=" + factory.additions + " remove=" + factory.removals + ")";
  }

  static class Factory
  {
    @Nonnull
    private final ComponentManager cm;
    private final BitVector additions;
    private final BitVector removals;
    @Nonnull
    private final BitVector bs;

    Factory( @Nonnull World world, BitVector additions, BitVector removals )
    {
      this.cm = world.getComponentManager();
      this.additions = additions;
      this.removals = removals;
      this.bs = new BitVector();
    }

    @Nonnull
    TransmuteOperation createOperation( int entityId )
    {
      BitVector componentBits = cm.componentBits( entityId );

      bs.clear();
      bs.or( componentBits );
      bs.or( additions );
      bs.andNot( removals );
      int compositionId = cm.compositionIdentity( bs );
      return new TransmuteOperation( compositionId,
                                     getAdditions( componentBits ), getRemovals( componentBits ) );
    }

    @Nonnull
    private Bag<ComponentMapper> getAdditions( @Nonnull BitVector origin )
    {
      ComponentTypeFactory tf = cm.typeFactory;
      Bag<ComponentMapper> types = new Bag( ComponentMapper.class );
      for ( int i = additions.nextSetBit( 0 ); i >= 0; i = additions.nextSetBit( i + 1 ) )
      {
				if ( !origin.get( i ) )
				{
					types.add( cm.getMapper( tf.getTypeFor( i ).getType() ) );
				}
      }

      return types;
    }

    @Nonnull
    private Bag<ComponentMapper> getRemovals( @Nonnull BitVector origin )
    {
      ComponentTypeFactory tf = cm.typeFactory;
      Bag<ComponentMapper> types = new Bag( ComponentMapper.class );
      for ( int i = removals.nextSetBit( 0 ); i >= 0; i = removals.nextSetBit( i + 1 ) )
      {
				if ( origin.get( i ) )
				{
					types.add( cm.getMapper( tf.getTypeFor( i ).getType() ) );
				}
      }

      return types;
    }
  }

  static class TransmuteOperation
  {
    private final ComponentMapper[] additions;
    private final ComponentMapper[] removals;
    public final short compositionId;

    public TransmuteOperation( int compositionId,
                               ComponentMapper[] additions,
                               ComponentMapper[] removals )
    {

      this.compositionId = (short) compositionId;
      this.additions = additions;
      this.removals = removals;
    }

    public TransmuteOperation( int compositionId,
                               @Nonnull Bag<ComponentMapper> additions,
                               @Nonnull Bag<ComponentMapper> removals )
    {

      this.compositionId = (short) compositionId;
      this.additions = new ComponentMapper[ additions.size() ];
      this.removals = new ComponentMapper[ removals.size() ];

      for ( int i = 0, s = additions.size(); s > i; i++ )
      {
        this.additions[ i ] = additions.get( i );
      }

      for ( int i = 0, s = removals.size(); s > i; i++ )
      {
        this.removals[ i ] = removals.get( i );
      }
    }

    public void perform( int entityId )
    {
      for ( final ComponentMapper addition : additions )
      {
        addition.internalCreate( entityId );
      }

      for ( final ComponentMapper removal : removals )
      {
        removal.internalRemove( entityId );
      }
    }

    @Nonnull
    Bag<Class<? extends Component>> getAdditions( @Nonnull Bag<Class<? extends Component>> out )
    {
      for ( final ComponentMapper addition : additions )
      {
        out.add( addition.getType().getType() );
      }

      return out;
    }

    @Nonnull
    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append( "TransmuteOperation(" );

      if ( additions.length > 0 )
      {
        sb.append( "add={" );
        String delim = "";
        for ( ComponentMapper mapper : additions )
        {
          sb.append( delim ).append( mapper.getType().getType().getSimpleName() );
          delim = ", ";
        }
        sb.append( "}" );
      }

      if ( removals.length > 0 )
      {
				if ( additions.length > 0 )
				{
					sb.append( " " );
				}

        sb.append( "remove={" );
        String delim = "";
        for ( ComponentMapper mapper : removals )
        {
          sb.append( delim ).append( mapper.getType().getType().getSimpleName() );
          delim = ", ";
        }
        sb.append( "}" );
      }
      sb.append( ")" );

      return sb.toString();
    }
  }

}
