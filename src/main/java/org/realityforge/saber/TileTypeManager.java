package org.realityforge.saber;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class TileTypeManager
{
  //TODO: In future this should be much more efficient representation. Perhaps a simple array
  private final Map<Integer, TileType> _tileTypes = new HashMap<>();

  @Nonnull
  public TileType registerTileType( final int value, @Nonnull final Texture texture )
  {
    //TODO: Braincheck this
    assert !_tileTypes.containsKey( value );
    final TileType tileType = new TileType( value, texture );
    _tileTypes.put( value, tileType );
    return tileType;
  }

  @Nonnull
  public TileType getTileType( final int value )
  {
    return Objects.requireNonNull( _tileTypes.get( value ) );
  }
}
