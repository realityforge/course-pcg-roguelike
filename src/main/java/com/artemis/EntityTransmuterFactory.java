package com.artemis;

import com.artemis.utils.BitVector;
import javax.annotation.Nonnull;

/**
 * Builder for {@link EntityTransmuter}.
 *
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class EntityTransmuterFactory
{
  @Nonnull
  private final ComponentTypeFactory types;
  @Nonnull
  private final BitVector additions;
  @Nonnull
  private final BitVector removals;
  @Nonnull
  private final World world;

  /**
   * Prepare new builder.
   */
  public EntityTransmuterFactory( @Nonnull final World world )
  {
    this.world = world;
    types = world.getComponentManager().typeFactory;
    additions = new BitVector();
    removals = new BitVector();
  }

  /**
   * Component to add upon transmutation. Overwrites and retires if component exists!
   */
  @Nonnull
  public EntityTransmuterFactory add( @Nonnull final Class<? extends Component> component )
  {
    final int index = types.getIndexFor( component );
    additions.set( index, true );
    removals.set( index, false );
    return this;
  }

  /**
   * Component to remove upon transmutation. Does nothing if missing.
   */
  @Nonnull
  public EntityTransmuterFactory remove( @Nonnull final Class<? extends Component> component )
  {
    final int index = types.getIndexFor( component );
    additions.set( index, false );
    removals.set( index, true );
    return this;
  }

  /**
   * Build instance
   */
  @Nonnull
  public EntityTransmuter build()
  {
    return new EntityTransmuter( world, additions, removals );
  }
}
