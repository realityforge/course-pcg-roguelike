package org.realityforge.saber.world;

final class LevelPosition
{
  private final int _column;
  private final int _row;

  LevelPosition( final int column, final int row )
  {
    _column = column;
    _row = row;
  }

  int getColumn()
  {
    return _column;
  }

  int getRow()
  {
    return _row;
  }
}
