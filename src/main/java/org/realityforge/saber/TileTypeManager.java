package org.realityforge.saber;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

public final class TileTypeManager
{
  //TODO: In future this should be much more efficient representation. Perhaps a simple array
  private final Map<Integer, TileType> _tileTypes = new HashMap<>();

  @Nonnull
  public TileType registerTileType( final int id, @Nonnull final Texture texture )
  {
    return registerTileType( id, texture, 0 );
  }

  @Nonnull
  public TileType registerTileType( final int id, @Nonnull final Texture texture, final int flags )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !_tileTypes.containsKey( id ),
                 () -> "Saber-0003: Attempted to add tile type with id " + id + " when tile with " +
                       "id already exists already exists" );
    }
    final TileType tileType = new TileType( id, texture, flags );
    _tileTypes.put( id, tileType );
    return tileType;
  }

  @Nonnull
  public TileType getTileType( final int value )
  {
    return Objects.requireNonNull( _tileTypes.get( value ) );
  }
}
