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

/**
 * A constructor for the enclosing type.
 *
 * @author mzechner
 */
public class Constructor
  extends Method
{
  Constructor( final String name,
               final Class enclosingType,
               final Class returnType,
               final Parameter[] parameters,
               final boolean isAbstract,
               final boolean isFinal,
               final boolean isStatic,
               final boolean isDefaultAccess,
               final boolean isPrivate,
               final boolean isProtected,
               final boolean isPublic,
               final boolean isNative,
               final boolean isVarArgs,
               final boolean isMethod,
               final boolean isConstructor,
               final int methodId,
               final Annotation[] annotations )
  {
    super( name,
           enclosingType,
           returnType,
           parameters,
           isAbstract,
           isFinal,
           isStatic,
           isDefaultAccess,
           isPrivate,
           isProtected,
           isPublic,
           isNative,
           isVarArgs,
           isMethod,
           isConstructor,
           methodId,
           annotations );
  }

  /**
   * @return a new instance of the enclosing type of this constructor.
   */
  public Object newInstance( final Object... params )
  {
    return super.invoke( null, params );
  }
}
