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

import java.util.Collection;
import javax.annotation.Nonnull;

public interface IReflectionCache
{
  // Class level methods
  @Nonnull
  Collection<Type> getKnownTypes();

  @Nonnull
  Type forName( String name );

  @Nonnull
  Object newArray( Class componentType, int size );

  int getArrayLength( Type type, Object obj );

  @Nonnull
  Object getArrayElement( Type type, Object obj, int i );

  void setArrayElement( Type type, Object obj, int i, Object value );

  // Field Methods
  @Nonnull
  Object get( Field field, Object obj )
    throws IllegalAccessException;

  void set( Field field, Object obj, Object value )
    throws IllegalAccessException;

  // Method Methods :p
  @Nonnull
  Object invoke( Method m, Object obj, Object[] params );
}
