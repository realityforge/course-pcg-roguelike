package com.artemis.injection;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Can resolve {@link World}, {@link ComponentMapper}, {@link BaseSystem} and
 * {@link com.artemis.Manager} types registered in the {@link World}
 *
 * @author Snorre E. Brekke
 */
public class ArtemisFieldResolver
  implements FieldResolver, UseInjectionCache
{
  private World world;
  private InjectionCache cache;
  private Map<Class<?>, Class<?>> systems;

  public ArtemisFieldResolver()
  {
    systems = new IdentityHashMap<Class<?>, Class<?>>();
  }

  @Override
  public void initialize( @Nonnull World world )
  {
    this.world = world;

    for ( BaseSystem es : world.getSystems() )
    {
      Class<?> origin = es.getClass();
      Class<?> clazz = origin;
      do
      {
        systems.put( clazz, origin );
      } while ( ( clazz = clazz.getSuperclass() ) != Object.class );
    }
  }

  @Nullable
  @Override
  @SuppressWarnings( "unchecked" )
  public Object resolve( Object target, Class<?> fieldType, @Nonnull Field field )
  {
    ClassType injectionType = cache.getFieldClassType( fieldType );
    switch ( injectionType )
    {
      case MAPPER:
        return getComponentMapper( field );
      case SYSTEM:
        return world.getSystem( (Class<BaseSystem>) systems.get( fieldType ) );
      case WORLD:
        return world;
      default:
        return null;

    }
  }

  @SuppressWarnings( "unchecked" )
  private ComponentMapper<?> getComponentMapper( @Nonnull Field field )
  {
    Class<?> mapperType = cache.getGenericType( field );
    return world.getMapper( (Class<? extends Component>) mapperType );

  }

  @Override
  public void setCache( InjectionCache cache )
  {
    this.cache = cache;
  }
}
