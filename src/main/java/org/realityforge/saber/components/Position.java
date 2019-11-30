package org.realityforge.saber.components;

import galdr.annotations.Component;
import javax.annotation.Nonnull;
import org.realityforge.saber.world.LevelPosition;

@Component
public final class Position
{
  @Nonnull
  public final LevelPosition position = new LevelPosition( 0, 0 );
}
