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
  private final Map<Class<?>, Class<?>> systems;

  public ArtemisFieldResolver()
  {
    systems = new IdentityHashMap<>();
  }

  @Override
  public void initialize( @Nonnull final World world )
  {
    this.world = world;

    for ( final BaseSystem es : world.getSystems() )
    {
      final Class<?> origin = es.getClass();
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
  public Object resolve( final Object target, final Class<?> fieldType, @Nonnull final Field field )
  {
    final ClassType injectionType = cache.getFieldClassType( fieldType );
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
  private ComponentMapper<?> getComponentMapper( @Nonnull final Field field )
  {
    final Class<?> mapperType = cache.getGenericType( field );
    return world.getMapper( (Class<? extends Component>) mapperType );

  }

  @Override
  public void setCache( final InjectionCache cache )
  {
    this.cache = cache;
  }
}
