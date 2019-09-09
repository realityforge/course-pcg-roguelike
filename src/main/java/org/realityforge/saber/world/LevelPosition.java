package org.realityforge.saber.world;

public final class LevelPosition
{
  private int _column;
  private int _row;

  public LevelPosition( final int column, final int row )
  {
    _column = column;
    _row = row;
  }

  public int getColumn()
  {
    return _column;
  }

  public void setColumn( final int column )
  {
    _column = column;
  }

  public int getRow()
  {
    return _row;
  }

  public void setRow( final int row )
  {
    _row = row;
  }
}
