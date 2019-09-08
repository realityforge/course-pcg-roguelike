/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.artemis.utils.reflect;

import com.artemis.gwtref.client.ReflectionCache;
import com.artemis.gwtref.client.Type;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.Constructor;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.Method;
import com.artemis.utils.reflect.ReflectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utilities for Class reflection.
 *
 * @author nexsoftware
 */
public final class ClassReflection
{
  /**
   * Returns the Class object associated with the class or interface with the supplied string name.
   */
  static public Class forName( final String name )
    throws ReflectionException
  {
    try
    {
      return ReflectionCache.forName( name ).getClassOfType();
    }
    catch ( final Exception e )
    {
      throw new ReflectionException( "Class not found: " + name, e );
    }
  }

  /**
   * Returns the simple name of the underlying class as supplied in the source code.
   */
  @Nonnull
  static public String getSimpleName( @Nonnull final Class c )
  {
    return c.getSimpleName();
  }

  /**
   * Determines if the supplied Object is assignment-compatible with the object represented by supplied Class.
   */
  static public boolean isInstance( final Class c, @Nonnull final Object obj )
  {
    return isAssignableFrom( c, obj.getClass() );
  }

  @Nonnull
  static public String getCanonicalName( @Nonnull final Class c )
  {
    return c.getSimpleName();
  }

  /**
   * Determines if the class or interface represented by first Class parameter is either the same as, or is a superclass or
   * superinterface of, the class or interface represented by the second Class parameter.
   */
  static public boolean isAssignableFrom( final Class c1, final Class c2 )
  {
    final Type c1Type = ReflectionCache.getType( c1 );
    final Type c2Type = ReflectionCache.getType( c2 );
    return c1Type.isAssignableFrom( c2Type );
  }

  /**
   * Returns true if the class or interface represented by the supplied Class is a member class.
   */
  static public boolean isMemberClass( final Class c )
  {
    return ReflectionCache.getType( c ).isMemberClass();
  }

  /**
   * Returns true if the class or interface represented by the supplied Class is a static class.
   */
  static public boolean isStaticClass( final Class c )
  {
    return ReflectionCache.getType( c ).isStatic();
  }

  /**
   * Returns true if the class or interface represented by the supplied Class is an abstract class.
   */
  static public boolean isAbstractClass( final Class c )
  {
    return ReflectionCache.getType( c ).isAbstract();
  }

