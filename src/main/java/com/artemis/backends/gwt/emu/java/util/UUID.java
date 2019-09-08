package java.util;

import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author senk.christian@gmail.com
 */
public class UUID
  implements Serializable, Comparable<UUID>
{
  private static final long serialVersionUID = 7373345728974414241L;
  private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
  private String value;

  /**
   *
   */
  private UUID()
  {
  }

  /**
   *
   */
  @Nonnull
  public static UUID fromString( final String uuidString )
  {
    //TODO: Validation

    final UUID uuid = new UUID();
    uuid.value = uuidString;

    return uuid;
  }

  /**
   *
   */
  @Nonnull
  public static UUID randomUUID()
  {
    return fromString( generateUUIDString() );
  }

  /**
   * Generate a RFC4122, version 4 ID. Example:
   * "92329D39-6F5C-4520-ABFC-AAB64544E172"
   */
  @Nonnull
  private static String generateUUIDString()
  {
    final char[] uuid = new char[ 36 ];
    int r;

    // rfc4122 requires these characters
    uuid[ 8 ] = uuid[ 13 ] = uuid[ 18 ] = uuid[ 23 ] = '-';
    uuid[ 14 ] = '4';

    // Fill in random data.  At i==19 set the high bits of clock sequence as
    // per rfc4122, sec. 4.1.5
    for ( int i = 0; i < 36; i++ )
    {
      if ( uuid[ i ] == 0 )
      {
        r = (int) ( Math.random() * 16 );
        uuid[ i ] = CHARS[ ( i == 19 ) ? ( r & 0x3 ) | 0x8 : r & 0xf ];
      }
    }
    return new String( uuid );
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo( @Nonnull final UUID arg0 )
  {
    return value.compareTo( arg0.value );
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( @Nullable final Object obj )
  {
		if ( this == obj )
		{
			return true;
		}
		if ( obj == null )
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}
    final UUID other = (UUID) obj;
    if ( value == null )
    {
			return other.value == null;
    }
    else
			return value.equals( other.value );
	}

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return value;
  }

}
