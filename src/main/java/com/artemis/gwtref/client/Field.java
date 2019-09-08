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
package com.artemis.gwtref.client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Field
{
  final String name;
  final Class enclosingType;
  final Class type;
  final boolean isFinal;
  final boolean isDefaultAccess;
  final boolean isPrivate;
  final boolean isProtected;
  final boolean isPublic;
  final boolean isStatic;
  final boolean isTransient;
  final boolean isVolatile;
  final int getter;
  final int setter;
  final Class[] elementTypes;
  @Nonnull
  final Annotation[] annotations;

  Field( final String name,
         final Class enclosingType,
         final Class type,
         final boolean isFinal,
         final boolean isDefaultAccess,
         final boolean isPrivate,
         final boolean isProtected,
         final boolean isPublic,
         final boolean isStatic,
         final boolean isTransient,
         final boolean isVolatile,
         final int getter,
         final int setter,
         final Class[] elementTypes,
         @Nullable final Annotation[] annotations )
  {
    this.name = name;
    this.enclosingType = enclosingType;
    this.type = type;
    this.isFinal = isFinal;
    this.isDefaultAccess = isDefaultAccess;
    this.isPrivate = isPrivate;
    this.isProtected = isProtected;
    this.isPublic = isPublic;
    this.isStatic = isStatic;
    this.isTransient = isTransient;
    this.isVolatile = isVolatile;
    this.getter = getter;
    this.setter = setter;
    this.elementTypes = elementTypes;
    this.annotations = annotations != null ? annotations : new Annotation[]{};
  }

  @Nonnull
  public Annotation[] getDeclaredAnnotations()
  {
    return annotations;
  }

  public Object get( final Object obj )
    throws IllegalAccessException
  {
    return ReflectionCache.instance.get( this, obj );
  }

  public void set( final Object obj, final Object value )
    throws IllegalAccessException
  {
    ReflectionCache.instance.set( this, obj, value );
  }

  @Nullable
  public Type getElementType( final int index )
  {
    if ( elementTypes != null && index < elementTypes.length )
    {
      return ReflectionCache.getType( elementTypes[ index ] );
    }
    return null;
  }

  public String getName()
  {
    return name;
  }

  public Type getEnclosingType()
  {
    return ReflectionCache.getType( enclosingType );
  }

  public Type getType()
  {
    return ReflectionCache.getType( type );
  }

  public boolean isSynthetic()
  {
    return false;
  }

  public boolean isFinal()
  {
    return isFinal;
  }

  public boolean isDefaultAccess()
  {
    return isDefaultAccess;
  }

  public boolean isPrivate()
  {
    return isPrivate;
  }

  public boolean isProtected()
  {
    return isProtected;
  }

  public boolean isPublic()
  {
    return isPublic;
  }

  public boolean isStatic()
  {
    return isStatic;
  }

  public boolean isTransient()
  {
    return isTransient;
  }

  public boolean isVolatile()
  {
    return isVolatile;
  }

  @Nonnull
  @Override
  public String toString()
  {
    return "Field [name=" +
           name +
           ", enclosingType=" +
           enclosingType +
           ", type=" +
           type +
           ", isFinal=" +
           isFinal
           +
           ", isDefaultAccess=" +
           isDefaultAccess +
           ", isPrivate=" +
           isPrivate +
           ", isProtected=" +
           isProtected +
           ", isPublic="
           +
           isPublic +
           ", isStatic=" +
           isStatic +
           ", isTransient=" +
           isTransient +
           ", isVolatile=" +
           isVolatile +
           ", getter="
           +
           getter +
           ", setter=" +
           setter +
           ", elementTypes=" +
           Arrays.toString( elementTypes ) +
           ", annotations="
           +
           Arrays.toString( annotations ) +
           "]";
  }

}
