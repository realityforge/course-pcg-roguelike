package com.artemis.injection;

import com.artemis.MundaneWireException;
import com.artemis.World;
import com.artemis.utils.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Can inject arbitrary fields annotated with {@link com.artemis.annotations.Wire},
 * typically registered via registered via {@link com.artemis.WorldConfiguration#register}
 *
 * @author Snorre E. Brekke
 */
public class WiredFieldResolver
  implements UseInjectionCache, PojoFieldResolver
{
  private InjectionCache cache;
  private Map<String, Object> pojos = new HashMap<>();
  private World world;

  public WiredFieldResolver()
  {
  }

  @Override
  public void initialize( final World world )
  {
    this.world = world;
  }

  @Nullable
  @Override
  public Object resolve( final Object target, final Class<?> fieldType, @Nonnull final Field field )
  {
    final ClassType injectionType = cache.getFieldClassType( fieldType );
    final CachedField cachedField = cache.getCachedField( field );

    if ( injectionType == ClassType.CUSTOM || injectionType == ClassType.WORLD )
    {
      if ( cachedField.wireType == WireType.WIRE )
      {
        String key = cachedField.name;
        if ( "".equals( key ) )
        {
          key = field.getType().getName();
        }

        if ( !pojos.containsKey( key ) && cachedField.failOnNull )
        {
          final String err = "Not registered: " + key + "=" + fieldType;
          throw new MundaneWireException( err );
        }

        return pojos.get( key );
      }
    }
    return null;
  }

  @Override
  public void setCache( final InjectionCache cache )
  {
    this.cache = cache;
  }

  @Override
  public void setPojos( final Map<String, Object> pojos )
  {
    this.pojos = pojos;
  }
}
