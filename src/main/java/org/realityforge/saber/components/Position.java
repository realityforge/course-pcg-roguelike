package org.realityforge.saber.components;

import com.artemis.PooledComponent;
import javax.annotation.Nonnull;
import org.realityforge.saber.world.LevelPosition;

public final class Position
  extends PooledComponent
{
  @Nonnull
  public final LevelPosition position = new LevelPosition( 0, 0 );

  @Override
  protected void reset()
  {
    position.setColumn( 0 );
    position.setRow( 0 );
  }
}
