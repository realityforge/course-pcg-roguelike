package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import javax.annotation.Nonnull;

/**
 * Process a subset of entities every x ticks.
 * <p>
 * A typical usage would be to regenerate ammo or health at certain intervals,
 * no need to do that every game loop, but perhaps every 100 ms. or every
 * second.
 * </p>
 *
 * @author Arni Arent
 */
public abstract class IntervalEntityProcessingSystem
  extends IntervalEntitySystem
{
  /**
   * Creates a new IntervalEntityProcessingSystem.
   *
   * @param aspect   the aspect to match entities
   * @param interval the interval at which the system is processed
   */
  public IntervalEntityProcessingSystem( final Aspect.Builder aspect, final float interval )
  {
    super( aspect, interval );
  }

  /**
   * Process a entity this system is interested in.
   *
   * @param e the entity to process
   */
  protected abstract void process( Entity e );

  @Override
  protected void processSystem()
  {
    processEntities( getEntities() );
  }

  protected void processEntities( @Nonnull final Bag<Entity> entities )
  {
    final Entity[] ids = entities.getData();
    for ( int i = 0, s = entities.size(); s > i; i++ )
    {
      process( ids[ i ] );
    }
  }

}
