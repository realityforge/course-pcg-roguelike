package org.realityforge.saber.world;

import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.saber.Texture;
import static org.realityforge.braincheck.Guards.*;

public final class TileType
{
  private final int _id;
  @Nullable
  private final Texture _texture;
  private final int _flags;

  TileType( final int id, @Nullable final Texture texture, final int flags )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> 0 < id,
                 () -> "Saber-0001: Invalid id (" + id + ") passed when creating tile" );
      invariant( () -> 0 == ( flags & ~Flags.MASK ),
                 () -> "Saber-0002: Invalid flags (" + flags + ") passed when creating tile " + id );
    }
    _id = id;
    _texture = texture;
    _flags = flags;
  }

  public int getId()
  {
    return _id;
  }

  @Nullable
  public Texture getTexture()
  {
    return _texture;
  }

  public boolean isSolid()
  {
    return ( Flags.SOLID & _flags ) == Flags.SOLID;
  }

  public boolean isFloor()
  {
    return ( Flags.FLOOR & _flags ) == Flags.FLOOR;
  }

  public static final class Flags
  {
    public static final int SOLID = 1 << 1;
    public static final int FLOOR = 1 << 2;
    private static final int MASK = SOLID | FLOOR;
  }
}
