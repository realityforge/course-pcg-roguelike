package com.artemis.utils;

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
  public static BitVector toBitVector( @Nonnull final IntBag bag, @Nonnull final BitVector out )
  {
    final int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }
}
