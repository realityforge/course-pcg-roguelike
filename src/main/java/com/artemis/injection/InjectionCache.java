package com.artemis.injection;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.utils.reflect.ClassReflection.*;

/**
 * Date: 31/7/2015
 * Time: 17:13 PM
 *
 * @author Snorre E. Brekke
 */
public class InjectionCache
{
  public static final SharedInjectionCache sharedCache = new SharedInjectionCache();
  private final Map<Class<?>, CachedClass> classCache = new HashMap<>();
  private final Map<Class<?>, ClassType> fieldClassTypeCache = new HashMap<>();
  private final Map<Field, CachedField> namedWireCache = new HashMap<>();
  private final Map<Field, Class<?>> genericsCache = new HashMap<>();
  private static final Wire DEFAULT_WIRE = new Wire()
  {
    @Nonnull
    @Override
    public Class<? extends Annotation> annotationType()
    {
      return Wire.class;
    }

    @Override
    public boolean injectInherited()
    {
      return true;
    }

    @Override
    public boolean failOnNull()
    {
      return true;
    }

    @Nullable
    @Override
    public String name()
    {
      return null;
    }
  };

  public CachedClass getCachedClass( final Class<?> clazz )
    throws ReflectionException
  {
    CachedClass cachedClass = classCache.get( clazz );
    if ( cachedClass == null )
    {
      cachedClass = new CachedClass( clazz );

      cachedClass.wireType = getWireType( clazz );
      if ( cachedClass.wireType == WireType.IGNORED && clazz != Object.class )
      {
        setWireAnnotation( cachedClass, DEFAULT_WIRE );
      }
      else if ( cachedClass.wireType == WireType.WIRE )
      {
        setWireAnnotation( cachedClass, ClassReflection.getAnnotation( clazz, Wire.class ) );
      }

      classCache.put( clazz, cachedClass );
    }
    return cachedClass;
  }

  /**
   * Set {@code @Wire} annotation value for cached class.
   */
  private void setWireAnnotation( @Nonnull final CachedClass cachedClass, @Nonnull final Wire wireAnnotation )
  {
    cachedClass.wireType = WireType.WIRE;
    cachedClass.wireAnnotation = wireAnnotation;
    cachedClass.failOnNull = wireAnnotation.failOnNull();
    cachedClass.injectInherited = wireAnnotation.injectInherited();
  }

  /**
   * Determine desired wiring on class by annotation.
   * Convention is {@code Wire(injectInherited=true)}
   */
  @Nonnull
  private WireType getWireType( final Class<?> clazz )
  {
    return
      isAnnotationPresent( clazz, Wire.class ) ? WireType.WIRE :
      isAnnotationPresent( clazz, SkipWire.class ) ? WireType.SKIPWIRE :
      WireType.IGNORED;
  }

  public CachedField getCachedField( @Nonnull final Field field )
  {
    CachedField cachedField = namedWireCache.get( field );
    if ( cachedField == null )
    {
      if ( field.isAnnotationPresent( Wire.class ) )
      {
        final Wire wire = field.getAnnotation( Wire.class );
        cachedField = new CachedField( field, WireType.WIRE, wire.name(), wire.failOnNull() );
      }
      else if ( field.isAnnotationPresent( SkipWire.class ) )
      {
        cachedField = new CachedField( field, WireType.SKIPWIRE, null, false );
      }
      else
      {
        cachedField = new CachedField( field, WireType.IGNORED, null, false );
      }
      namedWireCache.put( field, cachedField );
    }
    return cachedField;
  }

  public ClassType getFieldClassType( final Class<?> fieldType )
  {
    ClassType injectionType = fieldClassTypeCache.get( fieldType );
    if ( injectionType == null )
    {
      if ( ClassReflection.isAssignableFrom( ComponentMapper.class, fieldType ) )
      {
        injectionType = ClassType.MAPPER;
      }
      else if ( ClassReflection.isAssignableFrom( BaseSystem.class, fieldType ) )
      {
        injectionType = ClassType.SYSTEM;
      }
      else if ( ClassReflection.isAssignableFrom( World.class, fieldType ) )
      {
        injectionType = ClassType.WORLD;
      }
      else
      {
        injectionType = ClassType.CUSTOM;
      }
      fieldClassTypeCache.put( fieldType, injectionType );
    }
    return injectionType;
  }

  @Nullable
  public Class<?> getGenericType( @Nonnull final Field field )
  {
    Class<?> genericsType = genericsCache.get( field );
    if ( genericsType == null )
    {
      genericsType = field.getElementType( 0 );
      genericsCache.put( field, genericsType );
    }
    return genericsType;
  }

}
