package com.artemis.utils;

import java.util.BitSet;
import javax.annotation.Nonnull;

public final class ConverterUtil
{
  private ConverterUtil()
  {
  }

  @Nonnull
  public static IntBag toIntBag( @Nonnull final BitVector bs, @Nonnull final IntBag out )
  {
    return bs.toIntBag( out );
  }

  @Nonnull
  public static IntBag toIntBag( @Nonnull final BitSet bs, @Nonnull final IntBag out )
  {
    if ( bs.isEmpty() )
    {
      out.setSize( 0 );
      return out;
    }

    final int size = bs.cardinality();
    out.setSize( size );
    out.ensureCapacity( size );

    final int[] activesArray = out.getData();
    for ( int i = 0, id = -1, s = size; s > i; i++ )
    {
      id = bs.nextSetBit( id + 1 );
      activesArray[ i ] = id;
    }

    return out;
  }

  @Nonnull
  public static BitVector toBitVector( @Nonnull final IntBag bag, @Nonnull final BitVector out )
  {
    final int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }

  @Nonnull
  public static BitSet toBitSet( @Nonnull final IntBag bag, @Nonnull final BitSet out )
  {
    final int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }
}
