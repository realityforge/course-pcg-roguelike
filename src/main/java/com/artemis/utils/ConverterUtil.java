package com.artemis.utils;

import java.util.BitSet;

public final class ConverterUtil
{
  private ConverterUtil()
  {
  }

  public static IntBag toIntBag( BitVector bs, IntBag out )
  {
    return bs.toIntBag( out );
  }

  public static IntBag toIntBag( BitSet bs, IntBag out )
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

  public static BitVector toBitVector( IntBag bag, BitVector out )
  {
    int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }

  public static BitSet toBitSet( IntBag bag, BitSet out )
  {
    int[] data = bag.getData();
    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      out.set( data[ i ] );
    }

    return out;
  }
}