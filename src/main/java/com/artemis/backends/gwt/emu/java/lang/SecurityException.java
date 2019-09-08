package java.lang;

public class SecurityException
  extends RuntimeException
{
  public SecurityException()
  {
    super();
  }

  public SecurityException( final String message, final Throwable cause )
  {
    super( message, cause );
  }

  public SecurityException( final String s )
  {
    super( s );
  }

  public SecurityException( final Throwable cause )
  {
    super( cause );
  }
}
