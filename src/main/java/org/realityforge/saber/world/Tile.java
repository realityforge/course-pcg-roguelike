package org.realityforge.saber.world;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class Tile
{
  private final int _column;
  private final int _row;
  @Nonnull
  private TileType _tileType;
  private double _topLeftX;
  private double _topLeftY;

  public Tile( final int column, final int row, @Nonnull final TileType tileType )
  {
    assert column >= 0;
    assert row >= 0;
    _column = column;
    _row = row;
    _tileType = tileType;
  }

  public int getColumn()
  {
    return _column;
  }

  public int getRow()
  {
    return _row;
  }

  @Nonnull
  public TileType getTileType()
  {
    return _tileType;
  }

  public void setTileType( @Nonnull final TileType tileType )
  {
    _tileType = Objects.requireNonNull( tileType );
  }

  public double getTopLeftX()
  {
    return _topLeftX;
  }

  public void setTopLeftX( final double topLeftX )
  {
    _topLeftX = topLeftX;
  }

  public double getTopLeftY()
  {
    return _topLeftY;
  }

  public void setTopLeftY( final double topLeftY )
  {
    _topLeftY = topLeftY;
  }
}
