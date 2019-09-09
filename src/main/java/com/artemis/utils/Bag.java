package com.artemis.utils;

import com.artemis.utils.reflect.ArrayReflection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.lang.Math.*;

/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 *
 * @param <E> object type this bag holds
 * @author Arni Arent
 */
@SuppressWarnings( "unchecked" )
public class Bag<E>
  implements ImmutableBag<E>
{
  /**
   * The backing array.
   */
  Object[] data;
  /**
   * The amount of elements contained in bag.
   */
  protected int size = 0;
  /**
   * The iterator, it is only created once and reused when required.
   */
  private BagIterator it;

  /**
   * Constructs an empty Bag with an initial capacity of 64.
   */
  public Bag()
  {
    this( 64 );
  }

  /**
   * Constructs an empty Bag with an initial capacity of 64.
   */
  public Bag( final Class<E> type )
  {
    this( 64 );
  }

  /**
   * Constructs an empty Bag with the specified initial capacity.
   *
   * @param capacity the initial capacity of Bag
   */
  @SuppressWarnings( "unchecked" )
  public Bag( final int capacity )
  {
    data = (E[]) ArrayReflection.newInstance( Object.class, capacity );
  }

  public Bag( final Class<E> type, final int capacity )
  {
    this( capacity );
  }

  /**
   * Removes the element at the specified position in this Bag.
   * <p>
   * It does this by overwriting it was last element then removing last
   * element
   * </p>
   *
   * @param index the index of element to be removed
   * @return element that was removed from the Bag
   */
  public E remove( final int index )
    throws ArrayIndexOutOfBoundsException
  {
    final E e = (E) data[ index ]; // make copy of element to remove so it can be returned
    data[ index ] = data[ --size ]; // overwrite item to remove with last element
    data[ size ] = null; // null last element, so gc can do its work
    return e;
  }

  /**
   * Sorts the bag using the {@code comparator}.
   */
  public void sort( final Comparator<E> comparator )
  {
    Sort.instance().sort( this, comparator );
  }

  /**
   * Remove and return the last object in the bag.
   *
   * @return the last object in the bag, null if empty
   */
  @Nullable
  public E removeLast()
  {
    if ( size > 0 )
    {
      final E e = (E) data[ --size ];
      data[ size ] = null;
      return e;
    }

    return null;
  }

  /**
   * Removes the first occurrence of the specified element from this Bag, if
   * it is present.
   * <p>
   * If the Bag does not contain the element, it is unchanged. It does this
   * by overwriting it was last element then removing last element
   * </p>
   *
   * @param e element to be removed from this list, if present
   * @return {@code true} if this list contained the specified element
   */
  public boolean remove( @Nonnull final E e )
  {
    for ( int i = 0; i < size; i++ )
    {
      final E e2 = (E) data[ i ];

      if ( e.equals( e2 ) )
      {
        data[ i ] = data[ --size ]; // overwrite item to remove with last element
        data[ size ] = null; // null last element, so gc can do its work
        return true;
      }
    }

    return false;
  }

  /**
   * Check if bag contains this element.
   *
   * @param e element to check
   * @return {@code true} if the bag contains this element
   */
  @Override
  public boolean contains( @Nonnull final E e )
  {
    for ( int i = 0; size > i; i++ )
    {
      if ( e.equals( data[ i ] ) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes from this Bag all of its elements that are contained in the
   * specified Bag.
   *
   * @param bag Bag containing elements to be removed from this Bag
   * @return {@code true} if this Bag changed as a result of the call
   */
  public boolean removeAll( @Nonnull final ImmutableBag<E> bag )
  {
    boolean modified = false;

    for ( int i = 0, s = bag.size(); s > i; i++ )
    {
      final E e1 = bag.get( i );

      for ( int j = 0; j < size; j++ )
      {
        final E e2 = (E) data[ j ];

        if ( e1.equals( e2 ) )
        {
          remove( j );
          j--;
          modified = true;
          break;
        }
      }
    }

    return modified;
  }

  /**
   * Returns the element at the specified position in Bag.
   *
   * @param index index of the element to return
   * @return the element at the specified position in bag
   */
  @Override
  public E get( final int index )
    throws ArrayIndexOutOfBoundsException
  {
    return (E) data[ index ];
  }

  /**
   * Returns the element at the specified position in Bag. This method
   * ensures that the bag grows if the requested index is outside the bounds
   * of the current backing array.
   *
   * @param index index of the element to return
   * @return the element at the specified position in bag
   */
  public E safeGet( final int index )
  {
    if ( index >= data.length )
    {
      grow( Math.max( ( 2 * data.length ), ( 3 * index ) / 2 ) );
    }

    return (E) data[ index ];
  }

  /**
   * Returns the number of elements in this bag.
   *
   * @return the number of elements in this bag
   */
  @Override
  public int size()
  {
    return size;
  }

  /**
   * Returns the number of elements the bag can hold without growing.
   *
   * @return the number of elements the bag can hold without growing
   */
  public int getCapacity()
  {
    return data.length;
  }

  /**
   * Checks if the internal storage supports this index.
   *
   * @param index index to check
   * @return {@code true} if the index is within bounds
   */
  public boolean isIndexWithinBounds( final int index )
  {
    return index < getCapacity();
  }

  /**
   * Returns true if this bag contains no elements.
   *
   * @return {@code true} if this bag contains no elements
   */
  @Override
  public boolean isEmpty()
  {
    return size == 0;
  }

  /**
   * Adds the specified element to the end of this bag.
   * <p>
   * If required, it also increases the capacity of the bag.
   * </p>
   *
   * @param e element to be added to this list
   */
  public void add( final E e )
  {
    // is size greater than capacity increase capacity
    if ( size == data.length )
    {
      grow( data.length * 2 );
    }

    data[ size++ ] = e;
  }

  /**
   * Set element at specified index in the bag.
   *
   * @param index position of element
   * @param e     the element
   */
  public void set( final int index, final E e )
  {
    if ( index >= data.length )
    {
      grow( max( ( 2 * data.length ), index + 1 ) );
    }

    size = Math.max( size, index + 1 );
    data[ index ] = e;
  }

  /**
   * <em>Unsafe method.</em> Sets element at specified index in the bag,
   * without updating size. Internally used by artemis when operation is
   * known to be safe.
   *
   * @param index position of element
   * @param e     the element
   */
  public void unsafeSet( final int index, final E e )
  {
    data[ index ] = e;
  }

  /**
   * Increase the capacity of the bag.
   *
   * @throws ArrayIndexOutOfBoundsException if new capacity is smaller than old
   */
  private void grow()
  {
    grow( data.length * 2 );
  }

  private void grow( final int newCapacity )
    throws ArrayIndexOutOfBoundsException
  {
    final Object[] oldData = data;
    data = new Object[ newCapacity ];
    System.arraycopy( oldData, 0, data, 0, oldData.length );
  }

  /**
   * Check if an item, if added at the given item will fit into the bag.
   * <p>
   * If not, the bag capacity will be increased to hold an item at the index.
   * </p>
   *
   * <p>yeah, sorry, it's weird, but we don't want to change existing change behavior</p>
   *
   * @param index index to check
   */
  public void ensureCapacity( final int index )
  {
    if ( index >= data.length )
    {
      grow( index + 1 );
    }
  }

  /**
   * Removes all of the elements from this bag.
   * <p>
   * The bag will be empty after this call returns.
   * </p>
   */
  public void clear()
  {
    Arrays.fill( data, 0, size, null );
    size = 0;
  }

  /**
   * Add all items into this bag.
   *
   * @param items bag with items to add
   */
  public void addAll( @Nonnull final ImmutableBag<E> items )
  {
    for ( int i = 0, s = items.size(); s > i; i++ )
    {
      add( items.get( i ) );
    }
  }

  /**
   * Null out entries in underlying array from current size to oldSize.
   */
  public void clearTail( final int oldSize )
  {
    Arrays.fill( data, size, oldSize, null );
  }

  @Nonnull
  @Override
  public Iterator<E> iterator()
  {
    if ( it == null )
    {
      it = new BagIterator();
    }

    it.validCursorPos = false;
    it.cursor = 0;

    return it;
  }

  @Nonnull
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append( "Bag(" );
    for ( int i = 0; size > i; i++ )
    {
      if ( i > 0 )
      {
        sb.append( ", " );
      }
      sb.append( data[ i ] );
    }
    sb.append( ')' );
    return sb.toString();
  }

  @Override
  public boolean equals( @Nullable final Object o )
  {
    if ( this == o )
    {
      return true;
    }
    if ( o == null || getClass() != o.getClass() )
    {
      return false;
    }

    final Bag bag = (Bag) o;
    if ( size != bag.size() )
    {
      return false;
    }

    for ( int i = 0; size > i; i++ )
    {
      if ( data[ i ] != bag.data[ i ] )
      {
        return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    for ( int i = 0, s = size; s > i; i++ )
    {
      hash = ( 127 * hash ) + data[ i ].hashCode();
    }

    return hash;
  }

  /**
   * An Iterator for Bag.
   *
   * @see Iterator
   */
  private final class BagIterator
    implements Iterator<E>
  {
    /**
     * Current position.
     */
    private int cursor;
    /**
     * True if the current position is within bounds.
     */
    private boolean validCursorPos;

    @Override
    public boolean hasNext()
    {
      return ( cursor < size );
    }

    @Override
    public E next()
      throws NoSuchElementException
    {
      if ( cursor == size )
      {
        throw new NoSuchElementException( "Iterated past last element" );
      }

      final E e = (E) data[ cursor++ ];
      validCursorPos = true;
      return e;
    }

    @Override
    public void remove()
      throws IllegalStateException
    {
      if ( !validCursorPos )
      {
        throw new IllegalStateException();
      }

      validCursorPos = false;
      Bag.this.remove( --cursor );
    }
  }
}
