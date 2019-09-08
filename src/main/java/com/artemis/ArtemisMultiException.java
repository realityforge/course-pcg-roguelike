package com.artemis;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

@SuppressWarnings( "serial" )
public class ArtemisMultiException
  extends RuntimeException
{
  private final List<Throwable> exceptions = new ArrayList<>();

  /**
   * required constructor for serialization
   */
  public ArtemisMultiException()
  {
  }

  public ArtemisMultiException( @Nonnull final List<Throwable> exceptions )
  {
    super();
    this.exceptions.addAll( exceptions );
  }

  @Nonnull
  public List<Throwable> getExceptions()
  {
    return exceptions;
  }

  @Nonnull
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    for ( final Throwable t : exceptions )
    {
			if ( sb.length() > 0 )
			{
				sb.append( "\n" );
			}
      sb.append( t.getMessage() );
    }
    return sb.toString();
  }
}