  /**
   * Creates a new instance of the class represented by the supplied Class.
   */
  @Nonnull
  static public <T> T newInstance( @Nonnull final Class<T> c )
    throws ReflectionException
  {
    try
    {
      return (T) ReflectionCache.getType( c ).newInstance();
    }
    catch ( final NoSuchMethodException e )
    {
      throw new ReflectionException( "Could not use default constructor of " + c.getName(), e );
    }
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Constructor} containing the public constructors of the class represented by the supplied Class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Constructor[] getConstructors( final Class c )
  {
    final com.artemis.gwtref.client.Constructor[] constructors = ReflectionCache.getType( c ).getConstructors();
    final com.artemis.utils.reflect.Constructor[] result = new com.artemis.utils.reflect.Constructor[ constructors.length ];
    for ( int i = 0, j = constructors.length; i < j; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Constructor( constructors[ i ] );
    }
    return result;
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Constructor} that represents the public constructor for the supplied class which takes the supplied
   * parameter types.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Constructor getConstructor( @Nonnull final Class c, final Class... parameterTypes )
    throws ReflectionException
  {
    try
    {
      return new com.artemis.utils.reflect.Constructor( ReflectionCache.getType( c ).getConstructor( parameterTypes ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting constructor for class: " + c.getName(), e );
    }
    catch ( final NoSuchMethodException e )
    {
      throw new ReflectionException( "Constructor not found for class: " + c.getName(), e );
    }
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Constructor} that represents the constructor for the supplied class which takes the supplied parameter
   * types.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Constructor getDeclaredConstructor( @Nonnull final Class c, final Class... parameterTypes )
    throws ReflectionException
  {
    try
    {
      return new Constructor( ReflectionCache.getType( c ).getDeclaredConstructor( parameterTypes ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting constructor for class: " + c.getName(), e );
    }
    catch ( final NoSuchMethodException e )
    {
      throw new ReflectionException( "Constructor not found for class: " + c.getName(), e );
    }
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Method} containing the public member methods of the class represented by the supplied Class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Method[] getMethods( final Class c )
  {
    final com.artemis.gwtref.client.Method[] methods = ReflectionCache.getType( c ).getMethods();
    final com.artemis.utils.reflect.Method[] result = new com.artemis.utils.reflect.Method[ methods.length ];
    for ( int i = 0, j = methods.length; i < j; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Method( methods[ i ] );
    }
    return result;
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Method} that represents the public member method for the supplied class which takes the supplied parameter
   * types.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Method getMethod( @Nonnull final Class c, final String name, final Class... parameterTypes )
    throws ReflectionException
  {
    try
    {
      return new com.artemis.utils.reflect.Method( ReflectionCache.getType( c ).getMethod( name, parameterTypes ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting method: " + name + ", for class: " + c.getName(),
                                     e );
    }
    catch ( final NoSuchMethodException e )
    {
      throw new ReflectionException( "Method not found: " + name + ", for class: " + c.getName(), e );
    }
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Method} containing the methods declared by the class represented by the supplied Class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Method[] getDeclaredMethods( final Class c )
  {
    final com.artemis.gwtref.client.Method[] methods = ReflectionCache.getType( c ).getDeclaredMethods();
    final com.artemis.utils.reflect.Method[] result = new com.artemis.utils.reflect.Method[ methods.length ];
    for ( int i = 0, j = methods.length; i < j; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Method( methods[ i ] );
    }
    return result;
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Method} that represents the method declared by the supplied class which takes the supplied parameter types.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Method getDeclaredMethod( @Nonnull final Class c, final String name, final Class... parameterTypes )
    throws ReflectionException
  {
    try
    {
      return new Method( ReflectionCache.getType( c ).getMethod( name, parameterTypes ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting method: " + name + ", for class: " + c.getName(),
                                     e );
    }
    catch ( final NoSuchMethodException e )
    {
      throw new ReflectionException( "Method not found: " + name + ", for class: " + c.getName(), e );
    }
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Field} containing the public fields of the class represented by the supplied Class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Field[] getFields( final Class c )
  {
    final com.artemis.gwtref.client.Field[] fields = ReflectionCache.getType( c ).getFields();
    final com.artemis.utils.reflect.Field[] result = new com.artemis.utils.reflect.Field[ fields.length ];
    for ( int i = 0, j = fields.length; i < j; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Field( fields[ i ] );
    }
    return result;
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Field} that represents the specified public member field for the supplied class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Field getField( @Nonnull final Class c, final String name )
    throws ReflectionException
  {
    try
    {
      return new com.artemis.utils.reflect.Field( ReflectionCache.getType( c ).getField( name ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting field: " + name + ", for class: " + c.getName(),
                                     e );
    }
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Field} objects reflecting all the fields declared by the supplied class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Field[] getDeclaredFields( final Class c )
  {
    final com.artemis.gwtref.client.Field[] fields = ReflectionCache.getType( c ).getDeclaredFields();
    final com.artemis.utils.reflect.Field[] result = new com.artemis.utils.reflect.Field[ fields.length ];
    for ( int i = 0, j = fields.length; i < j; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Field( fields[ i ] );
    }
    return result;
  }

  /**
   * Returns this element's annotation for the specified type if such an annotation is present, else null.
   */
  @Nullable
  static public <T extends java.lang.annotation.Annotation> T getAnnotation( final Class c, final Class<T> annotationClass )
    throws ReflectionException
  {
    final com.artemis.utils.reflect.Annotation declaredAnnotation = getDeclaredAnnotation( c, annotationClass );
    return declaredAnnotation != null ? declaredAnnotation.getAnnotation( annotationClass ) : null;
  }

  /**
   * Returns a {@link com.artemis.utils.reflect.Field} that represents the specified declared field for the supplied class.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Field getDeclaredField( @Nonnull final Class c, final String name )
    throws ReflectionException
  {
    try
    {
      return new Field( ReflectionCache.getType( c ).getField( name ) );
    }
    catch ( final SecurityException e )
    {
      throw new ReflectionException( "Security violation while getting field: " + name + ", for class: " + c.getName(),
                                     e );
    }
  }

  /**
   * Returns true if the supplied class includes an annotation of the given class type.
   */
  static public boolean isAnnotationPresent( final Class c, final Class<? extends java.lang.annotation.Annotation> annotationType )
  {
    final java.lang.annotation.Annotation[] annotations = ReflectionCache.getType( c ).getDeclaredAnnotations();
		if ( annotations == null )
		{
			return false;
		}

    for ( final java.lang.annotation.Annotation annotation : annotations )
    {
      if ( annotation.annotationType().equals( annotationType ) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an array of {@link com.artemis.utils.reflect.Annotation} objects reflecting all annotations declared by the supplied class,
   * or an empty array if there are none. Does not include inherited annotations.
   */
  @Nonnull
  static public com.artemis.utils.reflect.Annotation[] getDeclaredAnnotations( final Class c )
  {
    final java.lang.annotation.Annotation[] annotations = ReflectionCache.getType( c ).getDeclaredAnnotations();
    final com.artemis.utils.reflect.Annotation[] result = new com.artemis.utils.reflect.Annotation[ annotations.length ];
    for ( int i = 0; i < annotations.length; i++ )
    {
      result[ i ] = new com.artemis.utils.reflect.Annotation( annotations[ i ] );
    }
    return result;
  }

  /**
   * Returns an {@link com.artemis.utils.reflect.Annotation} object reflecting the annotation provided, or null of this field doesn't
   * have such an annotation. This is a convenience function if the caller knows already which annotation
   * type he's looking for.
   */
  @Nullable
  static public com.artemis.utils.reflect.Annotation getDeclaredAnnotation( final Class c,
                                                                            final Class<? extends java.lang.annotation.Annotation> annotationType )
  {
    final java.lang.annotation.Annotation[] annotations = ReflectionCache.getType( c ).getDeclaredAnnotations();
    for ( final java.lang.annotation.Annotation annotation : annotations )
    {
      if ( annotation.annotationType().equals( annotationType ) )
      {
        return new Annotation( annotation );
      }
    }
    return null;
  }
}
