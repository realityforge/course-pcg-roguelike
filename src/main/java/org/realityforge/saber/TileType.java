package org.realityforge.saber;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

public final class TileType
{
  private final int _id;
  @Nonnull
  private final Texture _texture;
  private final int _flags;

  TileType( final int id, @Nonnull final Texture texture, final int flags )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> 0 < id,
                 () -> "Saber-0001: Invalid id (" + id + ") passed when creating tile" );
      invariant( () -> 0 == ( flags & ~Flags.MASK ),
                 () -> "Saber-0002: Invalid flags (" + flags + ") passed when creating tile " + id );
    }
    _id = id;
    _texture = Objects.requireNonNull( texture );
    _flags = flags;
  }

  public int getId()
  {
    return _id;
  }

  @Nonnull
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
