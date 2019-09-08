package java.lang;

public class ClassNotFoundException
  extends Exception
{
  public ClassNotFoundException()
  {
    super();
  }

  public ClassNotFoundException( final String message, final Throwable cause )
  {
    super( message, cause );
  }

  public ClassNotFoundException( final String message )
  {
    super( message );
  }

  public ClassNotFoundException( final Throwable cause )
  {
    super( cause );
  }
}
