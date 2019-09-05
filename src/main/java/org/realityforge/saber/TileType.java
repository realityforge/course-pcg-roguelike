package org.realityforge.saber;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class TileType
{
  private final int _value;
  @Nonnull
  private final Texture _texture;

  TileType( final int value, @Nonnull final Texture texture )
  {
    _value = value;
    _texture = Objects.requireNonNull( texture );
  }

  public int getValue()
  {
    return _value;
  }

  @Nonnull
  public Texture getTexture()
  {
    return _texture;
  }
}
