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

import com.google.gwt.core.client.GWT;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReflectionCache
{
  public static final IReflectionCache instance = GWT.create( IReflectionCache.class );

  public static Type forName( @Nonnull final String name )
  {
    final Type type = instance.forName( convert( name ) );
    if ( type == null )
    {
      throw new RuntimeException( "(artemis-odb) Couldn't find Type for class '" + name + "'" );
    }
    return type;
  }

  @Nullable
  public static Type getType( @Nullable final Class clazz )
  {
		if ( clazz == null )
		{
			return null;
		}
    final Type type = instance.forName( convert( clazz.getName() ) );
    if ( type == null )
    {
      throw new RuntimeException( "(artemis-odb) Couldn't find Type for class '" + clazz.getName() + "'" );
    }
    return type;
  }

  @Nonnull
  private static String convert( @Nonnull final String className )
  {
    if ( className.startsWith( "[" ) )
    {
      int dimensions = 0;
      char c = className.charAt( 0 );
      String suffix = "";
      while ( c == '[' )
      {
        dimensions++;
        suffix += "[]";
        c = className.charAt( dimensions );
      }
      final char t = className.charAt( dimensions );
      switch ( t )
      {
        case 'Z':
          return "boolean" + suffix;
        case 'B':
          return "byte" + suffix;
        case 'C':
          return "char" + suffix;
        case 'L':
          return className.substring( dimensions + 1, className.length() - 1 ).replace( '$', '.' ) + suffix;
        case 'D':
          return "double" + suffix;
        case 'F':
          return "float" + suffix;
        case 'I':
          return "int" + suffix;
        case 'J':
          return "long" + suffix;
        case 'S':
          return "short" + suffix;
        default:
          throw new IllegalArgumentException( "(artemis-odb) Couldn't transform '" +
                                              className +
                                              "' to qualified source name" );
      }
    }
    else
    {
      return className.replace( '$', '.' );
    }
  }

  public static Object newArray( final Class componentType, final int size )
  {
    return instance.newArray( componentType, size );
  }

  public static Collection<Type> getKnownTypes()
  {
    return instance.getKnownTypes();
  }
}
