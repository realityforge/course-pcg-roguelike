package com.artemis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Artemis pieces with priority pending registration.
 *
 * @author Daan van Yperen
 * @see WorldConfigurationBuilder
 */
class ConfigurationElement<T>
  implements Comparable<ConfigurationElement<T>>
{
  public final int priority;
  public final Class<?> itemType;
  public T item;

  public ConfigurationElement( @Nonnull T item, int priority )
  {
    this.item = item;
    itemType = item.getClass();
    this.priority = priority;
  }

  @Override
  public int compareTo( @Nonnull ConfigurationElement<T> o )
  {
    // Sort by priority descending.
    return Integer.compare( o.priority, priority );
  }

  @Override
  public boolean equals( @Nullable Object o )
  {
		if ( this == o )
		{
			return true;
		}
		if ( o == null || getClass() != o.getClass() )
		{
			return false;
		}
    return item.equals( ( (ConfigurationElement<?>) o ).item );
  }

  @Override
  public int hashCode()
  {
    return item.hashCode();
  }

  /**
   * create instance of Registerable.
   */
  @Nonnull
  public static <T> ConfigurationElement<T> of( @Nonnull T item )
  {
    return of( item, WorldConfigurationBuilder.Priority.NORMAL );
  }

  /**
   * create instance of Registerable.
   */
  @Nonnull
  public static <T> ConfigurationElement<T> of( @Nonnull T item, int priority )
  {
    return new ConfigurationElement<T>( item, priority );
  }
}
