package com.artemis.utils.reflect;

import com.artemis.BaseEntitySystem;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.utils.IntBag;
import java.util.Arrays;
import javax.annotation.Nonnull;
import static com.artemis.utils.reflect.ClassReflection.*;

public final class ReflectionUtil
{
  private static final Class<?>[] PARAM_ENTITY = { Entity.class };
  private static final Class<?>[] PARAM_ID = { int.class };
  private static final Class<?>[] PARAM_IDS = { IntBag.class };

  private ReflectionUtil()
  {
  }

  public static boolean implementsObserver( @Nonnull final BaseSystem owner, final String methodName )
  {
    try
    {
      final Method method = getMethod( owner.getClass(), methodName, PARAM_ENTITY );
      final Class declarer = method.getDeclaringClass();
      return !( Manager.class.equals( declarer ) || EntitySystem.class.equals( declarer ) );
    }
    catch ( final ReflectionException e )
    {
      throw new RuntimeException( e );
    }
  }

  public static boolean implementsAnyObserver( @Nonnull final BaseEntitySystem owner )
  {
    if ( isInstance( Manager.class, owner ) || isInstance( EntitySystem.class, owner ) )
    {
      return true; // case handled by implementsObserver(owner, methodName)
    }

    // check parent chain for user-supplied implementations of
    // inserted() and removed()
    Class type = owner.getClass();
    while ( type != BaseEntitySystem.class )
    {
      for ( final Method m : ClassReflection.getDeclaredMethods( type ) )
      {
        if ( isObserver( m ) )
        {
          return true;
        }
      }

      type = type.getSuperclass();
    }

    return false;
  }

  private static boolean isObserver( @Nonnull final Method m )
  {
    final String name = m.getName();
    if ( "inserted".equals( name ) || "removed".equals( name ) )
    {
      final Class[] types = m.getParameterTypes();
      return Arrays.equals( PARAM_ID, types ) || Arrays.equals( PARAM_IDS, types );
    }

    return false;
  }

  public static boolean isGenericType( @Nonnull final Field f, final Class<?> mainType, final Class typeParameter )
  {
    return mainType == f.getType() && typeParameter == f.getElementType( 0 );
  }
}
