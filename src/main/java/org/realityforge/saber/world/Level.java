package org.realityforge.saber.world;

import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

public final class Level
{
  private final int _columnCount;
  private final int _rowCount;
  @Nonnull
  private final Tile[] _tiles;

  public Level( final int columnCount, final int rowCount, @Nonnull final TileType defaultTileType )
  {
    assert columnCount > 0;
    assert rowCount > 0;
    _columnCount = columnCount;
    _rowCount = rowCount;
    _tiles = new Tile[ _columnCount * _rowCount ];
    int index = 0;
    for ( int i = 0; i < _rowCount; i++ )
    {
      for ( int j = 0; j < _columnCount; j++ )
      {
        _tiles[ index++ ] = new Tile( j, i, defaultTileType );
      }
    }
  }

  public boolean isValidTile( final int column, final int row )
  {
    return column >= 0 && column < _columnCount && row >= 0 && row < _rowCount;
  }

  @Nonnull
  public Tile[] getTiles()
  {
    return _tiles;
  }

  @Nonnull
  public Tile getTile( final int column, final int row )
  {
    if ( BrainCheckConfig.checkApiInvariants() )
    {
      Guards.apiInvariant( () -> isValidTile( column, row ),
                           () -> "Saber-0004: Tile with invalid coordinates requested. " + column + "," + row + " is " +
                                 "outside of bounds of " + _columnCount + "x" + _rowCount );
    }
    assert isValidTile( column, row );
    return _tiles[ column + row * _rowCount ];
  }

  public int getColumnCount()
  {
    return _columnCount;
  }

  public int getRowCount()
  {
    return _rowCount;
  }
}
