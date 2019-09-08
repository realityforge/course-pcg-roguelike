package com.artemis.utils;

import java.util.BitSet;
import javax.annotation.Nonnull;

public final class ConverterUtil
{
  private ConverterUtil()
  {
  }

  @Nonnull
  public static IntBag toIntBag( @Nonnull BitVector bs, @Nonnull IntBag out )
  {
    return bs.toIntBag( out );
  }

  @Nonnull
  public static IntBag toIntBag( @Nonnull BitSet bs, @Nonnull IntBag out )
  {
    if ( bs.isEmpty() )
    {
      out.setSize( 0 );
      return out;
    }

    int size = bs.cardinality();
    out.setSize( size );
    out.ensureCapacity( size );

    int[] activesArray = out.getData();
    for ( int i = 0, id = -1, s = size; s > i; i++ )
    {
      id = bs.nextSetBit( id + 1 );
      activesArray[ i ] = id;
    }

    return out;
  }

  @Nonnull
  public static BitVector toBitVector( @Nonnull IntBag bag, @Nonnull BitVector out )
  {
    int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }

  @Nonnull
  public static BitSet toBitSet( @Nonnull IntBag bag, @Nonnull BitSet out )
  {
    int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }
}